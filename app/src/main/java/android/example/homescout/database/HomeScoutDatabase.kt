package android.example.homescout.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BLEDevice::class, MaliciousTracker::class],
    version = 1)
//@TypeConverters(Converter::class)
abstract class HomeScoutDatabase : RoomDatabase() {

    abstract fun getBLEDeviceDao(): BLEDeviceDao
    abstract fun getMaliciousTrackerDao(): MaliciousTrackerDao
}