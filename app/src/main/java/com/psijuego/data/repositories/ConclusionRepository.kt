package com.psijuego.data.repositories

import com.psijuego.data.network.DatabaseFirebaseStorage
import java.io.File
import javax.inject.Inject

class ConclusionRepository @Inject constructor(
    private val databaseFirebaseStorage: DatabaseFirebaseStorage
)  {
    fun uploadDocument(file: File, getUrl: (String) -> Unit){
        databaseFirebaseStorage.uploadDocument(file, getUrl)
    }
}