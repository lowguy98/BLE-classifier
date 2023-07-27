package android.example.homescout.ui.welcome

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.homescout.databinding.FragmentWelcomeBinding
import android.example.homescout.ui.intro.PermissionAppIntro
import android.example.homescout.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar



class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    val isLocationPermissionGranted
        // version Q = API 29
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            requireActivity().hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }


    // LIFECYCLE FUNCTIONS & OVERRIDES
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        inflateBinding(inflater, container)
        setupOnClickListener()


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ONCREATEVIEW FUNCTIONS
    private fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
    }


    private fun setupOnClickListener() {
        binding.buttonGrantPermission.setOnClickListener {
            // requestLocationPermissionIsEnabled()
            startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
        }
    }

    // PRIVATE FUNCTIONS
    private fun requestLocationPermissionIsEnabled() {
        // implemented according to https://developer.android.com/training/permissions/requesting#manage-request-code-yourself
        when {
            isLocationPermissionGranted == true -> {
                Snackbar.make(binding.root, "Permission already granted. Thanks.", Snackbar.LENGTH_LONG).show()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                requestPermissionWithDialog()
            }
            else -> {
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun requestPermissionWithDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Location Required!")
            .setMessage("For this app to work you need to let it access your location in the background. This means the app needs to access your location \"All the time.")
            .setPositiveButton("ALLOW") { dialog, which ->
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .show()
    }

    private fun Fragment.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
    }

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
}