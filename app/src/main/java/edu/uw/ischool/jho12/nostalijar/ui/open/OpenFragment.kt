package edu.uw.ischool.jho12.nostalijar.ui.open

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import edu.uw.ischool.jho12.nostalijar.R
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentOpenBinding
import edu.uw.ischool.jho12.nostalijar.ui.details.DetailsFragment
import edu.uw.ischool.jho12.nostalijar.ui.settings.SettingsFragment
import java.util.Calendar
import java.util.TimeZone

class OpenFragment : Fragment() {

    private var _binding: FragmentOpenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[OpenViewModel::class.java]

        _binding = FragmentOpenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textOpen
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        handler.post(object : Runnable {
            override fun run() {
                // Keep the postDelayed before the updateTime(), so when the event ends, the handler will stop too.
                handler.postDelayed(this, 1000)
                updateTime()
            }
        })

        binding.viewBtnDetails.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_details)
        }
        return root
    }

    private fun updateTime() {
        // Set Current Date
        val currentDate = Calendar.getInstance()
        // set the time capsule open date
        val eventDate = Calendar.getInstance()
//        val dateString = grab from storage
//        val timeString = grab from storage
        eventDate[Calendar.YEAR] = 2024
        eventDate[Calendar.MONTH] = 2 // 0-11 so 1 less
        eventDate[Calendar.DAY_OF_MONTH] = 11
        eventDate[Calendar.HOUR] = 8
        eventDate[Calendar.MINUTE] = 27
        eventDate[Calendar.SECOND] = 0

        Log.i("Time", currentDate.time.toString())
        Log.i("Time2", eventDate.time.toString())

        // Find how many milliseconds until the event
        val diff = eventDate.timeInMillis - currentDate.timeInMillis
        // convert milli to day, hours, minutes, seconds
        val days = diff / (24 * 60 * 60 * 1000)
        val hours = diff / (1000 * 60 * 60) % 24
        val minutes = diff / (1000 * 60) % 60
        val seconds = (diff / 1000) % 60

        binding.countdown.text = "${days}d ${hours}h ${minutes}m ${seconds}s"

        if (currentDate.time >= eventDate.time) {
            endEvent()
        }
    }

    private fun endEvent() {
        binding.countdown.text = getString(R.string.timer_end_text)
        handler.removeMessages(0)
        binding.viewBtnDetails.isEnabled = true
        var builder = NotificationCompat.Builder(requireContext(), "nostalijarNotif")
            .setSmallIcon(R.drawable.ic_rounded_alarm_100dp)
            .setContentTitle("Time's up!")
            .setContentText("It's time to view your capsule!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}