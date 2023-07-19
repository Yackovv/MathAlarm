package com.example.myalarm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.R
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

        val colorBlue = ContextCompat.getColor(requireContext(), R.color.blue_color)

        bind.tvLevelEasy.setOnClickListener{
            dropAllColors()
            setupLevel(Level.EASY)
            if(selectedLevel == Level.EASY){
                bind.tvLevelEasy.setTextColor(colorBlue)
            }
        }
        bind.tvLevelPrenormal.setOnClickListener{
            dropAllColors()
            setupLevel(Level.PRENORMAL)
            if(selectedLevel == Level.PRENORMAL){
                bind.tvLevelPrenormal.setTextColor(colorBlue)
            }
        }
        bind.tvLevelNormal.setOnClickListener{
            dropAllColors()
            setupLevel(Level.NORMAL)
            if(selectedLevel == Level.NORMAL){
                bind.tvLevelNormal.setTextColor(colorBlue)
            }
        }
        bind.tvLevelHard.setOnClickListener{
            dropAllColors()
            setupLevel(Level.HARD)
            if(selectedLevel == Level.HARD){
                bind.tvLevelHard.setTextColor(colorBlue)
            }
        }

    }

    private fun dropAllColors(){
        val colorWhite = ContextCompat.getColor(requireContext(), R.color.white_color)
        bind.tvLevelEasy.setTextColor(colorWhite)
        bind.tvLevelPrenormal.setTextColor(colorWhite)
        bind.tvLevelNormal.setTextColor(colorWhite)
        bind.tvLevelHard.setTextColor(colorWhite)
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