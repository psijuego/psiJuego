package com.psijuego.core.utils

import android.content.Context
import android.net.ConnectivityManager


class UtilConnection {

    private val TYPE_WIFI = "WIFI"
    private val TYPE_MOBILE = "MOBILE"
    private val TYPE_OTHER = "OTHER"

    companion object{
        fun getInstance(): UtilConnection {
            return UtilConnection()
        }
    }

    fun getConnectionType(): String? {
        val connMgr = CoreModule.getContext()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connMgr != null) {
            val activeNetworkInfo = connMgr.activeNetworkInfo
            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    TYPE_WIFI
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) TYPE_MOBILE else TYPE_OTHER
            }
        }
        return null
    }

    fun checkInternetConnection(): Boolean {
        val cm =
            CoreModule.getContext()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }
}