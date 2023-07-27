package android.example.homescout.utils

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.example.homescout.models.DeviceTypeManager
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber
import kotlin.experimental.and

class BluetoothAPILogger() {

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    fun logResults(result: ScanResult) {

        // prepare to identify an device by manufacturer
        val manufacturerData = result.scanRecord?.manufacturerSpecificData
        //val services = result.scanRecord?.serviceUuids
        //Timber.i("$services: $services")

        val tmp_device = DeviceTypeManager.identifyDeviceType(result)
        Timber.i("DEVICE: ${tmp_device.type.toString()}")

        // identification for AirTag
        val statusByte: Byte? = result.scanRecord?.getManufacturerSpecificData(0x004c)?.get(2) // 16 for AirTag
        Timber.i( "statusByte: ${statusByte}")
        val deviceTypeInt = (statusByte?.and(0x30)?.toInt()?.shr(4)) // type = 1 for AirTag
        Timber.i( "deviceTypeInt: ${deviceTypeInt}")
        val device = result.device
        val address = device.address
        Timber.i( "address: ${address}")

        // methods or properties from scanResult
        val describeContents = result.describeContents()
        val advertisingSid = result.advertisingSid
        val dataStatus = result.dataStatus
        // device already declared
        val periodicAdvertisingInterval = result.periodicAdvertisingInterval
        val primaryPhy = result.primaryPhy
        val rssi = result.rssi
        val scanRecord = result.scanRecord
        val secondaryPhy = result.secondaryPhy
        val timestampNanos = result.timestampNanos
        val txPower = result.txPower
        val hashCode = result.hashCode().toString()
        val isConnectable = result.isConnectable
        val isLegacy = result.isLegacy
        val toString = result.toString()

        Timber.i( "describeContents: ${describeContents}")
        Timber.i( "advertisingSid: ${advertisingSid}")
        Timber.i( "dataStatus: ${dataStatus}")
        Timber.i( "periodicAdvertisingInterval: ${periodicAdvertisingInterval}")
        Timber.i( "primaryPhy: ${primaryPhy}")
        Timber.i( "rssi: ${rssi}")
        Timber.i( "secondaryPhy: ${secondaryPhy}")
        Timber.i( "timestampNanos: ${timestampNanos}")
        Timber.i( "txPower: ${txPower}")
        Timber.i( "hashCode: ${hashCode}")
        Timber.i( "isConnectable: ${isConnectable}")
        Timber.i( "isLegacy: ${isLegacy}")
        Timber.i( "toString: ${toString}")

        // methods or properties from BluetoothDevice
        //val fetchUuidWithSdp = device.fetchUuidsWithSdp().toString()
        // address already in place
        val alias = device.alias
        val bluetoothClass = device.bluetoothClass.deviceClass
        val bluetoothClassMajor = device.bluetoothClass.majorDeviceClass
        val bondState = device.bondState
        val deviceMame = device.name
        val type = device.type
        val uuids = device.uuids
        val deviceToString = device.toString()

        //Timber.i( "fetchUuidWithSdp: ${fetchUuidWithSdp}")
        Timber.i( "alias: ${alias}")
        Timber.i( "bluetoothClass: ${bluetoothClass}")
        Timber.i( "bluetoothClassMajor: ${bluetoothClassMajor}")
        Timber.i( "bondState: ${bondState}")
        Timber.i( "deviceMame: ${deviceMame}")
        Timber.i( "type: ${type}")
        Timber.i( "uuids: ${uuids}")
        Timber.i( "deviceToString: ${deviceToString}")

        // methods or properties from ScanRecord
        val SRadvertisingFlags = scanRecord?.advertiseFlags
        val bytes = scanRecord?.bytes
        val SRdeviceName = scanRecord?.deviceName
        val manufacturerSpecificData = scanRecord?.manufacturerSpecificData
        val serviceData = scanRecord?.serviceData
        val serviceSolicitationUuids = scanRecord?.serviceSolicitationUuids
        val serviceUuids = scanRecord?.serviceUuids
        val SRtxPowerLevel = scanRecord?.txPowerLevel
        val SRtoString = scanRecord?.toString()


        // iterate over manufacturer data
//            val size = manufacturerSpecificData?.size()
//            for (i in 0 until size!!) {
//                val key: Int = manufacturerSpecificData.keyAt(i)
//                val value: ByteArray? = manufacturerSpecificData.valueAt(i)
//                Timber.i("size: $size")
//                Timber.i("key: $key value: $value")
//                value?.forEach { byte ->  Timber.i("byte: $byte") }
//            }

        Timber.i( "SRadvertisingFlags: ${SRadvertisingFlags}")
        Timber.i( "bytes: ${bytes}")
        Timber.i( "SRdeviceName: ${SRdeviceName}")
        Timber.i( "manufacturerSpecificData: ${manufacturerSpecificData}")
        Timber.i( "serviceData: ${serviceData}")
        Timber.i( "serviceSolicitationUuids: ${serviceSolicitationUuids}")
        Timber.i( "serviceUuids: ${serviceUuids}")
        Timber.i( "SRtxPowerLevel: ${SRtxPowerLevel}")
        Timber.i( "SRtoString: ${SRtoString}")

//            val appleManufacturerSpecificData = result.scanRecord?.getManufacturerSpecificData(0x004c)
//
//            if (appleManufacturerSpecificData != null && deviceTypeInt == 1) {
//                Timber.i( "INVESTIGATING AIRTAG")
//                val statusByte: Byte = appleManufacturerSpecificData[2]
//                Timber.i( "statusByte: ${statusByte}")
//
//                val statusByteAnd = statusByte.and(0x30).toString(radix = 16)
//                Timber.i( "statusByteAnd: ${statusByteAnd}")
//
//                val statusByteAndtoInt = statusByte.and(0x30).toInt().toString(radix = 2)
//                Timber.i( "statusByteAndtoInt: ${statusByteAndtoInt}")
//
//                // Get the correct int from the byte
//                val deviceTypeInt = (statusByte.and(0x30).toInt() shr 4).toString(radix = 16)
//                Timber.i( "deviceTypeInt: ${deviceTypeInt}")
//
//            }


    }
}