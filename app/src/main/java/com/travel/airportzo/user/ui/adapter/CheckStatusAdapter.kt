package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BookingserviceAdapterBinding
import com.travel.airportzo.user.databinding.CancelAdapterBinding
import com.travel.airportzo.user.databinding.CheckStatusItemBinding
import com.travel.airportzo.user.model.GetOrderDetailData

class CheckStatusAdapter(
    var context: Context,
    private var bookingPackageData: ArrayList<GetOrderDetailData.Order_detail_array>
) : RecyclerView.Adapter<CheckStatusAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(var bookingServiceAdapterBinding: CheckStatusItemBinding) :
        RecyclerView.ViewHolder(bookingServiceAdapterBinding.root) {

        val context: Context = bookingServiceAdapterBinding.root.context

        @SuppressLint("SetTextI18n")
        fun bindItems(
            task: GetOrderDetailData.Order_detail_array,
            holder: CheckStatusAdapter.StatusViewHolder,
            currentPosition: Int
        ) {


            bookingServiceAdapterBinding.airportName.text = task.airport_name
            bookingServiceAdapterBinding.companyName.text = task.company_name
            bookingServiceAdapterBinding.cancelAdapterServiceDateTime.text =
                "${task.service_date_time} (GMT ${task.airport_gmt})"
            bookingServiceAdapterBinding.cancelAdapterServiceCost.text =
                "${context.getString(R.string.indianRupee)} ${task.net_amount}"
            bookingServiceAdapterBinding.cancelAdapterCancellationFee.text =
                "${context.getString(R.string.indianRupee)} ${task.cancellation_detail.cancellation_fee}"
            bookingServiceAdapterBinding.convenienceFee.text =
                "${context.getString(R.string.indianRupee)} ${(task.agent_conv_fee.toInt() + task.user_conv_fee.toInt())}"
            bookingServiceAdapterBinding.cancelAdapterRefundAmount.text =
                "${context.getString(R.string.indianRupee)} ${task.cancellation_detail.refund_amount}"
            bookingServiceAdapterBinding.cancelAdapterPlatformFees.text =
                "${context.getString(R.string.indianRupee)} ${task.cancellation_detail.airportzo_fee}"


            val discount = task.adult_discount.toInt() + task.child_discount.toInt() +
                    task.add_adult_discount.toInt() +task.add_child_discount.toInt()

            bookingServiceAdapterBinding.discountAmount.text =
                "${context.getString(R.string.indianRupee)} $discount"
            if (task.cancellation_detail.cancellation_hours == "0") {
                bookingServiceAdapterBinding.cancelAdapterCancelBeforeHours.text = "-"
            } else {
                bookingServiceAdapterBinding.cancelAdapterCancelBeforeHours.text =
                    "${task.cancellation_detail.cancellation_hours} Hrs"
            }

            context.let {
                Glide.with(it).load(task.company_logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(bookingServiceAdapterBinding.companyImage)
            }

            bookingServiceAdapterBinding.status.visibility = View.VISIBLE
            bookingServiceAdapterBinding.serviceStatus.visibility = View.VISIBLE
            bookingServiceAdapterBinding.statusView.visibility = View.GONE

            bookingServiceAdapterBinding.serviceStatus.background = ContextCompat.getDrawable(context, R.drawable.completed_package)
            bookingServiceAdapterBinding.serviceStatus.setTextColor(ContextCompat.getColor(context, R.color.green))



            when(task.status){
                "Cancelled" -> {
                    bookingServiceAdapterBinding.status.text = "Refund Status"
                    bookingServiceAdapterBinding.serviceStatus.text = task.cancellation_detail.refund_status
                }
                else -> {
                    bookingServiceAdapterBinding.status.text = "Service Status"
                    bookingServiceAdapterBinding.serviceStatus.text = task.status
                }
            }


            if (task.cancelled_by.lowercase() != "service provider"){
                bookingServiceAdapterBinding.convenienceLayout.visibility = View.GONE
            }else{
                bookingServiceAdapterBinding.convenienceLayout.visibility = View.VISIBLE
            }


//            when(task.status){
//                "Pending" -> {
//                    bookingServiceAdapterBinding.statusView.text = task.status
//                    bookingServiceAdapterBinding.statusView.background = ContextCompat.getDrawable(context, R.drawable.pending_roundcorner)
//                    bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pending_timer, 0, 0, 0)
//                    bookingServiceAdapterBinding.statusView.setTextColor(ContextCompat.getColor(context, R.color.pending_text))
//                }
//                "Cancelled" -> {
//
//
//                    bookingServiceAdapterBinding.statusView.text = task.status
//                    bookingServiceAdapterBinding.statusView.background = ContextCompat.getDrawable(context, R.drawable.cancelled_package)
//                    bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_1x, 0, 0, 0)
//                    bookingServiceAdapterBinding.statusView.setTextColor(ContextCompat.getColor(context, R.color.red))
//                }
//                "Completed" -> {
//                    bookingServiceAdapterBinding.statusView.text = task.status
//                    bookingServiceAdapterBinding.statusView.background = ContextCompat.getDrawable(context, R.drawable.completed_package)
//                    bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_completed_package, 0, 0, 0)
//                    bookingServiceAdapterBinding.statusView.setTextColor(ContextCompat.getColor(context, R.color.green))
//                }
//            }

            bookingServiceAdapterBinding.statusView.setPadding(10)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val cancelAdapterBinding =
            CheckStatusItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StatusViewHolder(cancelAdapterBinding)
    }

    override fun getItemCount(): Int = bookingPackageData.size

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) =
        holder.bindItems(bookingPackageData[position], holder, position)
}