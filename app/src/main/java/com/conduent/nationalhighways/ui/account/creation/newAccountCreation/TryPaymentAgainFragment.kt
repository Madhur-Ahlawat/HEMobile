package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTryPaymentAgainBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import javax.annotation.meta.When


class TryPaymentAgainFragment : BaseFragment<FragmentTryPaymentAgainBinding>(),View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_try_payment_again, container, false)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTryPaymentAgainBinding =
        FragmentTryPaymentAgainBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.tryPaymentAgain.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.tryPaymentAgain->{
                findNavController().popBackStack()
            }
        }
    }

}