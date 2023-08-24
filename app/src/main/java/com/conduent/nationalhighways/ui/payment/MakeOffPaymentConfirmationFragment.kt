package com.conduent.nationalhighways.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.*
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentMakeOffPaymentConfirmationBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.adapter.MakeOffPaymentVehicleAdapter
import com.conduent.nationalhighways.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MakeOffPaymentConfirmationFragment :
    BaseFragment<FragmentMakeOffPaymentConfirmationBinding>(), View.OnClickListener,
    MakeOffPaymentVehicleAdapter.OnVehicleItemClickedListener {

    private var mScreeType = 0
    private var list: MutableList<VehicleResponse> = ArrayList()
    private var mEmail = ""
    private var mModel: CardResponseModel? = null
    private val viewModel: MakeOneOfPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var number = ""
    private var mail = ""
    private var mOptionsType = ""

    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakeOffPaymentConfirmationBinding.inflate(inflater, container, false)

    override fun init() {
        AdobeAnalytics.setScreenTrack(
            "one of  payment:payment confirm",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of  payment: payment confirm",
            sessionManager.getLoggedInUser()
        )

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }
        if (arguments?.containsKey(Constants.EMAIL) == true) {
            mEmail = arguments?.getString(Constants.EMAIL).toString()
        }
        if (arguments?.containsKey(Constants.OPTIONS_TYPE) == true) {
            mOptionsType = arguments?.getString(Constants.OPTIONS_TYPE).toString()
        }
        if (arguments?.containsKey(Constants.DATA) == true) {
            list = arguments?.getParcelableArrayList(Constants.DATA)!!
        }

        mModel = arguments?.getParcelable(Constants.PAYMENT_DATA)
        binding.rvVechileList.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvVechileList.adapter = MakeOffPaymentVehicleAdapter(requireActivity(), list, this)
        binding.tvPaymentMethod.text = mModel?.card?.number
        binding.tvEmail.text = mEmail
        binding.tvAmount.text = "£ ${list[0].price}"

    }

    override fun initCtrl() {
        binding.btnPayNow.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.oneOfPaymentsPay, ::oneOfPaymentPay)
    }

    private fun oneOfPaymentPay(resource: Resource<OneOfPaymentModelResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    it.let {
                        val mBundle = Bundle()
                        mBundle.putParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP, it)
                        mBundle.putString(Constants.EMAIL, mEmail)
                        mBundle.putString(
                            Constants.OPTIONS_TYPE,
                            arguments?.getString(Constants.OPTIONS_TYPE)
                        )
                        mBundle.putParcelableArrayList(Constants.DATA, ArrayList(list))
                        AdobeAnalytics.setActionTrackPaymentMethodOrderId( "Confirm ",
                            " one of payment: payment confirm",
                            "payment ",
                            "english",
                            " one of payment",
                            "home",
                            "success","card",it.referenceNumber!!,"1",sessionManager.getLoggedInUser()
                        )

                        findNavController().navigate(
                            R.id.action_makeOffPaymentConfirmationFragment_to_makeOffPaymentSuccessfulFragment,
                            mBundle
                        )

                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)

                AdobeAnalytics.setActionTrackPaymentMethod( "Confirm ",
                 " one of payment: payment confirm",
                "payment ",
                "english",
                 " one of payment",
                "home",
                resource.errorMsg,"card",sessionManager.getLoggedInUser()
                )

            }
            else -> {
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPayNow -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                val vehicleList = VehicleList(
                    list[0].newPlateInfo?.number,
                    list[0].vehicleInfo?.make,
                    list[0].vehicleInfo?.model,
                    list[0].vehicleInfo?.vehicleClassDesc,
                    list[0].newPlateInfo?.country,
                    list[0].pendingDues.toString(),
                    list[0].futureQuantity.toString(),
                    (list[0].classRate?.let {
                        list[0].futureQuantity?.toDouble()
                            ?.times(it)
                    }).toString(),
                    list[0].vehicleInfo?.color,
                    list[0].classRate.toString(),
                    list[0].vehicleInfo?.vehicleClassDesc,
                    list[0].classRate.toString(),
                    "",
                    list[0].pastQuantity.toString(),
                    list[0].classRate.toString()
                )

                if (mOptionsType.equals("Email", true)) {
                    mail = mEmail
                } else {
                    number = mEmail
                }
                val paymentTypeInfo = PaymentTypeInfo(
                    mModel?.card?.type?.uppercase(Locale.ROOT),
                    mModel?.card?.number,
                    mModel?.token,
                    mModel?.card?.exp?.subSequence(0, 2).toString(),
                    "20${mModel?.card?.exp?.subSequence(2, 4)}",
                    list[0].price.toString(),
                    mModel?.check?.name,
                    "",
                    mail,
                    number, "", "", "", "", "", "", ""
                )
                val mVehicleList = ArrayList<VehicleList>()
                mVehicleList.clear()
                mVehicleList.add(vehicleList)
                val ftVehicleList = FtVehicleList(mVehicleList)
                val oneOfPayModelReq = OneOfPaymentModelRequest(ftVehicleList, paymentTypeInfo)
                viewModel.oneOfPaymentsPay(oneOfPayModelReq)
            }
        }
    }

    override fun onViewClicked() {
        Toast.makeText(requireContext(), "Vehicle selected", Toast.LENGTH_LONG).show()
    }
}