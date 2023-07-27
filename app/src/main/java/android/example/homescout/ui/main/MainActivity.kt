package android.example.homescout.ui.main

import android.content.Intent
import android.example.homescout.R
import android.example.homescout.databinding.ActivityMainBinding
import android.example.homescout.utils.Constants.ACTION_SHOW_NOTIFICATIONS_FRAGMENT
import android.example.homescout.utils.Constants.ACTION_SHOW_SETTINGS_FRAGMENT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    // PROPERTIES
    private var hasNavigatedToFragment = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController: NavController


    // LIFECYCLE FUNCTIONS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        navigateToFragmentIfNeeded(intent)

        if (!hasNavigatedToFragment) {
            setupBottomNavigation()
        }


    }

    private fun setupBottomNavigation() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_welcome,
                R.id.navigation_notifications,
                R.id.navigation_settings,
                R.id.navigation_scan
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent?) {
        navigateToFragmentIfNeeded(intent)
        super.onNewIntent(intent)
    }


    private fun navigateToFragmentIfNeeded(intent: Intent?) {

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//        val navController: NavController = navHostFragment.navController



        when (intent?.action) {
            ACTION_SHOW_SETTINGS_FRAGMENT -> {
                hasNavigatedToFragment = true
                setupBottomNavigation()
                navController.navigate(R.id.action_global_settings_fragment)
            }

            ACTION_SHOW_NOTIFICATIONS_FRAGMENT -> {
                hasNavigatedToFragment = true
                setupBottomNavigation()
                navController.navigate(R.id.action_global_notifications_fragment)
            }

        }
    }

}