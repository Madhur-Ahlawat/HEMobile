package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.ItemNotificationsBinding
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationFragment
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class NotificationAdapterNew(private val context: Context, private val list: List<AlertMessage?>?) :
    RecyclerView.Adapter<NotificationAdapterNew.NotificationViewHolderNew>() {

    class NotificationViewHolderNew(var binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolderNew {
        var binding = ItemNotificationsBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationViewHolderNew(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolderNew, @SuppressLint("RecyclerView") position: Int) {
        var binding = holder.binding
        var item = list!![position]
        var clickedItem=-1

        binding.notificationDate.text = Html.fromHtml(item?.createTs, Html.FROM_HTML_MODE_LEGACY)
        binding.message.text = item?.message
        binding.message.movementMethod = LinkMovementMethod.getInstance()
        if(item!!.isSeeMore){
            binding.message.minLines=1
            binding.message.maxLines=20
            binding.seeMore.text= "See less"
        }
        else{
            binding.message.maxLines=2
            binding.message.minLines=1
            binding.seeMore.text= "See more"
        }
        if (item!!.isSelectListItem!!) {
            binding.selectNotification.isChecked=true
        } else {
            binding.selectNotification.isChecked=false
        }
        if(NotificationFragment.isSelectionMode!!){
            binding.selectNotification.visible()
        }
        else{
            binding.selectNotification.gone()
        }
        binding.seeMore.setOnClickListener {
            clickedItem=position
            if(item!!.isSeeMore){
                item!!.isSeeMore=false
            }
            else{
                item!!.isSeeMore=true
            }
            notifyItemChanged(clickedItem)
        }
        binding.message.setOnLongClickListener(object:OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                clickedItem=position
                var item=list!![clickedItem]
                item?.isSelectListItem=true
                NotificationFragment.isSelectionMode=true
                this@NotificationAdapterNew.notifyDataSetChanged()
                return false
            }
        })
        binding.notificationDate.setOnLongClickListener(object:OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                clickedItem=position
                var item=list!![clickedItem]
                item?.isSelectListItem=true
                NotificationFragment.isSelectionMode=true
                this@NotificationAdapterNew.notifyDataSetChanged()
                return false
            }
        })
        binding.root.setOnLongClickListener(object:OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                clickedItem=position
                var item=list!![clickedItem]
                item?.isSelectListItem=true
                NotificationFragment.isSelectionMode=true
                this@NotificationAdapterNew.notifyDataSetChanged()
                return false
            }
        })
        binding.root.setOnClickListener {
            clickedItem=position
            var item=list!![clickedItem]
            if(item!!.isSelectListItem){
                item?.isSelectListItem=false
            }
            else{
                item?.isSelectListItem=true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.message.setOnClickListener {
            clickedItem=position
            var item=list!![clickedItem]
            if(item!!.isSelectListItem){
                item?.isSelectListItem=false
            }
            else{
                item?.isSelectListItem=true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.notificationDate.setOnClickListener {
            clickedItem=position
            var item=list!![clickedItem]
            if(item!!.isSelectListItem){
                item?.isSelectListItem=false
            }
            else{
                item?.isSelectListItem=true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}