package com.heandroid.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordFifthBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFifthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ForgotPasswordFifthFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private lateinit var dataBinding: FragmentForgotPasswordFifthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_fifth,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.btnSubmit.setOnClickListener {

            requireActivity().finish()

        }
    }


}