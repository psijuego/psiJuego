package com.psijuego.core.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri


class UtilShare {

    companion object {
        fun getInstance(): UtilShare {
            return UtilShare()
        }
    }

    private fun getWhatsappPackageNAme(context: Context): String? {
        var packagesName = ArrayList<String>()
        packagesName.add("com.whatsapp.w4b")
        packagesName.add("com.whatsapp")
        val packageManager = context.packageManager

        packagesName.forEach {
            try {
                packageManager.getPackageInfo(it, PackageManager.GET_ACTIVITIES)
                return it
            } catch (e: PackageManager.NameNotFoundException) {

            }
        }
        return null
    }

    fun getWhatsAppIntent(context: Context, uri: Uri): Intent {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "application/pdf"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        val whatsAppPackageName = getWhatsappPackageNAme(context)
        whatsAppPackageName.let {
            share.`package` = it
        }

        UtilFile.getInstance().grantUriPermission(context, share, uri)

        return share
    }

    fun getEmailIntentPdf(context: Context, uris: ArrayList<Uri>): Intent {
        val share = Intent(Intent.ACTION_SEND_MULTIPLE)
        share.type = "application/pdf"
        if (uris != null) {
            for (uri in uris) {
                UtilFile.getInstance().grantUriPermission(context, share, uri)
            }
            share.putExtra(Intent.EXTRA_STREAM, uris);
        }

        return share
    }
}