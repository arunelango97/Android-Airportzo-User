package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.SavedGstAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateGst
import com.travel.airportzo.user.utils.setOnDebounceListener

class SavedGstAdapter(
    var context: Context,
    private var passengerSavedGst: ArrayList<PassengerCreateGst>,
    var onClicked:(String)->Unit)
    :RecyclerView.Adapter<SavedGstAdapter.MyViewHolder>() {
   inner class MyViewHolder(var savedGstAdapterBinding: SavedGstAdapterBinding):RecyclerView.ViewHolder(savedGstAdapterBinding.root) {
       fun bindItems(task: PassengerCreateGst) {
           //bind the item here
           savedGstAdapterBinding.recentlyAddgstName.text = task.name
           savedGstAdapterBinding.addGstNo.text = task.gstin
           savedGstAdapterBinding.recentlyAddGstLayout.setOnDebounceListener {onClicked(task.token)}
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val savedGstAdapterBinding = SavedGstAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(savedGstAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
  holder.bindItems(passengerSavedGst[position])
    }

    override fun getItemCount(): Int {
       return passengerSavedGst.size
    }
}