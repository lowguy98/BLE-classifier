package android.example.homescout.models

import timber.log.Timber

class AirPods : DeviceType {

    override val type = "AirPods"

    override fun printManufacturer() {
        Timber.i( "AirPods")
    }
}
