package com.heandroid.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.makeoneofpayment.*
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentMakeOffPaymentConfirmationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.makeoneoffpayment.MakeOneOfPaymentViewModel
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

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


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOffPaymentConfirmationBinding =
        FragmentMakeOffPaymentConfirmationBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        mEmail = arguments?.getString(Constants.EMAIL)!!
        list = arguments?.getParcelableArrayList<VehicleResponse>(Constants.DATA)!!
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

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    loader?.dismiss()
                    it?.let {
                        Logg.logging(
                            "testing",
                            " MakeOffPaymentConfirmationFragment success it  $it"
                        )

                        val mBundle = Bundle()
                        mBundle.putParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP, it)
                        mBundle.putString(Constants.EMAIL, mEmail)
                        mBundle.putString(Constants.OPTIONS_TYPE,arguments?.getString(Constants.OPTIONS_TYPE))
                        mBundle.putParcelableArrayList(Constants.DATA, ArrayList(list))
                        findNavController().navigate(
                            R.id.action_makeOffPaymentConfirmationFragment_to_makeOffPaymentSuccessfulFragment,
                            mBundle
                        )

                    }
                }
            }
            is Resource.DataError -> {
                Logg.logging("testing", " MakeOffPaymentConfirmationFragment error called")

                loader?.dismiss()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPayNow -> {
                loader?.show(requireActivity().supportFragmentManager, "")

                val vehicleList = VehicleList(
                    list[0].newPlateInfo!!.number,
                    list[0].vehicleInfo!!.make!!,
                    list[0].vehicleInfo!!.model!!,
                    list[0].vehicleInfo?.vehicleClassDesc!!,
                    list[0].newPlateInfo!!.country,
                    "0",
                    list[0].futureQuantity.toString(),
                    "10",
                    list[0].vehicleInfo!!.color!!,
                    list[0].classRate.toString(),
                    "",
                    "",
                    "",
                    "0",
                    ""
                )

                val paymentTypeInfo = PaymentTypeInfo(
                    mModel!!.card.type,
                    mModel!!.card.number,
                    mModel!!.token,
                    mModel!!.card.exp.subSequence(0, 2).toString(),
                    "20${mModel!!.card.exp.subSequence(2, 4)}",
                    list[0].price.toString(),
                    mModel!!.check.name!!,
                    "",
                    mEmail,
                    "", "", "", "", "", "", "", ""
                )
                val mVehicleList = ArrayList<VehicleList>()
                mVehicleList.clear()
                mVehicleList.add(vehicleList)
                val ftVehicleList = FtVehicleList(mVehicleList)
                val oneOfPayModelReq = OneOfPaymentModelRequest(ftVehicleList, paymentTypeInfo)

                Logg.logging(
                    "testing",
                    " MakeOffPaymentConfirmationFragment onPay oneOfPayModelReq  $oneOfPayModelReq"
                )
                val mBundle = Bundle()
//                mBundle.putParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP, it)
                mBundle.putString(Constants.EMAIL, mEmail)
                mBundle.putString(Constants.OPTIONS_TYPE,arguments?.getString(Constants.OPTIONS_TYPE))
                mBundle.putParcelableArrayList(Constants.DATA, ArrayList(list))
                findNavController().navigate(
                    R.id.action_makeOffPaymentConfirmationFragment_to_makeOffPaymentSuccessfulFragment,
                    mBundle
                )

              //  viewModel.oneOfPaymentsPay(oneOfPayModelReq)
            }
        }
    }

    override fun onViewClicked() {
        Toast.makeText(requireContext(), "Vehicle selected", Toast.LENGTH_LONG).show()
    }
}