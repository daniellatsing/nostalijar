package edu.uw.ischool.jho12.nostalijar.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.core.app.NotificationCompat
import edu.uw.ischool.jho12.nostalijar.R
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentSettingsBinding
import java.util.Calendar

val CHANNEL_ID = "channel"
val CHANNEL_NAME = "channelName"

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    var selectedDay: String? = null
    private val code = 123

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var alarmReceiver: BroadcastReceiver? = null
    val ALARM_ACTION = "edu.uw.ischool.cammip.ALARM"

    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }

            val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotifChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        val notificationsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val radioGroup: RadioGroup = binding.radioGroup

        val textView: TextView = binding.preferences
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        radioGroup.setOnCheckedChangeListener { group, checked ->
            when (checked) {
                R.id.saturdayRB -> selectedDay = getString(R.string.saturday)
                R.id.fridayRB -> selectedDay = getString(R.string.friday)
            }
        }

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val saveButton = binding.saveBtn
        saveButton.setOnClickListener {


            if (selectedDay != null) {
                //let user know their preference was saved

                showToast("Preferences have been saved $selectedDay")
                val filter = IntentFilter(ALARM_ACTION)
                activity?.registerReceiver(alarmReceiver, filter)

                //Checks permissions to set exact alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    //alarmManager.setExact()

                    val calendar = Calendar.getInstance()

                    when (selectedDay) {
                        "Saturday" -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                        "Friday" -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                    }


                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }


            } else {
                showToast("Please select a day")
            }

            //Send preference to alarm manager
            //Selected day is held in var selectedDay
        }

        val cancelButton = binding.cancelBtn
        cancelButton.setOnClickListener {
            radioGroup.clearCheck()
            selectedDay = null
            //activity?.unregisterReceiver(alarmReceiver)

            pendingIntent?.let {
                alarmManager.cancel(it)
            }

        }

        return root
    }


    private fun Fragment.showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotif(context)
    }

    private fun showNotif(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Time Capsule Reminder")
            .setContentText("Make a time capsule!")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}