package com.example.pfemobile.ui.generator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pfemobile.GlobalData
import com.example.pfemobile.databinding.FragmentGeneratorBinding

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val generatorViewModel =
                ViewModelProvider(this).get(GeneratorViewModel::class.java)

        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.genUpdateButton.setOnClickListener {
            if(!GlobalData.bleConnected)
            {
                Toast.makeText(context, "Aucune connexion Bluetooth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amp = binding.ampInput.getFloatValue()
            val freq = binding.freqInput.getFloatValue()
            val offset = binding.freqInput.getFloatValue()
            val type = if(binding.typeGroup.checkedChipId == binding.sinusoidalChip.id) "SINE" else "PULSE"
            val rise = binding.riseInput.getFloatValue()
            val high = binding.riseInput.getFloatValue()
            val fall = binding.riseInput.getFloatValue()

            val message = "ECHO GEN NEW_WAVE $amp $freq $offset $type $rise $high $fall"

            Log.v("Gen", message)

            GlobalData.write(message)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}