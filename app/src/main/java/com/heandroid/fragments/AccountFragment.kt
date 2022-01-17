package com.heandroid.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentAccountBinding
import com.heandroid.view.CommunicationActivity
import com.heandroid.view.ProfileActivity

class AccountFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.profile.setOnClickListener {

            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }

        dataBinding.rlAccount.setOnClickListener {

            val intent = Intent(requireActivity(), CommunicationActivity::class.java)
            startActivity(intent)
        }
    }


}