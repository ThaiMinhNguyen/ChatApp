package com.example.chatapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.example.chatapp.ui.home_screen.HomeFragmentDirections
import com.example.chatapp.utils.Prefs
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.ChatViewModel
import com.example.chatapp.view_model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: androidx.navigation.NavController

    private val authViewModel: AuthenticationViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    @Inject lateinit var prefs: Prefs

    private var isBottomNavVisibleByDestination: Boolean = false
    private var isKeyboardVisible: Boolean = false

    private var hasDeepLink = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this) {}
        requestNotificationPermission()
        authViewModel.restoreSessionIfPossible()
        setUpView()
        setUpBottomNavigation()
        setUpKeyboardListener()
        observeAuthState()
        setUpObserver()

        // Check if có Deep Link trong onCreate
        hasDeepLink = intent.data != null
        Log.d("MyLog - MainActivity", "onCreate hasDeepLink: $hasDeepLink")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Quan trọng!

        // Debug Deep Link
        intent.data?.let { uri ->
            Log.d("MyLog - MainActivity", "Deep link received: $uri")
        }

        // Handle Deep Link
        if (intent.data != null) {
            hasDeepLink = true
            handleDeepLink()
            Log.d("MyLog - MainActivity", "Handling deep link in onNewIntent, hasDeepLink: $hasDeepLink")

        }
    }

    private fun handleDeepLink(){
        if (::navController.isInitialized) {
            Log.d("MyLog - MainActivity", "NavController ready, calling handleDeepLink")
            navController.handleDeepLink(intent)

            val uri = intent.data
            if (uri?.scheme == "chatapp" && uri.host == "chat") {
                val roomId = uri.lastPathSegment
                if (!roomId.isNullOrEmpty()) {
                    Log.d("MyLog - MainActivity", "Manual navigation to room: $roomId")
                    binding.root.post {
                        try {
                            val action = HomeFragmentDirections.actionHomeFragmentToDetailChatFragment(roomId)
                            navController.navigate(action)
                            hasDeepLink = false
                            Log.d("MyLog - MainActivity", "Navigation completed successfully")
                        } catch (e: Exception) {
                            Log.e("MyLog - MainActivity", "Navigation failed: ${e.message}")
                        }
                    }
                    hasDeepLink = false
                }
            } else if (uri?.scheme == "chatapp" && uri.host == "friends") {
                Log.d("MyLog - MainActivity", "Manual navigation to friends")
                navController.navigate(R.id.friendFragment)
                hasDeepLink = false
            }
        } else {
            Log.d("MyLog - MainActivity", "NavController not initialized")
        }
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
                launch {
                    chatViewModel.unreadTotal.collect { total ->
                        updateMessageBadgeCount(total)
                    }
                }
                launch {
                    chatViewModel.error.collect{
                        if (it != null) {
                            notifyError(it)
                            chatViewModel.onErrorHandle()
                        }
                    }
                }
            }
        }
    }

    private fun notifyError(error: String){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun updateFriendBadgeCount(count: Int){
        binding.bottomNavigation.getOrCreateBadge(R.id.friendFragment).apply {
            number = count
            backgroundColor = Color.RED
            badgeTextColor = Color.WHITE
            maxCharacterCount = 3
            isVisible = count > 0
        }
    }

    private fun updateMessageBadgeCount(count: Int){
        binding.bottomNavigation.getOrCreateBadge(R.id.homeFragment).apply {
            number = count
            backgroundColor = Color.RED
            badgeTextColor = Color.WHITE
            maxCharacterCount = 3
            isVisible = count > 0
        }
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
                    Log.d("MyLog - MainActivity", "observeAuthState: user=${user?.displayName}, hasDeepLink=$hasDeepLink")
                    if (user == null) {
                        navController.navigate(R.id.signInFragment)
                    } else {
                        prefs.setRememberLogin(true)
                        prefs.saveLastUid(user.uid)
                        userViewModel.startListening(user)
                        chatViewModel.listenUnreadTotal(user.uid)
                        chatViewModel.listenUnreadByRoom(user.uid)
                        if (navController.currentDestination?.id == R.id.signInFragment || navController.currentDestination?.id == R.id.signUpFragment) {

                                Log.d("MyLog - MainActivity", "Trigger user observe: Handle link")
                                handleDeepLink()

                            navController.navigate(R.id.homeFragment)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.stopAllListeners()
        chatViewModel.stopUnreadListeners()
        chatViewModel.stopListenTopRooms()
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showOpenNotificationSettingsDialog()
            }
        }
    }

    private fun requestNotificationPermission(){
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                showNotificationPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotificationPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.notification_permission_rationale))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showOpenNotificationSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.notification_permission_settings_hint))
            .setPositiveButton(getString(R.string.open_settings)) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}