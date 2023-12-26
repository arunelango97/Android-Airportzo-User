package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.GstAdapterBinding
import com.travel.airportzo.user.model.CheckoutData
import java.util.ArrayList

class GstSavedAdapter(var context: Context, private var checkoutGstSaved: ArrayList<CheckoutData.Gst>) :RecyclerView.Adapter<GstSavedAdapter.MyViewHolder>() {
   inner class MyViewHolder(private val gstAdapterBinding: GstAdapterBinding):RecyclerView.ViewHolder(gstAdapterBinding.root) {
       fun bindItems(task: CheckoutData.Gst) {
           //bind the item here
           gstAdapterBinding.recentlyAddgstName.text = task.gstName
           gstAdapterBinding.addGstNo.text = task.gstno
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val gstAdapterBinding = GstAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(gstAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(checkoutGstSaved[position])
    }

    override fun getItemCount(): Int {
        return checkoutGstSaved.size
    }
}