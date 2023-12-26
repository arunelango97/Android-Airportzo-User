package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.HomeofferAdapterBinding
import java.util.ArrayList

//class HomeOfferAdapter(var context: Context,var offerimageData: ArrayList<HomeServiceData.Image>) :RecyclerView.Adapter<HomeOfferAdapter.MyViewHolder>() {
//   inner class MyViewHolder(val homeofferAdapterBinding: HomeofferAdapterBinding):RecyclerView.ViewHolder(homeofferAdapterBinding.root) {
//       fun bindItems(task: HomeServiceData.Image) {
//           //bind the item here
//           Glide.with(context).load(task.offerimage).into(homeofferAdapterBinding.offerimage)
//       }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val homeofferAdapterBinding = HomeofferAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MyViewHolder(homeofferAdapterBinding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//       holder.bindItems(offerimageData[position])
//    }
//
//    override fun getItemCount(): Int {
//       return  offerimageData.size
//    }
//}