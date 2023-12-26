package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.PackageServiceAdapterBinding


class PackageServiceAdapter(var context: Context, private var businessNames: ArrayList<String>):RecyclerView.Adapter<PackageServiceAdapter.MyViewHolder>() {
    inner class MyViewHolder(private var packageServiceAdapterBinding: PackageServiceAdapterBinding) :
        RecyclerView.ViewHolder(packageServiceAdapterBinding.root) {
        fun bindItems(task: String) {
            //bind the item here
            packageServiceAdapterBinding.serviceText.text = task
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageServiceAdapter.MyViewHolder {
        val packageServiceAdapterBinding = PackageServiceAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(packageServiceAdapterBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return businessNames.size
    }

    //this method is binding the data on the list

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(businessNames[position])
    }
}

    //the class is holding the list view


