package com.psijuego.core.utils

import android.content.Context
import java.lang.ref.WeakReference

open class CoreModule {

    companion object {

        private lateinit var CONTEXT: WeakReference<Context>

        fun init(context: Context) {
            CONTEXT = WeakReference(context)
        }

        fun getContext(): Context? {
            return CONTEXT.get()
        }
    }
}