package com.heandroid.ui.account.creation.step5

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountVehicleListModel
import com.heandroid.data.model.account.CreateAccountVehicleModel
import com.heandroid.databinding.FragmentCreateAccountChoosePaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.VehicleHelper
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountChoosePaymentFragment : BaseFragment<FragmentCreateAccountChoosePaymentBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountChoosePaymentBinding.inflate(inflater, container, false)

    override fun init() {
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 5, 5)
        binding.enable = true
        binding.btnContine.setOnClickListener(this)
        binding.rgPaymentOptions.setOnCheckedChangeListener(this)
        val model =arguments?.getParcelable<CreateAccountRequestModel>(CREATE_ACCOUNT_DATA)
        val vehicle: MutableList<CreateAccountVehicleModel?>? = ArrayList()

        for(i in VehicleHelper.list?.indices!!) {
            VehicleHelper?.list?.get(i)?.run {
                vehicle?.add(CreateAccountVehicleModel(plateCountry = plateInfo?.country,
                                                       plateTypeDesc = /*vehicleInfo?.vehicleClassDesc*/"STANDARD",
                                                       vehicleColor = vehicleInfo?.color,
                                                       vehicleComments = plateInfo?.vehicleComments,
                                                       vehicleMake = vehicleInfo?.make,
                                                       vehicleModel = vehicleInfo?.model,
                                                       vehiclePlate = plateInfo?.number,
                                                       vehicleYear = vehicleInfo?.year))

            }
        }
        model?.ftvehicleList= CreateAccountVehicleListModel(vehicle=vehicle)

        binding.btnContine.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(CREATE_ACCOUNT_DATA,model)
            findNavController().navigate(R.id.action_choosePaymentFragment_to_cardFragment, bundle)
        }
    }

    override fun initCtrl() {}

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {
                if(binding.rbDebitCard.isChecked){
                    val bundle = Bundle()
                    bundle.putParcelable(CREATE_ACCOUNT_DATA, arguments?.getParcelable(CREATE_ACCOUNT_DATA))
                    findNavController().navigate(R.id.action_choosePaymentFragment_to_cardFragment, bundle)
                }else{
                   requireActivity().showToast(getString(R.string.please_select_option))
                }
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {
            R.id.rgPaymentOptions -> {
                binding.enable = true
            }
        }
    }
}