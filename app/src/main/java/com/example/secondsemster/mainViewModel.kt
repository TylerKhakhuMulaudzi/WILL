package com.example.secondsemster

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class mainViewModel():ViewModel() {

    private val firebaseDatabase=FirebaseDatabase.getInstance()

    private val _banner=MutableLiveData<List<slideModel>>()
    private val _brand = MutableLiveData<MutableList<BrandModel>>()
    private val _popular = MutableLiveData<MutableList<ItemsModel>>()

    val brands:LiveData<MutableList<BrandModel>> =_brand
    val popular:LiveData<MutableList<ItemsModel>> =_popular
    val banners: LiveData<List<slideModel>> = _banner

    fun loadBanners() {
        val Ref = firebaseDatabase.getReference("Banner")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<slideModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(slideModel::class.java)
                    if (list != null) {
                        lists.add(list)
                        Log.d("MainViewModel", "Banner loaded: ${list.url}")
                    } else {
                        Log.w("MainViewModel", "Null banner data encountered for key: ${childSnapshot.key}")
                    }
                }
                _banner.value = lists
                Log.d("MainViewModel", "Total banners loaded: ${lists.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error loading banners: ${error.message}")
            }
        })
    }
    fun loadBrands() {
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BrandModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(BrandModel::class.java)
                    if (list != null) {
                        lists.add(list)
                        Log.d("MainViewModel", "Brand loaded: ${list.title}, Image URL: ${list.picUrl}")
                    } else {
                        Log.w("MainViewModel", "Null brand data encountered for key: ${childSnapshot.key}")
                    }
                }
                _brand.value = lists
                Log.d("MainViewModel", "Total brands loaded: ${lists.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error loading brands: ${error.message}")
            }
        })
    }
    fun loadPopularItems() {
        val Ref = firebaseDatabase.getReference("Items")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) {
                        lists.add(list)
                        Log.d("MainViewModel", "Item loaded: ${list.title}, Price: ${list.price}, First Image URL: ${list.picUrl.firstOrNull()}")
                    } else {
                        Log.w("MainViewModel", "Null item data encountered for key: ${childSnapshot.key}")
                    }
                }
                _popular.value = lists
                Log.d("MainViewModel", "Total popular items loaded: ${lists.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error loading popular items: ${error.message}")
            }
        })
    }
}