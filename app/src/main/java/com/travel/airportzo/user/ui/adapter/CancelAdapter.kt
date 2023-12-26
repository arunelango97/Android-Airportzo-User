package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.CancelAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData
import kotlin.collections.ArrayList

class CancelAdapter(
    var context: Context,
    private var bookingPackageData: ArrayList<GetOrderDetailData.Order_detail_array>,
    var onClick: (GetOrderDetailData.Order_detail_array, CancelAdapter.MyViewHolder) -> Unit
) : RecyclerView.Adapter<CancelAdapter.MyViewHolder>() {

    inner class MyViewHolder(var cancelAdapterBinding: CancelAdapterBinding) :
        RecyclerView.ViewHolder(cancelAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(
            task: GetOrderDetailData.Order_detail_array,
            myView: CancelAdapter.MyViewHolder
        ) {
            onClick(task, myView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cancelAdapterBinding =
            CancelAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(cancelAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(bookingPackageData[position], holder)
    }

    override fun getItemCount(): Int {
        return bookingPackageData.size
    }
}