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
}