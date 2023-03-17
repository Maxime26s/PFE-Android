package com.example.pfemobile.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pfemobile.R

class FloatInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val floatInputEditText: EditText


    init {
        LayoutInflater.from(context).inflate(R.layout.float_input_view, this, true)
        labelTextView = findViewById(R.id.label_text_view)
        floatInputEditText = findViewById(R.id.float_input_edit_text)

        // Get the label attribute value from the AttributeSet
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatInputView)
        val label = typedArray.getString(R.styleable.FloatInputView_floatLabel)

        // Set the label text
        labelTextView.text = label

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

}
