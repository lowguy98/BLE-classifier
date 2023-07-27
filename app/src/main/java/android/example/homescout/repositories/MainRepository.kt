package android.example.homescout.repositories

import android.example.homescout.database.BLEDevice
import android.example.homescout.database.BLEDeviceDao
import android.example.homescout.database.MaliciousTracker
import android.example.homescout.database.MaliciousTrackerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val bleDeviceDao: BLEDeviceDao,
    private val maliciousTrackerDao: MaliciousTrackerDao
) {

    // BLE DEVICES
    suspend fun insertBLEDevice(bleDevice: BLEDevice) {
        withContext(Dispatchers.IO + NonCancellable) {
            bleDeviceDao.insertBLEDevice(bleDevice)
        }
    }

    suspend fun deleteBLEDevice(bleDevice: BLEDevice) = bleDeviceDao.deleteBLEDevice(bleDevice)

    suspend fun deleteBLEDevicesOlderThanTwoHours() {
        withContext(Dispatchers.IO + NonCancellable) {
            bleDeviceDao.deleteBLEDevicesOlderThanTwoHours()
        }
    }

    fun getAllBLEDevicesSortedByTimestamp() = bleDeviceDao.getAllBLEDevicesSortedByTimestamp()

    suspend fun clearBleDeviceTable() {
        withContext(Dispatchers.IO + NonCancellable){
            bleDeviceDao.clearTable()
        }
    }


    // MALICIOUS TRACKERS
    suspend fun insertMaliciousTracker(maliciousTracker: MaliciousTracker) {
        withContext(Dispatchers.IO + NonCancellable) {
            maliciousTrackerDao.insertMaliciousTracker(maliciousTracker)
        }
    }

    suspend fun getMaliciousTrackerByMac(macAddress: String): MaliciousTracker? {
        return withContext(Dispatchers.IO + NonCancellable) {
            maliciousTrackerDao.getMaliciousTrackerByMac(macAddress)
        }
    }

    suspend fun deleteMaliciousTracker(maliciousTracker: MaliciousTracker) {
        withContext(Dispatchers.IO + NonCancellable) {
            maliciousTrackerDao.deleteMaliciousTracker(maliciousTracker)
        }
    }

    fun getAllMaliciousTrackersSortedByTimestamp() = maliciousTrackerDao.getAllMaliciousTrackersSortedByTimestamp()

    suspend fun clearMaliciousTrackersTable() {
        withContext(Dispatchers.IO + NonCancellable){
            maliciousTrackerDao.clearTable()
        }
    }


}