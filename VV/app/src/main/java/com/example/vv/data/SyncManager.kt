package com.example.vv.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vv.data.db.AppDatabase
import com.example.vv.data.local.ExpenseEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class SyncManager(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore
) {

    private val TAG = "SyncManager"
    private var expenseListener: ListenerRegistration? = null
    private var categoryListener: ListenerRegistration? = null
    private val expenseCollection = firestore.collection("expense_entries")
    private val categoryCollection = firestore.collect




    // LiveData to observe sync status if needed
    private val _syncStatus = MutableLiveData<String>()
    val syncStatus: LiveData<String> = _syncStatus

    fun startExpenseSync(userId: String) {
        // Listen to Firestore changes for this user
        expenseListener = expenseCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore listen failed", error)
                    _syncStatus.postValue("Error syncing: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (docChange in snapshots.documentChanges) {
                            val doc = docChange.document
                            val firestoreEntry = doc.toObject(FirestoreExpenseEntry::class.java)
                            val localEntry = db.expenseEntryDao().getById(firestoreEntry.id.toInt())

                            when (docChange.type) {
                                com.google.firebase.firestore.DocumentChange.Type.ADDED,
                                com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                                    // Insert or update local DB if remote is newer
                                    if (localEntry == null || firestoreEntry.lastModified > localEntry.date) {
                                        val expenseEntry = ExpenseEntry(
                                            id = firestoreEntry.id.toInt(),
                                            date = firestoreEntry.date,
                                            startTime = firestoreEntry.startTime,
                                            endTime = firestoreEntry.endTime,
                                            description = firestoreEntry.description,
                                            categoryId = firestoreEntry.categoryId,
                                            amount = firestoreEntry.amount,
                                            photoUri = firestoreEntry.photoUri
                                        )
                                        db.expenseEntryDao().insert(expenseEntry)
                                        Log.d(TAG, "Synced expense from Firestore: ${expenseEntry.id}")
                                    }

                                }
                                com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                                    localEntry?.let {
                                        db.expenseEntryDao().delete(it)
                                        Log.d(TAG, "Deleted expense locally: ${it.id}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    fun stopExpenseSync() {
        expenseListener?.remove()
    }

    // Upload local changes to Firestore
    fun uploadExpenseEntry(expenseEntry: ExpenseEntry, userId: String) {
        val docId = expenseEntry.id.toString()
        val firestoreEntry = FirestoreExpenseEntry(
            id = docId,
            date = expenseEntry.date,
            startTime = expenseEntry.startTime,
            endTime = expenseEntry.endTime,
            description = expenseEntry.description,
            categoryId = expenseEntry.categoryId,
            amount = expenseEntry.amount,
            photoUri = expenseEntry.photoUri,
            lastModified = System.currentTimeMillis()
        )

        expenseCollection.document(docId)
            .set(firestoreEntry)
            .addOnSuccessListener {
                Log.d(TAG, "Uploaded expense to Firestore: $docId")
                _syncStatus.postValue("Expense uploaded")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to upload expense", e)
                _syncStatus.postValue("Upload failed: ${e.message}")
            }
    }
}
