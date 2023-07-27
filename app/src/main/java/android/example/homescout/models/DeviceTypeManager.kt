package android.example.homescout.models

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.example.homescout.utils.Constants.APPLE_COMPANY_IDENTIFIER
import android.os.ParcelUuid
import kotlin.experimental.and


class DeviceTypeManager {

    companion object {
        @SuppressLint("MissingPermission")
        fun identifyDeviceType(result: ScanResult): DeviceType {

            // Identification of Samsung Galaxy SmartTAG
            if (result.device.name == "Smart Tag") return GalaxySmartTag()

            // Identification of AirTag and Chipolo ONE Spot
            // The lines 24 to 36 are a modification of the code from AirGuard.
            // The file from AirGuard can be found here: https://github.com/seemoo-lab/AirGuard/blob/main/app/src/main/java/de/seemoo/at_tracking_detection/database/models/device/DeviceManager.kt
            // Especially, lines 24 to 28 in this file correspond to lines 19 to 25 from AirGuard.
            // @author: AirGuard, commit 773f6822cae99d13b73c75f6605dee6ee25d8543 on Mar 8, 2022 from Sn0wfreezeDev
            val appleManufacturerSpecificData = result.scanRecord?.manufacturerSpecificData?.get(APPLE_COMPANY_IDENTIFIER)
            if (appleManufacturerSpecificData != null) {

                val statusByte: Byte = appleManufacturerSpecificData[2]
                val deviceTypeInt = (statusByte.and(0x30).toInt() shr 4)

                when (deviceTypeInt) {
                    0 -> return AppleDevice()
                    1 -> return AirTag()
                    2 -> return Chipolo()
                    3 -> return AirPods()
                }
            }

            // Identification of Tile
            // The identification of a Tile tracker is based on the code from AirGuard.
            // The code below from line 45 to 54 is a combination of two files from AirGuard. In one file, AirGuard
            // identifies a tracker as a Tile Tracker. In the other file AirGuard determines the serviceUUID of the Tile Tracker.
            // In this file, except for line 52, the identification of a Tile tracker is performed in lines 45 to 54.
            // The corresponding lines from AirGuards file are lines 38 to 43: https://github.com/seemoo-lab/AirGuard/blob/main/app/src/main/java/de/seemoo/at_tracking_detection/database/models/device/DeviceManager.kt
            // @author: AirGuard, commit 773f6822cae99d13b73c75f6605dee6ee25d8543 on Mar 8, 2022 from Sn0wfreezeDev
            val serviceUUID = result.scanRecord?.serviceUuids
            if (serviceUUID != null) {
                val containsTileService = serviceUUID.contains(
                    // Line 52 determines the serviceUUID of a tile tracker used from AirGuard's code.
                    // AirGuard's file can be found here: https://github.com/seemoo-lab/AirGuard/blob/main/app/src/main/java/de/seemoo/at_tracking_detection/database/models/device/types/Tile.kt
                    // The corresponding line from AirGuard is line 44.
                    // @author: AirGuard, commit a99d9c841c1d80ce4ddc6e2a154da4d78507d9bf on Mar 15, 2022 from Sn0wfreezeDev
                    ParcelUuid.fromString("0000FEED-0000-1000-8000-00805F9B34FB"))
                if (containsTileService) return Tile()
            }

            if (result.device.name != null) {
                return DeviceWithName(result.device.name)
            }

            // Could not identify the device
            return Unknown()
        }
    }
}