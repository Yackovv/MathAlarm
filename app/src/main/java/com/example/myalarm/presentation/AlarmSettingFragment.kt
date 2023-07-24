package com.example.myalarm.presentation

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentAlarmSettingBinding
import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class AlarmSettingFragment : Fragment() {

    private var _bind: FragmentAlarmSettingBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentAlarmSettingBinding == null")

    private var screenMode = SCREEN_MODE_UNKNOWN
    private var alarmId = UNDEFINED_ID

    private val viewModel by lazy {
        ViewModelProvider(this)[AlarmSettingViewModel::class.java]
    }

    private val backgroundCircle by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.circle_day)
    }

    private var uri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    private var countOfQuestion by Delegates.notNull<Int>()

    private fun log(tag: String) {
        Log.d("AlarmSettingFragment", tag)
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
        log("onCreateView")
        _bind = FragmentAlarmSettingBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScreenMode()
        setupClickListeners()


        lifecycleScope.launch {
            AlarmSettingViewModel.countQuestionFlow.collect {
                countOfQuestion = it
            }
        }
        lifecycleScope.launch {
            AlarmSettingViewModel.levelFlow.collect {
                setupLevelOnTextView(it)
            }
        }
    }

    private fun setupScreenMode() {
        when (screenMode) {
            MODE_ADD -> launchModeAdd()
            MODE_EDIT -> launchModeEdit()
        }
    }

    private fun launchModeAdd() {
        bind.tvLevelDescription.text = getString(R.string.level_easy)
        bind.tvMusicDescription.text = getRingtoneTitle(uri)

        bind.ivSave.setOnClickListener {
            viewModel.addAlarm(
                alarmTime = bind.tvTime.text.toString(),
                level = setupLevelOnAlarm(bind.tvLevelDescription.text.toString()),
                countQuestion = countOfQuestion,
                ringtoneUriString = uri.toString(),
                vibration = bind.alarmSwitch.isChecked,
                monday = checkSelectedDay(bind.tvMonday),
                tuesday = checkSelectedDay(bind.tvTuesday),
                wednesday = checkSelectedDay(bind.tvWednesday),
                thursday = checkSelectedDay(bind.tvThursday),
                friday = checkSelectedDay(bind.tvFriday),
                saturday = checkSelectedDay(bind.tvSaturday),
                sunday = checkSelectedDay(bind.tvSunday)
            )
            parentFragmentManager.popBackStack()
        }

        bind.llLevel.setOnClickListener {
            val fragment = AlarmSelectLevelFragment.newInstanceAdd()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()

            hideFragment()
        }
    }

    private fun launchModeEdit() {
        viewModel.getAlarm(alarmId)
        lifecycleScope.launch {
            viewModel.alarmFlow.collect {

                log("collect modeEdit")

                bind.tvTime.text = it.alarmTime
                uri = it.ringtoneUriString.toUri()
                bind.tvMusicDescription.text = getRingtoneTitle(uri)
                checkActiveDay(it)
                setupLevelOnTextView(it.level)
                bind.alarmSwitch.isChecked = it.vibration
            }
        }

        bind.ivSave.setOnClickListener {
            viewModel.editAlarm(
                alarmTime = bind.tvTime.text.toString(),
                level = setupLevelOnAlarm(bind.tvLevelDescription.text.toString()),
                countQuestion = countOfQuestion,
                ringtoneUriString = uri.toString(),
                vibration = bind.alarmSwitch.isChecked,
                monday = checkSelectedDay(bind.tvMonday),
                tuesday = checkSelectedDay(bind.tvTuesday),
                wednesday = checkSelectedDay(bind.tvWednesday),
                thursday = checkSelectedDay(bind.tvThursday),
                friday = checkSelectedDay(bind.tvFriday),
                saturday = checkSelectedDay(bind.tvSaturday),
                sunday = checkSelectedDay(bind.tvSunday)
            )
            parentFragmentManager.popBackStack()
        }

        bind.llLevel.setOnClickListener {
            val levelFromTvLevel = setupLevelOnAlarm(bind.tvLevelDescription.text.toString())
            val fragment =
                AlarmSelectLevelFragment.newInstanceEdit(alarmId, levelFromTvLevel, countOfQuestion)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack("setting")
                .commit()

            hideFragment()
        }
    }


    private fun setupLevelOnTextView(level: Level) {
        with(bind) {
            when (level) {
                Level.EASY -> tvLevelDescription.text = getString(R.string.level_easy)
                Level.NORMAL -> tvLevelDescription.text = getString(R.string.level_normal)
                Level.HARD -> tvLevelDescription.text = getString(R.string.level_hard)
                Level.PRO -> tvLevelDescription.text = getString(R.string.level_pro)
            }
        }
    }

    private fun setupLevelOnAlarm(levelString: String): Level {
        return when (levelString) {
            getString(R.string.level_easy) -> Level.EASY
            getString(R.string.level_normal) -> Level.NORMAL
            getString(R.string.level_hard) -> Level.HARD
            getString(R.string.level_pro) -> Level.PRO
            else -> throw RuntimeException("Unknown level $levelString")
        }
    }

    private fun checkSelectedDay(day: TextView): Boolean {
        return day.background.constantState == backgroundCircle?.constantState
    }

    private fun checkActiveDay(alarm: Alarm) {
        if (alarm.monday) bind.tvMonday.background = backgroundCircle
        if (alarm.tuesday) bind.tvTuesday.background = backgroundCircle
        if (alarm.wednesday) bind.tvWednesday.background = backgroundCircle
        if (alarm.thursday) bind.tvThursday.background = backgroundCircle
        if (alarm.friday) bind.tvFriday.background = backgroundCircle
        if (alarm.saturday) bind.tvSaturday.background = backgroundCircle
        if (alarm.sunday) bind.tvSunday.background = backgroundCircle
    }

    private fun setActiveDay(day: TextView) {

        val backgroundClear = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        if (day.background.constantState == backgroundCircle?.constantState) {
            day.background = backgroundClear
        } else {
            day.background = backgroundCircle
        }
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
        bind.llMusic.setOnClickListener {
            openRingtonePicker()
        }
        bind.tvTime.setOnClickListener {
            setupTime()
        }
        bind.ivCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupTime() {
        val str = bind.tvTime.text.toString()
        val hours = str.substringBefore(" ").toInt()
        val minutes = str.substringAfterLast(" ").toInt()
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTimeFormat(CLOCK_24H)
                .setHour(hours)
                .setMinute(minutes)
                .build()

        picker.show(requireActivity().supportFragmentManager.beginTransaction(), "Hello")
        picker.addOnPositiveButtonClickListener {
            bind.tvTime.text = String.format("%02d : %02d", picker.hour, picker.minute)
        }
    }

    private fun hideFragment() {
        parentFragmentManager.beginTransaction().hide(this).commit()
        val fragment = requireActivity() as MainActivity
        fragment.fragment = this
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

    private fun openRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите мелодию для будильника")
        startActivityForResult(intent, PICK_RINGTONE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_RINGTONE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri =
                data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            bind.tvMusicDescription.text = getRingtoneTitle(uri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }
    }

    private fun getRingtoneTitle(uri: Uri?): String {
        val context = requireContext()
        return if (uri != null) {
            RingtoneManager.getRingtone(context, uri).getTitle(context)
        } else {
            RingtoneManager.getRingtone(context, this.uri).getTitle(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    companion object {

        private const val PICK_RINGTONE_REQUEST_CODE = 1
        private const val UNDEFINED_ID = 0
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