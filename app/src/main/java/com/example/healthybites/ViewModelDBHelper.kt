package edu.utap.photolist

import android.util.Log
import com.example.healthybites.Diet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "diet-list"

    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    private fun limitAndGet(
        query: Query,
        resultListener: (List<Diet>) -> Unit
    ) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(Diet::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    fun fetchDiet(
        resultListener: (List<Diet>) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val query = db.collection(rootCollection).whereEqualTo(
            "ownerUid",
            user!!.uid
        )
        limitAndGet(query, resultListener)
    }

    fun createDiet(
        diet: Diet,
        resultListener: () -> Unit
    ) {
        diet.firestoreID = db.collection(rootCollection).document().id
        db.collection(rootCollection).document(diet.firestoreID)
            .set(diet)
            .addOnSuccessListener {
                //refresh photo metas
                resultListener()
            }
            .addOnFailureListener { e ->
                Log.e(
                    javaClass.simpleName,
                    "Failed to create diet!diet id=${diet.id}", e
                )
            }
    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removeDiet(
        diet: Diet,
        resultListener: (List<Diet>) -> Unit
    ) {
        db.collection(rootCollection).document(diet.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.i(
                    javaClass.simpleName,
                    "Photo meta successfully deleted!"
                )
                //refresh photo metas
                fetchDiet {
                    resultListener(it)
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    javaClass.simpleName,
                    "Failed to deleting photo meta ${diet.id}", e
                )
            }
    }
}