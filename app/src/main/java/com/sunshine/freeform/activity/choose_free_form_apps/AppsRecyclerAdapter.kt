package com.sunshine.freeform.activity.choose_free_form_apps

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.graphics.drawable.Drawable
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunshine.freeform.R
import com.sunshine.freeform.room.FreeFormAppsEntity
import com.sunshine.freeform.room.NotificationAppsEntity
import com.sunshine.freeform.systemapi.UserHandle
import kotlinx.coroutines.*

/**
 * @author sunshine
 * @date 2021/1/31
 * 小窗应用回收布局适配器
 * @param allAppsList 所有应用列表
 * @param appsList 已经选择的应用列表
 */
class AppsRecyclerAdapter<T>(
        private val allAppsList: ArrayList<LauncherActivityInfo>,
        private val viewModel: ChooseAppsViewModel,
        private val appsList: ArrayList<T>,
        private val userManager: UserManager,
        private val type: Int,
        private val context: Context
) : RecyclerView.Adapter<AppsRecyclerAdapter.ViewHolder>() {

    //用于存储到sp中
    //private var freeFormAppsSet = freeFormAppsMap.keys.toMutableSet()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view: View = itemView.findViewById(R.id.view_app_click)
        val icon: ImageView = itemView.findViewById(R.id.imageView_app_icon)
        val name: AppCompatTextView = itemView.findViewById(R.id.textView_app_name)
        val switch: SwitchCompat = itemView.findViewById(R.id.switch_app)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //加载icon为费时操作，可能导致ui卡顿
        //holder.icon.setImageDrawable(allAppsList[position].getBadgedIcon(0))

        //2022.01.02 使用glide减少掉帧
        holder.icon.let {
            Glide.with(context).load(allAppsList[position].getBadgedIcon(0)).into(it)
        }
//        //使用glide+协程加载图片
//        holder.icon.load(allAppsList[position].getBadgedIcon(0))

        holder.name.text = allAppsList[position].label
        if (type == 1) {
            holder.switch.isChecked = contains(FreeFormAppsEntity(-1, allAppsList[position].applicationInfo.packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid)))
        } else {
            holder.switch.isChecked = contains(NotificationAppsEntity(allAppsList[position].applicationInfo.packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid)))
        }
//        println(freeFormAppsSet)
//        println(packages[position].activityInfo.applicationInfo.packageName)
//        println(holder.switch.isChecked)
//        println("---------")

        //点击对应改变数据库
        holder.view.setOnClickListener {
            holder.switch.isChecked = !holder.switch.isChecked
            if (holder.switch.isChecked) {
                insert(position)
            } else {
                delete(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return allAppsList.size
    }

    private fun insert(position: Int) {
        val packageName = allAppsList[position].applicationInfo.packageName
        viewModel.insertApps(packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid))

        if (type == 1) {
            appsList.add(FreeFormAppsEntity(-1, allAppsList[position].applicationInfo.packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid)) as T)
        } else {
            appsList.add(NotificationAppsEntity(allAppsList[position].applicationInfo.packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid)) as T)
        }

    }

    private fun delete(position: Int) {
        val packageName = allAppsList[position].applicationInfo.packageName
        viewModel.deleteApps(packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid))

        if (type == 1) {
            remove(FreeFormAppsEntity(-1, allAppsList[position].applicationInfo.packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid)))
        } else {
            remove(NotificationAppsEntity(allAppsList[position].applicationInfo.packageName, UserHandle.getUserId(allAppsList[position].user, allAppsList[position].applicationInfo.uid)))
        }
    }

    /**
     * 已经选择的列表中是否有item
     */
    private fun contains(item: FreeFormAppsEntity): Boolean {
        appsList.forEach {
            if ((it as FreeFormAppsEntity).userId == item.userId &&
                (it as FreeFormAppsEntity).packageName == item.packageName) return true
        }
        return false
    }

    /**
     * 已经选择的列表中是否有item
     */
    private fun contains(item: NotificationAppsEntity): Boolean {
        appsList.forEach {
            if ((it as NotificationAppsEntity).userId == item.userId &&
                (it as NotificationAppsEntity).packageName == item.packageName) return true
        }
        return false
    }

    /**
     * 从列表中移除
     */
    private fun remove(item: FreeFormAppsEntity) {
        var toBeRemovedItem: T? = null
        appsList.forEach {
            if ((it as FreeFormAppsEntity).userId == item.userId &&
                (it as FreeFormAppsEntity).packageName == item.packageName) {
                toBeRemovedItem = it
                return@forEach
            }
        }

        appsList.remove(toBeRemovedItem)
    }

    /**
     * 从列表中移除
     */
    private fun remove(item: NotificationAppsEntity) {
        var toBeRemovedItem: T? = null
        appsList.forEach {
            if ((it as NotificationAppsEntity).userId == item.userId &&
                (it as NotificationAppsEntity).packageName == item.packageName) {
                toBeRemovedItem = it
                return@forEach
            }
        }

        appsList.remove(toBeRemovedItem)
    }

    //来源 https://juejin.cn/post/6942276625090215943
    val View.viewScope: CoroutineScope
        get() {
            // 获取现有 viewScope 对象
            val key = "ViewScope".hashCode()
            var scope = getTag(key) as? CoroutineScope
            // 若不存在则新建 viewScope 对象
            if (scope == null) {
                scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                // 将 viewScope 对象缓存为 View 的 tag
                setTag(key,scope)
                val listener = object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View?) {
                    }

                    override fun onViewDetachedFromWindow(v: View?) {
                        // 当 view detach 时 取消协程的任务
                        scope.cancel()
                    }

                }
                addOnAttachStateChangeListener(listener)
            }
            return scope
        }
}