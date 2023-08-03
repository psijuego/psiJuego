package com.psijuego.ui.views.report.conclusions

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.psijuego.R
import com.psijuego.data.model.ui.CategoryUI
import com.psijuego.data.model.ui.HomeUI
import com.psijuego.databinding.FragmentConclusionsBinding
import com.psijuego.ui.views.report.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ConclusionsFragment : Fragment() {

    private lateinit var binding: FragmentConclusionsBinding
    private val viewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private var listCategoryUI: List<CategoryUI> = emptyList()
    private var homeUI: HomeUI = HomeUI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConclusionsBinding.inflate(inflater, container, false)
        initViewModel()
        setUpComponents()
        return binding.root
    }

    private fun setUpComponents() {
        bindToForm()
        with(binding) {
            btnQR.setOnClickListener { confirmContinueAction() }
            val cancel = resources.getString(R.string.cancel)
            val content = SpannableString(cancel)
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            btnCancel.text = content
            btnCancel.setOnClickListener { confirmCancelAction() }
            topAppBar.setNavigationOnClickListener { onBack() }
        }
    }

    private fun onNavigateToPdf() {
        bindToObject()
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_conclusionsFragment_to_exportReportFragment)
    }

    private fun bindToObject() {
        val conclusion = binding.tvConclusion.text.toString()
        if (conclusion.isNotEmpty()) {
            viewModel.setConclusion(conclusion)
        }
    }

    private fun bindToForm() {
        if (viewModel.conclusion.value != null) {
            binding.tvConclusion.text =
                Editable.Factory.getInstance().newEditable(viewModel.conclusion.value)
        }
    }

    private fun onCancel() {
        viewModel.setHomeUI(HomeUI())
        viewModel.setConclusion("")
        findNavController().navigate(R.id.action_conclusionsFragment_to_homeFragment)
    }

    private fun onBack() {
        bindToObject()
        findNavController().popBackStack()
    }

    private fun confirmCancelAction() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_cancel))
            .setMessage(resources.getString(R.string.cancel_supporting_text))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                onCancel()
            }
            .show()
    }

    private fun confirmContinueAction() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_continue))
            .setMessage(resources.getString(R.string.continue_supporting_text))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                onNavigateToPdf()
            }
            .show()
    }

    private fun initViewModel() {
        viewModel.categoryUI.observe(viewLifecycleOwner) {
            listCategoryUI = it
        }
        viewModel.homeUI.observe(viewLifecycleOwner) {
            homeUI = it
        }
    }

}