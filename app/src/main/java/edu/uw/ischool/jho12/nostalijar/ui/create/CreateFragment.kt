package edu.uw.ischool.jho12.nostalijar.ui.create

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentCreateBinding
import java.util.Calendar

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private val imageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter

    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data: Intent? = result.data
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    addImageToList(imageUri)
                }
            } ?: data?.data?.let { uri ->
                addImageToList(uri)
            }
        }
    }

    private fun addImageToList(uri: Uri) {
        if (imageUris.size < 7) {
            imageUris.add(uri)
            imageAdapter.notifyItemInserted(imageUris.size - 1)
        } else {
            Toast.makeText(context, "You can only add up to 7 images", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val view = binding.root

        checkCapsuleExists()

        setupImageSelection()
        setupImageAdapter()
        setupDateAndTimePickers()

        binding.inputTitle.filters = arrayOf(InputFilter.LengthFilter(30))
        binding.inputCaption.filters = arrayOf(InputFilter.LengthFilter(100))

        binding.createBtn.setOnClickListener {
            saveCapsule()
        }

        return view
    }

    private fun setupImageAdapter() {
        imageAdapter = ImageAdapter(requireContext(), imageUris)
        binding.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun setupImageSelection() {
        binding.inputImageBtn.setOnClickListener {
            if (imageUris.size >= 7) {
                Toast.makeText(context, "You have already added 7 images", Toast.LENGTH_LONG).show()
            } else {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pickImagesLauncher.launch(intent)
    }

    private fun setupDateAndTimePickers() {
        binding.selectDateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", monthOfYear + 1, dayOfMonth, year)
                binding.selectDateBtn.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.selectTimeBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                val formattedTime = String.format("%02d:%02d %s", if (hourOfDay % 12 == 0) 12 else hourOfDay % 12, minute, if (hourOfDay < 12) "AM" else "PM")
                binding.selectTimeBtn.text = formattedTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
    }

    private fun saveCapsule() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("TimeCapsulePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val imagesJson = gson.toJson(imageUris.map { it.toString() }) // Convert Uri list to JSON
        editor.putString("capsuleTitle", binding.inputTitle.text.toString())
        editor.putString("capsuleCaption", binding.inputCaption.text.toString())
        editor.putString("capsuleDate", binding.selectDateBtn.text.toString())
        editor.putString("capsuleTime", binding.selectTimeBtn.text.toString())
        editor.putString("capsuleImages", imagesJson)
        editor.putBoolean("capsuleExists", true)
        editor.apply()

        Toast.makeText(requireContext(), "Capsule saved!", Toast.LENGTH_SHORT).show()
    }

    private fun checkCapsuleExists() {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("TimeCapsulePrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("capsuleExists", false)) {
            binding.createBtn.isEnabled = false
            Toast.makeText(requireContext(), "A capsule is already waiting to be opened. You cannot create another.", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
