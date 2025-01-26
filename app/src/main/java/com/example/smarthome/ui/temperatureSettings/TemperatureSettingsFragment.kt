package com.example.smarthome.ui.temperatureSettings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.smarthome.R
import com.example.smarthome.R.drawable.custom_segment_buttons
import com.example.smarthome.databinding.FragmentTemperatureSettingsBinding

class TemperatureSettingsFragment : Fragment() {

    private var _binding: FragmentTemperatureSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemperatureSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        return root
    }

    private fun setupUI() {
        val btnHot = binding.btnHot
        val btnCold = binding.btnCold
        val btnNormal = binding.btnNormal
        val slider = binding.sliderTempSettings
        val textInsideCircle = binding.textInsideCircle

        val buttons = listOf(btnHot, btnCold, btnNormal)

        btnHot.setOnClickListener {
            updateButtonState(buttons, btnHot)
            updateSliderValue(slider, textInsideCircle, 22f)
        }

        btnCold.setOnClickListener {
            updateButtonState(buttons, btnCold)
            updateSliderValue(slider, textInsideCircle, 5f)
        }

        btnNormal.setOnClickListener {
            updateButtonState(buttons, btnNormal)
            updateSliderValue(slider, textInsideCircle, 16f)
        }

        buttons.forEach { it.background = requireContext().getDrawable(custom_segment_buttons) }

        slider.value = 30f
        slider.valueTo = 35f
        slider.valueFrom = -5f
        slider.stepSize = 1f
        textInsideCircle.text = slider.value.toInt().toString()

        slider.addOnChangeListener { _, value, _ ->
            textInsideCircle.text = value.toInt().toString()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateButtonState(buttons: List<Button>, activeButton: Button) {
        buttons.forEach { button ->
            if (button == activeButton) {
                button.background = requireContext().getDrawable(R.drawable.custom_segment_buttons_pressed)
            } else {
                button.background = requireContext().getDrawable(custom_segment_buttons)
            }
        }
    }

    private fun updateSliderValue(slider: com.google.android.material.slider.Slider, textView: android.widget.TextView, value: Float) {
        slider.value = value
        textView.text = value.toInt().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
