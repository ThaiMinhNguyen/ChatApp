package com.example.chatapp.ui

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.view_model.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: androidx.navigation.NavController

    private val authViewModel: AuthenticationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val callback = onBackPressedDispatcher.addCallback(this) {
            //Disable back button functionality
        }
        setUpView()
        setUpBottomNavigation()
        observeAuthState()
    }

    private fun setUpView(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun setUpBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id) {
                R.id.homeFragment, R.id.friendFragment, R.id.profileFragment -> binding.bottomNavCard.visibility = android.view.View.VISIBLE
                else -> binding.bottomNavCard.visibility = android.view.View.GONE
            }
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.user.collect{ user ->
                    if (user == null) {
                        navController.navigate(R.id.signInFragment)
                    } else {
                        if (navController.currentDestination?.id == R.id.signInFragment|| navController.currentDestination?.id == R.id.signUpFragment) {
                            navController.navigate(R.id.homeFragment)
                        }
                    }

                }
            }
        }
    }
}