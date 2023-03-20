package com.example.pfemobile.ui.oscilloscope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pfemobile.GlobalData
import com.example.pfemobile.databinding.FragmentOscilloscopeBinding

class OscilloscopeFragment : Fragment() {

    private var _binding: FragmentOscilloscopeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(OscilloscopeViewModel::class.java)

        _binding = FragmentOscilloscopeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.timeStepperInputView.setOnValueChangedListener { value -> GlobalData.timePerCell = value }
        binding.voltStepperInputView.setOnValueChangedListener { value -> GlobalData.voltPerCell = value }

        GlobalData.addListener("timePerCell") { value: Any ->
            binding.graphView.timePerCell = value as Float
            binding.timeStepperInputView.setIndexFromValue(value)
        }
        GlobalData.addListener("voltPerCell") { value: Any ->
            binding.graphView.voltPerCell = value as Float
            binding.voltStepperInputView.setIndexFromValue(value)
        }

        binding.timeStepperInputView.setIndexFromValue(0.1f)
        binding.voltStepperInputView.setIndexFromValue(1f)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}