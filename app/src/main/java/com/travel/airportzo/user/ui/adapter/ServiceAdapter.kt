package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.ServiceAdapterBinding
import com.travel.airportzo.user.model.ColorData
import com.travel.airportzo.user.model.ReadServiceJson
import com.travel.airportzo.user.model.ServiceData
import com.travel.airportzo.user.utils.setOnDebounceListener

class ServiceAdapter(
    var context: Context,
    var dataList: ArrayList<ReadServiceJson.DataItem>,
    private var colorData: ArrayList<ColorData>,
    var onClicked: (String, Int) -> Unit
) :
    RecyclerView.Adapter<ServiceAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val serviceAdapterBinding: ServiceAdapterBinding) :
        RecyclerView.ViewHolder(serviceAdapterBinding.root) {

        fun bindItems(task: ReadServiceJson.DataItem, colorData: ColorData) {
            serviceAdapterBinding.aAmount.text = task.price.toString()
            serviceAdapterBinding.sServiceName.text = task.name
            serviceAdapterBinding.card.setOnDebounceListener {
                colorData.colorbtn?.let { it1 ->
                    onClicked(
                        task.token,
                        it1
                    )
                }
            }
            serviceAdapterBinding.backGround.setBackgroundColor(colorData.color!!)
            serviceAdapterBinding.button.setBackgroundColor(colorData.colorbtn!!)
            serviceAdapterBinding.sServiceName.setTextColor(colorData.colorbtn)
            Glide.with(context).load(task.image).into(serviceAdapterBinding.topImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val serviceAdapterBinding =
            ServiceAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(serviceAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position], colorData[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}