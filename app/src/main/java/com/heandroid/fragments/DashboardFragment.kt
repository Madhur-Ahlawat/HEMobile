package com.heandroid.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dashboard,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var urlString = "https://mobileapp.sunpass.com/vector/account/home/ftAccountSettings.do?name=sms"
        dataBinding.btnUpdateNow.setOnClickListener {
            val url = "http://www.amazon.com"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlString)))
        }
    }
}