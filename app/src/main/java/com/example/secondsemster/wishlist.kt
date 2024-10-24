package com.example.secondsemster

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.secondsemster.databinding.ActivityWishlistBinding

class wishlist : AppCompatActivity() {
    private lateinit var binding: ActivityWishlistBinding
    private lateinit var managmentWishlist: ManagmentWishlist
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentWishlist = ManagmentWishlist(this)

        setVariable()
        initWishlist()
    }
    override fun onResume() {
        super.onResume()
        initWishlist()
    }
    private fun initWishlist(){
        val wishlistItems = managmentWishlist.getWishlist()

        binding.viewWishlist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewWishlist.adapter = WishlistAdapter(wishlistItems, this)

        binding.emptyTxt.visibility = if (wishlistItems.isEmpty()) View.VISIBLE else View.GONE
        binding.scrollView2.visibility = if (wishlistItems.isEmpty()) View.GONE else View.VISIBLE
    }
    private fun setVariable(){
        binding.backBtn.setOnClickListener { finish() }
    }
}