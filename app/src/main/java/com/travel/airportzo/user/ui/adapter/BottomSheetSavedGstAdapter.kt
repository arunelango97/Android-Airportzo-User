package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BottomsheetSavedgstAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateGst
import com.travel.airportzo.user.utils.setOnDebounceListener

class BottomSheetSavedGstAdapter(var context: Context, private var savedGst: ArrayList<PassengerCreateGst>, var click:(String)->Unit):
    RecyclerView.Adapter<BottomSheetSavedGstAdapter.MyViewHolder>() {

    private val saveGst by lazy {
        context.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_checkoutsaved_gst, null))
                setCancelable(true)
            }
        }
    }

    inner class MyViewHolder(var bottomSheetSavedGstAdapterBinding: BottomsheetSavedgstAdapterBinding):
        RecyclerView.ViewHolder(bottomSheetSavedGstAdapterBinding.root) {
        fun bindItems(task: PassengerCreateGst) {
            //bind the item here
            bottomSheetSavedGstAdapterBinding.recentlyAddgstName.text = task.name
            bottomSheetSavedGstAdapterBinding.addGstNo.text = task.gstin
            bottomSheetSavedGstAdapterBinding.card.setOnDebounceListener {
                if (task.selected) {
                    bottomSheetSavedGstAdapterBinding.recentlyAddgstName.setTextColor(
                        ContextCompat.getColor(context, R.color.black))
                    bottomSheetSavedGstAdapterBinding.addGstNo.setTextColor(
                        ContextCompat.getColor(context, R.color.grey))

                }else{
                    click(task.token)
                    bottomSheetSavedGstAdapterBinding.recentlyAddgstName.setTextColor(ContextCompat.getColor(context, R.color.white))
                    bottomSheetSavedGstAdapterBinding.addGstNo.setTextColor(ContextCompat.getColor(context, R.color.white))
                    bottomSheetSavedGstAdapterBinding.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    saveGst.dismiss()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bottomSheetSavedGstAdapterBinding = BottomsheetSavedgstAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(bottomSheetSavedGstAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(savedGst[position])
    }

    override fun getItemCount(): Int {
        return savedGst.size
    }
}