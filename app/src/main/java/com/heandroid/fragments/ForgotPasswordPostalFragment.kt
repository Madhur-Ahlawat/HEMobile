package com.heandroid.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordPostalBinding
import com.heandroid.utils.Constants


/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordPostalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordPostalFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var dataBinding: FragmentForgotPasswordPostalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(Constants.MODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_postal,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}