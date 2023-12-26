package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.PackageFeatureAdapterBinding

class PackageFeatureAdapter(var context: Context, var data: ArrayList<String>) :
    RecyclerView.Adapter<PackageFeatureAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val packageFeatureAdapterBinding = PackageFeatureAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(packageFeatureAdapterBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(data[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val packageFeatureAdapterBinding: PackageFeatureAdapterBinding) : RecyclerView.ViewHolder(packageFeatureAdapterBinding.root) {

        fun bindItems(task: String) {
            //bind the item here


            packageFeatureAdapterBinding.serviceText.text = task.trim()



        }

    }

}