package com.example.myalarm.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentChoiceLevelBinding
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.presentation.viewmodels.AlarmSettingViewModel
import com.example.myalarm.presentation.activities.MainActivity
import kotlinx.coroutines.launch

class AlarmSelectLevelFragment : Fragment() {

    private var _bind: FragmentChoiceLevelBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentChoiceLevelBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[AlarmSettingViewModel::class.java]
    }


    private var selectedLevel = Level.EASY
    private var countOfQuestion = 1
    private var alarmId = UNDEFINED_ID
    private var screenMode = SCREEN_MODE_UNKNOWN
    private val colorBlue by lazy {
        ContextCompat.getColor(requireContext(), R.color.blue_color)
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
        _bind = FragmentChoiceLevelBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupScreenMode()

        lifecycleScope.launch {
            viewModel.questionFlow.collect {
                bind.tvExample.text = it.example
            }
        }

        val onBackPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showFragmentAlarmSetting()
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressed)
    }

    private fun setupScreenMode() {
        when (screenMode) {
            MODE_ADD -> launchModeAdd()
            MODE_EDIT -> launchModeEdit()
        }
    }


    private fun launchModeAdd() {
        bind.tvLevelEasy.setTextColor(colorBlue)
        viewModel.generateQuestion(Level.EASY)

        lifecycleScope.launch {
            AlarmSettingViewModel.levelFlow.collect {
                dropAllColors()
                setupLevel(it)
                setupColorSelectedLevel(selectedLevel)
            }
        }

        lifecycleScope.launch {
            AlarmSettingViewModel.countQuestionFlow.collect {
                bind.seekBar.progress = it
            }
        }
    }

    private fun launchModeEdit() {
        bind.seekBar.progress = countOfQuestion
        viewModel.generateQuestion(selectedLevel)
        dropAllColors()
        setupColorSelectedLevel(selectedLevel)

    }

    private fun setupColorSelectedLevel(level: Level) {
        with(bind) {
            when (level) {
                Level.EASY -> tvLevelEasy.setTextColor(colorBlue)
                Level.NORMAL -> tvLevelNormal.setTextColor(colorBlue)
                Level.HARD -> tvLevelHard.setTextColor(colorBlue)
                Level.PRO -> tvLevelPro.setTextColor(colorBlue)
            }
        }
    }

    private fun setupClickListeners() {

        bind.ivSave.setOnClickListener {
            val countQuestion = bind.seekBar.progress
            lifecycleScope.launch {
                AlarmSettingViewModel.levelFlow.emit(selectedLevel)
            }
            lifecycleScope.launch {
                AlarmSettingViewModel.countQuestionFlow.emit(countQuestion)
            }
            closeFragment()
        }

        bind.ivCancel.setOnClickListener {
            closeFragment()
        }

        bind.tvLevelEasy.setOnClickListener {
            dropAllColors()
            setupLevel(Level.EASY)
            viewModel.generateQuestion(Level.EASY)
            if (selectedLevel == Level.EASY) {
                bind.tvLevelEasy.setTextColor(colorBlue)
            }
        }
        bind.tvLevelNormal.setOnClickListener {
            dropAllColors()
            setupLevel(Level.NORMAL)
            viewModel.generateQuestion(Level.NORMAL)
            if (selectedLevel == Level.NORMAL) {
                bind.tvLevelNormal.setTextColor(colorBlue)
            }
        }
        bind.tvLevelHard.setOnClickListener {
            dropAllColors()
            setupLevel(Level.HARD)
            viewModel.generateQuestion(Level.HARD)
            if (selectedLevel == Level.HARD) {
                bind.tvLevelHard.setTextColor(colorBlue)
            }
        }
        bind.tvLevelPro.setOnClickListener {
            dropAllColors()
            setupLevel(Level.PRO)
            viewModel.generateQuestion(Level.PRO)
            if (selectedLevel == Level.PRO) {
                bind.tvLevelPro.setTextColor(colorBlue)
            }
        }
    }

    private fun dropAllColors() {
        val colorWhite = ContextCompat.getColor(requireContext(), R.color.white_color)
        bind.tvLevelEasy.setTextColor(colorWhite)
        bind.tvLevelNormal.setTextColor(colorWhite)
        bind.tvLevelHard.setTextColor(colorWhite)
        bind.tvLevelPro.setTextColor(colorWhite)
    }


    private fun setupLevel(level: Level) {
        selectedLevel = level
    }

    private fun closeFragment() {
        showFragmentAlarmSetting()
        parentFragmentManager.popBackStack()
    }

    private fun showFragmentAlarmSetting() {
        val activity = requireActivity() as MainActivity
        parentFragmentManager.beginTransaction().show(activity.fragment).commit()
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

            if (!arguments.containsKey(SELECTED_LEVEL)) {
                throw RuntimeException("Selected level is absent")
            }
            val tempLevel = arguments.getParcelable<Level>(SELECTED_LEVEL)
                ?: throw RuntimeException("Unknown level")
            selectedLevel = tempLevel

            if (!arguments.containsKey(COUNT_OF_QUESTION)) {
                throw RuntimeException("Count of question is absent")
            }
            countOfQuestion = arguments.getInt(COUNT_OF_QUESTION)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    companion object {

        private const val UNDEFINED_ID = 0
        private const val ALARM_ID = "alarm_id"
        private const val SCREEN_MODE = "screen_mode"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val SCREEN_MODE_UNKNOWN = ""
        private const val SELECTED_LEVEL = "selected_level"
        private const val COUNT_OF_QUESTION = "count_of_question"

        fun newInstanceAdd(): AlarmSelectLevelFragment {
            return AlarmSelectLevelFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEdit(
            alarmId: Int,
            level: Level,
            countOfQuestion: Int
        ): AlarmSelectLevelFragment {
            return AlarmSelectLevelFragment().apply {
                arguments = Bundle().apply {
                    putInt(ALARM_ID, alarmId)
                    putString(SCREEN_MODE, MODE_EDIT)
                    putParcelable(SELECTED_LEVEL, level)
                    putInt(COUNT_OF_QUESTION, countOfQuestion)
                }
            }
        }

    }
}