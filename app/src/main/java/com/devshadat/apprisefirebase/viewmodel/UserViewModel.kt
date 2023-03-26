package com.devshadat.apprisefirebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devshadat.apprisefirebase.data.User
import com.devshadat.apprisefirebase.data.Users
import com.devshadat.apprisefirebase.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository: UserRepository
    private val _allUsers = MutableLiveData<List<Users>>()
    val allUsers: LiveData<List<Users>> = _allUsers

    init {
        repository = UserRepository().getInstance()
        repository.loadUsers(_allUsers)

    }

}