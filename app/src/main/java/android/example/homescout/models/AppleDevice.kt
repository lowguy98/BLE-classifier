package android.example.homescout.models

import timber.log.Timber

class AppleDevice : DeviceType {

    override val type = "AppleDevice"

    override fun printManufacturer() {
        Timber.i( "AppleDevice")
    }
}
