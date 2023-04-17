package com.example.pfemobile.ui.source

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pfemobile.GlobalData
import com.example.pfemobile.databinding.FragmentGeneratorBinding
import com.example.pfemobile.databinding.FragmentSourceBinding

class SourceFragment : Fragment() {

    private var _binding: FragmentSourceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sourceViewModel =
            ViewModelProvider(this).get(SourceViewModel::class.java)

        _binding = FragmentSourceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val scrollView = binding.scrollView
        scrollView.setOnTouchListener { v, event ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            v.performClick()

            // Clear focus from the EditText
            v.requestFocus()
            if (v is ViewGroup) {
                v.clearFocus()
            }
            false
        }

        binding.sourceUpdateButton.setOnClickListener {
            if (!GlobalData.bleConnected) {
                Toast.makeText(context, "Aucune connexion Bluetooth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val voltage = binding.sourceInput.getFloatValue()
            val rebasedVoltage = convertValueToRange(voltage)
            Log.v("Source", rebasedVoltage.toString())
            var hexValue = rebasedVoltage.toString(16).uppercase()
            if(hexValue.length == 1)
                hexValue = "0$hexValue"
            Log.v("Source", hexValue)
            val message = "SRC SET_VOLTAGE $hexValue"

            Log.v("Source", message)

            GlobalData.write(message)
        }

        binding.sourceInput.setFloatValue(GlobalData.sourceVoltage)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("sourceVoltage", binding.sourceInput.getFloatValue())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.sourceInput.setFloatValue(
                savedInstanceState.getFloat(
                    "sourceVoltage",
                    GlobalData.sourceVoltage
                )
            )
        }
    }

    private fun convertValueToRange(value: Float): Int {
        val min = 1.71f
        val max = 17.8f
        val range = max - min
        val scaledValue = ((value - min) / range).coerceIn(0.0f,1.0f)
        return (scaledValue * 79).toInt()
    }
}