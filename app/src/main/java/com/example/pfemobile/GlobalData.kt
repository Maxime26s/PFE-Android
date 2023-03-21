package com.example.pfemobile

import android.content.Context
import android.graphics.PointF
import android.util.Log

object GlobalData {
    private const val TAG = "GlobalData"

    private val listeners = mutableMapOf<String, (value: Any) -> Unit>()

    lateinit var bleCommunicator: BleCommunicator

    fun init(context: Context) {
        // Initialize the BleCommunicator
        bleCommunicator = BleCommunicator(
            context = context,
            onMessageReceived = { message ->
                // Handle the received message
                Log.d(TAG, "Message received: $message")
            }
        )
    }

    var bleConnected: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyListener("bleConnected", value)
            }
        }

    var chan1State: Boolean = true
        set(value) {
            if (value != field) {
                field = value
                notifyListener("chan1State", value)
            }
        }

    var chan1Data: List<PointF> = emptyList()
        set(value) {
            if (value != field) {
                field = value
                notifyListener("chan1Data", value)
            }
        }

    var chan2State: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyListener("chan2State", value)
            }
        }

    var chan2Data: List<PointF> = emptyList()
        set(value) {
            if (value != field) {
                field = value
                notifyListener("chan2Data", value)
            }
        }

    var timePerCell: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("timePerCell", value)
                write("ECHO $value")
            }
        }

    var chan1VoltPerCell: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("chan1VoltPerCell", value)
            }
        }

    var chan2VoltPerCell: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("chan2VoltPerCell", value)
            }
        }

    var sourceState: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyListener("sourceState", value)
            }
        }

    var sourceVoltage: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("sourceVoltage", value)
            }
        }

    var genState: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genState", value)
                if(value)
                    write("GEN START")
                else
                    write("GEN STOP")
            }
        }

    var genAmp: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genAmp", value)
                write("GEN SET_AMP $value")
            }
        }

    var genFreq: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genFreq", value)
                write("GEN SET_FREQ $value")
            }
        }

    var genOffset: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genOffset", value)
                write("GEN SET_OFFSET $value")
            }
        }

    fun addListener(key: String, listener: (value: Any) -> Unit) {
        listeners[key] = listener
    }

    fun removeListener(key: String) {
        listeners.remove(key)
    }

    private fun notifyListener(key: String, value: Any) {
        listeners[key]?.invoke(value)
    }

    private fun write(message: String){
        if(::bleCommunicator.isInitialized)
            bleCommunicator.write(message)
    }
}
