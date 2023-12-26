package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.BookinglocationAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData

class BookingLocationAdapter(
    var context: Context,
    var bookingLocationData: ArrayList<GetOrderDetailData.Order_detail>,
    var out:(GetOrderDetailData.Order_detail)->Unit,
    var onReview:(GetOrderDetailData.Order_detail_array)->Unit,
    var onAddComment:(GetOrderDetailData.Order_detail_array)->Unit,
    var viewReport:(GetOrderDetailData.Order_detail_array)->Unit,
    var onclicked:(GetOrderDetailData.Order_detail_array,BookingServiceAdapter.MyViewHolder)->Unit,
    var onCancel:(GetOrderDetailData.Order_detail_array,BookingServiceAdapter.MyViewHolder)->Unit,
    var onChatClick:(GetOrderDetailData.Order_detail_array,BookingServiceAdapter.MyViewHolder)->Unit):
    RecyclerView.Adapter<BookingLocationAdapter.MyViewHolder>() {
    var name:String=""

    inner class MyViewHolder(private val bookingLocationAdapterBinding: BookinglocationAdapterBinding):
        RecyclerView.ViewHolder(bookingLocationAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(task: GetOrderDetailData.Order_detail) {
            //bind the item here

            out(task)

            bookingLocationAdapterBinding.bookingAirportName.text = task.airport_code
            bookingLocationAdapterBinding.bookingAirportTerminal.text = task.airport_name+"-"+task.terminal_name
//            bookingLocationAdapterBinding.bookingAirportTiming.text=PackageServiceFragment.datee[absoluteAdapterPosition].toString() + PackageServiceFragment.timee[absoluteAdapterPosition].toString()

            bookingLocationAdapterBinding.summaryLocationRecyclerview.adapter = BookingServiceAdapter(context, bookingLocationData[absoluteAdapterPosition].order_detail_array,::onReview,::onViewRating,::reportView,::clicked,::onClickedCancel,::onChat)
        }
    }

    private fun onChat(orderDetailArray: GetOrderDetailData.Order_detail_array, myViewHolder: BookingServiceAdapter.MyViewHolder) {
   onChatClick(orderDetailArray,myViewHolder)
    }

    private fun onClickedCancel(orderDetailArray: GetOrderDetailData.Order_detail_array, myViewHolder: BookingServiceAdapter.MyViewHolder) {
        onCancel(orderDetailArray,myViewHolder)
    }

    private fun onReview(orderDetailArray: GetOrderDetailData.Order_detail_array, myViewHolder: BookingServiceAdapter.MyViewHolder) {
        onReview(orderDetailArray)
    }
    private fun onViewRating(orderDetailArray: GetOrderDetailData.Order_detail_array, myViewHolder: BookingServiceAdapter.MyViewHolder) {
        onAddComment(orderDetailArray)
    }

    private fun reportView(orderDetailArray: GetOrderDetailData.Order_detail_array, myViewHolder: BookingServiceAdapter.MyViewHolder) {
        viewReport(orderDetailArray)
    }

    private fun clicked(orderDetailArray: GetOrderDetailData.Order_detail_array,holder:BookingServiceAdapter.MyViewHolder) {
        onclicked(orderDetailArray,holder)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bookingLocationAdapterBinding = BookinglocationAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(bookingLocationAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems( bookingLocationData[position])
    }

    override fun getItemCount(): Int {
        return bookingLocationData.size
    }
}

