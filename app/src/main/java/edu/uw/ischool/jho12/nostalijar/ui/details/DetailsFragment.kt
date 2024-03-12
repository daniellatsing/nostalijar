package edu.uw.ischool.jho12.nostalijar.ui.details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.constraintlayout.helper.widget.Carousel
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.jho12.nostalijar.R
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentDetailsBinding
import edu.uw.ischool.jho12.nostalijar.ui.create.ImageAdapter

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

        // Dummy list of image URIs
        val imageUris: List<Uri> = listOf(Uri.parse("uri1"), Uri.parse("uri2"), Uri.parse("uri3"))

        val recyclerView: RecyclerView = binding.recyclerView
        val adapter = ImageAdapter(requireContext(), imageUris)
        val doneBtn: Button = binding.doneBtn
        val carousel: Carousel = binding.carousel

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val carouselAdapter = ImageAdapter(requireContext(), imageUris)
        carousel.setAdapter(object : Carousel.Adapter {
            override fun count(): Int {
                return carouselAdapter.itemCount
            }

            override fun populate(view: View, index: Int) {
                val imageView: ImageView =
                    view.findViewById(R.id.imageView)
                imageView.setImageURI(imageUris[index])

                val viewHolder = carouselAdapter.onCreateViewHolder(view.parent as ViewGroup, 0)
                carouselAdapter.onBindViewHolder(viewHolder, index)
                (view as ViewGroup).addView(viewHolder.itemView)
            }

            override fun onNewItem(index: Int) {
                // Called when an item is set.
            }
        })

        // Button logic
        doneBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Directing to Home", LENGTH_SHORT).show()
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home)
        }

        binding.shareBtn.setOnClickListener {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}