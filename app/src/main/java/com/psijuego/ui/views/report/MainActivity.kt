package com.psijuego.ui.views.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.psijuego.R
import com.psijuego.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var count: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.homeFragment -> {
                if (count != 1) {
                    Toast.makeText(
                        this,
                        "Pulse \"Atras\" una vez más para salir.",
                        Toast.LENGTH_SHORT
                    ).show()
                    count++
                } else {
                    finish()
                }
            }

            R.id.exportReportFragment -> {
                count = 0
            }

            else -> {
                count = 0
                super.onBackPressed()
            }
        }
        if (count != 0) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(3000)
                count = 0
            }

        }
    }
}