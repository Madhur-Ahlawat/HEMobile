package com.heandroid.ui.payment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.base.BaseFragment
import com.heandroid.databinding.FragmentMakePaymentAddVehicleBinding
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class MakePaymentAddVehicleFragment : BaseFragment<FragmentMakePaymentAddVehicleBinding>(),
    View.OnClickListener, AddVehicleListener, ItemClickListener {

    private lateinit var mAdapter: AddedVehicleListAdapter
    private var addDialog: AddVehicleDialog? = null
    private var loader: LoaderDialog? = null
    private var vehicleList = VehicleHelper.list
    private var isFromOneOfPayment: Boolean? = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakePaymentAddVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        mAdapter = AddedVehicleListAdapter(this)
        mAdapter.setList(vehicleList)
        binding.rvVehiclesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehiclesList.setHasFixedSize(true)
        binding.rvVehiclesList.adapter = mAdapter

        isFromOneOfPayment = arguments?.getBoolean(Constants.PAYMENT_ONE_OFF)

        setAdapter()
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.findVehicle.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addVehicleBtn -> {
                addDialog = AddVehicleDialog.newInstance(
                    getString(R.string.str_title),
                    getString(R.string.str_sub_title),
                    this
                )
                addDialog?.show(childFragmentManager, AddVehicleDialog.TAG)
            }
            R.id.findVehicle -> {

            }
        }
    }

    private fun setAdapter() {

        hideAndShowRecyclerView()

        mAdapter.setList(vehicleList)
        mAdapter.notifyDataSetChanged()

        if (vehicleList?.isNotEmpty() == true) {
            setBtnActivated()
        }
        if (vehicleList?.size ?: 0 < Constants.MAX_VEHICLE_SIZE) {
            setAddBtnActivated()
        } else {
            setAddBtnDisabled()
        }
    }

    private fun hideAndShowRecyclerView() {
        if (vehicleList?.isEmpty()!! || vehicleList?.size == 0) {
            binding.apply {
                Logg.logging("MakePayMent", " calling inside ")

                rvVehiclesList.gone()
                noVehiclesAdded.visible()
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
            setBtnDisabled()
            setAddBtnActivated()
        } else {
            binding.rvVehiclesList.visibility = View.VISIBLE
            binding.noVehiclesAdded.visibility = View.GONE
            binding.addVehiclesTxt.text = requireContext().getString(R.string.txt_your_vehicle)

        }
    }

    override fun onAddClick(details: VehicleResponse) {
        addDialog?.dismiss()

        setAdapter()
        val bundle = Bundle().apply {
            putParcelable(Constants.DATA, details)
            putBoolean(Constants.PAYMENT_PAGE, true)
        }
        findNavController().navigate(
            R.id.action_makePaymentAddVehicleFragment_to_addVehicleDetailsFragment,
            bundle
        )
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {
        setAddBtnActivated()
        if (vehicleList?.size ?: 0 > 0) {
            vehicleList?.removeAt(pos)
            if (::mAdapter.isInitialized) {
                mAdapter.setList(vehicleList)
                mAdapter.notifyItemRemoved(pos)
            }
        }
        if (vehicleList?.size == 0) {
            setBtnDisabled()
            binding.apply {
                rvVehiclesList.visibility = View.GONE
                noVehiclesAdded.visibility = View.VISIBLE
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
        }
    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        val bundle = Bundle()
        bundle.putInt(
            Constants.VEHICLE_SCREEN_KEY,
            Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT
        )
        bundle.putParcelable(
            Constants.DATA,
            details
        )

        findNavController().navigate(
            R.id.action_makePaymentAddVehicleFragment_to_addVehicleDoneFragment,
            bundle
        )
    }

    private fun setBtnActivated() {
        binding.findButton = true
    }

    private fun setBtnDisabled() {
        binding.findButton = false
    }

    private fun setAddBtnActivated() {
        binding.addButton = true
    }

    private fun setAddBtnDisabled() {
        binding.addButton = false
    }
}