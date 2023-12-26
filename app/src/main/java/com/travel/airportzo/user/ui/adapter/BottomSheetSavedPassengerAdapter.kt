package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BottomsheetSavedpassengerAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.utils.setOnDebounceListener

class BottomSheetSavedPassengerAdapter(
    var context: Context,
    var savedPassenger: ArrayList<PassengerCreateData>,
    var onClicked: (String) -> Unit
) : RecyclerView.Adapter<BottomSheetSavedPassengerAdapter.MyViewHolder>(), Filterable {

    var photosList: ArrayList<PassengerCreateData> = ArrayList()

    inner class MyViewHolder(var bottomSheetSavedPassengerAdapterBinding: BottomsheetSavedpassengerAdapterBinding) :
        RecyclerView.ViewHolder(bottomSheetSavedPassengerAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItem(task: PassengerCreateData, position: Int) {
            bottomSheetSavedPassengerAdapterBinding.checkoutPassengerName.text = task.name_view
            bottomSheetSavedPassengerAdapterBinding.checkoutPassengerNo.text =
                task.country_code.plus(" ").plus(task.mobile_number)
            bottomSheetSavedPassengerAdapterBinding.checkoutPassengerEmail.text = task.email_id
            bottomSheetSavedPassengerAdapterBinding.checkoutPassengerAge.text = task.age

            if (task.selected) {
                bottomSheetSavedPassengerAdapterBinding.checkoutCancel.visibility = View.VISIBLE
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerName.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
                )
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerNo.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
                )
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerEmail.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
                )
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerAge.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
                )
                bottomSheetSavedPassengerAdapterBinding.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                )
            } else {
                bottomSheetSavedPassengerAdapterBinding.checkoutCancel.visibility = View.GONE
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerName.setTextColor(
                    ContextCompat.getColor(context, R.color.textcolor)
                )
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerNo.setTextColor(
                    ContextCompat.getColor(context, R.color.greyText)
                )
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerEmail.setTextColor(
                    ContextCompat.getColor(context, R.color.greyText)
                )
                bottomSheetSavedPassengerAdapterBinding.checkoutPassengerAge.setTextColor(
                    ContextCompat.getColor(context, R.color.greyText)
                )
                bottomSheetSavedPassengerAdapterBinding.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.backgroundcolor
                    )
                )
            }

            bottomSheetSavedPassengerAdapterBinding.card.setOnDebounceListener {
                onClicked(task.token)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bottomSheetSavedPassengerAdapterBinding =
            BottomsheetSavedpassengerAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return MyViewHolder(bottomSheetSavedPassengerAdapterBinding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(savedPassenger[position], position)
//        holder.itemView.setOnDebounceListener {
//            selectedItemPosition =position
//            notifyDataSetChanged()
//        }
//
//        if (selectedItemPosition == position) {
//            holder.bottomSheetSavedPassengerAdapterBinding.checkoutCancel.visibility = View.GONE
//            holder.bottomSheetSavedPassengerAdapterBinding.card.setBackgroundColor(Color.WHITE)
//        } else {
//            holder.bottomSheetSavedPassengerAdapterBinding.checkoutCancel.visibility = View.VISIBLE
//            holder.bottomSheetSavedPassengerAdapterBinding.card.setBackgroundColor(Color.parseColor("#4ABED5"))
//        }
//    }
    }

    override fun getItemCount(): Int {
        return savedPassenger.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<PassengerCreateData>) {
        photosList = list as ArrayList<PassengerCreateData>
        savedPassenger = photosList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) {
                    savedPassenger = photosList
                } else {
                    savedPassenger = if (savedPassenger.isEmpty()) {
                        val filteredList = java.util.ArrayList<PassengerCreateData>()
                        photosList.filter {
                            (it.name.contains(constraint!!, ignoreCase = true))
                        }.forEach { filteredList.add(it) }
                        filteredList
                    } else {
                        val filteredList = java.util.ArrayList<PassengerCreateData>()
                        savedPassenger.filter { passengerCreateData ->
                            passengerCreateData.email_id.contains(constraint!!) ||
                                    passengerCreateData.name.contains(constraint) ||
                                    passengerCreateData.mobile_number.contains(constraint)
                        }.forEach { filteredList.add(it) }
                        filteredList
                    }


                }

                return FilterResults().apply { values = savedPassenger }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                savedPassenger = results?.values as ArrayList<PassengerCreateData>
                notifyDataSetChanged()
            }
        }
    }
}


