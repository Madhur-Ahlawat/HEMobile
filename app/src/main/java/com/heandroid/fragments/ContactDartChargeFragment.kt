package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.heandroid.R
import com.heandroid.databinding.ContactDartChargeFragmentBinding

class ContactDartChargeFragment: BaseFragment() {

    private lateinit var dataBinding: ContactDartChargeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            // Inflate the layout for this fragment
            dataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.contact_dart_charge_fragment,
                container,
                false
            )
            return dataBinding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    }
