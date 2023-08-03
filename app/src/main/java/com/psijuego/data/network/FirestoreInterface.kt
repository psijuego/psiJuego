package com.psijuego.data.network

import com.google.firebase.firestore.DocumentSnapshot

interface FirestoreInterface {

    suspend fun getDataFromFirestore(): List<DocumentSnapshot>

    fun saveDataFromFirestore()

    fun editDataFromFirestore()
}