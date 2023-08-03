package com.psijuego.ui.views.report.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.psijuego.R
import com.psijuego.core.utils.UtilFile
import com.psijuego.core.utils.UtilUploadFiles
import com.psijuego.data.model.ui.HomeUI
import com.psijuego.databinding.FragmentHomeBinding
import com.psijuego.ui.views.report.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val utilUploadFiles = UtilUploadFiles()
    private val utilFile = UtilFile()

    private lateinit var binding: FragmentHomeBinding
    private var homeUI: HomeUI = HomeUI()
    private val viewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private var mUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setUpComponents()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        bindToObject()
    }

    override fun onResume() {
        super.onResume()
        bindToForm()
    }

    private fun setUpComponents() {
        with(binding) {
            btnNext.setOnClickListener(::preValidate)
            btnUpload.setOnClickListener() {
                attachGalleryDraw()

            }
            ivDelete.setOnClickListener(::onDeleteImage)
        }
    }

    private fun preValidate(view: View) {
        with(binding) {
            if (tvPatientName.text.isNullOrEmpty()) {
                tvPatientName.error = getString(R.string.invalid_field)
                return
            }
            if (tvProfessionalName.text.isNullOrEmpty()) {
                tvProfessionalName.error = getString(R.string.invalid_field)
                return
            }
            onNext()
        }
    }

    private fun onNext() {
        viewModel.setHomeUI(bindToObject())
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_homeFragment_to_indicatorsFragment)
    }

    private fun onDeleteImage(view: View) {
        with(binding) {
            btnUpload.visibility = View.VISIBLE
            ivImage.visibility = View.GONE
            ivDelete.visibility = View.GONE
            tvDescriptionLabel.layoutParams =
                (tvDescriptionLabel.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topToBottom = R.id.btnUpload
                }
        }
        val filePath = mUri?.lastPathSegment?.let { utilFile.getImageFullFilePath(it) } ?: ""
        val deleted = utilFile.deleteFile(filePath)
        if (deleted) {
            mUri = null
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) mUri = result.data!!.data!!
                val success = result.resultCode == Activity.RESULT_OK
                if (success) mUri?.let { this.onAttachmentTaken(it) }
            }
        }

    private fun attachGalleryDraw() {
        resultLauncher.launch(
            Intent.createChooser(
                utilUploadFiles.pickDrawFromGalleryIntent(),
                getString(R.string.images)
            )
        )
    }

    private fun onAttachmentTaken(uri: Uri) {
        try {
            mUri = utilFile.copyContentUriImageToDir(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateComponent(mUri)
    }

    private fun updateComponent(uri: Uri?) {
        if (uri != null) {
            val uri = mUri
            with(binding) {
                btnUpload.visibility = View.GONE
                ivImage.visibility = View.VISIBLE
                ivDelete.visibility = View.VISIBLE
                tvDescriptionLabel.layoutParams =
                    (tvDescriptionLabel.layoutParams as ConstraintLayout.LayoutParams).apply {
                        topToBottom = R.id.ivImage
                    }
                showImage(uri)
            }
        }
    }

    private fun showImage(uri: Uri?) {
        if (uri != null) {
            Glide.with(this@HomeFragment)
                .load(uri)
                .into(binding.ivImage)
        }
    }

    private fun bindToObject(): HomeUI {
        with(binding) {
            tvProfessionalName.text?.toString()?.let { homeUI.nameProfessional = it }
            tvPatientName.text?.toString()?.let { homeUI.namePatient = it }
            tvRegistrationNumber.text?.toString()?.let { homeUI.agePatient = it }
            tvDescription.text?.toString()?.let { homeUI.drawDescription = it }
            homeUI.uri = mUri
        }
        return homeUI
    }

    private fun bindToForm() {
        if (homeUI != null) {
            with(binding) {
                homeUI.nameProfessional.let { tvProfessionalName.setText(it) }
                homeUI.namePatient.let { tvPatientName.setText(it) }
                homeUI.agePatient.let { tvRegistrationNumber.setText(it) }
                homeUI.drawDescription.let { tvDescription.setText(it) }
                updateComponent(homeUI.uri)
            }
        }
    }
}