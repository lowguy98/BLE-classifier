package android.example.homescout.models

import timber.log.Timber

class DeviceWithName(private val name: String) : DeviceType {

    override val type: String
        get() = name

    override fun printManufacturer() {
        Timber.i( name)
    }
}