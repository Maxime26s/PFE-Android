package com.example.pfemobile

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.getSystemService
import java.util.*

class BleCommunicator(private val context: Context, private val onMessageReceived: (String) -> Unit) {
    private val TAG = "BleCommunicator"
    private val serviceUUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
    private val characteristicUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")

    private val bluetoothManager: BluetoothManager =
        getSystemService(context, BluetoothManager::class.java)!!
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

    private val scanHandler = Handler(Looper.getMainLooper())
    private var scanning = false
    private val scanDurationMillis: Long = 3000

    private var strongestDevice: BluetoothDevice? = null
    private var highestRssi = Int.MIN_VALUE

    fun startScan() {
        if (scanning) {
            return
        }

        val filters = listOf(
            ScanFilter.Builder()
                .setDeviceName("PICO")
                .build()
        )

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bluetoothAdapter.bluetoothLeScanner.startScan(null, settings, scanCallback)
        scanning = true
        scanHandler.postDelayed({ stopScan() }, scanDurationMillis)
    }

    private fun stopScan() {
        if (!scanning) {
            return
        }

        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        scanning = false

        strongestDevice?.let { device ->
            connectToDevice(device)
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (result.device.name?.startsWith("PICO") == true && result.rssi > highestRssi) {
                highestRssi = result.rssi
                strongestDevice = result.device
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result ->
                if (result.device.name?.startsWith("PICO") == true && result.rssi > highestRssi) {
                    highestRssi = result.rssi
                    strongestDevice = result.device
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // Handle the error
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(
            context,
            false,
            object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    when (newState) {
                        BluetoothGatt.STATE_CONNECTED -> {
                            gatt.discoverServices()
                        }
                        BluetoothGatt.STATE_DISCONNECTED -> {
                            gatt.close()
                        }
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    gatt.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let { characteristic ->
                        gatt.setCharacteristicNotification(characteristic, true)
                    }
                }

                override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                    if (characteristic.uuid == characteristicUUID) {
                        onMessageReceived(characteristic.getStringValue(0))
                    }
                }
            })
    }

    fun write(message: String) {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let { characteristic ->
            characteristic.setValue(message)
            bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }

    fun read() {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let { characteristic ->
            bluetoothGatt?.readCharacteristic(characteristic)
        }
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
