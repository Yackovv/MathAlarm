package com.example.myalarm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.databinding.FragmentChoiceLevelBinding
import com.example.myalarm.domain.enteties.Level
import kotlinx.coroutines.launch

class AlarmChoiceLevelFragment : Fragment() {

    private var _bind: FragmentChoiceLevelBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentChoiceLevelBinding == null")

    private var selectedLevel = Level.EASY

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

        bind.ivSave.setOnClickListener {
            val countQuestion = bind.seekBar.progress
            lifecycleScope.launch {
                AlarmViewModel.level.emit(selectedLevel)
            }
            lifecycleScope.launch {
                AlarmViewModel.countQuestion.emit(countQuestion)
            }
            requireActivity().supportFragmentManager.popBackStack()
        }

        bind.tvLevelEasy.setOnClickListener{
            setupLevel(Level.EASY)
        }
        bind.tvLevelPrenormal.setOnClickListener{
            setupLevel(Level.PRENORMAL)
        }
        bind.tvLevelNormal.setOnClickListener{
            setupLevel(Level.NORMAL)
        }
        bind.tvLevelHard.setOnClickListener{
            setupLevel(Level.HARD)
        }

    }

    private fun setupLevel(level: Level){
        selectedLevel = level
    }


    companion object {

        fun newInstance(): AlarmChoiceLevelFragment {
            return AlarmChoiceLevelFragment()
        }
    }
}