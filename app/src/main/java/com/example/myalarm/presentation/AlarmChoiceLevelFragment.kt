package com.example.myalarm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myalarm.databinding.FragmentChoiceLevelBinding
import com.example.myalarm.domain.enteties.Level

class AlarmChoiceLevelFragment : Fragment() {

    private var _bind: FragmentChoiceLevelBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentChoiceLevelBinding == null")

    var levelCallback: ((Int, Level) -> Unit)? = null


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
            val a = bind.seekBar.progress
            levelCallback?.invoke(a, Level.HARD)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    companion object {

        fun newInstance(): AlarmChoiceLevelFragment {
            return AlarmChoiceLevelFragment()
        }
    }
}