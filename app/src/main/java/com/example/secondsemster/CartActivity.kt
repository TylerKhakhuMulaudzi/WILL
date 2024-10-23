package com.example.secondsemster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagmentCart
import com.example.secondsemster.databinding.ActivityCartBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private var tax:Double= 0.0
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val orderId = UUID.randomUUID().toString()
        val items = managmentCart.getListCart()
        val delivery = 10.0
        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100.0
        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 1.0

        // Create order details
        val order = hashMapOf(
            "orderId" to orderId,
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
            "status" to "pending"
        )

        // Save to Firebase Realtime Database under 'orders' node
        val orderRef = database.reference.child("orders").child(orderId)
        orderRef.setValue(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                managmentCart.clearCart()  // Clear the cart after successful order
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}