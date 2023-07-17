package com.example.myalarm.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentAlarmListBinding
import kotlinx.coroutines.launch

class AlarmListFragment : Fragment() {

    private var _bind: FragmentAlarmListBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentAlarmListBinding == null")

    private lateinit var alarmListAdapter: AlarmListAdapter

    private val viewModel by lazy {
        ViewModelProvider(this)[AlarmListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentAlarmListBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmListAdapter = AlarmListAdapter()

        bind.rvAlarmList.adapter = alarmListAdapter

        lifecycleScope.launch {
            viewModel.alarmList.collect {
                alarmListAdapter.submitList(it)
            }
        }

        alarmListAdapter.onLongClickListener = {
            viewModel.removeAlarm(it)
        }

        alarmListAdapter.onSwitchCompatListener = { alarm, isEnabled ->
            viewModel.changeEnabledState(alarm, isEnabled)
            Log.d("11111", alarm.enabled.toString())
        }

        alarmListAdapter.onClickListener = {
            requireActivity().supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, AlarmSettingFragment.newInstanceEditAlarm(it.id))
                .commit()
        }

        bind.ivAddAlarm.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, AlarmSettingFragment.newInstanceAddAlarm())
                .commit()
        }
    }
}