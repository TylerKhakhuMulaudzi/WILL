package com.example.secondsemster

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.secondsemster.databinding.ViewholderWishlistBinding
//import com.google.ai.client.generativeai.common.RequestOptions
import com.example.secondsemster.ManagmentWishlist

class WishlistAdapter (
    private val listItemsModel: ArrayList<ItemsModel>,
    context: Context
): RecyclerView.Adapter<WishlistAdapter.ViewHolder>(){

    class ViewHolder(val binding:ViewholderWishlistBinding):RecyclerView.ViewHolder(binding.root)

    private val managementWishlist = ManagmentWishlist(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderWishlistBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override  fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItemsModel[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "R${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.pic)

        holder.binding.removeBtn.setOnClickListener {
            managementWishlist.removeFromWishlist(position)
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int = listItemsModel.size
}