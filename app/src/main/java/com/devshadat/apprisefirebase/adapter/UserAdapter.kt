package com.example.firebaservrealdbk.Adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.devshadat.apprisefirebase.R
import com.devshadat.apprisefirebase.data.User
import com.devshadat.apprisefirebase.data.Users
import java.lang.String.format
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    private val userList = ArrayList<Users>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_layout, parent, false
        )
        return MyViewHolder(itemView)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        holder.fullname.text = currentitem.fullname
        holder.username.text = currentitem.username
        holder.phone.text = currentitem.phone
        holder.timestamp.text =
            SimpleDateFormat("dd-MMM-yyyy hh-mm a").format(Date(currentitem.timestamp!!))
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(userList: List<Users>) {

        this.userList.clear()
//        this.userList.addAll(userList)

        for (user in userList) {
            if (user.status.equals("true")) this.userList.add(user)
        }

        this.userList.sortBy { it.timestamp }
        notifyDataSetChanged()

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val fullname: TextView = itemView.findViewById(R.id.usr_fullname)
        val username: TextView = itemView.findViewById(R.id.usr_username)
        val phone: TextView = itemView.findViewById(R.id.usr_phone)
        var timestamp: TextView = itemView.findViewById(R.id.usr_logged_time)
    }

}