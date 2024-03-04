package edu.uw.ischool.jho12.nostalijar.ui.open

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.uw.ischool.jho12.nostalijar.databinding.FragmentOpenBinding

class OpenFragment : Fragment() {

    private var _binding: FragmentOpenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}