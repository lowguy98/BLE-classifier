package android.example.homescout.services
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.homescout.R
import android.example.homescout.database.BLEDevice
import android.example.homescout.models.DeviceTypeManager
import android.example.homescout.repositories.MainRepository
import android.example.homescout.ui.main.MainActivity
import android.example.homescout.utils.Constants
import android.example.homescout.utils.Constants.ACTION_SHOW_SETTINGS_FRAGMENT
import android.example.homescout.utils.Constants.ACTION_START_BLUETOOTH_SERVICE
import android.example.homescout.utils.Constants.ACTION_START_TRACKER_CLASSIFICATION_SERVICE
import android.example.homescout.utils.Constants.ACTION_STOP_BLUETOOTH_SERVICE
import android.example.homescout.utils.Constants.ACTION_STOP_TRACKER_CLASSIFICATION_SERVICE
import android.example.homescout.utils.Constants.CHANNEL_ID_BLUETOOTH_SCANNING
import android.example.homescout.utils.Constants.INTERVAL_BLE_SCAN
import android.example.homescout.utils.Constants.NOTIFICATION_CHANNEL_BLUETOOTH_SCANNING
import android.example.homescout.utils.Constants.SCAN_PERIOD
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothScanningService : LifecycleService() {


    private val handler = Handler(Looper.getMainLooper())

    private var isServiceRunning = false
    private var isScanning = false

    private val scanResults = HashMap<String, BLEDevice>()
    private var lastKnownLocation : LatLng? = null

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = applicationContext.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    @Inject
    lateinit var mainRepository : MainRepository


    override fun onCreate() {
        LocationTrackingService.lastKnownLocation.observe(this) {
            lastKnownLocation = it
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            when (it.action) {

                ACTION_START_BLUETOOTH_SERVICE -> {
                    if (!isServiceRunning) {
                        Timber.i("Start Service")
                        isServiceRunning = true
                        startForegroundService()
                        startTrackerClassificationService()

                    }
                }

                ACTION_STOP_BLUETOOTH_SERVICE -> {
                    Timber.i("Stop Service")
                    isServiceRunning = false
                    stopTrackerClassificationService()
                    stopSelf()
                }
            }

        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {

        startBleScan()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this,
            CHANNEL_ID_BLUETOOTH_SCANNING
        )
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_bluetooth_searching_24px)
            .setContentTitle("Home Scout")
            .setContentText("Bluetooth service is running.")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(Constants.NOTIFICATION_ID_BLUETOOTH_SCANNING, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_SETTINGS_FRAGMENT
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID_BLUETOOTH_SCANNING,
            NOTIFICATION_CHANNEL_BLUETOOTH_SCANNING,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun startBleScan() {

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.i("Permissions not granted.")
            return
        }

        if (!isScanning && isServiceRunning) {

            // stop scanning after SCAN_PERIOD
            handler.postDelayed({
                isScanning = false
                bleScanner.stopScan(scanCallback)
                insertScanResultsInDb()

                // loop: start scanning after INTERVAL_BLE_SCAN
                handler.postDelayed({
                    startBleScan()
                }, INTERVAL_BLE_SCAN )

            }, SCAN_PERIOD)

            scanResults.clear()
            isScanning = true
            bleScanner.startScan(scanCallback)

        } else {
            isScanning = false
            bleScanner.stopScan(scanCallback)
        }
    }

    private val scanCallback = object : ScanCallback() {


        override fun onScanResult(callbackType: Int, result: ScanResult) {

            lastKnownLocation?.let {

                val mac = result.device.address

                if (!scanResults.containsKey(mac)) {

                    val timestampInMilliSeconds = Calendar.getInstance().timeInMillis
                    val lat = it.latitude
                    val lng = it.longitude
                    val deviceType = DeviceTypeManager.identifyDeviceType(result).type

                    val bleDevice = BLEDevice(
                        mac,
                        timestampInMilliSeconds,
                        lat,
                        lng,
                        deviceType)

                    scanResults[mac] = bleDevice
                }
            }
        }
    }

    private fun insertScanResultsInDb() {
        for (key in scanResults.keys) {
            insertBLEDevice(scanResults[key]!!)
        }
    }

    private fun insertBLEDevice(bleDevice: BLEDevice) {
        lifecycleScope.launch {
            mainRepository.insertBLEDevice(bleDevice)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(applicationContext, TrackerClassificationService::class.java).also {
            it.action = action
            applicationContext.startService(it)
        }

    private fun startTrackerClassificationService() {
        sendCommandToService(ACTION_START_TRACKER_CLASSIFICATION_SERVICE)
    }

    private fun stopTrackerClassificationService() {
        sendCommandToService(ACTION_STOP_TRACKER_CLASSIFICATION_SERVICE)
    }

}