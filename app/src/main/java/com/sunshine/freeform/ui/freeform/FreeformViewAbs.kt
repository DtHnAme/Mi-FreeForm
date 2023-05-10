package com.sunshine.freeform.ui.freeform

import android.app.PendingIntent

abstract class FreeformViewAbs(open val config: FreeformConfig) {
    abstract fun toScreenCenter()
    abstract fun moveToFirst()
    abstract fun destroy()
}