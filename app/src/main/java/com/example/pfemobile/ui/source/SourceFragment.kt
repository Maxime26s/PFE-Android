package com.example.pfemobile.ui.source

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pfemobile.GlobalData
import com.example.pfemobile.databinding.FragmentSourceBinding

class SourceFragment : Fragment() {

    private var _binding: FragmentSourceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sourceViewModel =
            ViewModelProvider(this).get(SourceViewModel::class.java)

        _binding = FragmentSourceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        sourceViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.button.setOnClickListener { GlobalData.bleCommunicator.startScan() }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}