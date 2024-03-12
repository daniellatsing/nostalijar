package edu.uw.ischool.jho12.nostalijar.ui.details

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.uw.ischool.jho12.nostalijar.R
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentDetailsBinding
import edu.uw.ischool.jho12.nostalijar.ui.create.ImageAdapter
import com.google.gson.reflect.TypeToken

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireActivity().getSharedPreferences("TimeCapsulePrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val title = sharedPreferences.getString("capsuleTitle", "No Title")
        val caption = sharedPreferences.getString("capsuleCaption", "No Caption")
        val imagesJson = sharedPreferences.getString("capsuleImages", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        val imagesStringList: List<String> = gson.fromJson(imagesJson, type)
        val imageUris: List<Uri> = imagesStringList.map { Uri.parse(it) }

        binding.textDetailsTitle.text = title
        binding.textDetailsCaption.text = caption

        val imageAdapter = ImageAdapter(requireContext(), imageUris)
        binding.detailsImagesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.detailsImagesRecyclerView.adapter = imageAdapter

        binding.shareBtn.setOnClickListener {
            checkForSmsPermission()
            val message = "Capsule Title\nCapsule Caption\nLink"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setType("vnd.android-dir/mms-sms")
            intent.putExtra("sms_body", message)
            startActivity(intent)
        }

        binding.doneBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Directing to Home", LENGTH_SHORT).show()
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home)
        }

        return root
    }

    private fun checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED) {
            Log.d("DetailsFragment", "Permission not granted!")
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.SEND_SMS), REQUEST_SEND_SMS_PERMISSION
            )
        } else {
            sendSms()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_SEND_SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms()
                } else {
                    Toast.makeText(requireContext(), "SMS permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendSms() {
        val sharedPreferences = requireActivity().getSharedPreferences("TimeCapsulePrefs", Context.MODE_PRIVATE)
        val title = sharedPreferences.getString("capsuleTitle", "No Title")
        val caption = sharedPreferences.getString("capsuleCaption", "No Caption")

        val message = "$title\n$caption\nLink"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:")
            putExtra("sms_body", message)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No SMS app found", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_SEND_SMS_PERMISSION = 1
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
