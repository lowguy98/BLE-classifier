package android.example.homescout.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "malicious_tracker_table")
data class MaliciousTracker(
    var mac: String,
    var timestampInMilliSeconds: Long = 0L,
    var type: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}