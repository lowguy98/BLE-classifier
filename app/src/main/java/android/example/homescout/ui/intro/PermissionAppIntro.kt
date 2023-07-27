package android.example.homescout.ui.intro

import android.Manifest
import android.example.homescout.R
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class PermissionAppIntro : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        // APP INTRO CONFIGURATIONS
        isIndicatorEnabled = true
        isWizardMode = true
        setImmersiveMode()
        setTheme(R.style.Theme_HomeScout)


        // SLIDES OF THE APP INTRO
        addSlide(AppIntroFragment.createInstance(
            title = "Enable Bluetooth",
            description = "Please make sure you have Bluetooth enabled. " +
                    "Also allow the app to scan for BLE devices in the upcoming dialog.",
            titleTypefaceFontRes = R.font.roboto_bold,
            descriptionTypefaceFontRes = R.font.roboto,
            imageDrawable = R.drawable.ic_bluetooth_onboarding,
            backgroundColorRes = R.color.purple_500
        ))
        addSlide(
                AppIntroFragment.createInstance(
                    title = "Location Permission",
                    description = "For the location permission dialog please press " +
                            "\"Allow in settings\" and there select \"Allow all the time\".",
                    titleTypefaceFontRes = R.font.roboto_bold,
                    descriptionTypefaceFontRes = R.font.roboto,
                    imageDrawable = R.drawable.ic_location_on_onboarding,
                    backgroundColorRes = R.color.purple_500
                ))
        addSlide(
            AppIntroFragment.createInstance(
                title = "Battery Optimization",
                description = "For your safety, this app needs to run while locked. " +
                        "Therefore, battery optimization needs to be granted.",
                titleTypefaceFontRes = R.font.roboto_bold,
                descriptionTypefaceFontRes = R.font.roboto,
                imageDrawable = R.drawable.ic_batter_optimization_onboarding,
                backgroundColorRes = R.color.purple_500
            ))
        addSlide(AppIntroFragment.createInstance(
            title = "Thanks.",
            description = "I hope this app suits you :-)",
            titleTypefaceFontRes = R.font.roboto_bold,
            descriptionTypefaceFontRes = R.font.roboto,
            imageDrawable = R.drawable.ic_thumb_up_onboarding,
            backgroundColorRes = R.color.purple_500
        ))

        // ASK FOR NECESSARY PERMISSIONS
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            askForPermissions(arrayOf(
//                Manifest.permission.BLUETOOTH_SCAN,
//                Manifest.permission.BLUETOOTH_CONNECT), 1)
//        }
//        askForPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)


        XXPermissions.with(this).permission(arrayOf(
            Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_CONNECT,Permission.ACCESS_FINE_LOCATION)).request { permissions, allGranted ->

        }


    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }

}