package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.SavedPassengerAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.utils.setOnDebounceListener
import java.util.ArrayList

class SavedPassengerAdapter(
    var context: Context,
    var newPassengerData: ArrayList<PassengerCreateData>,
    var onClick: (String) -> Unit
) : RecyclerView.Adapter<SavedPassengerAdapter.MyViewHolder>(), Filterable {

    var photosList: ArrayList<PassengerCreateData> = ArrayList()

    inner class MyViewHolder(var savedPassengerAdapterBinding: SavedPassengerAdapterBinding) :
        RecyclerView.ViewHolder(savedPassengerAdapterBinding.root) {
        fun bindItems(task: PassengerCreateData) {
            //bind the item here
            savedPassengerAdapterBinding.checkoutPassengerName.text = task.name_view
            savedPassengerAdapterBinding.checkoutPassengerNo.text =
                task.country_code.plus(" ").plus(task.mobile_number)
            savedPassengerAdapterBinding.checkoutPassengerEmail.text = task.email_id
//            savedPassengerAdapterBinding.checkoutPassengerAge.text = task.age
            savedPassengerAdapterBinding.savePassenger.setOnDebounceListener {
                onClick(task.token)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val savedPassengerAdapterBinding = SavedPassengerAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(savedPassengerAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(newPassengerData[position])
    }

    override fun getItemCount(): Int {
        return newPassengerData.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<PassengerCreateData>) {
        photosList = list as ArrayList<PassengerCreateData>
        newPassengerData = photosList
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) {
                    newPassengerData = photosList
                } else {
                    newPassengerData = if (newPassengerData.isEmpty()) {
                        val filteredList = ArrayList<PassengerCreateData>()
                        photosList.filter {
                            (it.name.contains(constraint!!, ignoreCase = true))
                        }.forEach { filteredList.add(it) }
                        filteredList
                    } else {
                        val filteredList = ArrayList<PassengerCreateData>()
                        newPassengerData.filter { passengerCreateData ->
                            passengerCreateData.email_id.contains(constraint!!) ||
                                    passengerCreateData.name.contains(constraint) ||
                                    passengerCreateData.mobile_number.contains(constraint)
                        }.forEach { filteredList.add(it) }
                        filteredList
                    }


                }

                return FilterResults().apply { values = newPassengerData }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                newPassengerData = results?.values as ArrayList<PassengerCreateData>
                print(newPassengerData)
//                onCountClicked(ListFiltered.size.toString())

                notifyDataSetChanged()
            }
        }
    }


}