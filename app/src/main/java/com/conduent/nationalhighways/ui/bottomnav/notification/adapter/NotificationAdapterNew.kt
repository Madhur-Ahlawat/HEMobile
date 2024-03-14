package com.conduent.nationalhighways.ui.bottomnav.notification.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.text.HtmlCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.notification.AlertMessage
import com.conduent.nationalhighways.databinding.ItemNotificationsBinding
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationFragment
import com.conduent.nationalhighways.ui.bottomnav.notification.NotificationViewModel
import com.conduent.nationalhighways.utils.CustomTypefaceSpan
import kotlinx.coroutines.launch

class NotificationAdapterNew(
    private val context: NotificationFragment,
    private val list: List<AlertMessage?>?,
    private var viewModel: NotificationViewModel
) :
    RecyclerView.Adapter<NotificationAdapterNew.NotificationViewHolderNew>() {

    class NotificationViewHolderNew(var binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root){
            init {
                setupTextAccessibilityDelegate(binding)
            }

            private fun setupTextAccessibilityDelegate(binding: ItemNotificationsBinding) {
                val accessibilityDelegate: View.AccessibilityDelegate =
                    object : View.AccessibilityDelegate() {
                        override fun onInitializeAccessibilityNodeInfo(
                            host: View,
                            info: AccessibilityNodeInfo
                        ) {
                            super.onInitializeAccessibilityNodeInfo(host, info)
                            val infoCompat = AccessibilityNodeInfoCompat.wrap(info)

                            infoCompat.roleDescription = binding.selectNotification.text.toString()
                            infoCompat.isEditable = false
                        }
                    }
                binding.selectNotification.accessibilityDelegate = accessibilityDelegate
            }

        }


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
        val item = list?.get(position)
        var clickedItem: Int

        val text1 = item?.createTs?.replace("AM", "am")?.replace("PM", "pm")
        val text2 = item?.message

        val spannedHtml: Spanned =
            HtmlCompat.fromHtml("$text1<br><br>$text2", HtmlCompat.FROM_HTML_MODE_LEGACY)
        val spannableString = SpannableString.valueOf(spannedHtml)
        var color: Int = 0
        if (item?.isViewed != null && item.isViewed.equals("Y")) {
            color = context.resources.getColor(R.color.hint_color, null)
        } else {
            color = context.resources.getColor(R.color.black, null)
        }
        val typeface1 =
            Typeface.createFromAsset(context.requireContext().assets, "OpenSans-Semibold.ttf")
        spannableString.setSpan(
            CustomTypefaceSpan(typeface1, color),
            0,
            text1?.length ?: 0,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val typeface2 =
            Typeface.createFromAsset(context.requireContext().assets, "OpenSans-Regular.ttf")
        spannableString.setSpan(
            CustomTypefaceSpan(typeface2, color),
            text2?.length ?: 0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.selectNotification.text = spannableString
        binding.selectNotification.isChecked = item?.isSelectListItem == true
        binding.selectNotification.setOnCheckedChangeListener { _, p1 ->
            clickedItem = position
            val item2 = list?.get(clickedItem)
            item2?.isSelectListItem = p1
            context.lifecycleScope.launch {
                viewModel.notificationCheckUncheck.emit(item2!!)
            }
        }
        binding.root.setOnLongClickListener {
            clickedItem = position
            val item = list?.get(clickedItem)
            item?.isSelectListItem = true
            this@NotificationAdapterNew.notifyDataSetChanged()
            false
        }
        binding.root.setOnClickListener {
            clickedItem = position
            val item = list?.get(clickedItem)
            item?.isSelectListItem = item?.isSelectListItem != true
            this@NotificationAdapterNew.notifyItemChanged(clickedItem)
        }


    }


    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}