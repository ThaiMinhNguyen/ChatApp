package com.example.chatapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.example.chatapp.utils.Prefs
import com.example.chatapp.utils.UserUtils
import com.example.chatapp.view_model.AuthenticationViewModel
import com.example.chatapp.view_model.ChatViewModel
import com.example.chatapp.view_model.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        handleNotificationIntentIfAny(intent)
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
                    if (user == null) {
                        navController.navigate(R.id.signInFragment)
                    } else {
                        // Save session hint
                        prefs.setRememberLogin(true)
                        prefs.saveLastUid(user.uid)
                        userViewModel.startListening(user)
                        chatViewModel.listenUnreadTotal(user.uid)
                        chatViewModel.listenUnreadByRoom(user.uid)
                        if (navController.currentDestination?.id == R.id.signInFragment|| navController.currentDestination?.id == R.id.signUpFragment) {
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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntentIfAny(intent)
    }


    private fun handleNotificationIntentIfAny(intent: Intent) {
        val roomId = intent.getStringExtra("roomId") ?: return
        intent.removeExtra("roomId")
        Log.d("MyLog - MainActivity", roomId)
        val currentUser = authViewModel.user.value ?: return
        val otherId = UserUtils.getOtherId(roomId, currentUser.uid)
        val otherUser = userViewModel.people.value
            .firstOrNull { it.user.uid == otherId }?.user
        val room = chatViewModel.rooms.value.filter { it.participants.sorted().joinToString("_") == roomId }.firstOrNull()
        chatViewModel.setCurrentRoom(room)
        chatViewModel.setCurrentChatUser(otherUser)
        val args = Bundle().apply {
            putString("chatConversationId", roomId)
        }
        if (::navController.isInitialized) {

            navController.navigate(R.id.detailChatFragment, args)
        }
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