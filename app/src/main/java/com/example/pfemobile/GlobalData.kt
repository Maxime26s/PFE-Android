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
                if (value)
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
            }
        }

    var genFreq: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genFreq", value)
            }
        }

    var genOffset: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genOffset", value)
            }
        }

    var genType: String = "sin"
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genType", value)
            }
        }

    var genRise: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genRise", value)
            }
        }

    var genHigh: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genHigh", value)
            }
        }

    var genFall: Float = 0.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genFall", value)
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

    fun write(message: String) {
        if (::bleCommunicator.isInitialized){
            val chunked = message.chunked(20)
            Log.v("test", chunked.toString())
            for(chunk in chunked){
                Log.v("test", chunk)
                bleCommunicator.write(chunk)
                Thread.sleep(50)
            }

        }
    }
}
