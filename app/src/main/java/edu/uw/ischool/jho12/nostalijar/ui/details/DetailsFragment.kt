package edu.uw.ischool.jho12.nostalijar.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import edu.uw.ischool.jho12.nostalijar.R
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentDetailsBinding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.interfaces.ItemClickListener

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DetailsViewModel::class.java]

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDetails
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Create the image list with local resource IDs or file paths
        val imageList = ArrayList<SlideModel>()

        // Will need to implement with stored images from CreateFragment
        // imageList.add(SlideModel("String Url" or R.drawable, "title")

        // Set image list to ImageSlider
        binding.imageSlider.setImageList(imageList)

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT) // for all images

        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                TODO("Not yet implemented")
                // You can listen here.
            }
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
                // Do not use onItemSelected if you are using a double click listener at the same time.
                // Its just added for specific cases.
                // Listen for clicks under 250 milliseconds.
            }
        })

        // Button logic
        binding.doneBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Directing to Home", LENGTH_SHORT).show()
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}