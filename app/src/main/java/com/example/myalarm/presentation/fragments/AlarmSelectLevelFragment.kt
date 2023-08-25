package com.example.myalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentChoiceLevelBinding
import com.example.domain.domain.enteties.Level
import com.example.myalarm.logg
import com.example.myalarm.presentation.AlarmApplication
import com.example.myalarm.presentation.viewmodels.AlarmSettingViewModel
import com.example.myalarm.presentation.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmSelectLevelFragment : Fragment() {

    private var _bind: FragmentChoiceLevelBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentChoiceLevelBinding == null")

    @Inject
    lateinit var factory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, factory)[AlarmSettingViewModel::class.java]
    }
    private var selectedLevel = Level.EASY
    private var alarmId = UNDEFINED_ID
    private val colorBlue by lazy {
        ContextCompat.getColor(requireContext(), R.color.blue_color)
    }
    private val component by lazy {
        (requireActivity().application as AlarmApplication).component
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
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

        viewModel.getAlarm(alarmId)

        setupClickListeners()

        lifecycleScope.launch {
            viewModel.questionFlow.collect {
                bind.tvExample.text = it.example
            }
        }

        lifecycleScope.launch {
            viewModel.alarmFlow.collect {
                bind.seekBar.progress = it.countQuestion
                dropAllColors()
                setupColorSelectedLevel(it.level)
                viewModel.generateQuestion(it.level)
            }
        }
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
            logg("alarmId = $alarmId, level = $selectedLevel, count question = $countQuestion")
            viewModel.setupLevelAndCountOfQuestion(selectedLevel, countQuestion)
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
        parentFragmentManager.popBackStack()
    }


    private fun parseArguments() {

        val arguments = requireArguments()

        if (!arguments.containsKey(ALARM_ID)) {
            throw RuntimeException("Alarm id is absent")
        }
        alarmId = arguments.getInt(ALARM_ID)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    companion object {

        private const val UNDEFINED_ID = 0
        private const val ALARM_ID = "alarm_id"

        fun newInstanceEdit(alarmId: Int): AlarmSelectLevelFragment {
            return AlarmSelectLevelFragment().apply {
                arguments = Bundle().apply {
                    putInt(ALARM_ID, alarmId)
                }
            }
        }

    }
}