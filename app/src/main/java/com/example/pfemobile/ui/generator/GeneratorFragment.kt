package com.example.pfemobile.ui.generator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pfemobile.GlobalData
import com.example.pfemobile.R
import com.example.pfemobile.databinding.FragmentGeneratorBinding
import com.example.pfemobile.ui.FloatInputView

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val generatorViewModel =
                ViewModelProvider(this).get(GeneratorViewModel::class.java)

        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val scrollView = binding.scrollView
        scrollView.setOnTouchListener { v, event ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            v.performClick()

            // Clear focus from the EditText
            v.requestFocus()
            if (v is ViewGroup) {
                v.clearFocus()
            }
            false
        }

        binding.genUpdateButton.setOnClickListener {
            if(!GlobalData.bleConnected)
            {
                Toast.makeText(context, "Aucune connexion Bluetooth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amp = binding.ampInput.getFloatValue()
            val freq = binding.freqInput.getFloatValue()
            val offset = binding.offsetInput.getFloatValue()
            val type = if(binding.typeGroup.checkedChipId == binding.sinusoidalChip.id) "SINE" else "PULSE"
            val rise = binding.riseInput.getFloatValue()/100f
            val high = binding.highInput.getFloatValue()/100f
            val fall = binding.fallInput.getFloatValue()/100f

            val message = "GEN NEW_WAVE $amp $freq $offset $type $rise $high $fall"

            Log.v("Gen", message)

            GlobalData.write(message)
        }

        binding.sinChip.setOnClickListener {
            binding.typeGroup.check(binding.sinusoidalChip.id)
            binding.riseInput.setFloatValue(0.0f)
            binding.highInput.setFloatValue(0.0f)
            binding.fallInput.setFloatValue(0.0f)
        }
        binding.triangleChip.setOnClickListener {
            binding.typeGroup.check(binding.pulseChip.id)
            binding.riseInput.setFloatValue(50.0f)
            binding.highInput.setFloatValue(0.0f)
            binding.fallInput.setFloatValue(50.0f)
        }
        binding.squareChip.setOnClickListener {
            binding.typeGroup.check(binding.pulseChip.id)
            binding.riseInput.setFloatValue(0.0f)
            binding.highInput.setFloatValue(50.0f)
            binding.fallInput.setFloatValue(0.0f)
        }
        binding.sawtoothChip.setOnClickListener {
            binding.typeGroup.check(binding.pulseChip.id)
            binding.riseInput.setFloatValue(100.0f)
            binding.highInput.setFloatValue(0.0f)
            binding.fallInput.setFloatValue(0.0f)
        }

        binding.ampInput.setFloatValue(GlobalData.genAmp)
        binding.freqInput.setFloatValue(GlobalData.genFreq)
        binding.offsetInput.setFloatValue(GlobalData.genOffset)
        if(GlobalData.genType == "sin")
            binding.typeGroup.check(binding.sinusoidalChip.id)
        else
            binding.typeGroup.check(binding.pulseChip.id)
        binding.riseInput.setFloatValue(GlobalData.genRise)
        binding.highInput.setFloatValue(GlobalData.genHigh)
        binding.fallInput.setFloatValue(GlobalData.genFall)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("genAmp", binding.ampInput.getFloatValue())
        outState.putFloat("genFreq", binding.freqInput.getFloatValue())
        outState.putFloat("genOffset", binding.offsetInput.getFloatValue())
        if(binding.typeGroup.checkedChipId == binding.sinusoidalChip.id)
            outState.putString("genType", "sin")
        else
            outState.putString("genType", "pulse")
        outState.putFloat("genRise", binding.riseInput.getFloatValue())
        outState.putFloat("genHigh", binding.highInput.getFloatValue())
        outState.putFloat("genFall", binding.fallInput.getFloatValue())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.ampInput.setFloatValue(savedInstanceState.getFloat("genAmp", GlobalData.genAmp))
            binding.freqInput.setFloatValue(savedInstanceState.getFloat("genFreq", GlobalData.genFreq))
            binding.offsetInput.setFloatValue(savedInstanceState.getFloat("genOffset", GlobalData.genOffset))
            val genType = savedInstanceState.getString("genType", GlobalData.genType)
            if(genType == "sin")
                binding.typeGroup.check(binding.sinusoidalChip.id)
            else
                binding.typeGroup.check(binding.pulseChip.id)
            binding.riseInput.setFloatValue(savedInstanceState.getFloat("genRise", GlobalData.genRise))
            binding.highInput.setFloatValue(savedInstanceState.getFloat("genHigh", GlobalData.genHigh))
            binding.fallInput.setFloatValue(savedInstanceState.getFloat("genFall", GlobalData.genFall))
        }
    }
}