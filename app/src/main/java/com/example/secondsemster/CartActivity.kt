package com.example.secondsemster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagmentCart
import com.example.secondsemster.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Properties
import java.util.UUID
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class CartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private var tax:Double= 0.0
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        managmentCart= ManagmentCart(this)
        database = FirebaseDatabase.getInstance()

        setVariable()
        initCartList()
        calculateCart()
    }

    private fun initCartList() {
        binding.viewCart.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.viewCart.adapter = CartAdapter(managmentCart.getListCart(),this,object:ChangeNumberItemsListener{
            override fun onChanged() {
                calculateCart()
            }
        })
        with(binding){
            emptyTxt.visibility = if(managmentCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView2.visibility = if(managmentCart.getListCart().isEmpty()) View.GONE else View.VISIBLE

        }
    }
    private fun calculateCart(){
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0
        val total=Math.round((managmentCart.getTotalFee()+tax+delivery)*100)/1
        val itemTotal = Math.round(managmentCart.getTotalFee()*100)/100

        with(binding){
            totalFeeTxt.text = "R$itemTotal"
            taxTxt.text = "R$tax"
            deliveryTxt.text= "R$delivery"
            totalTxt.text = "R$total"
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
        binding.button2.setOnClickListener {
            if (managmentCart.getListCart().isNotEmpty()){
                saveOrderToFirebase()
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun saveOrderToFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in, Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        val orderId = UUID.randomUUID().toString()
        val items = managmentCart.getListCart()
        val delivery = 10.0
        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100.0
        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 1.0

        // Create order details
        val order = hashMapOf(
            "orderId" to orderId,
            "userId" to currentUser.uid,
            "userEmail" to currentUser.email,
            "timestamp" to System.currentTimeMillis(),
            "items" to items.map { item ->
                hashMapOf(
                    "title" to item.title,
                    "price" to item.price,
                    "numberInCart" to item.numberInCart,
                    "size" to (item.size.firstOrNull() ?: ""),
                    "imageUrl" to (item.picUrl.firstOrNull() ?: "")
                )
            },
            "subtotal" to itemTotal,
            "tax" to tax,
            "deliveryFee" to delivery,
            "total" to total,
            "status" to "pending",
            "emailSent" to false
        )

        // Save to Firebase Realtime Database under 'orders' node
        val orderRef = database.reference.child("orders").child(orderId)
        orderRef.setValue(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully, confirmation email will be sent shortly", Toast.LENGTH_SHORT).show()
                managmentCart.clearCart()  // Clear the cart after successful order
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun sendOrderConfirmationEmail(userEmail: String, order: HashMap<String, Any>, orderId: String) {
        Thread{
            try {
                val props = Properties()
                props.put("mail.smtp.host", "smtp.gmail.com")
                props.put("mail.smtp.socketFactory.port", "465")
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                props.put("mail.smtp.auth", "true")
                props.put("mail.smtp.port", "465")

                val session = Session.getInstance(props,object:Authenticator(){
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("tylerkhakhu@gmail.com","ltatzipzpfrxarcz")
                    }
                })
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("tylerkhakhu@gmail.com"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(userEmail))
                message.subject = "Order Confirmation"

                val items = order["items"] as List<HashMap<String, Any>>
                val total = order["total"] as Double

                val emailContent = StringBuilder()
                emailContent.append("<html><body>")
                emailContent.append("<h2>Thank you for your order!</h2>")
                emailContent.append("<p>Your order ID is: $orderId</p>")
                emailContent.append("<h3>Order Details:</h3>")
                emailContent.append("<table border='1' cellpadding='5'>")
                emailContent.append("<tr><th>Item</th><th>Size</th><th>Quantity</th><th>Price</th></tr>")

                items.forEach { item ->
                    emailContent.append("<tr>")
                    emailContent.append("<td>${item["title"]}</td>")
                    emailContent.append("<td>${item["size"]}</td>")
                    emailContent.append("<td>${item["numberInCart"]}</td>")
                    emailContent.append("<td>R${item["price"]}</td>")
                    emailContent.append("</tr>")
                }

                emailContent.append("</table>")
                emailContent.append("<p>Total: R$total</p>")
                emailContent.append("</body></html>")

                message.setContent(emailContent.toString(), "text/html; charset=utf-8")

                Transport.send(message)

                runOnUiThread {
                    Toast.makeText(this@CartActivity, "Order placed and confirmation email sent", Toast.LENGTH_SHORT).show()
                }

                database.reference.child("orders").child(orderId).child("emailSent").setValue(true)

            }catch (e:Exception){
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CartActivity, "Order placed but failed to send email: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}