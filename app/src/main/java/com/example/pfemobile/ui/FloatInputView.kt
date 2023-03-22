package com.example.pfemobile.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.pfemobile.R
import java.util.regex.Matcher
import java.util.regex.Pattern
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

    private var updatingText = false
    private var previousInputValue: Float? = null

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
        val decimals = typedArray.getInt(R.styleable.FloatInputView_decimals, 1)


        // Set the label text
        labelTextView.text = label

        val regex = "(([1-9][0-9]{0,9})|(0))((\\.[0-9]{0,$decimals})?)|(\\.)?"
        val pattern: Pattern = Pattern.compile(regex)
        floatInputEditText.filters = arrayOf<InputFilter>(MinMaxInputFilter(minValue, maxValue, pattern))

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

    private fun setValue(value: Float) {
        floatInputEditText.setText(value.toString())
        floatInputEditText.setSelection(floatInputEditText.text.length) // Move cursor to the end of text
    }

    inner class MinMaxInputFilter(private val min: Float, private val max: Float, private val pattern: Pattern) :
        InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val input = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend)
            val matcher: Matcher = pattern.matcher(input)

            if (!matcher.matches()) {
                return ""
            }

            val value = input.toFloatOrNull() ?: return null
            if (value > max) {
                setValue(max)
                listener.invoke(max)
                return ""
            } else if (value < min) {
                setValue(min)
                listener.invoke(min)
                return ""
            }

            listener.invoke(value)
            return null
        }
    }
}

