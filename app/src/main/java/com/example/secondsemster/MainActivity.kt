package com.example.secondsemster

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.secondsemster.databinding.ActivityMainBinding
import androidx.viewpager2.widget.CompositePageTransformer

class MainActivity : BaseActivity() {
    private val viewModel = mainViewModel()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
        initBrand()
        initpopular()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(this, Observer { items->
            banners(items)
        binding.progressBarBanner.visibility=View.GONE
        })
        viewModel.loadBanners()
    }
    private fun banners(images:List<slideModel>){
        binding.viewPageSlider.adapter =sliderAdpter(images,binding.viewPageSlider)
        binding.viewPageSlider.clipChildren=false
        binding.viewPageSlider.clipToPadding=false
        binding.viewPageSlider.offscreenPageLimit=3
        binding.viewPageSlider.getChildAt(0).overScrollMode= RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(4))
        }
        binding.viewPageSlider.setPageTransformer(compositePageTransformer)
        if (images.size>1){
            binding.dotIndicator.visibility=View.VISIBLE
            binding.dotIndicator.attachTo(binding.viewPageSlider)
        }

    }

    private fun initBrand(){
        binding.progressBarBrand.visibility = View.VISIBLE
        viewModel.brands.observe(this, Observer {
        binding.viewBrand.layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
            binding.viewBrand.adapter =BrandAdapter(it)
            binding.progressBarBrand.visibility = View.GONE
        })
        viewModel.loadBrand()
    }

    private fun initpopular(){
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.popular.observe(this, Observer {
            binding.viewPopular.layoutManager = GridLayoutManager(this@MainActivity,2)
            binding.viewPopular.adapter = PopularAdapter(it)
            binding.progressBarPopular.visibility = View.GONE
        })
        viewModel.loadPupolar()
    }
}