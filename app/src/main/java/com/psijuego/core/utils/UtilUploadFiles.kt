package com.psijuego.core.utils

import android.content.Intent
import android.provider.MediaStore

class UtilUploadFiles{

    fun pickDrawFromGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
}