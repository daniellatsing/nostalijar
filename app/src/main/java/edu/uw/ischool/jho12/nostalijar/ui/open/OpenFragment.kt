package edu.uw.ischool.jho12.nostalijar.ui.open

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import edu.uw.ischool.jho12.nostalijar.MainActivity
import edu.uw.ischool.jho12.nostalijar.R
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentOpenBinding
import java.text.SimpleDateFormat
import java.util.*

class OpenFragment : Fragment() {

    private var _binding: FragmentOpenBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOpenBinding.inflate(inflater, container, false)
        createNotificationChannel()
        sharedPreferences = requireActivity().getSharedPreferences("TimeCapsulePrefs", Context.MODE_PRIVATE)

        binding.viewBtnDetails.setOnClickListener {
            it.findNavController().navigate(R.id.navigation_details)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkIfTimeToOpen()
    }

    private fun checkIfTimeToOpen() {
        val openDateStr = sharedPreferences.getString("capsuleDate", null)
        val openTimeStr = sharedPreferences.getString("capsuleTime", null)

        if (openDateStr != null && openTimeStr != null) {
            val dateTimeFormat = SimpleDateFormat("MM/dd/yyyy HH:mm a", Locale.getDefault())
            val openDateTime = dateTimeFormat.parse("$openDateStr $openTimeStr")
            val currentTime = Calendar.getInstance().time

            if (openDateTime != null) {
                if (currentTime.before(openDateTime)) {
                    val diff = openDateTime.time - currentTime.time
                    binding.viewBtnDetails.isEnabled = false
                    binding.viewBtnDetails.setBackgroundColor(Color.GRAY)
                    binding.openDateTime.text = "Opens on: $openDateStr at $openTimeStr"

                    countdownTimer = object : CountDownTimer(diff, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            binding.countdown.text = "Opens in: ${millisUntilFinished / 1000} seconds"
                        }

                        override fun onFinish() {
                            binding.viewBtnDetails.isEnabled = true
                            binding.viewBtnDetails.setBackgroundColor(Color.GREEN)
                            binding.countdown.text = "The capsule is ready to open!"
                            sendNotification()
                        }
                    }.start()

                } else {
                    binding.viewBtnDetails.isEnabled = true
                    binding.viewBtnDetails.setBackgroundColor(Color.GREEN)
                    binding.openDateTime.text = "The capsule is ready to open!"
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun sendNotification() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireContext(), getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Nostalijar")
            .setContentText("Your time capsule is ready to open!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(requireContext()).notify(1, builder.build())
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification() // Resend the notification if permission is granted
            } else {
                Toast.makeText(requireContext(), "Notification permission denied. Cannot send notifications.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countdownTimer?.cancel()
    }
}
