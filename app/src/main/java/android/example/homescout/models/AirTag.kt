package android.example.homescout.models

import timber.log.Timber

class AirTag : DeviceType {

    override val type = "AirTag"

    override fun printManufacturer() {
        Timber.i( "AirTag")
    }
}
