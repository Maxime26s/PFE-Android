package com.example.pfemobile.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.pfemobile.R

class SwitchView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val labelView: TextView
    private val switchView: SwitchCompat

    init {
        LayoutInflater.from(context).inflate(R.layout.switch_view, this, true)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SwitchLabelView)
        val labelText = attributes.getString(R.styleable.SwitchLabelView_switchLabel)
        val isChecked = attributes.getBoolean(R.styleable.SwitchLabelView_isChecked, false)
        attributes.recycle()

        labelView = findViewById(R.id.labelView)
        switchView = findViewById(R.id.switchView)

        labelView.text = labelText
        switchView.isChecked = isChecked

        switchView.setOnCheckedChangeListener { _, isChecked ->
            // Do something when the checked state changes
        }
    }

    var isChecked: Boolean
        get() = switchView.isChecked
        set(value) { switchView.isChecked = value }

    var labelText: String
        get() = labelView.text.toString()
        set(value) { labelView.text = value }

    fun setOnCheckedChangeListener(listener: (CompoundButton, Boolean) -> Unit) {
        switchView.setOnCheckedChangeListener(listener)
    }
}