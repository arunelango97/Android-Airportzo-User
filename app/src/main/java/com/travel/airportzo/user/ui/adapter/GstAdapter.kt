package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.GstAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateGst

import kotlin.collections.ArrayList

class GstAdapter(var context: Context, private var checkoutGst: ArrayList<PassengerCreateGst>) : RecyclerView.Adapter<GstAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val gstAdapterBinding: GstAdapterBinding): RecyclerView.ViewHolder(gstAdapterBinding.root) {
        fun bindItems(task: PassengerCreateGst) {
            //bind the item here
            gstAdapterBinding.recentlyAddgstName.text = task.name
            gstAdapterBinding.addGstNo.text = task.gstin
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val gstAdapterBinding = GstAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(gstAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(checkoutGst[position])
    }

    override fun getItemCount(): Int {
        return checkoutGst.size
    }
}