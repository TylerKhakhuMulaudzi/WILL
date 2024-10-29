package com.example.secondsemster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.checkerframework.checker.regex.qual.Regex

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var editName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var email: TextView
    private lateinit var profileImage: ImageView
    private lateinit var editPassword: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var saveBankDetails:Button

    private lateinit var cardPaymentHeaderLayout: LinearLayout
    private lateinit var cardPaymentDetails: LinearLayout
    private lateinit var cardArrow: ImageView

    private lateinit var editCardholderName: EditText
    private lateinit var editCardNumber: EditText
    private lateinit var editExpiryDate: EditText
    private lateinit var editCvv: EditText

    private var isCardDetailsVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        editName = findViewById(R.id.edit_name)
        editPhone = findViewById(R.id.edit_phone)
        editAddress = findViewById(R.id.edit_address)
        email = findViewById(R.id.email)
        profileImage = findViewById(R.id.profile_image)
        editPassword = findViewById(R.id.edit_password)
        changePasswordButton = findViewById(R.id.change_password)
        cardPaymentHeaderLayout = findViewById(R.id.card_payment_header_layout)
        cardPaymentDetails = findViewById(R.id.card_payment_details)
        cardArrow = findViewById(R.id.card_arrow)
        editCardholderName = findViewById(R.id.edit_cardholder_name)
        editCardNumber = findViewById(R.id.edit_card_number)
        editExpiryDate = findViewById(R.id.edit_expiry_date)
        editCvv = findViewById(R.id.edit_cvv)
        saveBankDetails = findViewById(R.id.saveDetails)




        cardPaymentHeaderLayout.setOnClickListener {
            isCardDetailsVisible = !isCardDetailsVisible
            if (isCardDetailsVisible) {
                saveBankDetails.visibility = View.VISIBLE
                cardPaymentDetails.visibility = View.VISIBLE
                cardArrow.rotation = 90f
                saveBankDetails.setOnClickListener {
                    loadUserBankDetails()
                }

            } else {
                cardPaymentDetails.visibility = View.GONE
                cardArrow.rotation = 0f
            }
        }
        changePasswordButton.setOnClickListener {
                loadUserFirebasedata()
        }

    }

    private fun registerAndLoadUserData(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User registered successfully, now load the data
                    loadUserFirebasedata()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun loadUserFirebasedata() {
        try {

            val userEmail = email.text.toString()
            val userName = editName.text.toString()
            val userPhone = editPhone.text.toString()
            val userAddress = editAddress.text.toString()
            val userPassword = editPassword.text.toString()

            if (userName.isEmpty() || userEmail.isEmpty()) {
                Toast.makeText(this, "Name and Email must be filled out", Toast.LENGTH_SHORT).show()
                return
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return
            }

            val userData = mapOf(
                "name" to userName,
                "email" to userEmail,
                "phone" to userPhone,
                "address" to userAddress,
                "password" to userPassword
            )

            // Get reference to the Realtime Database
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users")

            userRef.setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_NAME", userName)
                    }
                    val pushIntent = Intent(this,CartActivity::class.java).apply {
                        putExtra("email", userEmail)
                    }
                    startActivity(intent)
                    startActivity(pushIntent)
                    finish()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadUserBankDetails() {

            val userCardholderName = editCardholderName.text.toString()
            val userCardNumber = editCardNumber.text.toString()
            val userExpiryDate = editExpiryDate.text.toString()
            val userCvv = editCvv.text.toString()

            if (userCardholderName.isEmpty() || userCardNumber.isEmpty()
                || userExpiryDate.isEmpty() || userCvv.isEmpty()
            ) {
                Toast.makeText(this, "All the cards details are required", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (!userCardNumber.matches(Regex("^[0-9]{16}$"))) {
                Toast.makeText(
                    this,
                    "Please enter a valid 16 digit card number",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (!userExpiryDate.matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}\$"))) {
                Toast.makeText(
                    this,
                    "Please enter the expiry date in the format MM/YY",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (!userCvv.matches(Regex("^[0-9]{3}\$"))) {
                Toast.makeText(this, "Please enter a valid 3 digit CVV", Toast.LENGTH_SHORT).show()
                return
            }

            val bankDetails = HashMap<String, Any>().apply {
                put("cardholderName", userCardholderName)
                put("cardNumber", userCardNumber)
                put("expiryDate", userExpiryDate)
                put("cvv", "***")
            }

            val database = FirebaseDatabase.getInstance()
            val bankDetailsRef = database.getReference("users").child("bankDetails")

            bankDetailsRef.setValue(bankDetails)
                .addOnSuccessListener {
                    Toast.makeText(this, "Bank details saved successfully", Toast.LENGTH_SHORT)
                        .show()
                    editCardNumber.text.clear()
                    editCvv.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error saving bank details: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

    }


}
