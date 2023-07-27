package android.example.homescout.models

import timber.log.Timber

class Tile : DeviceType {

    override val type = "Tile"

    override fun printManufacturer() {
        Timber.i( "Tile")
    }
}
