package android.example.homescout.utils

object Constants {

    // Database
    const val HOMESCOUT_DATABASE_NAME = "homescout_db"
    const val TRACKING_PREFERENCES = "tracking_preferences"

    // Permissions
    const val LOCATION_PERMISSION_REQUEST_CODE = 1

    // Bluetooth
    const val APPLE_COMPANY_IDENTIFIER = 76

    // Notifications for found device
    const val CHANNEL_ID_FOUND_DEVICE = "found_device_channel"
    const val NOTIFICATION_CHANNEL_FOUND_DEVICE = "found_device"
    const val NOTIFICATION_ID_FOUND_DEVICE = 1
    const val ACTION_SHOW_NOTIFICATIONS_FRAGMENT = "ACTION_SHOW_NOTIFICATIONS_FRAGMENT"

    // Notifications for tracking service
    const val CHANNEL_ID_LOCATION_TRACKING = "location_tracking_channel"
    const val NOTIFICATION_CHANNEL_LOCATION_TRACKING = "location_tracking"
    const val NOTIFICATION_ID_LOCATION_TRACKING = 2
    const val ACTION_SHOW_SETTINGS_FRAGMENT = "ACTION_SHOW_SETTINGS_FRAGMENT"

    // Tracking Service
    const val ACTION_START_TRACKING_SERVICE = "ACTION_START_TRACKING_SERVICE"
    const val ACTION_STOP_TRACKING_SERVICE = "ACTION_STOP_TRACKING_SERVICE"
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val STATIONARY_MOVING_DISTANCE = 50.0
    const val SIZE_OF_APPROX_2_MINUTES = 24

    // Bluetooth Service
    const val ACTION_START_BLUETOOTH_SERVICE = "ACTION_START_BLUETOOTH_SERVICE"
    const val ACTION_STOP_BLUETOOTH_SERVICE = "ACTION_STOP_BLUETOOTH_SERVICE"
    const val CHANNEL_ID_BLUETOOTH_SCANNING = "bluetooth_scanning_channel"
    const val NOTIFICATION_CHANNEL_BLUETOOTH_SCANNING = "bluetooth_scanning"
    const val NOTIFICATION_ID_BLUETOOTH_SCANNING = 3
    const val INTERVAL_BLE_SCAN = 12000L
    const val SCAN_PERIOD = 12000L

    // Tracker Identification Service
    const val ACTION_START_TRACKER_CLASSIFICATION_SERVICE = "ACTION_START_TRACKER_CLASSIFICATION_SERVICE"
    const val ACTION_STOP_TRACKER_CLASSIFICATION_SERVICE = "ACTION_STOP_TRACKER_CLASSIFICATION_SERVICE"
    const val CHANNEL_ID_TRACKER_CLASSIFICATION = "tracker_classification_channel"
    const val NOTIFICATION_CHANNEL_TRACKER_CLASSIFICATION = "tracker_classification"
    const val NOTIFICATION_ID_TRACKER_CLASSIFICATION = 4
    const val INTERVAL_TRACKER_CLASSIFICATION = 30000L
}