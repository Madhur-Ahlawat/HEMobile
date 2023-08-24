package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleDetailChangesBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.MakeOneOfPaymentViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessVehicleDetailFragment : BaseFragment<FragmentBusinessVehicleDetailChangesBinding>(),
    View.OnClickListener {

    private val viewModel: MakeOneOfPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isViewCreated = false
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessVehicleDetailChangesBinding.inflate(inflater, container, false)

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)

        Log.d("vehicleData", Gson().toJson(nonUKVehicleModel))

        binding.apply {
            regNum.text = nonUKVehicleModel?.plateNumber
            typeOfVehicle.text = Utils.getVehicleType(nonUKVehicleModel?.vehicleClass.toString())
            vehicleModel.text = nonUKVehicleModel?.vehicleModel
            vehicleMake.text = nonUKVehicleModel?.vehicleMake
            vehicleColor.text = nonUKVehicleModel?.vehicleColor
        }


    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this@BusinessVehicleDetailFragment)
        binding.notVehicle.setOnClickListener(this@BusinessVehicleDetailFragment)
        binding.inCorrectVehicleNumber.setOnClickListener(this)
    }

    override fun observer() {
        if (!isViewCreated) {
            observe(viewModel.getCrossingDetails, ::getUnSettledCrossings)
        }
        isViewCreated = true
    }


    private fun getUnSettledCrossings(resource: Resource<CrossingDetailsModelsResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    it.let {
                        it.vehicleModel = nonUKVehicleModel?.vehicleModel
                        it.vehicleMake = nonUKVehicleModel?.vehicleMake
                        it.vehicleColor= nonUKVehicleModel?.vehicleColor
                        val unSettledTrips = it.unSettledTrips?.toDouble()
                        val chargingRate = it.chargingRate?.toDouble()
                        val customerClassRate = it.customerClassRate?.toDouble()
                        if (unSettledTrips != null) {
                            val bundle = Bundle()
                            bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                            bundle.putParcelable(Constants.NAV_DATA_KEY,resource.data)
                            if(chargingRate != customerClassRate){
                                findNavController().navigate(
                                    R.id.action_businessVehicleDetailFragment_to_deletePaymentMethodFragment,
                                    bundle
                                )
                            }else {
                                if (unSettledTrips > 0) {

                                    findNavController().navigate(
                                        R.id.action_businessVehicleDetailFragment_to_pay_for_crossingFragment,
                                        bundle
                                    )

                                } else {
                                    findNavController().navigate(
                                        R.id.action_businessVehicleDetailFragment_to_additional_crossingFragment,
                                        bundle
                                    )
                                }
                            }
                        }

                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }

    }

    override fun onClick(view: View?) {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
        when (view?.id) {
            R.id.confirmBtn -> {
                val accountData = NewCreateAccountRequestModel
                val vehicleList = accountData.vehicleList
                when(navFlowCall) {

                    Constants.PAY_FOR_CROSSINGS -> {
                        nonUKVehicleModel?.let { vehicleList.add(it) }
                        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                        val model = CrossingDetailsModelsRequest(
                            nonUKVehicleModel?.plateNumber,
                            nonUKVehicleModel?.vehicleClass,
                            "UK",
                            nonUKVehicleModel?.vehicleMake,
                            nonUKVehicleModel?.vehicleModel
                        )

                        viewModel.getCrossingDetails(model)
                    }

                    else -> {

                        val oldPlateNumber = arguments?.getString(Constants.OLD_PLATE_NUMBER,"").toString()
                        if (oldPlateNumber.isNotEmpty()) {
                            val index = arguments?.getInt(Constants.VEHICLE_INDEX)
                            if (index != null) {
                                vehicleList.removeAt(index)
                            }
                        }
                        nonUKVehicleModel?.let {

                            vehicleList.add(it)
                            val editCall = navFlowCall.equals(Constants.EDIT_SUMMARY,true)
                            if(editCall){
                                findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_accountSummaryFragment,bundle)
                            }else {
                                findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_vehicleListFragment,bundle)
                            }

                        }
                    }

                }




            }

            R.id.notVehicle -> {
                bundle.putParcelable(Constants.VEHICLE_DETAIL, nonUKVehicleModel)
                findNavController().navigate(
                    R.id.action_businessVehicleDetailFragment_to_yourVehicleFragment,
                    bundle
                )
            }

            R.id.inCorrectVehicleNumber -> {
                findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_findYourVehicleFragment,bundle)
            }
        }
    }
}