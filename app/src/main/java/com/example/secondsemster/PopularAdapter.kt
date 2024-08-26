package com.example.secondsemster

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.secondsemster.databinding.ViewholderRecommendedBinding

class PopularAdapter(val items:MutableList<ItemsModel>):RecyclerView.Adapter<PopularAdapter.ViewHolder>() {
    private var context: Context? = null
    class ViewHolder (val binding: ViewholderRecommendedBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.ViewHolder {
        context = parent.context
        val binding = ViewholderRecommendedBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularAdapter.ViewHolder, position: Int) {
        holder.binding.titleTxt.text = items[position].title
        holder.binding.priceTxt.text = "R"+items[position].price.toString()
        holder.binding.ratingTxt.text = items[position].rating.toString()

        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(holder.itemView.context)
            .load(items[position].picUrl[0])
            .apply(requestOptions)
            .into(holder.binding.pic)

        /*holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context)
        }*/
    }

    override fun getItemCount(): Int = items.size
}