package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ReviewAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData

class ReviewAdapter(var context: Context, var dataList: ArrayList<ServiceTicketData.Reviews>) :
    RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val reviewAdapterBinding = ReviewAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(reviewAdapterBinding)
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
    inner class MyViewHolder(private val reviewAdapterBinding: ReviewAdapterBinding) : RecyclerView.ViewHolder(reviewAdapterBinding.root) {

        fun bindItems(task: ServiceTicketData.Reviews) {
            val context = reviewAdapterBinding.root.context
            //bind the item here

            Glide.with(context).load(task.image).apply(RequestOptions.circleCropTransform()).error(R.drawable.user).into(reviewAdapterBinding.cServerImage)
            reviewAdapterBinding.cServerName.text = task.name
            reviewAdapterBinding.cServiceReviewTime.text = task.review_date_time
            reviewAdapterBinding.cServiceRating.rating=task.rating.toFloat()
            reviewAdapterBinding.cServiceReview.text = task.review



        }

    }

}