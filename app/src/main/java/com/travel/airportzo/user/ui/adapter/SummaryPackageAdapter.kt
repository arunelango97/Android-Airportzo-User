package com.travel.airportzo.user.ui.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SummarypackageAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.fragments.ChooseServiceFragment
import com.travel.airportzo.user.utils.Utility
import kotlin.math.log

class SummaryPackageAdapter(
    var context: Context,
    private var summaryPackagesData: ArrayList<ServiceTicketData.Service_array>,
    var selectedItem: String,
    var shortValue: String,
    private val summaryFragControl: (ServiceTicketData.Service_array) -> Unit

    ) : RecyclerView.Adapter<SummaryPackageAdapter.MyViewHolder>() {

    private val addNotes by lazy {
        context.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_addnotes, null))
                /** brand color*/
                val reportBtn = findViewById<MaterialButton>(R.id.reportProblem)
                reportBtn?.setBackgroundColor(Color.parseColor(SavedSharedPreference
                    .getCustomColor(context).brand_colour))

                setCancelable(true)
            }
        }
    }

    private val editNote by lazy {
        context.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_editnotes, null))

                /** brand color*/
                val updateBtn = findViewById<MaterialButton>(R.id.update)
                 updateBtn?.setBackgroundColor(Color.parseColor(SavedSharedPreference
                     .getCustomColor(context).brand_colour))
                setCancelable(true)
            }
        }
    }


    private var addNotesToken: String = ""
    private var editNotesToken: String = ""


    inner class MyViewHolder(private var summaryPackageAdapterBinding: SummarypackageAdapterBinding) :
        RecyclerView.ViewHolder(summaryPackageAdapterBinding.root) {


        fun bindItems(task: ServiceTicketData.Service_array,position: Int) {
            /** brand color*/
            summaryPackageAdapterBinding.addNotes.setTextColor(Color.parseColor(SavedSharedPreference.getCustomColor(itemView.context).brand_colour))

    /** adapter item removing */


            summaryPackageAdapterBinding.packageCardCloseImg.setOnClickListener{
                    summaryFragControl(task)

            }




            //bind the item here


            if (selectedItem == "INR") {
                if (ChooseServiceFragment.data[0].stationData[0].children_count == 0){
                    val priceamount = ChooseServiceFragment.data[0].stationData[0].adult_count * task.price_adult.toInt()
                    Log.d("inr", task.totalAmount.toString())
                    summaryPackageAdapterBinding.cCardAmount.text = "₹".plus(priceamount)
                }else{
                    val priceamount = ChooseServiceFragment.data[0].stationData[0].adult_count * task.price_adult.toInt() + ChooseServiceFragment.data[0].stationData[0].children_count * task.price_children.toInt()
                    Log.d("inr", task.totalAmount.toString())
                    summaryPackageAdapterBinding.cCardAmount.text = "₹".plus(priceamount)
                }

            } else if (selectedItem == "USD") {
                if (ChooseServiceFragment.data[0].stationData[0].children_count == 0) {
                    val priceamount = ChooseServiceFragment.data[0].stationData[0].adult_count * task.price_adult.toInt()
                    val total = priceamount.toInt() / shortValue.toDouble()
                    summaryPackageAdapterBinding.cCardAmount.text =
                        "$".plus(String.format("%.2f", total))
                }else{
                    val priceamount = ChooseServiceFragment.data[0].stationData[0].adult_count * task.price_adult.toInt() + ChooseServiceFragment.data[0].stationData[0].children_count * task.price_children.toInt()
                    val total = priceamount.toInt() / shortValue.toDouble()
                    summaryPackageAdapterBinding.cCardAmount.text =
                        "$".plus(String.format("%.2f", total))
                }
            }






            summaryPackageAdapterBinding.cCardPackageName.text = task.service_name
            summaryPackageAdapterBinding.cCardPersonCount.text =
                ChooseServiceFragment.data[0].stationData[0].adult_count.toString()
            summaryPackageAdapterBinding.childCount.text =
                ChooseServiceFragment.data[0].stationData[0].children_count.toString()
            summaryPackageAdapterBinding.cCardMilesCount.text = "10"


            val jsonArray = Utility.stationarray.asJsonArray
            for (i in 0 until jsonArray.size()) {
                val stationObject = jsonArray.get(i).asJsonObject
                val serviceArray = stationObject.get("service_array").asJsonArray
                for (j in 0 until serviceArray.size()) {
                    val serviceObject = serviceArray.get(j).asJsonObject
                    val serviceToken = serviceObject.get("service_token").asString
                    if (serviceToken == task.service_token) {
                        val notes = Utility.stationarray.get(i).asJsonObject.get("service_array").asJsonArray.get(j).asJsonObject.get("notes").asString
                        if (notes.isNotEmpty()) {
                            summaryPackageAdapterBinding.addNotes.visibility = View.GONE
                            summaryPackageAdapterBinding.editNotes.visibility = View.VISIBLE
                        } else {
                            summaryPackageAdapterBinding.addNotes.visibility = View.VISIBLE
                            summaryPackageAdapterBinding.editNotes.visibility = View.GONE
                        }
                    }
                }
            }
            summaryPackageAdapterBinding.addNotes.setOnDebounceListener {
                addNote(task)
            }
            summaryPackageAdapterBinding.editNotes.setOnDebounceListener {
                editNote(task)
            }

        }

    }


    private fun addNote(serviceGroup: ServiceTicketData.Service_array) {
        addNotesToken = serviceGroup.service_token

        addNotes.findViewById<ImageView>(R.id.bottomSheetAddNotesCloseBtn)?.setOnDebounceListener {
            addNotes.dismiss()
        }

        val notes = addNotes.findViewById<EditText>(R.id.descriptionEdit)

        addNotes.findViewById<Button>(R.id.reportProblem)?.setOnDebounceListener {

            val newNote = notes?.text.toString()
            if (newNote.isEmpty()) {
                Toast.makeText(context, "Notes field is empty", Toast.LENGTH_SHORT).show()
            } else {
                val jsonArray = Utility.stationarray.asJsonArray
                for (i in 0 until jsonArray.size()) {
                    val stationObject = jsonArray.get(i).asJsonObject
                    val serviceArray = stationObject.get("service_array").asJsonArray
                    for (j in 0 until serviceArray.size()) {
                        val serviceObject = serviceArray.get(j).asJsonObject
                        val serviceToken = serviceObject.get("service_token").asString
                        if (serviceToken == addNotesToken) {
                            Utility.stationarray.get(i).asJsonObject.get("service_array").asJsonArray.get(
                                j
                            ).asJsonObject.addProperty("notes", newNote)
                            Log.d("Updated Json Array", Utility.stationarray.toString())
                        }
                    }
                }
                notes?.text?.clear()
                notifyDataSetChanged()
                addNotes.dismiss()
            }
        }
        addNotes.show()
    }


    private fun editNote(serviceGroup: ServiceTicketData.Service_array) {
        editNotesToken = serviceGroup.service_token
        val editNotes = editNote.findViewById<EditText>(R.id.editDescriptionEdit)
        val jsonArray = Utility.stationarray.asJsonArray
        for (i in 0 until jsonArray.size()) {
            val stationObject = jsonArray.get(i).asJsonObject
            val serviceArray = stationObject.get("service_array").asJsonArray
            for (j in 0 until serviceArray.size()) {
                val serviceObject = serviceArray.get(j).asJsonObject
                val serviceToken = serviceObject.get("service_token").asString
                if (serviceToken == editNotesToken) {
                    val existingNote =
                        Utility.stationarray.get(i).asJsonObject.get("service_array").asJsonArray.get(
                            j
                        ).asJsonObject.get("notes").asString
                    editNotes?.setText(existingNote)
                }
            }
        }

        editNote.findViewById<Button>(R.id.update)?.setOnDebounceListener {
            val updatedNote = editNotes?.text.toString()
            if (updatedNote.isEmpty()) {
                Toast.makeText(context, "Notes field is empty", Toast.LENGTH_SHORT).show()
            } else {
                for (i in 0 until jsonArray.size()) {
                    val stationObject = jsonArray.get(i).asJsonObject
                    val serviceArray = stationObject.get("service_array").asJsonArray
                    for (j in 0 until serviceArray.size()) {
                        val serviceObject = serviceArray.get(j).asJsonObject
                        val serviceToken = serviceObject.get("service_token").asString
                        if (serviceToken == editNotesToken) {
                            Utility.stationarray.get(i).asJsonObject.get("service_array").asJsonArray.get(
                                j
                            ).asJsonObject.addProperty("notes", updatedNote)
                        }
                    }
                }
                editNotes?.text?.clear()
                notifyDataSetChanged()
                editNote.dismiss()
            }
        }
        editNote.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val summaryPackageAdapterBinding =
            SummarypackageAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(summaryPackageAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(summaryPackagesData[position],position)


    }

    override fun getItemCount(): Int {
        return summaryPackagesData.size
    }
}