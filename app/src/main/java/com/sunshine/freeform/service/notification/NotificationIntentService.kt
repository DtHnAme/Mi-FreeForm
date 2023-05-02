package com.sunshine.freeform.service.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import com.sunshine.freeform.app.MiFreeform
import com.sunshine.freeform.ui.freeform.FreeformConfig
import com.sunshine.freeform.ui.freeform.FreeformHelper
import com.sunshine.freeform.ui.freeform.FreeformService
import com.sunshine.freeform.ui.freeform.FreeformView
import java.lang.reflect.Method

/**
 * 用于开启目标程序
 */
class NotificationIntentService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetPackage: String? = intent?.getStringExtra("packageName")
        val targetUserId: Int? = intent?.getIntExtra("userId", 0)
        val targetIntent: PendingIntent? = intent?.getParcelableExtra(Intent.EXTRA_INTENT)
        if (targetPackage.isNullOrBlank() || targetUserId == null) stopSelf()
        else startFreeForm(targetPackage, targetUserId, targetIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("WrongConstant")
    private fun startFreeForm(targetPackage: String, targetUserId: Int, targetIntent: PendingIntent?) {
        //关闭通知栏
        collapseStatusBar()
        //点击后清除小窗
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2)

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfo = packageManager.queryIntentActivities(intent, 0)

        try {
            var activityName = ""
            resolveInfo.forEach {
                if (it.activityInfo.applicationInfo.packageName == targetPackage) {
                    activityName = it.activityInfo.name
                }
            }
            val sp = getSharedPreferences(MiFreeform.APP_SETTINGS_NAME, Context.MODE_PRIVATE)
            startService(
                Intent(this, FreeformService::class.java)
                    .setAction(FreeformService.ACTION_START_INTENT)
                    .putExtra(Intent.EXTRA_USER, targetUserId)
                    .putExtra(Intent.EXTRA_INTENT, targetIntent)
                    .putExtra(Intent.EXTRA_COMPONENT_NAME, ComponentName(targetPackage, activityName))
            )
            stopSelf()
        } catch (e: PackageManager.NameNotFoundException) {
            stopSelf()
        }
    }

    @SuppressLint("WrongConstant")
    private fun collapseStatusBar() {
        //31==S
        if (Build.VERSION.SDK_INT >= 31) {
            MiFreeform.me.execShell("cmd statusbar collapse", false)
        } else {
            val service = getSystemService("statusbar") ?: return
            try {
                val clazz = Class.forName("android.app.StatusBarManager")
                val collapse: Method? = clazz.getMethod("collapsePanels")
                collapse?.isAccessible = true
                collapse?.invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}