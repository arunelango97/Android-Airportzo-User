package com.travel.airportzo.user.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.databinding.ChatReceiverBinding
import com.travel.airportzo.user.databinding.ChatReceiverimageBinding
import com.travel.airportzo.user.databinding.ChatSenderBinding
import com.travel.airportzo.user.databinding.ChatSenderimageBinding
import com.travel.airportzo.user.model.ChatData
import com.travel.airportzo.user.ui.activity.ImageActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private var messageData: ArrayList<ChatData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messageInBinding: ChatReceiverBinding
    private lateinit var messageInImageBinding: ChatReceiverimageBinding
    private lateinit var messageOutBinding: ChatSenderBinding
    private lateinit var messageOutImageBinding: ChatSenderimageBinding
    private val messageTypeSen = 0
    private val messageTypeRec = 1
    private val messageTypeSenImg = 2
    private val messageTypeRecImg = 3

    inner class MessageInViewHolder(messageInBinding: ChatReceiverBinding): RecyclerView.ViewHolder(messageInBinding.root) {
        fun bindIn(messageData: ChatData) {
            messageInBinding.apply {
                checkoutPassengerName.text = messageData.message
                val date: Date = messageData.date_time!!.toDate()
                val timeStampForCover = SimpleDateFormat("EEE hh:mma MMM d, yyyy", Locale.getDefault()).format(date)
                val timeStamp = formatToYesterdayOrToday(timeStampForCover)
                dateText.text = timeStamp.toString()
            }
        }
    }

    inner class MessageInImageViewHolder(messageInImageBinding: ChatReceiverimageBinding): RecyclerView.ViewHolder(messageInImageBinding.root){
        fun bindInImage(messageData: ChatData){
            messageInImageBinding.apply {
                Glide.with(itemView.context).load(messageData.message).into(imageUploadView)

                val date: Date = messageData.date_time!!.toDate()
                val timeStampForCover = SimpleDateFormat("EEE hh:mma MMM d, yyyy", Locale.getDefault()).format(date)
                val timeStamp = formatToYesterdayOrToday(timeStampForCover)
                dateText.text = timeStamp.toString()

                imageUploadView.setOnClickListener {
                    val intent = Intent(itemView.context, ImageActivity::class.java)
                    intent.putExtra("MessageImage", messageData.message)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    inner class MessageOutViewHolder(messageOutBinding: ChatSenderBinding): RecyclerView.ViewHolder(messageOutBinding.root){
        fun bindOut(messageData: ChatData){
            messageOutBinding.apply {
                checkoutPassengerName.text = messageData.message
                val date: Date = messageData.date_time!!.toDate()
                val timeStampForCover = SimpleDateFormat("EEE hh:mma MMM d, yyyy",Locale.getDefault()).format(date)
                val timeStamp = formatToYesterdayOrToday(timeStampForCover)
                dateText.text = timeStamp.toString()

                /*senderMessageView.setOnClickListener { messageDelete.visibility = View.GONE }
                senderMessageView.setOnLongClickListener {
                    messageDelete.visibility = View.VISIBLE
                    true
                }
                messageDelete.setOnClickListener {
                    val firebaseDb = FirebaseFirestore.getInstance()
                    firebaseDb.collection("post").document(messageData.post_id.toString()).collection("comments").document("U6dktiq9MFaFFdRbA7au")
                        .delete()
                        .addOnSuccessListener { Toast.makeText(itemView.context, "MessageDeleted", Toast.LENGTH_SHORT).show() }
                        .addOnFailureListener { e -> Log.w("DeleteError", "Error deleting document", e) }
                    Toast.makeText(itemView.context, "MessageDeleted", Toast.LENGTH_SHORT).show()
                }*/
            }
        }
    }

    inner class MessageOutImageViewHolder(messageOutImageBinding: ChatSenderimageBinding): RecyclerView.ViewHolder(messageOutImageBinding.root){
        fun bindOutImage(messageData: ChatData){
            Glide.with(itemView.context).load(messageData.message).into(messageOutImageBinding.imageUploadView)
            val date: Date = messageData.date_time!!.toDate()
            val timeStampForCover = SimpleDateFormat("EEE hh:mma MMM d, yyyy",Locale.getDefault()).format(date)
            val timeStamp = formatToYesterdayOrToday(timeStampForCover)
            messageOutImageBinding.dateText.text = timeStamp.toString()

            messageOutImageBinding.imageUploadView.setOnClickListener {
                val intent = Intent(itemView.context, ImageActivity::class.java)
                intent.putExtra("MessageImage", messageData.message)
                itemView.context.startActivity(intent)
            }
        }
    }

    private fun formatToYesterdayOrToday(timeStampForCover: String): Any {
        val dateTime = SimpleDateFormat("EEE hh:mma MMM d, yyyy", Locale.getDefault()).parse(timeStampForCover)
        val calendar = Calendar.getInstance()
        calendar.time = dateTime as Date
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        val timeFormatter: DateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val yesterdayFormatter: DateFormat = SimpleDateFormat("MMM d yyyy EEE hh:mm a", Locale.getDefault())

        return if (calendar[Calendar.YEAR] == today[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == today[Calendar.DAY_OF_YEAR]) {
            "Today " + timeFormatter.format(dateTime)
        } else if (calendar[Calendar.YEAR] == yesterday[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == yesterday[Calendar.DAY_OF_YEAR]) {
            "Yesterday " + timeFormatter.format(dateTime)
        } else {
            yesterdayFormatter.format(dateTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            messageTypeRec -> {
                messageInBinding = ChatReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageInViewHolder(messageInBinding)
            }
            messageTypeSen -> {
                messageOutBinding = ChatSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageOutViewHolder(messageOutBinding)
            }
            messageTypeRecImg -> {
                messageInImageBinding = ChatReceiverimageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageInImageViewHolder(messageInImageBinding)
            }
            else -> {
                messageOutImageBinding = ChatSenderimageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MessageOutImageViewHolder(messageOutImageBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (messageData[position].tpmysg) {
            messageTypeRec -> {
                (holder as ChatAdapter.MessageInViewHolder).bindIn(messageData[position])
            }
            messageTypeSen -> {
                (holder as MessageOutViewHolder).bindOut(messageData[position])
            }
            messageTypeRecImg -> {
                (holder as MessageInImageViewHolder).bindInImage(messageData[position])
            }
            else -> {
                (holder as MessageOutImageViewHolder).bindOutImage(messageData[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return messageData.size
    }

    override fun getItemViewType(position: Int): Int {
        return messageData[position].tpmysg!!
    }
}