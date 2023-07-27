package android.example.homescout.ui.notifications

import android.example.homescout.repositories.MainRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    val maliciousTrackerSortedByTimestamp = mainRepository.getAllMaliciousTrackersSortedByTimestamp()

    fun clearAll() {
        viewModelScope.launch {
            mainRepository.clearMaliciousTrackersTable()
        }
    }
}