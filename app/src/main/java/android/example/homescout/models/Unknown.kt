package android.example.homescout.models

import timber.log.Timber

class Unknown : DeviceType {

    override val type = "Unknown"

    override fun printManufacturer() {
        Timber.i( "Unknown")
    }
}
