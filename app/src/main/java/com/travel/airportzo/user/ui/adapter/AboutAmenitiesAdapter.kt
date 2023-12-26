package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.AboutAmentiesAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData

class AboutAmenitiesAdapter(var context: Context, var dataList: ArrayList<ServiceTicketData.Amenities>) :
    RecyclerView.Adapter<AboutAmenitiesAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val aboutAmenitiesAdapter = AboutAmentiesAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(aboutAmenitiesAdapter)
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
    inner class MyViewHolder(private val aboutAmenitiesAdapterBinding: AboutAmentiesAdapterBinding) : RecyclerView.ViewHolder(aboutAmenitiesAdapterBinding.root) {

        fun bindItems(task: ServiceTicketData.Amenities) {
            val context = aboutAmenitiesAdapterBinding.root.context
            //bind the item here

            Glide.with(context).load(task.image).into(aboutAmenitiesAdapterBinding.amenitiesImage)
            aboutAmenitiesAdapterBinding.amenitiesName.text = task.name



        }

    }

}