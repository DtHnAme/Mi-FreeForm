package com.sunshine.freeform.ui.freeform

import android.app.ActivityOptions
import android.app.ActivityManager
import android.app.ActivityOptionsHidden
import android.app.PendingIntent
import android.app.PendingIntentHidden
import android.app.Service
import android.app.TaskInfoHidden
import android.content.ComponentName
import android.content.ContextHidden
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.os.SystemClock
import android.view.Display
import com.sunshine.freeform.utils.ServiceUtils
import com.sunshine.freeform.utils.ServiceUtils.activityManager
import dev.rikka.tools.refine.Refine

class FreeformService: Service(), ScreenListener.ScreenStateListener {
    private lateinit var mFreeformView: FreeformView
    private lateinit var mScreenListener: ScreenListener
    private var mConfig = FreeformConfig()
    private var mIntent: Parcelable? = null
        set(value) {
            mConfig.intent = value
            field = value
        }
    private var mComponentName: ComponentName? = null
        set(value) {
            mConfig.componentName = value
            field = value
        }

    private var mUserId: Int = 0
        set(value) {
            field = if (value < 0) Refine.unsafeCast<ContextHidden>(this).userId else value
            mConfig.userId = field
        }

    private val mVirtualDisplay by lazy {
        ServiceUtils.displayManager.createVirtualDisplay(
            "MiFreeform@${SystemClock.uptimeMillis()}",
            500,
            500,
            100,
            null,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
        )
    }

    private val mRunningTasks: List<ActivityManager.RunningTaskInfo>
        get() = activityManager.getTasks(100)

    private val mFocusedRunningTask: ActivityManager.RunningTaskInfo?
        get() {
            mRunningTasks.forEach {
                if (Refine.unsafeCast<TaskInfoHidden>(it).isFocused) {
                    return it
                }
            }
            return null
        }

    private val mFreeformRunningTasks: ArrayList<ActivityManager.RunningTaskInfo>
        get() {
            val runningTaskInfo = ArrayList<ActivityManager.RunningTaskInfo>()
            mRunningTasks.forEach {
                if (Refine.unsafeCast<TaskInfoHidden>(it).displayId == mVirtualDisplay.display.displayId) {
                    runningTaskInfo.add(it)
                }
            }
            return runningTaskInfo
        }

    override fun onCreate() {
        ServiceUtils.initWithShizuku(this)

        mScreenListener = ScreenListener(this)
        mScreenListener.addScreenStateListener(this)

        initFreeformView()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null)
            return START_NOT_STICKY
        when (intent.action) {
            ACTION_START_INTENT -> {
                if (mFreeformView.isDestroy) {
                    initFreeformView()
                }
                mUserId = intent.getIntExtra(Intent.EXTRA_USER, 0)
                mIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT)
                mComponentName = intent.getParcelableExtra(Intent.EXTRA_COMPONENT_NAME)
                mFreeformView.config = mConfig
                if (startIntent() < 0) {
                    return START_NOT_STICKY
                }
                startFreeformView()
            }
            ACTION_CALL_INTENT -> {
                if (mIntent == null)
                    return START_NOT_STICKY
                if (startIntent(
                        parcelable = intent.getParcelableExtra(Intent.EXTRA_INTENT),
                        displayId = intent.getIntExtra(EXTRA_DISPLAY_ID, mVirtualDisplay.display.displayId)
                    ) < 0) {
                    return START_NOT_STICKY
                }
            }
            ACTION_DESTROY_FREEFORM -> {
                mFreeformView.destroy()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        mFreeformView.destroy()
        mVirtualDisplay.release()
        mScreenListener.unregisterListener()
    }

    private fun initFreeformView() {
        mFreeformView = FreeformView(mConfig, this, mVirtualDisplay, mScreenListener)
        mFreeformView.initSystemService()
        mFreeformView.initConfig()
        mFreeformView.initView()
    }

    private fun startFreeformView() {
        if (mFreeformView.isFloating || mFreeformView.isHidden) {
            mFreeformView.moveToFirst()
        } else {
            mFreeformView.showWindow()
        }
    }

    private fun startIntent(
        parcelable: Parcelable? = mIntent,
        displayId: Int = mVirtualDisplay.display.displayId,
        options: ActivityOptions = ActivityOptions.makeBasic().setLaunchDisplayId(displayId),
        componentName: ComponentName? = mComponentName,
    ): Int {
        var result = -1
        if (parcelable is Intent) {
            mIntent = parcelable
            mComponentName = parcelable.component
            result = callIntent(parcelable, options, userId = mUserId)
        } else if (componentName != null) {
            mComponentName = componentName
            mIntent = Intent(Intent.ACTION_MAIN).apply {
                component = componentName
                setPackage(componentName.packageName)
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            result = callIntent(mIntent as Intent, options, userId = mUserId)
            if (parcelable is PendingIntent) {
                result = callPendingIntent(parcelable, options)
            }
        }
        return result
    }

    private fun callIntent(intent: Intent,
                   options: ActivityOptions,
                   withoutAnim: Boolean = true,
                   userId: Int = mUserId,
    ): Int {
        if (withoutAnim) intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_ANIMATION
        return activityManager.startActivityAsUserWithFeature(
            null, SHELL, null, intent,
            intent.type, null, null, 0, 0,
            null, options.toBundle(), userId,
        )
    }

    private fun callPendingIntent(pendingIntent: PendingIntent,
                                  options: ActivityOptions,
                                  displayId: Int = options.launchDisplayId,
    ): Int {
        val pendingIntentHidden = Refine.unsafeCast<PendingIntentHidden>(pendingIntent)
        val activityOptionsHidden = Refine.unsafeCast<ActivityOptionsHidden>(options).setCallerDisplayId(displayId)
        return activityManager.sendIntentSender(
            pendingIntentHidden.target, pendingIntentHidden.whitelistToken, 0, null,
            null, null, null, activityOptionsHidden.toBundle()
        )
    }

    companion object {
        const val SHELL = "com.android.shell"

        const val ACTION_START_INTENT = "com.sunshine.freeform.action.start.intent"
        const val ACTION_CALL_INTENT = "com.sunshine.freeform.action.call.intent"
        const val ACTION_DESTROY_FREEFORM = "com.sunshine.freeform.action.destroy.freeform"

        const val EXTRA_DISPLAY_ID = "com.sunshine.freeform.action.intent.display.id"
    }

    override fun onScreenOn() {
    }

    override fun onScreenOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (mFreeformView.isDestroy) {
                mFreeformRunningTasks.forEach {
                    startService(
                        Intent(this, FreeformService::class.java)
                            .setAction(ACTION_CALL_INTENT)
                            .putExtra(Intent.EXTRA_INTENT, it.baseIntent)
                            .putExtra(EXTRA_DISPLAY_ID, Display.DEFAULT_DISPLAY)
                    )
                }
                startService(
                    Intent(this, FreeformService::class.java)
                        .setAction(ACTION_CALL_INTENT)
                        .putExtra(Intent.EXTRA_INTENT, mFocusedRunningTask!!.baseIntent)
                        .putExtra(EXTRA_DISPLAY_ID, Display.DEFAULT_DISPLAY)
                )
            }

            if (mFreeformRunningTasks.isEmpty()) {
                stopSelf()
            }
        } else {
            if (mFreeformView.isDestroy) {
                stopSelf()
            }
        }
    }

    override fun onUserPresent() {
    }
}
