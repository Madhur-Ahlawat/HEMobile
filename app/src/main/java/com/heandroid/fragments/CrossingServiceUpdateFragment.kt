package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.heandroid.R
import com.heandroid.databinding.CrossingServiceUpdateFragmentBinding

class CrossingServiceUpdateFragment: BaseFragment() {

    private lateinit var dataBinding: CrossingServiceUpdateFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.crossing_service_update_fragment,
            container,
            false
        )
        return dataBinding.root
    }
}