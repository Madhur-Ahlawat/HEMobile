package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.PaymentHistoryAdapter
import com.heandroid.model.RetrievePaymentListApiResponse
import com.heandroid.utils.Constants
import kotlinx.android.synthetic.main.activity_payment_history.*


class ActivityPaymentHistory : AppCompatActivity() {

    private lateinit var paymentListAdapter: PaymentHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)
        setView()
    }

    private fun setView() {

        tv_back.setOnClickListener {
            finish()
            startActivity(Intent(this, DashboardPage::class.java))
        }

        var bundle = intent.getBundleExtra(Constants.PAYMENT_DATA)
        var paymentApiRes = bundle?.getSerializable(Constants.PAYMENT_RESPONSE) as RetrievePaymentListApiResponse?
        var list = paymentApiRes?.transactionList
        paymentListAdapter = PaymentHistoryAdapter(this)
        if (list != null) {
            paymentListAdapter.setList(list)
        }
        rv_payment.layoutManager = LinearLayoutManager(this)
        rv_payment.setHasFixedSize(true)
        rv_payment.adapter = paymentListAdapter
    }
}