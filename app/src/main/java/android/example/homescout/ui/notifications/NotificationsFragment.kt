package android.example.homescout.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.example.homescout.R
import android.example.homescout.databinding.FragmentNotificationsBinding
import android.example.homescout.ui.main.MainActivity
import android.example.homescout.utils.Constants
import android.example.homescout.utils.Constants.CHANNEL_ID_FOUND_DEVICE
import android.example.homescout.utils.Constants.NOTIFICATION_CHANNEL_FOUND_DEVICE
import android.example.homescout.utils.Constants.NOTIFICATION_ID_FOUND_DEVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    // PROPERTIES
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var maliciousTrackersAdapter: MaliciousTrackerAdapter = MaliciousTrackerAdapter()

    // PROPERTIES lateinit
    private lateinit var notificationsViewModel: NotificationsViewModel


    // LIFECYCLE FUNCTIONS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupViewModelAndBinding(inflater, container)
        setupButtonClearAllOnClickListener()
        setupRecyclerView()

        notificationsViewModel.maliciousTrackerSortedByTimestamp.observe(viewLifecycleOwner) {
            maliciousTrackersAdapter.submitList(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // FUNCTIONS USED IN LIFECYCLE (for code readability)
    private fun setupViewModelAndBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
    }

    private fun setupButtonClearAllOnClickListener() {
        binding.buttonClearAll.setOnClickListener {
            notificationsViewModel.clearAll()
        }
    }

    private fun setupRecyclerView() {
        binding.maliciousTrackersRecyclerView.apply {
            adapter = maliciousTrackersAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }
    }


    // PRIVATE FUNCTIONS
    private fun sendNotification() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID_FOUND_DEVICE
        )
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Home Scout")
            .setContentText("This is a first notification on a found BLE device")
            .setContentIntent(getMainActivityPendingIntent())

        notificationManager.notify(NOTIFICATION_ID_FOUND_DEVICE,notificationBuilder.build() )

    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID_FOUND_DEVICE,
            NOTIFICATION_CHANNEL_FOUND_DEVICE,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        requireContext(),
        0,
        Intent(requireContext(), MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_NOTIFICATIONS_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )
}