package android.example.homescout.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ble_device_table")
data class BLEDevice(
    var macAddress: String? = null,
    var timestampInMilliSeconds: Long = 0L,
    var lat: Double,
    var lng: Double,
    var type: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}