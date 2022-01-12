package com.sunshine.freeform.activity.floating_view

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import com.sunshine.freeform.callback.ServiceStateListener
import com.sunshine.freeform.utils.ShellUtils
import java.io.ObjectOutputStream
import java.net.Socket

/**
 * @author sunshine
 * @date 2021/2/19
 * 一些小窗配置
 */
object FreeFormConfigOld {

    private const val TAG = "FreeFormConfig"

    //控制模式 1 root 2 xposed
    var controlModel = 2

    const val IP_ADDRESS = "127.0.0.1"
    private const val TOUCH_PORT = 10259
    const val PORT = 12065
    var touchSocket: Socket? = null
    var touchOos: ObjectOutputStream? = null

    var sendThread: Thread? = null

    var handler: Handler? = null

    var dpi = 300                         //小窗默认分辨率为300dpi，可以自定义

    //所有小窗的集合，用于屏幕旋转时监听
    var freeFormViewSet = HashSet<FreeFormWindow>()
    //屏幕方向，1 竖屏 2横屏 0未定义
    var orientation = Configuration.ORIENTATION_UNDEFINED

    //root需要的远程服务是否启动成功
    var socketStartSuccess = false

    //最小化的边缘
    const val SMALL_FREEFORM_POSITION = 250
    //最小化的边缘留白
    const val SMALL_FREEFORM_DISTANCE = 250

    fun startActivityForHook(packageName: String) {
        var freeFormWindow: FreeFormWindow? = null
        freeFormViewSet.forEach {
            if (it.packageName == packageName) {
                freeFormWindow = it
                return@forEach
            }
        }
        if (freeFormWindow != null) {
            ShellUtils.execCommand("${freeFormWindow!!.command}${freeFormWindow!!.displayId}", true)
        }
    }

    fun init(listener: ServiceStateListener?, controlModel: Int) {
        FreeFormConfigOld.controlModel = controlModel
        //root
        if (controlModel == 1) {
            initSocket(
                listener
            )
            startSendEventThread()
        }
        //xposed直接回调即可
        else {
            //不需要就直接标志成功
            socketStartSuccess = true
            listener?.onStart()
        }

    }

    /**
     * 初始化连接
     * 回调给活动
     */
    private fun initSocket(listener: ServiceStateListener?) {
        Thread(Runnable {
            if (touchSocket == null) {
                try {
                    socketStartSuccess = true
                    touchSocket = Socket(
                        IP_ADDRESS,
                        TOUCH_PORT
                    )
                    touchOos = ObjectOutputStream(
                        touchSocket!!.getOutputStream())
                    listener?.onStart()
                }catch (e: Exception) {
                    socketStartSuccess = false
                    println("initSocket $e")
                    listener?.onStop()
                }
            }
        }, "initSocket").start()
    }

    /**
     * 初始化发送消息的线程
     */
    private fun startSendEventThread() {
        sendThread = Thread (Runnable {
            Looper.prepare()
            handler = @SuppressLint("HandlerLeak")
            object : Handler(Looper.myLooper()!!) {}
            Looper.loop()
        }, "sendThread")
        sendThread?.start()
    }

    /**
     * 显示的小窗中是否存在要打开的小窗，存在就不允许打开了
     */
    fun hasFreeFormWindow(packageName: String): Boolean {
        freeFormViewSet.forEach {
            if (packageName == it.packageName) return true
        }
        return false
    }

    /**
     * 屏幕旋转时，需要获取所有显示的小窗
     * 然后移除掉并重新创建
     * 屏幕方向通过无障碍直接设置，不需要传送参数，这样可以在第一次就知道屏幕的方向
     */
    fun orientationChanged() {
        freeFormViewSet.forEach {
            it.resize()
        }
    }

    /**
     * 当销毁服务时，因为这个是全局的，所以不会销毁，只有重启软件才会销毁
     * 而这样如果不重启软件而只是重启服务，这里就会有问题
     * 在服务的销毁中调用
     */
    fun onDelete(removeAllFreeForm: Boolean) {
        if (removeAllFreeForm) {
            freeFormViewSet.forEach {
                it.destroy()
            }
        }

        try {
            touchOos?.close()
            touchSocket?.shutdownOutput()
            touchSocket?.close()
            touchSocket = null
        }catch (e: Exception) {
            touchOos = null
            touchSocket = null
        }
        socketStartSuccess = false
        //停止发送消息的线程
        sendThread?.interrupt()
    }
}