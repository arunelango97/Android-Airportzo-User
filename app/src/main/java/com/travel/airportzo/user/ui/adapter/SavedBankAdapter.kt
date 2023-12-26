package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.SavedBankAdapterBinding
import com.travel.airportzo.user.model.BankDetailData
import com.travel.airportzo.user.utils.setOnDebounceListener
import java.util.ArrayList

class SavedBankAdapter(
    var context: Context,
    private var newBankData: ArrayList<BankDetailData>,
    var onclick:(String)->Unit)
    :RecyclerView.Adapter<SavedBankAdapter.MyViewHolder>() {

  inner  class MyViewHolder(var savedBankAdapterBinding: SavedBankAdapterBinding):RecyclerView.ViewHolder(savedBankAdapterBinding.root) {

fun bindItems(task:BankDetailData){
    savedBankAdapterBinding.card.setOnDebounceListener {
        onclick(task.token)
    }

    savedBankAdapterBinding.bankNo.text=task.account_number
    savedBankAdapterBinding.bankPlace.text=task.branch_name

   if (task.is_primary){
       savedBankAdapterBinding.primary.visibility=View.VISIBLE
   }

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val savedBankAdapterBinding = SavedBankAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(savedBankAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(newBankData[position])
    }

    override fun getItemCount(): Int {
    return newBankData.size
    }
}