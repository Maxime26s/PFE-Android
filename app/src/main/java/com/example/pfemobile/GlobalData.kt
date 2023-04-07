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

                if(message.startsWith("array('H', [")){
                    val arr = stringToArray(message)
                    Log.v("fdsafds", timePerCell.toString())
                    val xunit = timePerCell*12 / arr.size;

                    if(!chan2State){
                        val newArr1 = mutableListOf<PointF>()

                        for(i in arr.indices){
                            val x = xunit * i
                            val y = (arr[i] - 2048f) / 2048f * 15f
                            newArr1.add(PointF(x,y))
                        }

                        chan1Data = newArr1
                    }
                    else{
                        val newArr1 = mutableListOf<PointF>()
                        val newArr2 = mutableListOf<PointF>()

                        for(i in arr.indices){
                            val x = xunit * (i/2) * 2f
                            val y = (arr[i] - 2048f) / 2048f * 15f
                            if(i%2 == 0) newArr1.add(PointF(x,y))
                            else newArr2.add(PointF(x,y))
                        }

                        chan1Data = newArr1
                        chan2Data = newArr2
                    }
                }
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

    var sourceVoltage: Float = 5.0f
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

    var genAmp: Float = 0.2f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genAmp", value)
            }
        }

    var genFreq: Float = 1000.0f
        set(value) {
            if (value != field) {
                field = value
                notifyListener("genFreq", value)
                Log.v("wtf", value.toString())
            }
        }

    var genOffset: Float = 0.5f
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
        if (::bleCommunicator.isInitialized) {
            bleCommunicator.write(message)
        }
    }

    private fun stringToArray(input: String): IntArray {
        val parsedArray = input.substring(11, input.length-1)

        Log.v("dsa", parsedArray)

        // Remove the square brackets and split the string into individual numbers
        val numbers = parsedArray.substring(1, parsedArray.length-1).split(", ")

        // Convert each string number into an integer and add it to a new array
        val output = IntArray(numbers.size) { index -> numbers[index].toInt() }

        return output
    }

}
