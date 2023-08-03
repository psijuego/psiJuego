package com.psijuego.data.network

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.psijuego.core.Constants
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DatabaseFirestore @Inject constructor() : FirestoreInterface {

    override suspend fun getDataFromFirestore(): List<DocumentSnapshot> =
        suspendCoroutine { continuation ->
            val collectionRef = Firebase.firestore.collection(Constants.INDICATORS)
            val task: Task<QuerySnapshot> = collectionRef.get()

            task.addOnSuccessListener { querySnapshot ->
                continuation.resume(querySnapshot.documents)
            }.addOnFailureListener { exception ->
                if (exception is FirebaseFirestoreException) {
                    continuation.resumeWithException(exception)
                } else {
                    continuation.resumeWithException(
                        Exception(
                            "Firestore query failed.",
                            exception
                        )
                    )
                }
            }
        }

    override fun saveDataFromFirestore() {
        TODO("Not yet implemented")
    }

    override fun editDataFromFirestore() {
        TODO("Not yet implemented")
    }
}
