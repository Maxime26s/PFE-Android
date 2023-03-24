package com.example.pfemobile.ui.oscilloscope

import android.graphics.PointF
import android.os.Bundle
import android.provider.Settings.Global
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        binding.timeStepperInputView.setOnValueChangedListener { value ->
            GlobalData.timePerCell = value
        }
        binding.voltStepperInputView1.setOnValueChangedListener { value ->
            GlobalData.chan1VoltPerCell = value
        }
        binding.voltStepperInputView2.setOnValueChangedListener { value ->
            GlobalData.chan2VoltPerCell = value
        }

        binding.chan1Switch.setOnCheckedChangeListener { _, value ->
            GlobalData.chan1State = value
        }
        binding.chan2Switch.setOnCheckedChangeListener { _, value ->
            GlobalData.chan2State = value
        }

        GlobalData.addListener("timePerCell") { value: Any ->
            binding.graphView.timePerCell = value as Float
            binding.timeStepperInputView.setIndexFromValue(value)
        }
        GlobalData.addListener("chan1VoltPerCell") { value: Any ->
            binding.graphView.chan1VoltPerCell = value as Float
            binding.voltStepperInputView1.setIndexFromValue(value)
        }
        GlobalData.addListener("chan2VoltPerCell") { value: Any ->
            binding.graphView.chan2VoltPerCell = value as Float
            binding.voltStepperInputView2.setIndexFromValue(value)
        }
        GlobalData.addListener("chan1State") { value: Any ->
            binding.graphView.chan1State = value as Boolean
            binding.chan1Switch.isChecked = value
        }
        GlobalData.addListener("chan2State") { value: Any ->
            binding.graphView.chan2State = value as Boolean
            binding.chan2Switch.isChecked = value
        }

        GlobalData.addListener("bleConnected") { value: Any ->
            if (_binding == null) return@addListener

            if (value as Boolean)
                binding.connectButton.text = "Déconnexion"
            else
                binding.connectButton.text = "Connexion"
        }
        GlobalData.addListener("chan1Data") { value: Any ->
            binding.graphView.chan1Data = value as List<PointF>
            binding.graphView.invalidate()
        }
        GlobalData.addListener("chan2Data") { value: Any ->
            binding.graphView.chan2Data = value as List<PointF>
            binding.graphView.invalidate()
        }

        if (GlobalData.bleConnected)
            binding.connectButton.text = "Déconnexion"
        else
            binding.connectButton.text = "Connexion"

        binding.graphView.invalidate()

        binding.timeStepperInputView.setIndexFromValue(0.1f)
        binding.voltStepperInputView1.setIndexFromValue(1f)
        binding.voltStepperInputView2.setIndexFromValue(1f)
        binding.chan1Switch.isChecked = true
        binding.chan2Switch.isChecked = false

        binding.connectButton.setOnClickListener {
            if (!GlobalData.bleCommunicator.isScanning()) {
                if (GlobalData.bleCommunicator.isConnected()) {
                    GlobalData.bleCommunicator.disconnect()
                    Toast.makeText(context, "Disconnecting...", Toast.LENGTH_SHORT).show()
                } else {
                    GlobalData.bleCommunicator.startScan()
                    Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show()
                }
            }

            if (_binding == null) return@setOnClickListener
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}