package com.example.myalarm.presentation.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myalarm.R
import com.example.myalarm.logg
import com.example.myalarm.presentation.fragments.AlarmListFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private val preferences by lazy{
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isDialogFirstShow()
        checkPermissions()

        val fragment = AlarmListFragment()
        supportFragmentManager.beginTransaction().replace(
            R.id.main_container, fragment
        ).setReorderingAllowed(true)
            .commit()
    }

    private fun isDialogFirstShow(){
        val isDialogShow = preferences.getBoolean(IS_DIALOG_SHOW, false)

        if(!isDialogShow){
            showDialogOpenDoNotDisturb()
        }
    }

    //TODO Изменить текст и заголовок на нормальные
    private fun showDialogOpenDoNotDisturb(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Title")
            .setMessage("Text text Text text Text text Text text Text text")
            .setNeutralButton("Cancel") { _, _ ->
                makeIsDialogTrue()
            }
            .setPositiveButton("Accept") { _, _ ->
                openDoNotDisturbSetting()
                makeIsDialogTrue()
            }
            .show()
    }

    private fun openDoNotDisturbSetting(){
        startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    private fun makeIsDialogTrue() {
        preferences.edit().apply {
            putBoolean(IS_DIALOG_SHOW, true).apply()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionsForApi33()
        } else {
            checkPermissionsLowApi33()
        }
    }

    private fun checkPermissionsLowApi33() {
        val notificationManager = NotificationManagerCompat.from(this)
        if (!notificationManager.areNotificationsEnabled()) {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionsForApi33() {
        val permissionDenied = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_DENIED
        logg("$permissionDenied")
        if (permissionDenied) {
            requestPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            POST_NOTIFICATION_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == POST_NOTIFICATION_RC && permissions.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val POST_NOTIFICATION_RC = 110
        private const val SHARED_PREFERENCES_NAME = "DialogShow"
        private const val IS_DIALOG_SHOW = "isDialogShow"
    }
}