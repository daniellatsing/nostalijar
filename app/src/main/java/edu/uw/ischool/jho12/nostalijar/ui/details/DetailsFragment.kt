package edu.uw.ischool.jho12.nostalijar.ui.details

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentDetailsBinding
import edu.uw.ischool.jho12.nostalijar.ui.create.ImageAdapter

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
