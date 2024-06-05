package com.conduent.nationalhighways.utils.common

import androidx.appcompat.widget.AppCompatTextView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.extn.changeTextColor
import com.google.android.material.card.MaterialCardView

object DashboardUtils {

    fun setAccountStatus(status: String, tvTitle: AppCompatTextView) {
        if (status.equals("OPEN", true) || status.equals("ACTIVE", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.open)
            tvTitle.changeTextColor(R.color.green)
        } else if (status.equals("SUSPENDED", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.suspended)
            tvTitle.changeTextColor(R.color.color_C93E28)
        } else if (status.equals("DORMANT", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.dormant)
            tvTitle.changeTextColor(R.color.color_942514)
        } else if (status.equals("CLOSED", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.closed)
            tvTitle.changeTextColor(R.color.color_383f43)
        } else if (status.equals("CLOSE PEND", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.close_Pending)
            tvTitle.changeTextColor(R.color.color_144e81)
        } else {
            tvTitle.text = status
            tvTitle.changeTextColor(R.color.FFF7BF)
        }
    }
    fun setAccountStatusNew(status: String, tvTitle: AppCompatTextView, viewGroup: MaterialCardView,type:Int) {
        if (status.uppercase().equals("OPEN", true) || status.uppercase().equals("ACTIVE", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.open)
            tvTitle.run {
                setTextColor(viewGroup.context.resources.getColor(R.color.dark_blue_color,null))
            }
            viewGroup.run{
                setCardBackgroundColor(viewGroup.context.resources.getColor(R.color.green_status))
            }
        } else if (status.uppercase().equals("SUSPENDED", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.suspended)
            tvTitle.run {
                setTextColor(viewGroup.context.resources.getColor(R.color.white_color,null))
            }
            viewGroup.run{
                setCardBackgroundColor(viewGroup.context.resources.getColor(R.color.color_942514))
            }
        } else if (status.uppercase().equals("DORMANT", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.dormant)
            tvTitle.run {
                setTextColor(viewGroup.context.resources.getColor(R.color.white_color,null))
            }
            viewGroup.run{
                setCardBackgroundColor(viewGroup.context.resources.getColor(R.color.color_942514))
            }
        }  else {
            tvTitle.text = status
            tvTitle.run {
                setTextColor(viewGroup.context.resources.getColor(R.color.dark_blue_color,null))
            }
            viewGroup.run{
                setCardBackgroundColor(viewGroup.context.resources.getColor(R.color.FFF7BF))
            }
        }
    }

    fun setAccountFinancialStatus(status: String, tvTitle: AppCompatTextView) {
        if (status.equals("AUTOPAY", true)) {
            tvTitle.text = tvTitle.context.getString(R.string.auto_top_up)
        } else {
            tvTitle.text = tvTitle.context.getString(R.string.manual_top_up)
        }
        tvTitle.text = status

    }

    fun setAccountType(type: String, subType: String?, tvTitle: AppCompatTextView) {
        when {
            type.equals("BUSINESS", true) -> {
                tvTitle.text = tvTitle.context.getString(R.string.business)
            }
            type.equals("NONREVENUE", true) -> {
                tvTitle.text = tvTitle.context.getString(R.string.exempt_partner)
            }
            type.equals("PRIVATE", true) -> {
                when {
                    subType.equals("PAYG", true) -> {
                        tvTitle.text =
                            tvTitle.context.getString(R.string.personal_pay_as_you_go_acc)
                    }
                    subType.equals("STANDARD", true) -> {
                        tvTitle.text = tvTitle.context.getString(R.string.personal_pre_pay_acc)
                    }
                    else -> {
                        tvTitle.text = tvTitle.context.getString(R.string.personal_pre_pay_acc)
                    }
                }
            }
            else -> {
                tvTitle.text = "$type $subType"
            }
        }
    }

}