package com.example.myalarm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentChoiceLevelBinding
import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import kotlinx.coroutines.launch

class AlarmChoiceLevelFragment : Fragment() {

    private var _bind: FragmentChoiceLevelBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentChoiceLevelBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[AlarmViewModel::class.java]
    }

    private var selectedLevel = Level.EASY
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
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.questionFlow.collect {
                    bind.tvExample.text = it.example
                }
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
        bind.tvLevelEasy.setTextColor(colorBlue)
        viewModel.generateQuestion(Level.EASY)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                AlarmViewModel.levelFlow.collect {
                    setupLevel(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                AlarmViewModel.countQuestionFlow.collect {
                    bind.seekBar.progress = it
                }
            }
        }
    }

    private fun launchModeEdit() {
        viewModel.getAlarm(alarmId)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.alarmFlow.collect {
                    bind.seekBar.progress = it.countQuestion
                    setupLevel(it.level)
                    viewModel.generateQuestion(selectedLevel)
                    dropAllColors()
                    setupColorSelectedLevel(it)
                }
            }
        }
    }

    private fun setupColorSelectedLevel(it: Alarm) {
        with(bind) {
            when (it.level) {
                Level.EASY -> tvLevelEasy.setTextColor(colorBlue)
                Level.NORMAL -> tvLevelPrenormal.setTextColor(colorBlue)
                Level.HARD -> tvLevelNormal.setTextColor(colorBlue)
                Level.PRO -> tvLevelHard.setTextColor(colorBlue)
            }
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

    private fun setupClickListeners() {

        bind.ivSave.setOnClickListener {
            val countQuestion = bind.seekBar.progress
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    AlarmViewModel.levelFlow.emit(selectedLevel)
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    AlarmViewModel.countQuestionFlow.emit(countQuestion)
                }
            }
            requireActivity().supportFragmentManager.popBackStack()
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
        bind.tvLevelPrenormal.setOnClickListener {
            dropAllColors()
            setupLevel(Level.NORMAL)
            viewModel.generateQuestion(Level.NORMAL)
            if (selectedLevel == Level.NORMAL) {
                bind.tvLevelPrenormal.setTextColor(colorBlue)
            }
        }
        bind.tvLevelNormal.setOnClickListener {
            dropAllColors()
            setupLevel(Level.HARD)
            viewModel.generateQuestion(Level.HARD)
            if (selectedLevel == Level.HARD) {
                bind.tvLevelNormal.setTextColor(colorBlue)
            }
        }
        bind.tvLevelHard.setOnClickListener {
            dropAllColors()
            setupLevel(Level.PRO)
            viewModel.generateQuestion(Level.PRO)
            if (selectedLevel == Level.PRO) {
                bind.tvLevelHard.setTextColor(colorBlue)
            }
        }
    }

    private fun dropAllColors() {
        val colorWhite = ContextCompat.getColor(requireContext(), R.color.white_color)
        bind.tvLevelEasy.setTextColor(colorWhite)
        bind.tvLevelPrenormal.setTextColor(colorWhite)
        bind.tvLevelNormal.setTextColor(colorWhite)
        bind.tvLevelHard.setTextColor(colorWhite)
    }


    private fun setupLevel(level: Level) {
        selectedLevel = level
    }

    private fun closeFragment() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {

        private const val UNDEFINED_ID = 0
        private const val ALARM_ID = "alarm_id"
        private const val SCREEN_MODE = "screen_mode"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val SCREEN_MODE_UNKNOWN = ""

        fun newInstanceAdd(alarmId: Int): AlarmChoiceLevelFragment {
            return AlarmChoiceLevelFragment().apply {
                arguments = Bundle().apply {
                    putInt(ALARM_ID, alarmId)
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEdit(alarmId: Int): AlarmChoiceLevelFragment {
            return AlarmChoiceLevelFragment().apply {
                arguments = Bundle().apply {
                    putInt(ALARM_ID, alarmId)
                    putString(SCREEN_MODE, MODE_EDIT)
                }
            }
        }

    }
}