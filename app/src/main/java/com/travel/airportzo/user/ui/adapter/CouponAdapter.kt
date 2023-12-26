package com.travel.airportzo.user.ui.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CouponItemBinding
import com.travel.airportzo.user.model.CouponData
import com.travel.airportzo.user.utils.setOnDebounceListener

class CouponAdapter(var dataList: ArrayList<CouponData>, val onclicked:(String) -> Unit) : RecyclerView.Adapter<CouponAdapter.CouponHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponHolder {
        val couponItemBinding = CouponItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponHolder(couponItemBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int = dataList.size

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CouponHolder, position: Int) =
        holder.bindItems(dataList[position])

    //the class is holding the list view
    inner class CouponHolder(private val couponItemBinding: CouponItemBinding) : RecyclerView.ViewHolder(couponItemBinding.root) {
        private val context = couponItemBinding.root.context
        fun bindItems(task: CouponData) {
            //bind the item here
            couponItemBinding.couponName.text = task.name
            couponItemBinding.date.text = task.to_date
            couponItemBinding.description.text = task.description
            couponItemBinding.coupon.text = task.code
            itemView.setOnDebounceListener { onclicked(task.code) }
        }

    }

}