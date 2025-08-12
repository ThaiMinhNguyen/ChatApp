package com.example.chatapp.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: androidx.navigation.NavController

    private val authViewModel: AuthenticationViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var isBottomNavVisibleByDestination: Boolean = false
    private var isKeyboardVisible: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this) {}
        setUpView()
        setUpRealTimeListener()
        setUpBottomNavigation()
        setUpKeyboardListener()
        observeAuthState()
        setUpObserver()
    }

    private fun setUpObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.loading.collect {
                        if (it) {
                            binding.llProgressBar.visibility = View.VISIBLE
                        } else {
                            binding.llProgressBar.visibility = View.GONE
                        }
                    }
                }
                launch {
                    userViewModel.loading.collect {
                        if (it) {
                            binding.llProgressBar.visibility = View.VISIBLE
                        } else {
                            binding.llProgressBar.visibility = View.GONE
                        }
                    }
                }
                launch {
                    userViewModel.people.collect{ people ->
                        val friendRequestCount = people.count { !it.isFriend && it.isRequestReceived }
                        updateFriendBadgeCount(friendRequestCount)
                    }
                }
            }
        }
    }

    private fun updateFriendBadgeCount(count: Int){
        binding.bottomNavigation.getOrCreateBadge(R.id.friendFragment).apply {
            number = count
            backgroundColor = Color.RED
            badgeTextColor = Color.WHITE
            maxCharacterCount = 3
            if(count > 0) {
                isVisible = true
            } else {
                isVisible = false
            }
        }
    }

    private fun setUpView(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun setUpRealTimeListener(){
        val currentUser = authViewModel.user.value
        if(currentUser != null){
            userViewModel.startListening(currentUser)
        }
    }

    private fun setUpBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener{_, destination, _ ->
            isBottomNavVisibleByDestination = when(destination.id) {
                R.id.homeFragment, R.id.friendFragment, R.id.profileFragment -> true
                else -> false
            }
            updateBottomNavVisibility()
        }
    }

    private fun setUpKeyboardListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (isKeyboardVisible != imeVisible) {
                isKeyboardVisible = imeVisible
                updateBottomNavVisibility()
            }
            insets
        }
    }

    private fun updateBottomNavVisibility() {
        val shouldShowBottomNav = isBottomNavVisibleByDestination && !isKeyboardVisible
        binding.bottomNavCard.visibility = if (shouldShowBottomNav) View.VISIBLE else View.GONE
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.user.collect{ user ->
                    if (user == null) {
                        navController.navigate(R.id.signInFragment)
                    } else {
                        userViewModel.startListening(user)
                        if (navController.currentDestination?.id == R.id.signInFragment|| navController.currentDestination?.id == R.id.signUpFragment) {
                            navController.navigate(R.id.homeFragment)
                        }
                    }
                }
            }
        }
    }
}