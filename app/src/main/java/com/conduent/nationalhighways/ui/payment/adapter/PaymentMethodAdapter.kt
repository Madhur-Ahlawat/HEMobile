package com.conduent.nationalhighways.ui.payment.adapter

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.databinding.PaymentmethodadapterlayoutBinding
import com.conduent.nationalhighways.ui.payment.newpaymentmethod.NewPaymentMethodFragment.Companion.isDirectDebit
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils

class PaymentMethodAdapter(
    private var context: Context,
    private var paymentList: MutableList<CardListResponseModel?>?,
    private val paymentMethodCallback: PaymentMethodCallback
) :
    RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {

    private var defaultDescriptionText: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentMethodViewHolder = PaymentMethodViewHolder(
        PaymentmethodadapterlayoutBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
    )

    class PaymentMethodViewHolder(val binding: PaymentmethodadapterlayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: PaymentMethodViewHolder,
        position: Int
    ) {

        holder.binding.ivCardType.setImageResource(
            Utils.setCardImage(
                paymentList?.get(position)?.cardType ?: ""
            )
        )

        holder.binding.ivCardTypeLargeFont.setImageResource(
            Utils.setCardImage(
                paymentList?.get(position)?.cardType ?: ""
            )
        )

        if (paymentList?.get(position)?.primaryCard == true) {
            holder.binding.textDefault.visibility = View.VISIBLE
            holder.binding.textMakeDefault.visibility = View.GONE
            defaultDescriptionText = context.getString(R.string.str_default)
        } else {
            holder.binding.textDefault.visibility = View.GONE
            defaultDescriptionText = context.getString(R.string.str_default)
            if (paymentList?.get(position)?.emandateStatus == "ACTIVE" && paymentList?.get(
                    position
                )?.bankAccount == true
            ) {
                holder.binding.textMakeDefault.visibility = View.GONE
                defaultDescriptionText = ""
            } else {
                holder.binding.textMakeDefault.visibility = View.VISIBLE
                defaultDescriptionText = context.getString(R.string.str_make_default_)
            }
        }


        if (paymentList?.get(position)?.emandateStatus == "PENDING" && paymentList?.get(position)?.bankAccount == true) {
            holder.binding.tvSelectPaymentMethod.text = context.getString(
                R.string.str_your_direct_debit_for,
                paymentList?.get(position)?.bankAccountNumber.toString()
            )
            holder.binding.ivCardType.setImageResource(R.drawable.directdebit)
            holder.binding.delete.visibility = View.GONE
            Log.e("TAG", "onBindViewHolder:defaultDescriptionText---> " + defaultDescriptionText)

            holder.binding.cardView.contentDescription = context.getString(
                R.string.str_your_direct_debit_for,
                Utils.accessibilityForNumbers(paymentList?.get(position)?.bankAccountNumber.toString())
            ) + " " + defaultDescriptionText
        } else if (paymentList?.get(position)?.emandateStatus == "ACTIVE" && paymentList?.get(
                position
            )?.bankAccount == true
        ) {
            val htmlText =
                Html.fromHtml(
                    context.getString(R.string.direct_debit) + "<br>" +
                            paymentList?.get(
                                position
                            )?.bankAccountNumber?.let {
                                Utils.maskCardNumber(
                                    it
                                )
                            }.toString()

                )

            holder.binding.tvSelectPaymentMethod.text = htmlText
            holder.binding.ivCardType.setImageResource(R.drawable.directdebit)
            holder.binding.delete.visibility = View.VISIBLE
            isDirectDebit = true
            Log.e("TAG", "onBindViewHolder:defaultDescriptionText--> " + defaultDescriptionText)

            holder.binding.cardView.contentDescription =
                context.getString(R.string.direct_debit) + " " +
                        Utils.accessibilityForNumbers(
                            paymentList?.get(
                                position
                            )?.bankAccountNumber?.let {
                                Utils.maskCardNumber(
                                    it
                                )
                            }.toString()
                        ) + " " + defaultDescriptionText

        } else {
            val htmlText =
                Html.fromHtml(
                    paymentList?.get(position)?.cardType + "<br>" +
                            paymentList?.get(
                                position
                            )?.cardNumber?.let {
                                Utils.maskCardNumber(
                                    it
                                )
                            }.toString()

                )

            holder.binding.tvSelectPaymentMethod.text = htmlText
            holder.binding.delete.visibility = View.VISIBLE

            Log.e("TAG", "onBindViewHolder:defaultDescriptionText " + defaultDescriptionText)

            holder.binding.cardView.contentDescription =
                paymentList?.get(position)?.cardType + " " +
                        Utils.accessibilityForNumbers(
                            paymentList?.get(
                                position
                            )?.cardNumber?.let {
                                Utils.maskCardNumber(
                                    it
                                )
                            }.toString()
                        ) + " " + defaultDescriptionText

        }


        holder.binding.tvSelectPaymentMethod.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                holder.binding.tvSelectPaymentMethod.viewTreeObserver.removeOnGlobalLayoutListener(
                    this
                )
                val tvSelectPaymentMethodLinesCount = holder.binding.tvSelectPaymentMethod.lineCount
                Log.e(
                    "TAG",
                    "onGlobalLayout: tvSelectPaymentMethodLinesCount " + tvSelectPaymentMethodLinesCount
                )
                if (tvSelectPaymentMethodLinesCount >= 3) {
                    holder.binding.clCardStatus.visibility = View.GONE
                    holder.binding.clCardStatusLargeFont.visibility = View.VISIBLE
                    if (paymentList?.get(position)?.primaryCard == true) {
                        holder.binding.textDefaultLargefont.visibility = View.VISIBLE
                        holder.binding.textMakeDefaultLargefont.visibility = View.GONE
                    } else {
                        holder.binding.textDefaultLargefont.visibility = View.GONE
                        if (paymentList?.get(position)?.emandateStatus == "ACTIVE" && paymentList?.get(
                                position
                            )?.bankAccount == true
                        ) {
                            holder.binding.textMakeDefaultLargefont.visibility = View.GONE
                        } else {
                            holder.binding.textMakeDefaultLargefont.visibility = View.VISIBLE
                        }
                    }
                } else {
                    holder.binding.clCardStatus.visibility = View.VISIBLE
                    holder.binding.clCardStatusLargeFont.visibility = View.GONE
                }

            }
        })



        holder.binding.textMakeDefault.setOnClickListener {
            paymentMethodCallback.paymentMethodCallback(
                position,
                Constants.MAKE_DEFAULT
            )
        }
        holder.binding.delete.setOnClickListener {
            if (paymentList?.get(position)?.bankAccount == true && paymentList?.get(position)?.emandateStatus == "ACTIVE") {
                paymentMethodCallback.paymentMethodCallback(
                    position,
                    Constants.DIRECT_DEBIT
                )
            } else {
                paymentMethodCallback.paymentMethodCallback(
                    position,
                    Constants.DELETE_CARD
                )
            }


            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return paymentList?.size ?: 0
    }

    fun updateList(paymentList: MutableList<CardListResponseModel?>?) {
        this.paymentList = paymentList!!
        notifyDataSetChanged()

    }

    interface PaymentMethodCallback {
        fun paymentMethodCallback(position: Int, value: String)

    }
}