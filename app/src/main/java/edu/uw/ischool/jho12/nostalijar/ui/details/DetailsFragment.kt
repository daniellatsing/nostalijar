package edu.uw.ischool.jho12.nostalijar.ui.details

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.constraintlayout.helper.widget.Carousel
import androidx.core.app.ActivityCompat
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

        binding.shareBtn.setOnClickListener {
            checkForSmsPermission()
            val message = "Capsule Title\nCapsule Caption\nLink"
            // on below line creating an intent to send sms
            val intent = Intent(Intent.ACTION_VIEW)
//            intent.addCategory(Intent.CATEGORY_APP_MESSAGING)
            // on below line putting extra as sms body with the data from edit text
            intent.setType("vnd.android-dir/mms-sms")
            intent.putExtra("sms_body", message)
//            intent.putExtra(Intent.EXTRA_STREAM, imageuri)
            // on below line starting activity to send sms.
            startActivity(intent)
        }


        // Button logic
        doneBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Directing to Home", LENGTH_SHORT).show()
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home)
        }
        return root
    }

    private fun checkForSmsPermission() {
        // This will (probably) prompt only once, when first installed/run on the device.
        // Once obtained, the permission will be "sticky".
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permission not granted!")
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(
                requireContext() as Activity, arrayOf(Manifest.permission.SEND_SMS), 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}