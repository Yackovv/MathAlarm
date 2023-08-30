package com.example.myalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myalarm.R
import com.example.myalarm.databinding.FragmentAlarmListBinding
import com.example.myalarm.presentation.AlarmApplication
import com.example.myalarm.presentation.AlarmListAdapter
import com.example.myalarm.presentation.viewmodels.AlarmListViewModel
import com.example.myalarm.presentation.viewmodels.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmListFragment : Fragment() {

    private var _bind: FragmentAlarmListBinding? = null
    private val bind
        get() = _bind ?: throw RuntimeException("FragmentAlarmListBinding == null")

    private val alarmListAdapter by lazy {
        AlarmListAdapter()
    }

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[AlarmListViewModel::class.java]
    }
    private val component by lazy {
        (requireActivity().application as AlarmApplication).component
    }
    private lateinit var job: Job

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
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

        bind.rvAlarmList.adapter = alarmListAdapter
        flowCollects()
        setupClickListeners()
        setupSwipeListener()
    }

    private fun setupSwipeListener() {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val alarm = alarmListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.removeAlarm(alarm)
                viewModel.turnOffAlarm(requireContext(), alarm.id)
            }
        }
        val a = ItemTouchHelper(callback)
        a.attachToRecyclerView(bind.rvAlarmList)
    }

    private fun setupClickListeners() {
        alarmListAdapter.onClickListener = {
            requireActivity().supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_container, AlarmSettingFragment.newInstanceAddAlarm(it.id))
                .commit()
        }

        bind.ivAddAlarm.setOnClickListener {
            viewModel.addAlarm()
        }

        alarmListAdapter.onSwitchCompatListener = { alarm, isEnabled ->
            viewModel.changeEnabledState(alarm, isEnabled)
            if (isEnabled) {
                viewModel.turnOnAlarm(requireContext(), alarm.id)
            } else {
                viewModel.turnOffAlarm(requireContext(), alarm.id)
            }
        }
    }

    private fun flowCollects() {
        lifecycleScope.launch {
            viewModel.alarmList.collect {
                alarmListAdapter.submitList(it)
            }
        }

        job = lifecycleScope.launch {
            viewModel.newAlarmId.collect {
                requireActivity().supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_container, AlarmSettingFragment.newInstanceAddAlarm(it))
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        job.cancel()
    }
}