package com.conduent.nationalhighways.ui.viewcharges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.tollrates.TollRatesResp
import com.conduent.nationalhighways.databinding.AdapterViewChargesBinding
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class TollRateAdapter(private val context: Context?, var list: List<TollRatesResp?>?) :
    RecyclerView.Adapter<TollRateAdapter.VehicleViewHolder>() {

    private var mDescription: String? = null
    private var prePayValue: String? = null

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder =
        VehicleViewHolder(
            AdapterViewChargesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TollRateAdapter.VehicleViewHolder, position: Int) {

        holder.binding.data = list?.get(position)
        if (position == 0) {
            holder.binding?.run {
                rootView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                rootView.contentDescription = context?.getString(R.string.type_of_vehicle) +
                        context?.getString(R.string.str_comma) + " " +
                        data?.vehicleType?.replace(
                            "\n",
                            " "
                        ) + context?.getString(R.string.str_comma) + " " +
                        context?.getString(R.string.pay_as_you_go) + context?.getString(R.string.str_comma) + context?.getString(
                    R.string.str_space
                ) +
                        if (list?.get(position)?.videoRate != 0.0) context?.getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            list?.get(position)?.videoRate
                        )
                        else
                            "Free"
                prePayValue =
                    context?.getString(R.string.str_comma) + context?.getString(R.string.str_space) +
                            context?.getString(R.string.pre_pay) + context?.getString(R.string.str_comma) + " " + if (list?.get(
                            position
                        )?.etcRate != 0.0
                    )
                        context?.getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            list?.get(position)?.etcRate
                        )
                    else
                        context?.getString(R.string.free)
                rootView.contentDescription = rootView.contentDescription.toString() + prePayValue
                lablePrepay.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                labelTypeOfVehicle.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                labelPayAsYouGo.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                viewFooter.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                lablePrepay.visible()
                labelTypeOfVehicle.visible()
                labelPayAsYouGo.visible()
                viewFooter.visible()
            }
        } else if (position > 0 && position < list!!.size) {
            holder.binding?.run {

                rootView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                mDescription = context?.getString(R.string.type_of_vehicle) +
                        context?.getString(R.string.str_comma) + context?.getString(R.string.str_space) +
                        data?.vehicleType?.replace(
                            "\n",
                            " "
                        ) + context?.getString(R.string.str_comma) + context?.getString(R.string.str_space) +
                        context?.getString(R.string.pay_as_you_go) + context?.getString(R.string.str_comma) + " " +
                         if (list?.get(position)?.videoRate != 0.0) context?.getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    list?.get(position)?.videoRate
                ) else
                    context?.getString(R.string.free)
                prePayValue = context?.getString(R.string.str_comma) + " " +
                        context?.getString(R.string.pre_pay) + context?.getString(R.string.str_comma) + " " + if (list?.get(
                        position
                    )?.etcRate != 0.0
                )
                    context?.getString(R.string.currency_symbol) + String.format(
                        "%.2f",
                        list?.get(position)?.etcRate
                    )
                else
                    context?.getString(R.string.free)
                mDescription = mDescription + prePayValue
                rootView.contentDescription = mDescription
                lablePrepay.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                labelTypeOfVehicle.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                labelPayAsYouGo.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                viewFooter.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                lablePrepay.gone()
                labelTypeOfVehicle.gone()
                labelPayAsYouGo.gone()
                viewFooter.gone()
            }
        } else if (position == list!!.size) {
            holder.binding?.run {
                rootView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                mDescription = context?.getString(R.string.type_of_vehicle) +
                        context?.getString(R.string.str_comma) + " " +
                        data?.vehicleType?.replace(
                            "\n",
                            " "
                        ) + context?.getString(R.string.str_comma) + " " +
                        context?.getString(R.string.pay_as_you_go) + context?.getString(R.string.str_comma) + " " +
                         if (list?.get(position)?.videoRate != 0.0) context?.getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    list?.get(position)?.videoRate
                ) else context?.getString(R.string.free)
                prePayValue =
                    context?.getString(R.string.str_comma) + " " +
                            context?.getString(R.string.pre_pay) + context?.getString(R.string.str_comma) + " " + if (list?.get(
                            position
                        )?.etcRate != 0.0
                    )
                        context?.getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            list?.get(position)?.etcRate
                        )
                    else
                        context?.getString(R.string.free)
                mDescription = mDescription + prePayValue
                rootView.contentDescription = mDescription
                lablePrepay.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                labelTypeOfVehicle.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                labelPayAsYouGo.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                viewFooter.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                lablePrepay.gone()
                labelTypeOfVehicle.gone()
                labelPayAsYouGo.gone()
                viewFooter.gone()

            }
        }
        if (list?.get(position)?.etcRate != 0.0) {
            holder.binding.valuePayAsYouGo.text =
                context?.getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    list?.get(position)?.videoRate
                )
            holder.binding.valuePrepay.text =
                context?.getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    list?.get(position)?.etcRate
                )

        } else {
            holder.binding.valuePayAsYouGo.text = context?.getString(R.string.str_free)
            holder.binding.valuePrepay.text = context?.getString(R.string.str_free)

        }
        if (position == 3) {
            holder.binding.view.visibility = View.GONE
        } else {
            holder.binding.view.visibility = View.VISIBLE
        }



        holder.binding.apply {

            executePendingBindings()
        }
    }

    inner class VehicleViewHolder(val binding: AdapterViewChargesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}
