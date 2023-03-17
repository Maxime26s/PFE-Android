package com.example.pfemobile.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.pfemobile.GlobalData
import com.example.pfemobile.R

class StepperInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val label: String?
    private val values: Map<String, Float>
    private var valueIndex = 0
        set(value) {
            if (value != field) {
                field = value
                listener.invoke(getCurrentValue())
                valueText.text = values.keys.toList()[valueIndex]
            }
        }
    private val labelText: TextView
    private val minusButton: Button
    private val valueText: TextView
    private val plusButton: Button

    private var listener: (value: Float) -> Unit = { }

    init {
        LayoutInflater.from(context).inflate(R.layout.stepper_input_view, this, true)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepperInputView)

        label = typedArray.getString(R.styleable.StepperInputView_stepperLabel)
        values = parseValues(typedArray.getString(R.styleable.StepperInputView_mapValues))

        typedArray.recycle()

        labelText = findViewById(R.id.label_text_view)
        minusButton = findViewById(R.id.minus_button)
        valueText = findViewById(R.id.value_text_view)
        plusButton = findViewById(R.id.plus_button)

        labelText.text = label
        valueText.text = values.keys.toList()[valueIndex]

        minusButton.setOnClickListener {
            if (valueIndex > 0) {
                valueIndex--
            }
        }

        plusButton.setOnClickListener {
            if (valueIndex < values.size - 1) {
                valueIndex++
            }
        }
    }

    private fun parseValues(valuesString: String?): Map<String, Float> {
        val valuesMap = mutableMapOf<String, Float>()
        if (!valuesString.isNullOrEmpty()) {
            val valuesArray = valuesString.split("|").toTypedArray()
            for (valuePair in valuesArray) {
                val pair = valuePair.split(":").toTypedArray()
                if (pair.size == 2) {
                    valuesMap[pair[0]] = pair[1].toFloat()
                }
            }
        }
        return valuesMap
    }

    private fun getCurrentValue(): Float {
        return values.values.toList()[valueIndex]
    }

    private fun getIndexFromValue(value: Float): Int {
        return values.values.indexOf(value)
    }

    fun setIndexFromValue(value: Float){
        valueIndex = getIndexFromValue(value)
    }

    fun reset(){
        valueIndex = 0
        listener.invoke(getCurrentValue())
    }

    fun setOnValueChangedListener(listener: (value: Float) -> Unit) {
        this.listener = listener
    }
}
