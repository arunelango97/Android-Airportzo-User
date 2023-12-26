package com.travel.airportzo.user.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BookingticketAdapterBinding
import com.travel.airportzo.user.model.MyBookingData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener

class BookingTicketAdapter (var dataList: ArrayList<MyBookingData>, var onClicked: (String) -> Unit) :
    RecyclerView.Adapter<BookingTicketAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bookingTicketAdapter = BookingticketAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
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
    inner class MyViewHolder(var bookingTicketAdapterBinding: BookingticketAdapterBinding) : RecyclerView.ViewHolder(bookingTicketAdapterBinding.root) {

        private val context = bookingTicketAdapterBinding.root.context

        fun bindItems(task: MyBookingData) {

            /** brand color*/
            bookingTicketAdapterBinding.nameView.setBackgroundColor(Color
                .parseColor(SavedSharedPreference.getCustomColor(itemView.context).brand_colour))


            bookingTicketAdapterBinding.aAirportList.text = task.journey
            bookingTicketAdapterBinding.dDataAndTime.text = task.service_dates[0]
            bookingTicketAdapterBinding.pPassengerListCount.text = task.total_adult
            if (task.total_children == "0"){
                bookingTicketAdapterBinding.childrenListCount.visibility = View.GONE
                bookingTicketAdapterBinding.childrenList.visibility = View.GONE
            }
            bookingTicketAdapterBinding.childrenListCount.text = task.total_children
            bookingTicketAdapterBinding.serviceCount.text = task.total_service
            bookingTicketAdapterBinding.aAmount.text = "${context.getString(R.string.indianRupee)}${task.total_amount}"
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
                    bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pending_timer, 0, 0, 0)
                    bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.pending_text))
                }
                "Cancelled" -> {
                    bookingTicketAdapterBinding.pendingStatus.text = task.status
                    bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.cancelled_package)
                    bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_1x, 0, 0, 0)
                    bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                "Completed" -> {
                    bookingTicketAdapterBinding.pendingStatus.text = task.status
                    bookingTicketAdapterBinding.pendingStatus.background = ContextCompat.getDrawable(context, R.drawable.completed_package)
                    bookingTicketAdapterBinding.pendingStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_completed_package, 0, 0, 0)
                    bookingTicketAdapterBinding.pendingStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                }
                else -> bookingTicketAdapterBinding.pendingStatus.visibility = View.GONE
            }

            bookingTicketAdapterBinding.pendingStatus.setPadding(10)

        }

    }

}