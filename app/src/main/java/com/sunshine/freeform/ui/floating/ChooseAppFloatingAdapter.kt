package com.sunshine.freeform.ui.floating

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.UserHandle
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sunshine.freeform.R
import com.sunshine.freeform.ui.floating_apps_sort.FloatingAppsSortActivity
import com.sunshine.freeform.ui.choose_apps.ChooseAppsActivity
import com.sunshine.freeform.room.FreeFormAppsEntity
import com.sunshine.freeform.ui.freeform.*
import java.lang.reflect.Method

/**
 * @author sunshine
 * @date 2021/3/7
 * 选择应用侧边栏适配器
 */
class ChooseAppFloatingAdapter(
    private val context: Context,
    private val apps: List<FreeFormAppsEntity>?,
    private val callback: ChooseAppFloatingView.ClickListener,
    private val allAppsCallback: ChooseAppFloatingView.ClickListener
) : RecyclerView.Adapter<ChooseAppFloatingAdapter.ViewHolder>() {

    private var resolveInfoList: MutableList<ResolveInfo>
    private lateinit var launcherApps: LauncherApps
    private val userHandleMap = HashMap<Int, UserHandle>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = itemView.findViewById(R.id.imageView_icon)
        val click: View = itemView.findViewById(R.id.view_click)
        val appName: TextView = itemView.findViewById(R.id.textView_appName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        userManager.userProfiles.forEach {
            userHandleMap[com.sunshine.freeform.systemapi.UserHandle.getUserId(it)] = it
        }

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_floating,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        //2021.07.25更新，新增展示最小化的应用
        return apps?.size?.plus(2)!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //2021.07.15优化了选择应用界面的样式，增加了应用名称显示，所有应用移动到了最上面，新增编辑应用
        when (position) {
            0 -> {
                holder.icon.setImageResource(R.drawable.ic_all)
                holder.appName.text = context.getString(R.string.all_apps)
                holder.click.setOnClickListener {
                    allAppsCallback.onClick()
                }
            }

            apps!!.size + 1 -> {
                holder.icon.setImageResource(R.drawable.ic_add)
                holder.appName.text = context.getString(R.string.edit_apps)
                holder.click.setOnClickListener {
                    context.startActivity(Intent(context, ChooseAppsActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                    callback.onClick()
                }
            }
            else -> {
                val packageName = apps[position - 1].packageName
                try {
                    //val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                    val userHandle = if (userHandleMap.containsKey(apps[position - 1].userId)) userHandleMap[apps[position - 1].userId]!! else userHandleMap[0]!!
                    val appInfo = launcherApps.getApplicationInfo(apps[position - 1].packageName, 0, userHandle)
                    var activityName = ""
                    resolveInfoList.forEach {
                        it.activityInfo
                        if (it.activityInfo.applicationInfo.packageName == packageName) {
                            activityName = it.activityInfo.name
                        }
                    }
                    Glide.with(context)
                        .load(appInfo.loadIcon(context.packageManager))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.icon)
                    holder.appName.text = getLabel(appInfo, apps[position - 1].userId)
                    holder.click.setOnClickListener {
                        context.startService(
                            Intent(context, FreeformService::class.java)
                                .setAction(FreeformService.ACTION_START_INTENT)
                                .putExtra(Intent.EXTRA_USER, apps[position - 1].userId)
                                .putExtra(Intent.EXTRA_INTENT,
                                    Intent(Intent.ACTION_MAIN)
                                        .setComponent(ComponentName(packageName, activityName))
                                        .setPackage(packageName)
                                        .addCategory(Intent.CATEGORY_LAUNCHER)
                                )
                        )
                        callback.onClick()
                    }
                    //长按进入排序界面
                    holder.click.setOnLongClickListener {
                        context.startActivity(Intent(context, FloatingAppsSortActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                        context.startService(
                            Intent(context, FreeformService::class.java)
                                .setAction(FreeformService.ACTION_START_INTENT)
                                .putExtra(Intent.EXTRA_INTENT,
                                    Intent(context, FloatingAppsSortActivity::class.java)
                                )
                        )
                        callback.onClick()
                        true
                    }
                } catch (e: PackageManager.NameNotFoundException) {}
            }
        }

    }

    private fun getLabel(applicationInfo: ApplicationInfo, userId: Int): CharSequence {
        return if (userId == 0) {
            context.packageManager.getApplicationLabel(applicationInfo)
        } else {
            "${context.packageManager.getApplicationLabel(applicationInfo)}-${context.getString(R.string.fenshen)}${userId}"
        }
    }

    private fun getActivityOptions(): ActivityOptions? {
        val options = ActivityOptions.makeBasic()
        val freeFormStackId = 5
        try {
            val method: Method = ActivityOptions::class.java.getMethod(
                "setLaunchWindowingMode",
                Int::class.javaPrimitiveType
            )
            method.invoke(options, freeFormStackId)
        } catch (e: Exception) {

        }
        return options
    }

    init {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        resolveInfoList = context.packageManager.queryIntentActivities(intent, 0)
    }
}