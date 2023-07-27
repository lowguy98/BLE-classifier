package android.example.homescout.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MaliciousTrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaliciousTracker(maliciousTracker: MaliciousTracker)

    @Query("SELECT * FROM malicious_tracker_table WHERE mac = :key")
    fun getMaliciousTrackerByMac(key: String) : MaliciousTracker?

    @Query("SELECT * FROM malicious_tracker_table ORDER BY timestampInMilliSeconds DESC")
    fun getAllMaliciousTrackersSortedByTimestamp() : LiveData<List<MaliciousTracker>>

    @Delete
    suspend fun deleteMaliciousTracker(maliciousTracker: MaliciousTracker)

    @Query("DELETE FROM malicious_tracker_table")
    suspend fun clearTable()
}