package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.AdapterNotificationBinding
import com.conduent.nationalhighways.databinding.ItemNotificationsBinding
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationFragment
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.visible

class NotificationAdapterNew(private val context: Context, private val list: List<AlertMessage?>?) :
    RecyclerView.Adapter<NotificationAdapterNew.NotificationViewHolderNew>() {
    private var showClearButton: Boolean?=null
    private var showCheckBoxes: Boolean? = null

    class NotificationViewHolderNew(var binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolderNew {
        var binding = ItemNotificationsBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationViewHolderNew(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolderNew, position: Int) {
        var binding = holder.binding
        var item = list!![position]
        var clickedItem=-1

        binding.notificationDate.text = Html.fromHtml(item?.createTs, Html.FROM_HTML_MODE_LEGACY)
        binding.message.text = item?.message
        binding.message.movementMethod = LinkMovementMethod.getInstance()

        if (item!!.isSelectListItem!!) {
            binding.selectNotification.isChecked=true
        } else {
            binding.selectNotification.isChecked=false
        }
        if(showCheckBoxes!!){
            binding.selectNotification.visible()
        }
        else{
            binding.selectNotification.gone()
        }

        binding.root.setOnLongClickListener(object:OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                item.isSelectListItem=true
                enableSelectionMode()
                this@NotificationAdapterNew.notifyDataSetChanged()
                return true
            }
        })
    }

    private fun enableSelectionMode() {
        showCheckBoxes=true
    }
    private fun disableSelectionMode() {
        showCheckBoxes=false
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}