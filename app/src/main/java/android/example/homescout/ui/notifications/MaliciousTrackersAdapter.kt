package android.example.homescout.ui.notifications

import android.example.homescout.R
import android.example.homescout.database.MaliciousTracker
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MaliciousTrackerAdapter : RecyclerView.Adapter<MaliciousTrackerAdapter.MaliciousTrackerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaliciousTrackerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_malicious_tracker, parent, false)

        return MaliciousTrackerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffCallback = object : DiffUtil.ItemCallback<MaliciousTracker>() {
        override fun areItemsTheSame(oldItem: MaliciousTracker, newItem: MaliciousTracker
        ): Boolean { return oldItem.id == newItem.id }

        override fun areContentsTheSame(oldItem: MaliciousTracker, newItem: MaliciousTracker
        ): Boolean { return oldItem.hashCode() == newItem.hashCode() }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<MaliciousTracker>) = differ.submitList(list)


    override fun onBindViewHolder(holder: MaliciousTrackerViewHolder, position: Int) {
        val maliciousTracker = differ.currentList[position]
        holder.bind(maliciousTracker)
    }

    class MaliciousTrackerViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val deviceType: TextView = view.findViewById(R.id.device_type)
        private val macAddress: TextView = view.findViewById(R.id.mac_address)
        private val date: TextView = view.findViewById(R.id.date)

        fun bind(maliciousTracker: MaliciousTracker) {
            deviceType.text = maliciousTracker.type
            macAddress.text = maliciousTracker.mac
            val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
            date.text = dateFormat.format(maliciousTracker.timestampInMilliSeconds)
        }
    }


}