package android.example.homescout.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.homescout.R
import android.example.homescout.ui.main.MainActivity
import android.example.homescout.utils.Constants.ACTION_SHOW_SETTINGS_FRAGMENT
import android.example.homescout.utils.Constants.ACTION_START_BLUETOOTH_SERVICE
import android.example.homescout.utils.Constants.ACTION_START_TRACKER_CLASSIFICATION_SERVICE
import android.example.homescout.utils.Constants.ACTION_START_TRACKING_SERVICE
import android.example.homescout.utils.Constants.ACTION_STOP_BLUETOOTH_SERVICE
import android.example.homescout.utils.Constants.ACTION_STOP_TRACKER_CLASSIFICATION_SERVICE
import android.example.homescout.utils.Constants.ACTION_STOP_TRACKING_SERVICE
import android.example.homescout.utils.Constants.CHANNEL_ID_LOCATION_TRACKING
import android.example.homescout.utils.Constants.LOCATION_UPDATE_INTERVAL
import android.example.homescout.utils.Constants.NOTIFICATION_CHANNEL_LOCATION_TRACKING
import android.example.homescout.utils.Constants.NOTIFICATION_ID_LOCATION_TRACKING
import android.example.homescout.utils.Constants.SIZE_OF_APPROX_2_MINUTES
import android.example.homescout.utils.Constants.STATIONARY_MOVING_DISTANCE
import android.example.homescout.utils.RingBuffer
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : LifecycleService() {

    var isServiceRunning = false
    var isBluetoothServiceRunning = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val userPositionsHistoryBuffer = RingBuffer<LatLng>(SIZE_OF_APPROX_2_MINUTES)

    companion object {
        private val _lastKnownLocation = MutableLiveData<LatLng?>()
        val lastKnownLocation: LiveData<LatLng?>
            get() = _lastKnownLocation
    }

    // LIFECYCLE FUNCTIONS
    override fun onCreate() {
        _lastKnownLocation.value = null
        super.onCreate()
    }

    override fun onDestroy() {
        _lastKnownLocation.value = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {

            when (it.action) {

                ACTION_START_TRACKING_SERVICE -> {
                    if (!isServiceRunning) {
                        Timber.i("Start Service")
                        isServiceRunning = true
                        startForegroundService()
                    }
                }

                ACTION_STOP_TRACKING_SERVICE -> {
                    Timber.i("Stop Service")
                    isServiceRunning = false
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    userPositionsHistoryBuffer.clear()
                    stopBluetoothScanningService()
                    stopSelf()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    // FUNCTIONS TO START SERVICE
    private fun startForegroundService() {

        updateLocationTracking()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID_LOCATION_TRACKING)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle("Home Scout")
            .setContentText("Tracking service is running.")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID_LOCATION_TRACKING, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_SETTINGS_FRAGMENT
        },
        FLAG_IMMUTABLE
    )

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID_LOCATION_TRACKING,
            NOTIFICATION_CHANNEL_LOCATION_TRACKING,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }


    // FUNCTIONS FOR LOCATION TRACKING
    private fun addPosition(currentLocation: Location?) {
        currentLocation?.let {
            val position = LatLng(currentLocation.latitude, currentLocation.longitude)
            userPositionsHistoryBuffer.put(position)
            _lastKnownLocation.value = position
        }
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {

            super.onLocationResult(result)
            result.locations.let { locations ->
                for (location in locations){
                    addPosition(location)
                }
            }

            val isUserStationary = isUserStationary()

            if (!isUserStationary && !isBluetoothServiceRunning) {
                startBluetoothScanningService()
                return
            }

            if (isUserStationary && isBluetoothServiceRunning) {
                stopBluetoothScanningService()
            }
        }
    }

    private fun isUserStationary(): Boolean {

        val allDistancesTraveled = mutableListOf<Float>()
        val orderedUserPositionBuffer = userPositionsHistoryBuffer.getElementsOrderedTailToHead()
        val secondLastIndex = orderedUserPositionBuffer.size - 2

        for (i in 0..secondLastIndex) {

            val currentLocation = Location("currentLocation").apply {
                latitude = orderedUserPositionBuffer[i].latitude
                longitude = orderedUserPositionBuffer[i].longitude
            }

            val nextLocation = Location("nextLocation").apply {
                latitude = orderedUserPositionBuffer[i + 1].latitude
                longitude = orderedUserPositionBuffer[i + 1].longitude
            }

            val distanceBetweenTwoLocations = currentLocation.distanceTo(nextLocation)
            allDistancesTraveled.add(distanceBetweenTwoLocations)
        }

        return allDistancesTraveled.sum() <= STATIONARY_MOVING_DISTANCE
    }

    private fun updateLocationTracking() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.Builder(
            PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).build()


        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    private fun sendCommandToService(action: String) =
        Intent(applicationContext, BluetoothScanningService::class.java).also {
            it.action = action
            // does not actually start service, but delivers the intent to the service
            applicationContext.startService(it)
        }

    private fun startBluetoothScanningService() {
        sendCommandToService(ACTION_START_BLUETOOTH_SERVICE)
        isBluetoothServiceRunning = true
    }

    private fun stopBluetoothScanningService() {
        sendCommandToService(ACTION_STOP_BLUETOOTH_SERVICE)
        isBluetoothServiceRunning = false
    }


    private fun sendCommandToServiceX(action: String) =
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