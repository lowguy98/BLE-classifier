package android.example.homescout.ui.settings

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.example.homescout.R
import android.example.homescout.databinding.FragmentSettingsBinding
import android.example.homescout.services.LocationTrackingService
import android.example.homescout.ui.intro.PermissionAppIntro
import android.example.homescout.utils.Constants.ACTION_START_TRACKING_SERVICE
import android.example.homescout.utils.Constants.ACTION_STOP_TRACKING_SERVICE
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {


    // PROPERTIES
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel

    private val touchListenerDistance: Slider.OnSliderTouchListener =
        object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Not needed
            }

            override fun onStopTrackingTouch(slider: Slider) {
                settingsViewModel.updateDistance(slider.value)
            }
        }

    private val touchListenerTime: Slider.OnSliderTouchListener =
        object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Not needed
            }

            override fun onStopTrackingTouch(slider: Slider) {
                settingsViewModel.updateTimeInMin(slider.value)
            }
        }

    private val touchListenerOccurrences: Slider.OnSliderTouchListener =
        object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Not needed
            }

            override fun onStopTrackingTouch(slider: Slider) {
                settingsViewModel.updateOccurrences(slider.value)
            }
        }

    private val isBluetoothEnabled : Boolean
        get() = bluetoothAdapter.isEnabled

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = requireContext().getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    // LIFECYCLE FUNCTIONS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        setupViewModelAndBinding(inflater, container)
        setupSwitchTrackingProtection()
        setupSliderDistance()
        setupSliderTimeInMin()
        setupSliderOccurences()
        setupColorChangeForInfoButtons()
        observeTrackingPreferences()
        addOnSliderTouchListeners()
        setupButtonsForDifferentTrackingPreferences()


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // FUNCTIONS USED IN LIFECYCLE (for code readability)
    private fun setupViewModelAndBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.settingsViewModel = settingsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupSliderDistance() {
        binding.sliderDistance.setLabelFormatter { value: Float ->
            "%.0f m".format(value)
        }

        binding.infoSliderDistance.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.description_slider_distance))
                .setMessage(getString(R.string.text_dialog_slider_distance))
                .setPositiveButton("Ok") { dialog, which ->
                    // No Respond to positive button press needed
                }
                .show()
        }
    }

    private fun setupSwitchTrackingProtection() {
        binding.switchTrackingProtection.setOnCheckedChangeListener { _, checked ->

            if (!isBluetoothEnabled) {

                binding.switchTrackingProtection.isChecked = false

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Enable Bluetooth required!")
                    .setMessage("Please turn on Bluetooth. Thanks.")
                    .setPositiveButton("Ok") { _, _ ->
                        // Respond to positive button press
                        val intentBluetooth = Intent().apply {
                            action = Settings.ACTION_BLUETOOTH_SETTINGS
                        }
                        requireContext().startActivity(intentBluetooth)
                    }
                    .show()
                return@setOnCheckedChangeListener
            }


//            if (ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                binding.switchTrackingProtection.isChecked = false
//                startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
//                return@setOnCheckedChangeListener
//            }

            if (!XXPermissions.isGranted(requireContext(), Permission.ACCESS_BACKGROUND_LOCATION)){
                XXPermissions.with(this).permission(Permission.ACCESS_BACKGROUND_LOCATION).request { permissions, allGranted ->

                }
                binding.switchTrackingProtection.isChecked = false
                startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
                return@setOnCheckedChangeListener
            }

            settingsViewModel.onSwitchToggled(checked)
            if (checked) {
                sendCommandToService(ACTION_START_TRACKING_SERVICE)
            } else {
                sendCommandToService(ACTION_STOP_TRACKING_SERVICE)
            }
        }
    }

    private fun setupSliderTimeInMin() {
        binding.sliderTimeInMin.setLabelFormatter { value: Float ->
            "%.0f min".format(value)
        }

        binding.infoSliderTimeInMin.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.description_slider_time_in_min))
                .setMessage(getString(R.string.text_dialog_slider_time_in_min))
                .setPositiveButton("Ok") { dialog, which ->
                    // No Respond to positive button press needed
                }
                .show()
        }
    }

    private fun setupSliderOccurences() {
        binding.sliderOccurrences.setLabelFormatter { value: Float ->
            "%.0f times".format(value)
        }



        binding.infoSliderOccurrences.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.description_slider_occurrences))
                .setMessage(getString(R.string.text_dialog_slider_occurrences))
                .setPositiveButton("Ok") { dialog, which ->
                    // No Respond to positive button press needed
                }
                .show()
        }
    }

    private fun setupColorChangeForInfoButtons() {
        settingsViewModel.isSwitchEnabled.observe(viewLifecycleOwner) {

            if (it) {
                binding.infoSliderDistance.setColorFilter(
                    getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                binding.infoSliderTimeInMin.setColorFilter(
                    getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                binding.infoSliderOccurrences.setColorFilter(
                    getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
            } else {
                binding.infoSliderDistance.setColorFilter(getColor(requireContext(), R.color.grey))
                binding.infoSliderTimeInMin.setColorFilter(getColor(requireContext(), R.color.grey))
                binding.infoSliderOccurrences.setColorFilter(
                    getColor(
                        requireContext(),
                        R.color.grey
                    )
                )
            }
        }
    }


    private fun observeTrackingPreferences() {
        settingsViewModel.distance.observe(viewLifecycleOwner) {
            binding.sliderDistance.value = it
        }

        settingsViewModel.timeInMin.observe(viewLifecycleOwner) {
            binding.sliderTimeInMin.value = it
        }

        settingsViewModel.occurrences.observe(viewLifecycleOwner) {
            binding.sliderOccurrences.value = it
        }
    }

    private fun addOnSliderTouchListeners() {
        binding.sliderDistance.addOnSliderTouchListener(touchListenerDistance)
        binding.sliderTimeInMin.addOnSliderTouchListener(touchListenerTime)
        binding.sliderOccurrences.addOnSliderTouchListener(touchListenerOccurrences)
    }

    private fun setupButtonsForDifferentTrackingPreferences() {
        binding.buttonDefault.setOnClickListener {
            binding.sliderDistance.value = 200.0F
            binding.sliderTimeInMin.value = 1.0F
            binding.sliderOccurrences.value = 4.0F
        }

        binding.buttonApple.setOnClickListener {
            binding.sliderDistance.value = 850.0F
            binding.sliderTimeInMin.value = 10.0F
            binding.sliderOccurrences.value = 4.0F
        }

        binding.buttonAirGuard.setOnClickListener {
            binding.sliderDistance.value = 400.0F
            binding.sliderTimeInMin.value = 1.0F
            binding.sliderOccurrences.value = 3.0F
        }
    }

    // PRIVATE FUNCTIONS
    private fun sendCommandToService(action: String) =
        Intent(requireContext(), LocationTrackingService::class.java).also {
            it.action = action
            // does not actually start service, but delivers the intent to the service
            requireContext().startService(it)
        }
}