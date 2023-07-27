package android.example.homescout.ui.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.example.homescout.R
import android.example.homescout.databinding.FragmentScanBinding
import android.example.homescout.ui.intro.PermissionAppIntro
import android.example.homescout.utils.BluetoothAPILogger
import android.example.homescout.utils.Constants.SCAN_PERIOD
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import org.tensorflow.lite.Interpreter
import timber.log.Timber


class ScanFragment : Fragment() {


    // PROPERTIES
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val isBluetoothEnabled : Boolean
        get() = bluetoothAdapter.isEnabled
    private val scanResults = mutableListOf<ScanResult>()
    private var isScanning = false
        set(value) {
            field = value
            if (value) {
                binding.buttonScan.text = getString(R.string.scan_button_stop)
            } else {
                binding.buttonScan.text = getString(R.string.scan_button_scan)
            }
        }


    // PROPERTIES lateinit
    private lateinit var scanSettings: ScanSettings

    private lateinit var tflite: Interpreter

    // PROPERTIES lazy
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = activity?.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanResultAdapter: ScanResultAdapter by lazy {
        ScanResultAdapter(scanResults) { result ->
            // User tapped on a scan result
            with(result.device) {
                Snackbar.make(binding.root, "Tapped on: $address", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // LIFECYCLE FUNCTIONS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupViewModelAndBinding(inflater, container)
        buildScanSettings()
        setOnClickListenerForScanButton()
        setupRecyclerView()

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    // FUNCTIONS USED IN onCreateView() (for code readability)
    private fun setupViewModelAndBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
    }


    private fun buildScanSettings() {
        scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }

    private fun setOnClickListenerForScanButton() {
        binding.buttonScan.setOnClickListener {
            if (!isBluetoothEnabled) {

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
                return@setOnClickListener
            }

            if (isScanning) {
                stopBleScan()
            } else {
                startBLEScan()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.scanResultsRecyclerView.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = binding.scanResultsRecyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun checkIfBluetoothIsEnabled() {

        binding.buttonScan.isEnabled = isBluetoothEnabled

        if (!isBluetoothEnabled) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Bluetooth required!")
                .setMessage("Please enable Bluetooth. Thanks")
                .setPositiveButton("Ok") { _, _ ->
                    // Respond to positive button press
                    val intentBluetooth = Intent().apply {
                        action = Settings.ACTION_BLUETOOTH_SETTINGS
                    }
                    requireContext().startActivity(intentBluetooth)

                }
                .show()
        }
    }

    // CALLBACKS
    private val scanCallback = object : ScanCallback() {


        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                BluetoothAPILogger().logResults(result)
            }
            val scanRecord: ByteArray? = result.scanRecord?.bytes
            val scanRecord_hex: String? = if (scanRecord != null) {
                bytesToHex(scanRecord)
            } else {
                null
            }
            println(scanRecord_hex)
            // this might needs to be changed as the device.address might change due to
            // MAC randomization
            // check if the current found result is already in the entire scanResult list
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            // element not found returns -1
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else { // found new device
//                with(result.device) {
//                    //Timber.i( address: $address")
//                }
                scanResults.add(result)
                scanResultAdapter.notifyItemInserted(scanResults.size - 1)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.i("onScanFailed: code $errorCode")
        }
    }

    // PRIVATE FUNCTIONS
    private fun startBLEScan() {

        if (!isBluetoothEnabled) { checkIfBluetoothIsEnabled() }

        scanResults.clear()
        scanResultAdapter.notifyDataSetChanged()
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.BLUETOOTH_SCAN
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
//            return
//        }

        if (!XXPermissions.isGranted(requireContext(), Permission.BLUETOOTH_SCAN)){
            startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
            return
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isScanning = false
            bleScanner.stopScan(scanCallback)
        }, SCAN_PERIOD)

        bleScanner.startScan(null, scanSettings, scanCallback)
        isScanning = true
    }

    private fun stopBleScan() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.BLUETOOTH_SCAN
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
//            return
//        }
        if (!XXPermissions.isGranted(requireContext(), Permission.BLUETOOTH_SCAN)){
            startActivity(Intent(requireContext(), PermissionAppIntro::class.java))
            return
        }
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    //transfer scanRecords into hex format
    private val hexArray = "0123456789ABCDEF".toCharArray()
    private fun bytesToHex(bytes: ByteArray): String? {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}