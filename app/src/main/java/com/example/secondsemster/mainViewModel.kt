package com.example.secondsemster

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
    private val _popular = MutableLiveData<MutableList<itemModel>>()

    val brands:LiveData<MutableList<BrandModel>> =_brand
    val popular:LiveData<MutableList<itemModel>> =_popular
    val banners: LiveData<List<slideModel>> = _banner

    fun loadBanners(){
       val Ref = firebaseDatabase.getReference("Banner")
       Ref.addValueEventListener(object: ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               val lists = mutableListOf<slideModel>()
               for (childSnapshot in snapshot.children){
                   val list = childSnapshot.getValue(slideModel::class.java)
                   if (list!=null){
                       lists.add(list)
                   }
               }
               _banner.value = lists
           }

           override fun onCancelled(error: DatabaseError) {

           }


       })
    }
    fun loadBrand(){
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BrandModel>()
                for (childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(BrandModel::class.java)
                    if (list!=null){
                       lists.add(list)
                    }
                    _brand.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun loadPupolar(){
        val Ref = firebaseDatabase.getReference("Items")
        Ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<itemModel>()
                for (childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(itemModel::class.java)
                    if (list!=null){
                        lists.add(list)
                    }
                    _popular.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}