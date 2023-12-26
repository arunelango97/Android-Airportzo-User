package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.BookingpackageAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData

class BookingPackageAdapter(var context: Context, var bookingPackageData: ArrayList<GetOrderDetailData.Order_detail_array>,
) : RecyclerView.Adapter<BookingPackageAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val bookingPackageAdapterBinding: BookingpackageAdapterBinding):
        RecyclerView.ViewHolder(bookingPackageAdapterBinding.root) {
        fun bindItems(task: GetOrderDetailData.Order_detail_array) {
            //bind the item here
            bookingPackageAdapterBinding.bookingPackageName.text = task.service_name
            bookingPackageAdapterBinding.bookingPackageCount.text = task.total_adult
            bookingPackageAdapterBinding.bookingPackagePrice.text = task.net_amount

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bookingPackageAdapterBinding = BookingpackageAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(bookingPackageAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(bookingPackageData[position])
    }

    override fun getItemCount(): Int {
        return  bookingPackageData.size
    }
}