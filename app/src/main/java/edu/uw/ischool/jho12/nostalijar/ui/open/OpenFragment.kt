package edu.uw.ischool.jho12.nostalijar.ui.open

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countdownTimer?.cancel()
    }
}
