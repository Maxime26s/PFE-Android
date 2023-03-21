package com.example.pfemobile.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pfemobile.R
import kotlin.math.pow
import kotlin.math.roundToInt

class FloatInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val floatInputEditText: EditText

    private val minValue: Float
    private val maxValue: Float
    private val decimalsCoefficient: Float

    private var listener: (value: Float) -> Unit = { }

    init {
        LayoutInflater.from(context).inflate(R.layout.float_input_view, this, true)
        labelTextView = findViewById(R.id.label_text_view)
        floatInputEditText = findViewById(R.id.float_input_edit_text)

        // Get the label attribute value from the AttributeSet
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatInputView)
        val label = typedArray.getString(R.styleable.FloatInputView_floatLabel)
        minValue = typedArray.getFloat(R.styleable.FloatInputView_minValue, 0f)
        maxValue = typedArray.getFloat(R.styleable.FloatInputView_maxValue, 100f)
        val decimals = typedArray.getInt(R.styleable.FloatInputView_decimals, 2)
        decimalsCoefficient = 10f.pow(decimals)

        // Set the label text
        labelTextView.text = label

        // Add TextWatcher to limit input to minimum and maximum values
        floatInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val inputString = s.toString()
                    val inputValue = inputString.toFloatOrNull()
                    if (inputValue != null) {
                        if (inputValue < minValue) {
                            s.replace(0, s.length, minValue.toString())
                        } else if (inputValue > maxValue) {
                            s.replace(0, s.length, maxValue.toString())
                        } else if (
                            inputString.indexOf(".") != -1 && inputString.indexOf(".") < inputString.length - 3 ||
                            inputString.indexOf(",") != -1 && inputString.indexOf(",") < inputString.length - 3
                        ) {
                            val roundedValue =
                                ((inputValue * decimalsCoefficient).toInt() / decimalsCoefficient).toString()
                            s.replace(
                                0,
                                s.length,
                                roundedValue
                            )
                        }

                        listener.invoke(s.toString().toFloat())
                    }
                }
            }
        })

        // Recycle the typed array
        typedArray.recycle()
    }

    fun setLabel(label: String) {
        labelTextView.text = label
    }

    fun setFloatValue(value: Float) {
        floatInputEditText.setText(value.toString())
    }

    fun getFloatValue(): Float {
        return floatInputEditText.text.toString().toFloat()
    }

    fun setOnValueChangedListener(listener: (value: Float) -> Unit) {
        this.listener = listener
    }
}
