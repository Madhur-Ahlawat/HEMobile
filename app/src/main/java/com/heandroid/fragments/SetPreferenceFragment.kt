package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentSelectCommunicationPreferenceBinding

class SetPreferenceFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentSelectCommunicationPreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_communication_preference,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.btnNext.setOnClickListener {
            dataBinding.clSelectPref.visibility = GONE
            dataBinding.clLikeAdvice.visibility = VISIBLE
            dataBinding.clEmailAdvice.visibility = GONE
            dataBinding.updatePhoneView.visibility = VISIBLE
            dataBinding.clPhoneNumber.visibility = GONE
        }

        dataBinding.btnNextTwo.setOnClickListener {
            dataBinding.clSelectPref.visibility = GONE
            dataBinding.clLikeAdvice.visibility = GONE
            dataBinding.clEmailAdvice.visibility = VISIBLE
        }

        dataBinding.btnPrevTwo.setOnClickListener {
            dataBinding.clSelectPref.visibility = VISIBLE
            dataBinding.clLikeAdvice.visibility = GONE
            dataBinding.clEmailAdvice.visibility = GONE
            dataBinding.updatePhoneView.visibility = GONE
            dataBinding.clPhoneNumber.visibility = VISIBLE
        }

        dataBinding.btnPrevious.setOnClickListener {
            dataBinding.clSelectPref.visibility = GONE
            dataBinding.clLikeAdvice.visibility = VISIBLE
            dataBinding.clEmailAdvice.visibility = GONE
        }
    }

}