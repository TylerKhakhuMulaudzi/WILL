package com.example.secondsemster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1762.Helper.ManagmentCart
import com.example.secondsemster.databinding.ActivityDetailBinding
import java.util.ResourceBundle.getBundle

class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item:ItemsModel
    private var numberOrder = 1
    private lateinit var managmentCart: ManagmentCart
    private lateinit var managmentWishlist: ManagmentWishlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        managmentWishlist = ManagmentWishlist(this)

        getBundle()
        banners()
        initLists()
    }

    private fun initLists() {
        val sizeList = ArrayList<String>()
        for (size in item.size){
            sizeList.add(size.toString())
        }
        binding.sizeList.adapter=SizeAdapter(sizeList)
        binding.sizeList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val colorList = ArrayList<String>()
        for (imageUrl in item.picUrl){
            colorList.add(imageUrl)
        }

        binding.colorList.adapter = ColorAdapter(colorList)
        binding.colorList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun banners() {
    val sliderItems=ArrayList<slideModel>()
        for (imageUrl in item.picUrl){
            sliderItems.add(slideModel(imageUrl))
        }
        binding.slider.adapter = sliderAdpter(sliderItems,binding.slider)
        binding.slider.clipChildren=true
        binding.slider.clipToPadding=true
        binding.slider.offscreenPageLimit=1

        if (sliderItems.size>1){
            binding.dotIndicator.visibility = View.VISIBLE
            binding.dotIndicator.attachTo(binding.slider)
        }
    }

    private fun getBundle() {
        item = intent.getParcelableExtra("object")!!

        binding.titleTxt.text = item.title
        binding.descriptionTxt.text = item.description
        binding.priceTxt.text = "R" + item.price
        binding.ratingTxt.text = "${item.rating} Rating"

        binding.addToCartBtn.setOnClickListener {
            item.numberInCart = numberOrder
            managmentCart.insertFood(item)
        }

        binding.backBtn.setOnClickListener { finish() }

        binding.cartBtn.setOnClickListener {
            if (!managmentWishlist.isInWishlist(item)) {
                managmentWishlist.addToWishlist(item)
            }
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@DetailActivity, wishlist::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }, 100)
        }

        binding.favBtn.setOnClickListener {
            if (managmentWishlist.isInWishlist(item)) {
                managmentWishlist.removeFromWishlist(
                    managmentWishlist.getWishlist().indexOfFirst { it.title == item.title }
                )
            } else {
                managmentWishlist.addToWishlist(item)
            }
            updateWishlistButtonAppearance()
        }
    }

    private fun updateWishlistButtonAppearance() {
        val isInWishlist = managmentWishlist.isInWishlist(item)
        binding.favBtn.setImageResource(
            if (isInWishlist) R.drawable.heart_filled
            else R.drawable.heart
        )
        binding.cartBtn.setImageResource(
            if (isInWishlist) R.drawable.heart_filled
            else R.drawable.heart
        )
    }
}