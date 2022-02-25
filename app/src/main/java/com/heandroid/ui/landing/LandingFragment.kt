package com.heandroid.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentLandingBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants

class LandingFragment : BaseFragment<FragmentLandingBinding>(), View.OnClickListener,RadioGroup.OnCheckedChangeListener {

    private var screenType : String =""
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLandingBinding {
        return FragmentLandingBinding.inflate(inflater, container, false)
    }

    override fun init() {
        binding.apply {
            btnContinue.setOnClickListener(this@LandingFragment)
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rb_create_account -> {
                screenType = Constants.CREATE_ACCOUNT
            }

            R.id.rb_one_of_payment -> {
                screenType = Constants.ONE_OFF_PAYMENT
            }

            R.id.rb_resolve_penalty -> {
                screenType = Constants.RESOLVE_PENALTY
            }
            R.id.rb_check_for_paid -> {
                screenType = Constants.CHECK_FOR_PAID
            }
            R.id.rb_view_charges -> {
                screenType = Constants.VIEW_CHARGES
            }

        }


    }

    override fun onClick(v: View?) {
        v?.let {
            when(v.id)
            {
                R.id.btn_continue->{

                }
            }
        }
    }
}