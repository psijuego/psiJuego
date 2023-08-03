package com.psijuego.ui.views.report.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.psijuego.R
import com.psijuego.core.utils.UtilPDF
import com.psijuego.databinding.FragmentWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        setUpComponents()
        return binding.root
    }

    private fun setUpComponents() {
        binding.welcomeFragment.setOnClickListener {
            navigate()
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(400)
            navigate()
        }
    }

    private fun navigate() {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_welcomeFragment_to_homeFragment)
    }
}