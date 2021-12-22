package com.heandroid.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordThirdBinding
import com.heandroid.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordThirdFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var dataBinding: FragmentForgotPasswordThirdBinding

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

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_third,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (param1 == Constants.EMAIL) {
            dataBinding.topTitle.text = getString(R.string.str_check_your_mail)
        } else {
            dataBinding.topTitle.text = getString(R.string.str_check_text_message)

        }
        dataBinding.btnVerify.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString(Constants.MODE, selectedOpt)
            Navigation.findNavController(dataBinding.root)
                .navigate(
                    R.id.action_forgotPasswordThirdFragment_to_forgotPasswordFourthFragment
                )


        }
    }

}