package android.example.homescout.ui.scan

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.example.homescout.R
import android.example.homescout.models.DeviceTypeManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_scan_result, parent,false)

        return ViewHolder(view, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.ViewHolder(view) {

        private val deviceType: TextView = view.findViewById(R.id.device_type)
        private val macAddress: TextView = view.findViewById(R.id.mac_address)
        private val signalStrength: TextView = view.findViewById(R.id.signal_strength)


        @SuppressLint("MissingPermission", "SetTextI18n")
        fun bind(result: ScanResult) {
            // display device device type, mac address and RSSI of ble device
            macAddress.text = result.device.address
            deviceType.text = DeviceTypeManager.identifyDeviceType(result).type
            signalStrength.text = "${result.rssi} dBm"

            view.setOnClickListener { onClickListener.invoke(result) }
        }
    }
}
