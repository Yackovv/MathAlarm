package com.example.mathalarm.presentation.activities

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
import com.example.mathalarm.R
import com.example.mathalarm.presentation.fragments.AlarmListFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private val preferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIntent()
        isDialogFirstShow()
        checkPermissions()

        val fragment = AlarmListFragment()
        supportFragmentManager.beginTransaction().replace(
            R.id.main_container, fragment
        ).commit()
    }

    private fun isDialogFirstShow() {
        val isDialogShow = preferences.getBoolean(IS_DIALOG_SHOW, false)

        if (!isDialogShow) {
            showDialogOpenDoNotDisturb()
        }
    }

    private fun showDialogOpenDoNotDisturb() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_change_do_not_disturb))
            .setMessage(getString(R.string.message_change_do_not_disturb))
            .setNeutralButton(getString(R.string.dialog_fragment_cancel)) { _, _ ->
                makeIsDialogTrue()
            }
            .setPositiveButton(getString(R.string.dialog_fragment_accept)) { _, _ ->
                openDoNotDisturbSetting()
                makeIsDialogTrue()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }

    private fun openDoNotDisturbSetting() {
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
            showDialogTurnOnNotification()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionsForApi33() {
        val permissionDenied = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_DENIED
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

    private fun showDialogTurnOnNotification() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Включить уведомления")
            .setMessage("Предоставте развершение на показ уведомлений")
            .setNeutralButton("Cancel") { _, _ ->
                finish()
            }
            .setPositiveButton("Accept") { _, _ ->
                openNotificationPostSetting()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }

    private fun openNotificationPostSetting() {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(this)
        }
    }

    private fun checkIntent() {
        if (intent.hasExtra(CLOSE_ACTIVITY_EXTRA)) {
            if (intent.getStringExtra(CLOSE_ACTIVITY_EXTRA) == CLOSE_ACTIVITY) {
                finish()
            }
        }
    }

    companion object {
        private const val POST_NOTIFICATION_RC = 110
        private const val SHARED_PREFERENCES_NAME = "DialogShow"
        private const val IS_DIALOG_SHOW = "isDialogShow"
        private const val CLOSE_ACTIVITY_EXTRA = "close_activity"
        private const val CLOSE_ACTIVITY = "close"
    }
}