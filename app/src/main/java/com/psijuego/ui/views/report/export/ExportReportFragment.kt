package com.psijuego.ui.views.report.export

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.psijuego.R
import com.psijuego.core.utils.ResourceState
import com.psijuego.core.utils.UtilFile
import com.psijuego.core.utils.UtilPDF
import com.psijuego.core.utils.UtilShare
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.model.ui.HomeUI
import com.psijuego.databinding.FragmentExportReportBinding
import com.psijuego.ui.views.report.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ExportReportFragment : Fragment() {

    private lateinit var binding: FragmentExportReportBinding
    private val viewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var listCategoryUI: List<CategoryUI>
    private lateinit var homeUI: HomeUI
    private lateinit var conclusion: String
    private lateinit var pdfFile: File
    private lateinit var qrFile: File
    private var isPdfSelect = true
    private var pdfUri: Uri? = null
    private var qrUri: Uri? = null
    private var bitmap: Bitmap? = null
    private val urisList = ArrayList<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExportReportBinding.inflate(inflater, container, false)
        setUpViewModel()
        return binding.root
    }

    private fun setUpViewModel() {
        listCategoryUI = viewModel.categoryUI.value ?: emptyList()
        homeUI = viewModel.homeUI.value ?: HomeUI()
        conclusion = viewModel.conclusion.value ?: ""
        createPdfDocument()
        viewModel.uploadState.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceState.Success -> {
                    Toast.makeText(requireContext(), resources.getString(R.string.qr_created), Toast.LENGTH_SHORT).show()
                    binding.topAppBar.menu.findItem(R.id.new_qr).isEnabled = true
                    createQr(it.data)
                }

                is ResourceState.Failure -> {
                    Toast.makeText(requireContext(), R.string.qr_failed, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun createPdfDocument() {
        UtilPDF.getInstance().createPdf(homeUI, listCategoryUI, conclusion).let {
            pdfFile = it.first
            pdfUri = it.second
        }
        viewModel.uploadDocument(pdfFile)
        setUpComponent()
    }

    private fun setUpComponent() {
        with(binding) {

            if (pdfFile.exists()) {
                pdfView.fromFile(pdfFile).load()
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                val selectedFile = if (isPdfSelect) pdfFile else qrFile
                when (menuItem.itemId) {
                    R.id.new_qr -> {
                        setUpActionQrButton()
                        true
                    }

                    R.id.share -> {
                        val anchorView = topAppBar.findViewById<View>(R.id.share)
                        showMenu(anchorView)
                        true
                    }

                    R.id.save -> {
                        onSave(selectedFile)
                        true
                    }

                    else -> super.onContextItemSelected(menuItem)
                }
            }

            btnNewReport.setOnClickListener { confirmAction() }

        }
    }

    private fun showMenu(view: View) {
        val popup = PopupMenu(context, view)
        popup.menuInflater.inflate(R.menu.on_share_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedUri = if (isPdfSelect) pdfUri else qrUri


            when (menuItem.itemId) {
                R.id.whatsapp -> {
                    onWhatsAppPDFShare(selectedUri)
                    true
                }

                R.id.email -> {
                    onEmailPDFShare(selectedUri)
                    true
                }

                else -> {
                    super.onContextItemSelected(menuItem)
                }
            }
        }
        popup.show()

    }

    private fun onSave(file: File) {
        UtilFile.getInstance().saveFileToDownloads(file)
        val message =
            if (isPdfSelect) resources.getString(R.string.report_saved) else resources.getString(R.string.qr_saved)
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onEmailPDFShare(uri: Uri?) {
        if (uri != null) {
            urisList.clear()
            urisList.add(uri)
            val intent =
                UtilShare.getInstance().getEmailIntentPdf(binding.root.context, urisList)
            requireContext().startActivity(intent)
        }
    }

    private fun onWhatsAppPDFShare(uri: Uri?) {
        if (uri != null) {
            val intent = UtilShare.getInstance().getWhatsAppIntent(binding.root.context, uri)
            requireContext().startActivity(intent)
        }

    }

    private fun confirmAction() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_new_report))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                onNewReport()
            }
            .show()
    }

    private fun onNewReport() {
        viewModel.setHomeUI(HomeUI())
        viewModel.setConclusion("")
        viewModel.setCategoryUI(emptyList())
        findNavController().navigate(R.id.action_exportReportFragment_to_homeFragment)
    }

    private fun setUpActionQrButton() {
        val menu = binding.topAppBar.menu
        val menuItem = menu.findItem(R.id.new_qr)
        if (isPdfSelect) {
            binding.topAppBar.title = resources.getString(R.string.qr_code)
            menuItem.setIcon(R.drawable.ic_document)
            binding.qrCode.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.VISIBLE
            binding.pdfView.visibility = View.GONE
            isPdfSelect = false
        } else {
            menuItem.setIcon(R.drawable.ic_qr)
            binding.topAppBar.title = resources.getString(R.string.report)
            binding.qrCode.visibility = View.GONE
            binding.tvTitle.visibility = View.GONE
            binding.pdfView.visibility = View.VISIBLE
            isPdfSelect = true
        }
    }


    private fun createQr(url: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            bitmap = barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, 750, 750)
            bitmap?.let { bitmap ->
                UtilPDF.getInstance().createQRPdf(homeUI, bitmap).let {
                    qrFile = it.first
                    qrUri = it.second
                }
            }
            binding.qrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}