package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.HometopserviceAdapterBinding
import java.util.ArrayList

//class HomeTopServiceAdapter(
//   var context: Context,
//   var topserviceimageData: ArrayList<HomeServiceData.Topservice>
//) :RecyclerView.Adapter<HomeTopServiceAdapter.MyViewHolder>() {
//  inner class MyViewHolder(val hometopserviceAdapterBinding: HometopserviceAdapterBinding):RecyclerView.ViewHolder(hometopserviceAdapterBinding.root) {
//      fun bindItems(task: HomeServiceData.Topservice) {
//          //bind the item here
//          Glide.with(context).load(task.topserviceimages).into(hometopserviceAdapterBinding.topserviceimage)
//      }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val hometopserviceAdapterBinding = HometopserviceAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MyViewHolder(hometopserviceAdapterBinding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//     holder.bindItems(topserviceimageData[position])
//    }
//
//    override fun getItemCount(): Int {
//        return topserviceimageData.size
//    }
//}