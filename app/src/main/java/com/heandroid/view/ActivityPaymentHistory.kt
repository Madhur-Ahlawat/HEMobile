package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.PaymentHistoryAdapter
import com.heandroid.databinding.ActivityPaymentHistoryBinding
import com.heandroid.model.PaymentModel
import com.heandroid.model.RetrievePaymentListApiResponse
import com.heandroid.utils.Constants


class ActivityPaymentHistory : AppCompatActivity() {

    private lateinit var paymentListAdapter: PaymentHistoryAdapter
    private lateinit var databinding: ActivityPaymentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_payment_history)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_history)
        setView()
    }

    override fun onResume() {
        super.onResume()
        setData()
    }
    private fun setView() {

        databinding.tvBack.setOnClickListener {
            finish()
            //startActivity(Intent(this, DashboardPage::class.java))
        }


    }

    private fun setData()
    {
        var bundle = intent.getBundleExtra(Constants.PAYMENT_DATA)
        var paymentApiRes = bundle?.getSerializable(Constants.PAYMENT_RESPONSE) as RetrievePaymentListApiResponse?
        var list = paymentApiRes?.transactionList
        if (list != null) {
            setRecyclerviewData(list)
            setPaymentValue(list)
            }

           }

    private fun setPaymentValue(list: List<PaymentModel>) {
        var sum = 0.0
        for(item in list)
        {
            sum = (sum+item.amount).toInt().toDouble()
        }

        databinding.tvPaymentCount.text = sum.toString()
    }

    private fun setRecyclerviewData(list: List<PaymentModel>) {
        paymentListAdapter = PaymentHistoryAdapter(this)
        if (list != null) {
            paymentListAdapter.setList(list)
        }
        databinding.rvPayment.layoutManager = LinearLayoutManager(this)
        databinding.rvPayment.setHasFixedSize(true)
        databinding.rvPayment.adapter = paymentListAdapter

    }

}