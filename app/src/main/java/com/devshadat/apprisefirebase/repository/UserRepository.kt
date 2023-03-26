package com.devshadat.apprisefirebase.repository

import androidx.lifecycle.MutableLiveData
import com.devshadat.apprisefirebase.data.User
import com.devshadat.apprisefirebase.data.Users
import com.google.firebase.database.*

class UserRepository {

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    @Volatile
    private var INSTANCE: UserRepository? = null

    fun getInstance(): UserRepository {
        return INSTANCE ?: synchronized(this) {

            val instance = UserRepository()
            INSTANCE = instance
            instance
        }


    }


    fun loadUsers(userList : MutableLiveData<List<Users>>){

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    val _userList : List<Users> = snapshot.children.map { dataSnapshot ->

                        dataSnapshot.getValue(Users::class.java)!!

                    }


                    userList.postValue(_userList)

                }catch (e : Exception){

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }

}