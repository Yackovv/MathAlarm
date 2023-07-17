package com.example.myalarm.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentAlarmSettingBinding
import com.example.myalarm.domain.enteties.Alarm

class AlarmSettingFragment : Fragment() {

    private var _bind: FragmentAlarmSettingBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentAlarmSettingBinding == null")

    private var screenMode = SCREEN_MODE_UNKNOWN
    private var alarmId = Alarm.UNDEFINED_ID

    private val viewModel by lazy {
        ViewModelProvider(this)[AlarmViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentAlarmSettingBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScreenMode()
        setupClickListeners()
    }

    private fun setupScreenMode() {
        when (screenMode) {
            MODE_ADD -> launchModeAdd()
            MODE_EDIT -> launchModeEdit()
        }
    }

    private fun launchModeAdd() {
        //TODO
    }

    private fun launchModeEdit() {
        viewModel.getAlarm(alarmId)

    }

    private fun setActiveDay(day: TextView) {

        val backgroundCircle = ContextCompat.getDrawable(requireContext(), R.drawable.circle_day)
        val backgroundClear = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        if (day.background.constantState == backgroundCircle?.constantState) {
            day.background = backgroundClear
        } else {
            day.background = backgroundCircle
        }
    }

    private fun createAlarm() {
        val hours = bind.etHours.text.toString()
        val minutes = bind.etMinutes.text.toString()
        // TODO
    }

    private fun setupClickListeners() {
        bind.tvMonday.setOnClickListener {
            setActiveDay(bind.tvMonday)
        }
        bind.tvTuesday.setOnClickListener {
            setActiveDay(bind.tvTuesday)
        }
        bind.tvWednesday.setOnClickListener {
            setActiveDay(bind.tvWednesday)
        }
        bind.tvThursday.setOnClickListener {
            setActiveDay(bind.tvThursday)
        }
        bind.tvFriday.setOnClickListener {
            setActiveDay(bind.tvFriday)
        }
        bind.tvSaturday.setOnClickListener {
            setActiveDay(bind.tvSaturday)
        }
        bind.tvSunday.setOnClickListener {
            setActiveDay(bind.tvSunday)
        }
        bind.llLevel.setOnClickListener {
            val fragment = AlarmChoiceLevelFragment.newInstance()
            fragment.levelCallback = { count, level ->
                bind.tvLevelDescription.text = level.toString()
                Log.d("11111", "$count, $level")
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun parseArguments() {

        val arguments = requireArguments()
        if (!arguments.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Screen mode is absent")
        }
        val scMode = arguments.getString(SCREEN_MODE)
        if (scMode != MODE_ADD && scMode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $scMode")
        }
        screenMode = scMode
        if (screenMode == MODE_EDIT) {
            if (!arguments.containsKey(ALARM_ID)) {
                throw RuntimeException("Alarm id is absent")
            }
            alarmId = arguments.getInt(ALARM_ID)
        }

    }

    companion object {

        private const val ALARM_ID = "alarm_id"
        private const val SCREEN_MODE = "screen_mode"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val SCREEN_MODE_UNKNOWN = ""

        fun newInstanceEditAlarm(alarmId: Int): AlarmSettingFragment {
            return AlarmSettingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ALARM_ID, alarmId)
                    putString(SCREEN_MODE, MODE_EDIT)
                }
            }
        }

        fun newInstanceAddAlarm(): AlarmSettingFragment {
            return AlarmSettingFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }
    }

}