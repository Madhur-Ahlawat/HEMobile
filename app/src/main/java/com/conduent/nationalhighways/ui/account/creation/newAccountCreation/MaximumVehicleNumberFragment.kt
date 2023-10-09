package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentMaximumVehicleNumberBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.VEHICLE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MaximumVehicleNumberFragment : BaseFragment<FragmentMaximumVehicleNumberBinding>(),
    View.OnClickListener {
    private var mList: ArrayList<VehicleResponse?> = ArrayList()
    private var totalCount: Int = 0
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    private var plateNumber = ""
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaximumVehicleNumberBinding =
        FragmentMaximumVehicleNumberBinding.inflate(inflater, container, false)

    override fun init() {
        plateNumber = arguments?.getString(Constants.PLATE_NUMBER, "").toString()
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)

        Log.e("TAG", "init: isExempted --> " + NewCreateAccountRequestModel.isExempted)
        Log.e("TAG", "init: navFlowCall --> " + navFlowCall)
        if (NewCreateAccountRequestModel.isExempted) {

            binding.maximumVehicleAdded.text = getString(
                R.string.vehicle_number_plate_is_exempt_from_dart_ncharge_payment,
                NewCreateAccountRequestModel.plateNumber
            )
            binding.maximumVehicleAddedNote.visibility = View.GONE
            when (navFlowCall) {

                Constants.PAY_FOR_CROSSINGS -> {
                    binding.textMaximumVehicle.text = getString(
                        R.string.crossing_vehicle_exempt_detail_message,
                        NewCreateAccountRequestModel.plateNumber
                    )
                    binding.inCorrectVehicleNumber.visible()
                    binding.cancelBtn.visibility = View.VISIBLE
                    binding.cancelBtn.text = getString(R.string.vehicle_no_longer_exempt)
                }

                else -> {
                    binding.textMaximumVehicle.text = getString(
                        R.string.str_vehicle_exempt_detail_message,
                        NewCreateAccountRequestModel.plateNumber
                    )
                    binding.cancelBtn.visibility = View.GONE
                    binding.btnContinue.text = getString(R.string.str_continue)
                }
            }
        }

        if (NewCreateAccountRequestModel.isRucEligible) {
            nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)
            binding.maximumVehicleAddedNote.visibility = View.GONE
            when (navFlowCall) {
                Constants.PAY_FOR_CROSSINGS -> {
                    binding.cancelBtn.gone()
                    binding.inCorrectVehicleNumber.visible()
                    binding.textMaximumVehicle.text =
                        getString(R.string.str_no_ruc_desc_pay_for_crossing)
                    binding.maximumVehicleAdded.text = getString(
                        R.string.str_vehicle_exempt_message,
                        NewCreateAccountRequestModel.plateNumber
                    )
                }

                else -> {
                    binding.inCorrectVehicleNumber.gone()
                    binding.cancelBtn.visible()
                    binding.btnContinue.text = getString(R.string.str_add_to_account)
                    binding.textMaximumVehicle.text = getString(R.string.str_no_ruc_desc)
                    binding.maximumVehicleAdded.text = getString(
                        R.string.str_vehicle_exempt_message,
                        NewCreateAccountRequestModel.plateNumber
                    )

                }
            }
        }
        if (NewCreateAccountRequestModel.isVehicleAlreadyAdded) {
            binding.textMaximumVehicle.text = getString(
                R.string.str_vehicle_already_exist_desc,
                NewCreateAccountRequestModel.plateNumber
            )
            binding.maximumVehicleAdded.text = getString(
                R.string.str_vehicle_already_added_system,
                NewCreateAccountRequestModel.plateNumber
            )
            binding.maximumVehicleAddedNote.visibility = View.VISIBLE
            binding.cancelBtn.visibility = View.VISIBLE
            binding.btnContinue.text = getString(R.string.str_add_another)
        }


        if (NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal) {
            binding.maximumVehicleAdded.text = getString(
                R.string.vehicle_s_mha_has_already_been_assigned_to_this_account,
                plateNumber
            )
            binding.textMaximumVehicle.text =
                getString(R.string.you_have_already_added_this_vehicle_to_this_account)
            binding.maximumVehicleAddedNote.visibility = View.INVISIBLE
            binding.btnContinue.text = getString(R.string.str_add_another)
            binding.textMaximumVehicle.gravity = Gravity.CENTER
        }

        if (NewCreateAccountRequestModel.isMaxVehicleAdded) {
            binding.maximumVehicleAdded.text =
                getString(R.string.maximum_number_of_vehicles_have_been_registered_against_the_account)
            binding.textMaximumVehicle.text =
                getString(R.string.you_ve_reached_the_maximum_amount_of_vehicles_that_you_can_add_to_your_account_you_ll_need_to_remove_a_vehicle_before_adding_another)
            binding.maximumVehicleAddedNote.visibility = View.GONE
            binding.cancelBtn.visibility = View.GONE
            binding.btnContinue.text = getString(R.string.str_continue)
            binding.textMaximumVehicle.gravity = Gravity.CENTER
        }
        if (sessionManager.getLoggedInUser()) {
            vehicleMgmtViewModel.getVehicleInformationApi("0", "20")
        }
    }

    override fun initCtrl() {

        binding.cancelBtn.setOnClickListener(this)
        binding.btnContinue.setOnClickListener(this)
        binding.inCorrectVehicleNumber.setOnClickListener(this)
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.vehicleListVal, ::handleVehicleListData)
    }

    private fun handleVehicleListData(resource: Resource<List<VehicleResponse?>?>?) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    val response = resource.data
                    totalCount = response.size
                    mList.addAll(response)
                    NewCreateAccountRequestModel.addedVehicleList2 = mList

                }
            }

            is Resource.DataError -> {
                if (resource.errorModel?.errorCode == Constants.TOKEN_FAIL) {
                    displaySessionExpireDialog()
                } else if (resource.errorModel?.errorCode != Constants.NO_DATA_FOR_GIVEN_INDEX) {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }

            else -> {
            }
        }
    }

    override fun onClick(view: View?) {
        val navCall = navFlowCall.equals(VEHICLE_MANAGEMENT, true)
        when (view?.id) {
            R.id.btnContinue -> {

                when (binding.btnContinue.text) {
                    getString(R.string.str_add_another) -> findNavController().navigate(
                        R.id.action_maximumFragment_to_findYourVehicleFragment,
                        bundle()
                    )

                    getString(R.string.str_continue) ->

                        when (navFlowCall) {

                            Constants.PAY_FOR_CROSSINGS -> {
                                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
                                requireActivity().finish()
                            }

                            else -> {
                                if (NewCreateAccountRequestModel.vehicleList.size == 0) {

                                    if (NewCreateAccountRequestModel.isExempted || navCall) {
                                        findNavController().popBackStack()
                                    } else {
                                        noVehicleAddedDialog()
                                    }
                                } else {
                                    findNavController().navigate(
                                        R.id.action_maximumFragment_to_vehicleListFragment,
                                        bundle()
                                    )

                                }
                            }
                        }


                    getString(R.string.str_add_to_account) -> {
                        val accountData = NewCreateAccountRequestModel
                        val vehicleList = accountData.vehicleList

                        nonUKVehicleModel?.let {

                            vehicleList.add(it)
                            findNavController().navigate(
                                R.id.action_maximumFragment_to_vehicleListFragment,
                                bundle()
                            )


                        }
                    }
                }

                NewCreateAccountRequestModel.isMaxVehicleAdded = false
            }

            R.id.cancel_btn -> {

                when (binding.cancelBtn.text) {

                    getString(R.string.vehicle_no_longer_exempt) -> findNavController().navigate(
                        R.id.action_maximumFragment_to_addVehicleFragment,
                        bundle()
                    )

                    else -> {
                        when (navFlowCall) {

                            Constants.PAY_FOR_CROSSINGS -> {
                                findNavController().popBackStack()
                            }

                            else -> {
                                if (NewCreateAccountRequestModel.addedVehicleList2.isEmpty()) {
                                    if (navCall) {
                                        findNavController().popBackStack()
                                    } else {
                                        if (NewCreateAccountRequestModel.isVehicleAlreadyAdded) {
                                            if (NewCreateAccountRequestModel.vehicleList.size == 0) {
                                                noVehicleAddedDialog()

                                            }

                                        } else {
                                            if (NewCreateAccountRequestModel.isRucEligible) {
                                                findNavController().navigate(
                                                    R.id.action_maximumFragment_to_findYourVehicleFragment,
                                                    bundle()
                                                )
                                            } else {
                                                findNavController().popBackStack()

                                            }

                                        }
                                    }
                                } else {
                                    if (sessionManager.getLoggedInUser()) {
                                        findNavController().navigate(
                                            R.id.action_maximumFragment_to_vehicleHomeListFragment,
                                            bundle()
                                        )
                                    } else {
                                        findNavController().navigate(
                                            R.id.action_maximumFragment_to_vehicleListFragment,
                                            bundle()
                                        )
                                    }

                                }

                                NewCreateAccountRequestModel.isMaxVehicleAdded = false
                            }
                        }
                    }
                }


            }

            R.id.inCorrectVehicleNumber -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putParcelable(Constants.VEHICLE_DETAIL, nonUKVehicleModel)
        bundle.putParcelable(Constants.NAV_DATA_KEY, CrossingDetailsModelsResponse(

            vehicleColor = nonUKVehicleModel?.vehicleColor,
            plateCountry = nonUKVehicleModel?.plateCountry,
            isExempted = nonUKVehicleModel?.isExempted,
            vehicleClass = nonUKVehicleModel?.vehicleClass,
            vehicleModel = nonUKVehicleModel?.vehicleModel,
            plateNo = nonUKVehicleModel?.plateNumber?:"",
            vehicleMake = nonUKVehicleModel?.vehicleMake,
            isRUCEligible = nonUKVehicleModel?.isRUCEligible,
            ))
        return bundle
    }

    private fun noVehicleAddedDialog() {
        displayCustomMessage(getString(R.string.str_no_vehicle),
            getString(R.string.str_you_must_at_least_one_vehicle),
            getString(R.string.str_add_vehicle),
            getString(R.string.str_cancel),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    findNavController().navigate(R.id.action_maximumFragment_to_findYourVehicleFragment)

                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
                    requireActivity().finish()
                }
            })
    }


}