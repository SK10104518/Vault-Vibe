package com.example.vv.ui.activities

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.example.vv.data.db.entities.Expense
import com.example.vv.ui.viewmodel.ExpenseViewModel
import org.w3c.dom.Text
import java.util.*

@Composable
fun ExpenseEntryScreen(viewModel: ExpenseViewModel = viewModel()) {
    val context = LocalContext.current

    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick Image")
        }

        imageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (description.isBlank() || amount.isBlank() || category.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount == null) {
                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val saveExpense = { photoUrl: String? ->
                    val newExpense = Expense(
                        description = description,
                        amount = parsedAmount,
                        category = category,
                        timestamp = Timestamp.now(),
                        photoUrl = photoUrl
                    )

                    viewModel.addExpense(newExpense,
                        onSuccess = {
                            Toast.makeText(context, "Expense saved", Toast.LENGTH_SHORT).show()
                            description = ""
                            amount = ""
                            category = ""
                            imageUri = null
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to save: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }

                if (imageUri != null) {
                    val storageRef = FirebaseStorage.getInstance().reference
                    val fileName = "expenses/${UUID.randomUUID()}.jpg"
                    val photoRef = storageRef.child(fileName)

                    photoRef.putFile(imageUri!!)
                        .addOnSuccessListener {
                            photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                saveExpense(downloadUri.toString())
                            }.addOnFailureListener {
                                Toast.makeText(context, "Error getting URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Photo upload failed", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    saveExpense(null)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            Text("Save Expense", color = Color.White)
        }
    }
}
