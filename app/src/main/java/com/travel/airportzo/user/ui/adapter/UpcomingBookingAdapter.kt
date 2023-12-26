package com.travel.airportzo.user.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BookingticketAdapterBinding
import com.travel.airportzo.user.databinding.UpcomingbookingItemBinding
import com.travel.airportzo.user.model.ReadServiceJson
import com.travel.airportzo.user.utils.setOnDebounceListener

class UpcomingBookingAdapter (var dataList: ArrayList<ReadServiceJson.BookingDataItem>, var onClicked: (String) -> Unit) :
    RecyclerView.Adapter<UpcomingBookingAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bookingTicketAdapter = UpcomingbookingItemBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(bookingTicketAdapter)
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
    inner class MyViewHolder(var bookingTicketAdapterBinding: UpcomingbookingItemBinding) : RecyclerView.ViewHolder(bookingTicketAdapterBinding.root) {

        private val context = bookingTicketAdapterBinding.root.context

        fun bindItems(task: ReadServiceJson.BookingDataItem) {
            bookingTicketAdapterBinding.aAirportList.text = task.journey
            bookingTicketAdapterBinding.dDataAndTime.text = task.serviceDates[0]
            bookingTicketAdapterBinding.pPassengerListCount.text = task.totalAdult
            if (task.totalChildren == "0"){
                bookingTicketAdapterBinding.childrenListCount.visibility = View.GONE
                bookingTicketAdapterBinding.childrenList.visibility = View.GONE
            }
            bookingTicketAdapterBinding.childrenListCount.text = task.totalChildren
            bookingTicketAdapterBinding.serviceCount.text = task.totalService
            bookingTicketAdapterBinding.aAmount.text = "${context.getString(R.string.indianRupee)}${task.totalAmount}"
            bookingTicketAdapterBinding.pendingStatus.text = task.status

            if (task.status=="Cancelled") {
                bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.cancelled_package)
                bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_1x, 0, 0 , 0)
            } else if (task.status=="Completed") {
                bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.completed_package)
                bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_completed_package, 0, 0 , 0)
            }

            bookingTicketAdapterBinding.pendingStatus.setPadding(10)

            bookingTicketAdapterBinding.layout.setOnDebounceListener { onClicked(task.token) }

            when(task.status){
                "Pending" -> {
                    bookingTicketAdapterBinding.pendingStatus.text = task.status
                    bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.pending_roundcorner)
                    bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.pending_timer, 0, 0, 0)
                    bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.pending_text))
                }
                "Cancelled" -> {
                    bookingTicketAdapterBinding.pendingStatus.text = task.status
                    bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.cancelled_package)
                    bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_cancel_1x, 0, 0, 0)
                    bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                "Completed" -> {
                    bookingTicketAdapterBinding.pendingStatus.text = task.status
                    bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.completed_package)
                    bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_completed_package, 0, 0, 0)
                    bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                }
                else -> bookingTicketAdapterBinding.pendingStatus.visibility = View.GONE
            }

            bookingTicketAdapterBinding.pendingStatus.setPadding(10)

        }

    }

}