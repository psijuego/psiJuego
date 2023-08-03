package com.psijuego.data.network

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import javax.inject.Inject

class DatabaseFirebaseStorage @Inject constructor() {
    private var storageRef = Firebase.storage.reference

    fun uploadDocument(file: File, getUrl: (String) -> Unit) {
        try {
            val uri = Uri.fromFile(file)
            val fileName = file.name
            val fileRef = storageRef.child("documents/$fileName")
            fileRef.putFile(uri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val fileUrl = downloadUrl.toString()
                    getUrl(fileUrl)
                }.addOnFailureListener {
                    it.printStackTrace()
                    getUrl("")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getUrl("")
        }
    }
}