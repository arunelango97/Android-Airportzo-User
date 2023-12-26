package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.NotificationAdapterBinding
import com.travel.airportzo.user.model.NotificationData
import java.util.ArrayList

class NotificationAdapter(var context: Context, private var  notificationData: ArrayList<NotificationData>) :RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    inner class MyViewHolder(var notificationAdapterBinding: NotificationAdapterBinding):RecyclerView.ViewHolder(notificationAdapterBinding.root) {
        fun bindItems(task: NotificationData) {
            //bind the item here
            Glide.with(context).load(task.bannerimage).into(notificationAdapterBinding.image)

            notificationAdapterBinding.description.text = task.notificationtitle
            notificationAdapterBinding.name.text = task.notificationdes
            notificationAdapterBinding.date.text = task.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val notificationAdapterBinding = NotificationAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(notificationAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
holder.bindItems(notificationData[position])
    }

    override fun getItemCount(): Int {
     return notificationData.size
    }
}