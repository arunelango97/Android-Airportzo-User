package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.textview.MaterialTextView
import com.travel.airportzo.user.R

class CustomSpinnerAdapter(private val context: Context, private val spinnerData: ArrayList<String>, val itemClicker: (String) -> Unit): BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = spinnerData.size

    override fun getItem(position: Int): Any = spinnerData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val itemViewHolder: ItemViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.default_spinner_item, parent, false)
            itemViewHolder = ItemViewHolder(view)
            view.tag = itemViewHolder
        } else {
            view = convertView
            itemViewHolder = view.tag as ItemViewHolder
        }

        itemViewHolder.spinnerText.text = spinnerData[position]

        itemViewHolder.spinnerText.setOnClickListener { itemClicker(spinnerData[position]) }

        return view
    }

    inner class ItemViewHolder(view: View) {
        val spinnerText: MaterialTextView = view.findViewById(R.id.spinnerText) as MaterialTextView
    }
}