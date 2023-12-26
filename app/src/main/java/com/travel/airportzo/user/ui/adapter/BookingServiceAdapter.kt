package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BookingserviceAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData
import com.travel.airportzo.user.utils.setOnDebounceListener

class BookingServiceAdapter(
    var context: Context,
    private var bookingPackageData: ArrayList<GetOrderDetailData.Order_detail_array>,
    var onReview: (GetOrderDetailData.Order_detail_array, BookingServiceAdapter.MyViewHolder) -> Unit,
    var onViewRating: (GetOrderDetailData.Order_detail_array, BookingServiceAdapter.MyViewHolder) -> Unit,
    var onViewReport: (GetOrderDetailData.Order_detail_array, BookingServiceAdapter.MyViewHolder) -> Unit,
    var onClick: (GetOrderDetailData.Order_detail_array, BookingServiceAdapter.MyViewHolder) -> Unit,
    var onClickCancel: (GetOrderDetailData.Order_detail_array, BookingServiceAdapter.MyViewHolder) -> Unit,
    var onClickChat: (GetOrderDetailData.Order_detail_array, BookingServiceAdapter.MyViewHolder) -> Unit
) : RecyclerView.Adapter<BookingServiceAdapter.MyViewHolder>() {

    var notes: String = ""
    private val note by lazy {
        context.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_notes, null))
                setCancelable(true)
            }
        }
    }

    inner class MyViewHolder(var bookingServiceAdapterBinding: BookingserviceAdapterBinding) :
        RecyclerView.ViewHolder(bookingServiceAdapterBinding.root) {
//

        @SuppressLint("SetTextI18n")
        fun bindItems(
            task: GetOrderDetailData.Order_detail_array,
            holder: BookingServiceAdapter.MyViewHolder,
            currentPosition: Int
        ) {

            if (task.status == "Cancelled") {
                bookingServiceAdapterBinding.statusView.text = "${task.status} by ${task.cancelled_by}"
                bookingServiceAdapterBinding.statusView.background =
                    ContextCompat.getDrawable(context, R.drawable.cancelled_package)
                bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_cancel_1x,
                    0,
                    0,
                    0
                )
                bookingServiceAdapterBinding.statusView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )

                bookingServiceAdapterBinding.bookingNotes.visibility = View.GONE
//                bookingServiceAdapterBinding.statusText.visibility = View.VISIBLE


                bookingServiceAdapterBinding.cCardName.text = task.company_name
                bookingServiceAdapterBinding.bookingServiceOrderId.text = task.token
                bookingServiceAdapterBinding.bookingServiceTime.text =
                    "${task.service_date_time} (GMT ${task.airport_gmt})}"
                bookingServiceAdapterBinding.bookingPackageName.text = task.service_name
                bookingServiceAdapterBinding.bookingPackageCount.text = task.total_adult
                bookingServiceAdapterBinding.bookingPackagePrice.text = "₹".plus(task.net_amount)

//                task.status=="Assign" || task.status=="Ongoing"

            } else if (task.has_chat) {
                bookingServiceAdapterBinding.statusText.visibility = View.GONE
                bookingServiceAdapterBinding.message.visibility = View.VISIBLE

                bookingServiceAdapterBinding.cCardName.text = task.company_name
                bookingServiceAdapterBinding.bookingServiceOrderId.text = task.token
                bookingServiceAdapterBinding.bookingServiceTime.text =
                    "${task.service_date_time} (GMT ${task.airport_gmt})}"
                bookingServiceAdapterBinding.bookingPackageName.text = task.service_name
                bookingServiceAdapterBinding.bookingPackageCount.text = task.total_adult
                bookingServiceAdapterBinding.bookingPackagePrice.text = "₹".plus(task.net_amount)

            } else if (task.status == "Completed") {

                bookingServiceAdapterBinding.statusView.text = task.status
                bookingServiceAdapterBinding.statusView.background =
                    ContextCompat.getDrawable(context, R.drawable.completed_package)
                bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_completed_package,
                    0,
                    0,
                    0
                )
                bookingServiceAdapterBinding.statusView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )


                bookingServiceAdapterBinding.statusText.visibility = View.GONE

                if (TextUtils.isEmpty(task.report_description)){
                    bookingServiceAdapterBinding.cCardCloseImg.visibility = View.VISIBLE
                }else{
                    bookingServiceAdapterBinding.viewReport.visibility = View.VISIBLE
                }

                bookingServiceAdapterBinding.message.visibility = View.GONE
                bookingServiceAdapterBinding.bookingNotes.visibility = View.GONE
                bookingServiceAdapterBinding.bookingRateUs.visibility = View.VISIBLE


                if (task.rating == "0") {
                    bookingServiceAdapterBinding.bookingRateUs.visibility = View.VISIBLE
                    bookingServiceAdapterBinding.bookingRateUs.setOnDebounceListener {
                        onReview(task, holder)
                    }
                } else {
//                    if (task.comment == ""){
                        bookingServiceAdapterBinding.bookingRateUs.visibility = View.GONE
                        bookingServiceAdapterBinding.viewRating.visibility = View.VISIBLE
                        bookingServiceAdapterBinding.viewRating.setOnDebounceListener {
                            onViewRating(task, holder)
                        }
//                    }else{
//                        bookingServiceAdapterBinding.bookingRateUs.visibility = View.GONE
//                        bookingServiceAdapterBinding.ratingBar.visibility = View.VISIBLE
//                        bookingServiceAdapterBinding.ratingBar.rating = task.rating.toFloat()
//                    }
                }

                bookingServiceAdapterBinding.cCardCloseImg.setOnDebounceListener {
                    bookingServiceAdapterBinding.spinner.performClick()
                    onClick(task, holder)
                }

                bookingServiceAdapterBinding.cCardName.text = task.company_name
                bookingServiceAdapterBinding.bookingServiceOrderId.text = task.token
                bookingServiceAdapterBinding.bookingServiceTime.text =
                    "${task.service_date_time} (GMT ${task.airport_gmt})}"
                bookingServiceAdapterBinding.bookingPackageName.text = task.service_name
                bookingServiceAdapterBinding.bookingPackageCount.text = task.total_adult
                bookingServiceAdapterBinding.bookingPackagePrice.text = "₹".plus(task.net_amount)
            } else {
                //bind the item here
                bookingServiceAdapterBinding.cCardName.text = task.company_name
                bookingServiceAdapterBinding.bookingServiceOrderId.text = task.token
                bookingServiceAdapterBinding.bookingServiceTime.text =
                    "${task.service_date_time} (GMT ${task.airport_gmt})}"
                bookingServiceAdapterBinding.bookingPackageName.text = task.service_name
                bookingServiceAdapterBinding.bookingPackageCount.text = task.total_adult
                bookingServiceAdapterBinding.bookingPackagePrice.text = "₹".plus(task.net_amount)

                bookingServiceAdapterBinding.cardName.setOnDebounceListener {
                    onClickCancel(task, holder)
                }

            }

            if (task.notes.isEmpty()) {
                bookingServiceAdapterBinding.bookingNotes.visibility = View.GONE
            } else {
                bookingServiceAdapterBinding.bookingNotes.visibility = View.VISIBLE
                bookingServiceAdapterBinding.bookingNotes.setOnDebounceListener {
                    note.show()
                    note.findViewById<TextView>(R.id.updateGstHint)?.text = task.notes
                }
            }


            bookingServiceAdapterBinding.statusView.text = task.status
            bookingServiceAdapterBinding.statusView.setPadding(10)
            bookingServiceAdapterBinding.message.setOnDebounceListener {
                onClickChat(task, holder)
            }

            bookingServiceAdapterBinding.viewReport.setOnDebounceListener {
                onViewReport(task, holder)
            }

            when(task.status){
                "Pending" -> {
                    bookingServiceAdapterBinding.statusView.text = task.status
                    bookingServiceAdapterBinding.statusView.background = ContextCompat.getDrawable(context, R.drawable.pending_roundcorner)
                    bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pending_timer, 0, 0, 0)
                    bookingServiceAdapterBinding.statusView.setTextColor(ContextCompat.getColor(context, R.color.pending_text))
                }
                "Cancelled" -> {
                    if (task.cancelled_by.lowercase() == "service provider"){
                        bookingServiceAdapterBinding.statusView.text = "${task.status} by ${task.cancelled_by}"
                    }else{
                        bookingServiceAdapterBinding.statusView.text = task.status
                    }
                    bookingServiceAdapterBinding.statusView.background = ContextCompat.getDrawable(context, R.drawable.cancelled_package)
                    bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_1x, 0, 0, 0)
                    bookingServiceAdapterBinding.statusView.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                "Completed" -> {
                    bookingServiceAdapterBinding.statusView.text = task.status
                    bookingServiceAdapterBinding.statusView.background = ContextCompat.getDrawable(context, R.drawable.completed_package)
                    bookingServiceAdapterBinding.statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_completed_package, 0, 0, 0)
                    bookingServiceAdapterBinding.statusView.setTextColor(ContextCompat.getColor(context, R.color.green))
                }
            }


            bookingServiceAdapterBinding.statusView.setPadding(10)

            Glide.with(context).load(task.company_logo).apply(RequestOptions.circleCropTransform())
                .error(R.drawable.user).into(bookingServiceAdapterBinding.bookingServiceImage)


            if (currentPosition == bookingPackageData.size - 1){
                bookingServiceAdapterBinding.nameView3.visibility = View.GONE
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bookingServiceAdapterBinding = BookingserviceAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(bookingServiceAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(bookingPackageData[position], holder, position)
    }

    override fun getItemCount(): Int {
        return bookingPackageData.size
    }

}

