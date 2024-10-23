package com.example.secondsemster

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var editName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var email: TextView
    private lateinit var profileImage: ImageView
    private lateinit var editPassword: EditText  // Ensure this is properly initialized


    private lateinit var cardPaymentHeaderLayout: LinearLayout
    private lateinit var cardPaymentDetails: LinearLayout
    private lateinit var cardArrow: ImageView  // The arrow ImageView

    private var isCardDetailsVisible = false  // Moved here to avoid scope issues


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize UI elements
        editName = findViewById(R.id.edit_name)
        editPhone = findViewById(R.id.edit_phone)
        editAddress = findViewById(R.id.edit_address)
        email = findViewById(R.id.email)
        profileImage = findViewById(R.id.profile_image)
        editPassword = findViewById(R.id.edit_password)  // Initialize this EditText
        cardPaymentHeaderLayout = findViewById(R.id.card_payment_header_layout)
        cardPaymentDetails = findViewById(R.id.card_payment_details)
        cardArrow = findViewById(R.id.card_arrow)

        cardPaymentHeaderLayout.setOnClickListener {
            isCardDetailsVisible = !isCardDetailsVisible
            if (isCardDetailsVisible) {
                cardPaymentDetails.visibility = View.VISIBLE
                cardArrow.rotation = 90f  // Rotate the arrow to indicate expansion
            } else {
                cardPaymentDetails.visibility = View.GONE
                cardArrow.rotation = 0f  // Reset the arrow to original position
            }
        }

        // Load user data
        loadUserData()
    }

    private fun loadUserData() {
        val user: FirebaseUser? = auth.currentUser

        user?.let {
            val userId = it.uid
            email.text = it.email  // Display user email

            // Fetch profile data from Firestore
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Populate UI fields with user data
                        editName.setText(document.getString("name"))
                        editPhone.setText(document.getString("phone"))
                        editAddress.setText(document.getString("address"))
                        val profilePicUrl = document.getString("profilePictureUrl")
                        if (profilePicUrl != null) {
                            // Load profile picture with Glide or similar library
                            Glide.with(this).load(profilePicUrl).into(profileImage)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }


    private fun updateUserProfile() {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val userData = hashMapOf(
                "name" to editName.text.toString(),
                "phone" to editPhone.text.toString(),
                "address" to editAddress.text.toString()
            )

            // Update Firestore with new user data
            db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private var imageUri: Uri? = null  // This will hold the image URI

    // Upload the profile picture
    private fun uploadProfilePicture() {
        imageUri?.let {
            val userId = auth.currentUser?.uid
            val storageRef = storage.reference.child("profile_pictures/$userId.jpg")

            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Save image URL to Firestore
                        saveProfilePictureUri(uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun saveProfilePictureUri(downloadUrl: String) {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("users").document(userId)
                .update("profilePictureUrl", downloadUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile picture URL", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun changePassword() {
        val user = auth.currentUser
        val newPassword = editPassword.text.toString()

        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error updating password", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun loadOrderHistory() {
        val userId = auth.currentUser?.uid
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Display each order
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load order history: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
