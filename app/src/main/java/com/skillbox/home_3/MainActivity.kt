package com.skillbox.home_3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.skillbox.home_3.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val COUNTER = "counterKey"
private const val MAX_PROGRESS = "progressKey"
private const val RUN_TIMER_KEY = "runTimerKey"
private const val BUTTON_START_VISIBLE = "buttonStartVisibleKey"
private const val BUTTON_STOP_VISIBLE = "buttonCancelVisibleKey"
private const val SLIDER_ENABLED = "sliderEnabledKey"

private var isRunTimer: Boolean = false

class MainActivity : AppCompatActivity() {

    private lateinit var bg: ActivityMainBinding
    private var currentProgress: Int = 10
    private var maxProgress: Int = 10
    private var buttonStart: Boolean = true
    private var buttonStop: Boolean = false
    private var sliderEnable: Boolean = true
    private var setCurrentProgress: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bg = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bg.root)

        savedInstanceState?.let { bundle ->
            currentProgress = bundle.getInt(COUNTER)
            maxProgress = bundle.getInt(MAX_PROGRESS)
            isRunTimer = bundle.getBoolean(RUN_TIMER_KEY)
            buttonStart = bundle.getBoolean(BUTTON_START_VISIBLE)
            buttonStop = bundle.getBoolean(BUTTON_STOP_VISIBLE)
            sliderEnable = bundle.getBoolean(SLIDER_ENABLED)
        }

        if (isRunTimer) {
            runTimer()
            setCurrentProgress = false
        } else {
            setCurrentProgress = true
            bg.progressBar.max = maxProgress
            bg.progressBar.progress = currentProgress
            bg.timer.text = currentProgress.toString()
        }

        bg.buttonStart.setOnClickListener {
            buttonStart = false
            buttonStop = true
            sliderEnable = false
            isRunTimer = true
            runTimer()
        }

        bg.buttonStop.setOnClickListener {
            buttonStart = true
            buttonStop = false
            sliderEnable = true
            isRunTimer = false
            setCurrentProgress = true
            changeVisibility(buttonStart, buttonStop, sliderEnable)
            Toast.makeText(this@MainActivity, getString(R.string.pause), Toast.LENGTH_SHORT).show()
        }

        bg.slider.addOnChangeListener { sliderItem, _, _ ->
            setProgressBar(sliderItem.value.toInt(), sliderItem.value.toInt(), setCurrentProgress)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(RUN_TIMER_KEY, isRunTimer)
        outState.putInt(COUNTER, currentProgress)
        outState.putInt(MAX_PROGRESS, maxProgress)
        outState.putBoolean(BUTTON_START_VISIBLE, buttonStart)
        outState.putBoolean(BUTTON_STOP_VISIBLE, buttonStop)
        outState.putBoolean(SLIDER_ENABLED, sliderEnable)
        super.onSaveInstanceState(outState)
    }

    private fun runTimer() {
        lifecycleScope.launch(Dispatchers.Main) {
            while (isRunTimer)
                if (currentProgress != 0) {
                    bg.timer.text = currentProgress.toString()
                    bg.progressBar.progress = currentProgress
                    currentProgress--
                    changeVisibility(buttonStart, buttonStop, sliderEnable)
                    delay(1000)
                } else {
                    isRunTimer = false
                    currentProgress = maxProgress
                    bg.timer.text = currentProgress.toString()
                    bg.progressBar.progress = currentProgress
                    buttonStart = true
                    buttonStop = false
                    sliderEnable = true
                    changeVisibility(buttonStart, buttonStop, sliderEnable)
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.finish),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun changeVisibility(buttonStart: Boolean, buttonStop: Boolean, sliderEnable: Boolean) {
        bg.buttonStart.isVisible = buttonStart
        bg.buttonStop.isVisible = buttonStop
        bg.slider.isEnabled = sliderEnable
    }

    private fun setProgressBar(progressCurrent: Int, progressMax: Int, setCurrent: Boolean) {
        bg.timer.text = progressCurrent.toString()
        bg.progressBar.max = progressMax
        bg.progressBar.progress = progressCurrent
        if (setCurrent) currentProgress = progressMax
    }
}