package com.example.pfemobile

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.util.Log
import java.util.*

class BleManager(private val context: Context) {

    private val TAG = BleManager::class.java.simpleName

    // Bluetooth related objects
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null

    // UUIDs for service and characteristic
    private val SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val CHARACTERISTIC_RX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private val CHARACTERISTIC_TX_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private val CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // Byte array for received data
    private var receivedData: ByteArray = byteArrayOf()

    /**
     * Initialize Bluetooth adapter and start scanning for BLE devices.
     */
    fun startScanning() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.startLeScan(leScanCallback)
    }

    /**
     * Stop scanning for BLE devices.
     */
    fun stopScanning() {
        bluetoothAdapter?.stopLeScan(leScanCallback)
    }

    /**
     * Connect to a BLE device and start GATT communication.
     */
    fun connect(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    /**
     * Disconnect from a BLE device and stop GATT communication.
     */
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt = null
    }

    /**
     * Write data to the connected BLE device.
     */
    fun writeData(data: ByteArray) {
        bluetoothGatt?.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_TX_UUID)?.apply {
            value = data
            writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            bluetoothGatt?.writeCharacteristic(this)
        }
    }

    /**
     * Get received data.
     */
    fun getReceivedData(): ByteArray {
        return receivedData
    }

    /**
     * Bluetooth LE scan callback.
     */
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        if (device.name?.startsWith("PICO") == true) {
            connect(device)
            stopScanning()
        }
    }

    /**
     * Bluetooth GATT callback.
     */
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server.")
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.")
                receivedData = byteArrayOf()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered.")
                // Get the characteristic we want to listen to.
                val characteristic =
                    gatt?.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_RX_UUID)
                characteristic?.let {
                    // Enable notification for the characteristic.
                    gatt.setCharacteristicNotification(it, true)
                    // Get the descriptor for the characteristic's client characteristic configuration.
                    val descriptor = it.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                    // Enable notification for the descriptor.
                    descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                } ?: run {
                    Log.d(TAG, "Characteristic not found.")
                }
            } else {
                Log.d(TAG, "Service discovery failed.")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            characteristic?.value?.let {
                receivedData += it
            }
        }
    }

    fun startScanningForClosest() {
        Log.v(TAG, "a")

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val scanFilters = mutableListOf<ScanFilter>()
        scanFilters.add(ScanFilter.Builder().build())
        val scanSettings = ScanSettings.Builder().build()
        val picoDevices = mutableMapOf<String, Int>() // Map to store PICO devices and their signal strength (RSSI)

        Log.v(TAG, "b")

        // Start scanning for BLE devices
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Log.v(TAG, "device found")
                val device = result.device
                if (device.name?.startsWith("PICO") == true) {
                    // Store the device and its RSSI in the map
                    picoDevices[device.address] = result.rssi
                    Log.v(TAG, "pico found")
                }
                else{
                    Log.v(TAG, "pico not found")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "Scan failed with error code $errorCode")
            }
        }
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        scanner?.startScan(scanFilters, scanSettings, scanCallback)

        Log.v(TAG, scanner.toString())

        // Stop scanning after a fixed period of time
        val res = Handler().postDelayed({
            scanner?.stopScan(scanCallback)

            Log.v(TAG, "stopped")

            // Sort the PICO devices by signal strength
            val sortedPicoDevices = picoDevices.toList().sortedByDescending { (_, rssi) -> rssi }.toMap()

            Log.v(TAG, sortedPicoDevices.toString())

            // Connect to the PICO device with the strongest signal
            sortedPicoDevices.keys.firstOrNull()?.let { deviceAddress ->
                val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                bluetoothGatt = device?.connectGatt(context, false, gattCallback)
            }
        }, 5000)

        Log.v(TAG, res.toString())
    }

}