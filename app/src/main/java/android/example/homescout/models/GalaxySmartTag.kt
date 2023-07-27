package android.example.homescout.models

import timber.log.Timber

class GalaxySmartTag : DeviceType {

    override val type = "Galaxy SmartTag+"

    override fun printManufacturer() {
        Timber.i( "GalaxySmartTag")
    }

}
