package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.ItemNotificationsBinding
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationFragment
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationViewModel
import kotlinx.coroutines.launch

class NotificationAdapterNew(
    private val context: NotificationFragment,
    private val list: List<AlertMessage?>?,
    private var viewModel: NotificationViewModel
) :
    RecyclerView.Adapter<NotificationAdapterNew.NotificationViewHolderNew>() {

    class NotificationViewHolderNew(var binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolderNew {
        var binding = ItemNotificationsBinding.inflate(LayoutInflater.from(context.requireContext()), parent, false)
        return NotificationViewHolderNew(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolderNew, @SuppressLint("RecyclerView") position: Int) {
        var binding = holder.binding
        var item = list!![position]
        var clickedItem = -1


        binding.message.text = Html.fromHtml(item?.message, Html.FROM_HTML_MODE_LEGACY)
        binding.notificationDate.text = item?.createTs
        if (item!!.isSeeMore) {
            binding.message.minLines = 1
            binding.message.maxLines = 20
            binding.seeMore.text = "See less"
        } else {
            binding.message.maxLines = 2
            binding.message.minLines = 1
            binding.seeMore.text = "See more"
        }
        binding.selectNotification.isChecked = item.isSelectListItem
        binding.selectNotification.setOnCheckedChangeListener(object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                clickedItem = position
                var item = list[clickedItem]
                if (p1) {
                    item?.isSelectListItem = true
                } else {
                    item?.isSelectListItem = false
                }
                context.lifecycleScope.launch{
                    viewModel.notificationCheckUncheck.emit(item!!)
                }
            }
        })
        binding.seeMore.setOnClickListener {
            clickedItem = position
            item.isSeeMore = !item.isSeeMore
            notifyItemChanged(clickedItem)
        }
        binding.message.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                clickedItem = position
                var item = list[clickedItem]
                item?.isSelectListItem = true
                this@NotificationAdapterNew.notifyDataSetChanged()
                return false
            }
        })
        binding.notificationDate.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                clickedItem=position
                var item=list!![clickedItem]
                item?.isSelectListItem=true
                this@NotificationAdapterNew.notifyDataSetChanged()
                return false
            }
        })
        binding.root.setOnLongClickListener(object:OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                clickedItem=position
                var item = list[clickedItem]
                item?.isSelectListItem=true
                this@NotificationAdapterNew.notifyDataSetChanged()
                return false
            }
        })
        binding.root.setOnClickListener {
            clickedItem = position
            var item = list[clickedItem]
            item?.isSelectListItem = item?.isSelectListItem != true
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.message.setOnClickListener {
            clickedItem = position
            var item = list[clickedItem]
            if (item?.isSelectListItem == true) {
                item.isSelectListItem = false
            } else {
                item?.isSelectListItem = true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.notificationDate.setOnClickListener {
            clickedItem = position
            var item = list[clickedItem]
            if (item?.isSelectListItem == true) {
                item.isSelectListItem = false
            } else {
                item?.isSelectListItem = true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding?.message?.setMovementMethod(LinkMovementMethod.getInstance())
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}