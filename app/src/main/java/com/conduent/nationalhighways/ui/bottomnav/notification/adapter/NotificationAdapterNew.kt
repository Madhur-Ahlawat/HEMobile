package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.ItemNotificationsBinding
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationFragment
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationViewModel
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
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
        val binding = ItemNotificationsBinding.inflate(
            LayoutInflater.from(context.requireContext()),
            parent,
            false
        )
        return NotificationViewHolderNew(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolderNew,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val binding = holder.binding
        val item = list!![position]
        var clickedItem: Int


        binding.message.text = Html.fromHtml(item?.message, Html.FROM_HTML_MODE_LEGACY)
        binding.notificationDate.text = item?.createTs?.replace("AM", "am")?.replace("PM", "pm")
        binding.selectNotification.text =
            binding.notificationDate.text.toString() + "\n" + binding.message.text.toString()
        binding.selectNotification.isChecked = item?.isSelectListItem == true
        binding.clParent.contentDescription = binding.notificationDate.getText().toString() + ". " + binding.message.getText().toString()
        binding.selectNotification.setOnCheckedChangeListener { _, p1 ->
            clickedItem = position
            val item2 = list[clickedItem]
            item2?.isSelectListItem = p1
            context.lifecycleScope.launch {
                viewModel.notificationCheckUncheck.emit(item2!!)
            }
        }
        binding.message.setOnLongClickListener {
            clickedItem = position
            val item1 = list[clickedItem]
            item1?.isSelectListItem = true
            this@NotificationAdapterNew.notifyDataSetChanged()
            false
        }
        binding.notificationDate.setOnLongClickListener {
            clickedItem = position
            val item = list[clickedItem]
            item?.isSelectListItem = true
            this@NotificationAdapterNew.notifyDataSetChanged()
            false
        }
        binding.root.setOnLongClickListener {
            clickedItem = position
            val item = list[clickedItem]
            item?.isSelectListItem = true
            this@NotificationAdapterNew.notifyDataSetChanged()
            false
        }
        binding.root.setOnClickListener {
            clickedItem = position
            val item = list[clickedItem]
            item?.isSelectListItem = item?.isSelectListItem != true
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.message.setOnClickListener {
            clickedItem = position
            val item = list[clickedItem]
            if (item?.isSelectListItem == true) {
                item.isSelectListItem = false
            } else {
                item?.isSelectListItem = true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.notificationDate.setOnClickListener {
            clickedItem = position
            val item4 = list[clickedItem]
            if (item4?.isSelectListItem == true) {
                item4.isSelectListItem = false
            } else {
                item4?.isSelectListItem = true
            }
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }
        binding.message.movementMethod = LinkMovementMethod.getInstance()
        if (item?.isViewed != null && item.isViewed.equals("Y")) {
            holder.itemView.isFocusable = false
            holder.itemView.isEnabled = false
            binding.notificationDate.setTextColor(context.resources.getColor(R.color.hint_color))
            binding.message.setTextColor(context.resources.getColor(R.color.hint_color))
        } else {
            holder.itemView.isFocusable = true
            holder.itemView.isEnabled = true
            binding.notificationDate.setTextColor(context.resources.getColor(R.color.black))
            binding.message.setTextColor(context.resources.getColor(R.color.black))

        }
        binding.selectNotification.setAccessibilityDelegate()



        binding.notificationDate.post {
            val delegateArea = Rect()
            binding.notificationDate.getHitRect(delegateArea)
            // Increase the height of the hit rectangle by 48dp (adjust as needed)
            val heightIncrease = context.resources.getDimensionPixelSize(R.dimen.margin_48dp)
            delegateArea.top -= heightIncrease
            delegateArea.bottom += heightIncrease

            // Create a TouchDelegate and set it on the parent layout of binding.notificationDate
            val parentLayout = binding.notificationDate.parent as ConstraintLayout
            parentLayout.touchDelegate = TouchDelegate(delegateArea, binding.notificationDate)
        }


    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}