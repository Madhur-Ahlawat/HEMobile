package com.heandroid.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsRequest
import com.heandroid.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentMakeOffPaymentCrossingBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.makeoneoffpayment.MakeOneOfPaymentViewModel
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeOffPaymentCrossingFragment : BaseFragment<FragmentMakeOffPaymentCrossingBinding>(),
    FutureCrossingQuantityListner, View.OnClickListener {

    var list: MutableList<VehicleResponse?>? = ArrayList()
    private var totalPrice: Double? = 0.0
    private val viewModel: MakeOneOfPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var mScreeType = 0


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOffPaymentCrossingBinding =
        FragmentMakeOffPaymentCrossingBinding.inflate(inflater, container, false)

    override fun init() {
        list = arguments?.getParcelableArrayList<VehicleResponse?>(Constants.DATA)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        Logg.logging("testing", " MakeOffPaymentCrossingFragment list  $list")

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }
        loader?.show(requireActivity().supportFragmentManager, "")

        list!![0]?.classRate =
            VehicleClassTypeConverter.toClassPrice(list!![0]?.vehicleInfo?.vehicleClassDesc.toString())

        for (i in list?.indices!!) {
            val futureCrossingAmount =
                (list?.get(i)?.classRate?.times(list?.get(i)?.futureQuantity?.toDouble() ?: 0.0))
            val payableCrossingAmount =
                (list?.get(i)?.classRate ?: 0.0).times(
                    list?.get(i)?.pastQuantity?.toDouble() ?: 0.0
                )
            totalPrice = totalPrice?.plus(payableCrossingAmount.plus(futureCrossingAmount ?: 0.0))
        }

        Logg.logging("testing", " MakeOffPaymentCrossingFragment list  $list")

        val model = CrossingDetailsModelsRequest(
            list!![0]?.newPlateInfo!!.number,
            list!![0]?.vehicleInfo?.vehicleClassDesc!!,
            list!![0]?.newPlateInfo?.country!!,
            list!![0]?.vehicleInfo!!.make!!,
            list!![0]?.vehicleInfo!!.model!!
        )
        Logg.logging("testing", " MakeOffPaymentCrossingFragment model  $model")

        viewModel.getCrossingDetails(model)

    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.getCrossingDetails, ::getUnSettledCrossings)

    }

    private lateinit var adapter: MakeOffPaymentCrossingAdapter
    private var payableCrossingAmount=0.0

    private fun getUnSettledCrossings(resource: Resource<CrossingDetailsModelsResponse?>?) {

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    loader?.dismiss()
                    it.let {

                        list?.get(0)?.classRate = it.customerClassRate.toDouble()
//                        list!![0]?.classRate =
//                            VehicleClassTypeConverter.toClassPrice(list!![0]?.vehicleInfo?.vehicleClassDesc.toString())

                        list?.get(0)?.pastQuantity = it.unSettledTrips.toInt()

                        binding.tvTotalPaymentAmount.text =
                            requireActivity().getString(R.string.price, " $totalPrice")
                        binding.rvCrossing.layoutManager = LinearLayoutManager(requireActivity())
                        adapter = MakeOffPaymentCrossingAdapter(requireActivity(), list, this)
                        binding.rvCrossing.adapter = adapter

                        val futureCrossingAmount =
                            (list?.get(0)?.classRate ?: 0.0).times(list?.get(0)?.futureQuantity!!)

//                        val payableCrossingAmount = (list?.get(0)?.classRate
//                            ?: 0.0).times(list?.get(0)?.pastQuantity?.toDouble() ?: 0.0)
                         payableCrossingAmount = it.unPaidAmt.toDouble()
                        list?.get(0)?.pendingDues = payableCrossingAmount

                        val mTempPrice = payableCrossingAmount.plus(futureCrossingAmount)
                        totalPrice = mTempPrice

                        binding.tvTotalPaymentAmount.text =
                            requireActivity().getString(R.string.price, " $totalPrice")
                        list?.get(0)?.price = totalPrice
                        adapter.notifyItemChanged(0)

                    }
                }
            }
            is Resource.DataError -> {
                loader?.dismiss()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
        }

    }


    override fun onAdd(position: Int) {
        updateQuantity(position, +1)
    }

    override fun onMinus(position: Int) {
        updateQuantity(position, -1)
    }

    private fun updateQuantity(position: Int, quantity: Int) {
        list?.get(position)?.futureQuantity =
            (list?.get(position)?.futureQuantity ?: 0) + (quantity)

        val futureCrossingAmount =
            (list?.get(position)?.classRate ?: 0.0).times(list?.get(position)?.futureQuantity!!)

//        val payableCrossingAmount = (list?.get(position)?.classRate
//            ?: 0.0).times(list?.get(position)?.pastQuantity?.toDouble() ?: 0.0)

        val mTempPrice = payableCrossingAmount.plus(futureCrossingAmount)
        totalPrice = mTempPrice

        binding.tvTotalPaymentAmount.text =
            requireActivity().getString(R.string.price, " $totalPrice")
        list?.get(position)?.price = totalPrice
        adapter.notifyItemChanged(position)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {
                val bundle = Bundle()
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                bundle.putParcelableArrayList(Constants.DATA, ArrayList(list))
                findNavController().navigate(
                    R.id.action_makeOneOffPaymentCrossingFragment_to_makeOffPaymentReceiptFragment,
                    bundle
                )
            }
        }
    }
}