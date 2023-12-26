package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.WifiAdapterBinding

class WifiAdapter (var context: Context, var dataList: ArrayList<String>) :
    RecyclerView.Adapter<WifiAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val wifiAdapterBinding = WifiAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(wifiAdapterBinding)


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val wifiAdapterBinding: WifiAdapterBinding) : RecyclerView.ViewHolder(wifiAdapterBinding.root) {

        fun bindItems(task: String) {
            val context = wifiAdapterBinding.root.context
            //bind the item here
            wifiAdapterBinding.wifi.text = task

        }
    }
}