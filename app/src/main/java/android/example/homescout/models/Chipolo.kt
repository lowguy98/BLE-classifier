package android.example.homescout.models

import timber.log.Timber

class Chipolo : DeviceType {

    override val type = "Chipolo ONE Spot"

    override fun printManufacturer() {
        Timber.i( "Chipolo")
    }
}
