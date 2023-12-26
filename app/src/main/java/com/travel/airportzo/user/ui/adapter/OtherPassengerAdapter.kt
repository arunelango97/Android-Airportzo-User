package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.OtherpassengerAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData

class OtherPassengerAdapter(
    var context: Context,
    private var bookingOtherPassengerData: ArrayList<GetOrderDetailData.Passenger_array>
) : RecyclerView.Adapter<OtherPassengerAdapter.MyViewHolder>() {
    inner class MyViewHolder(private var otherPassengerAdapterBinding: OtherpassengerAdapterBinding):RecyclerView.ViewHolder(otherPassengerAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(task: GetOrderDetailData.Passenger_array){
            otherPassengerAdapterBinding.checkoutPassengerName.text=task.name
            otherPassengerAdapterBinding.checkoutPassengerAge.text=task.age
            otherPassengerAdapterBinding.checkoutPassengerNo.text=task.country_code+task.mobile_number
            otherPassengerAdapterBinding.checkoutPassengerEmail.text=task.email_id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val otherPassengerAdapterBinding = OtherpassengerAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(otherPassengerAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(bookingOtherPassengerData[position])
    }

    override fun getItemCount(): Int {
        return bookingOtherPassengerData.size
    }
}