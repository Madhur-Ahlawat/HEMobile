package com.heandroid.ui.vehicle.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.ui.base.BaseFragment
import com.heandroid.databinding.FragmentMakePaymentAddVehicleBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVechileViewModel
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleDialog
import com.heandroid.ui.vehicle.addvehicle.AddVehicleListener
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
@AndroidEntryPoint
class MakePaymentAddVehicleFragment : BaseFragment<FragmentMakePaymentAddVehicleBinding>(),
    View.OnClickListener, AddVehicleListener, ItemClickListener {

    private val mVehicleList = ArrayList<VehicleResponse>()
    private lateinit var mAdapter: AddedVehicleListAdapter
    private var addDialog: AddVehicleDialog? = null
    private var isAccountVehicle: Boolean? = false
    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountVechileViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakePaymentAddVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        mAdapter = AddedVehicleListAdapter(this)
        mAdapter.setList(mVehicleList)
        binding.rvVehiclesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehiclesList.setHasFixedSize(true)
        binding.rvVehiclesList.adapter = mAdapter
        if (mVehicleList.isEmpty() || mVehicleList.size == 0) {
            binding.apply {
                rvVehiclesList.visibility = View.GONE
                noVehiclesAdded.visibility = View.VISIBLE
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
            setBtnDisabled()
            setAddBtnActivated()
        } else {
            setAdapter()
        }

        isAccountVehicle = arguments?.getBoolean("IsAccountVehicle")

        if(isAccountVehicle == true){
            setAdapter(isAccountVehicle!!)
        }
    }

    override fun initCtrl() {
        binding.addVehicleBtn.setOnClickListener(this)
        binding.findVehicle.setOnClickListener(this)

    }

    override fun observer() {}
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
                if(isAccountVehicle == true){
                    callLoader()
                    viewModel.getVehicleData(
                        mVehicleList[0].plateInfo.number,
                        Constants.AGENCY_ID
                    )
                    // mVehicleList.clear()
                    //isAccountVehicle = false
                    observe(viewModel.findVehicleLiveData, ::handleVehicleResponse)
                }
            }
        }
    }

    private fun callLoader() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, "")
    }

    private fun handleVehicleResponse(resource: Resource<VehicleInfoDetails?>?) {
        try {
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {
                    if (resource.data?.retrievePlateInfoDetails != null) {
                        val bundle =  Bundle()
                        bundle.putParcelable(Constants.FIND_VEHICLE_DATA,resource.data)
                        findNavController().navigate(R.id.action_findYourVehicleFragment_to_showVehicleDetailsFragment, bundle)
                    }
                }
                is Resource.DataError -> { ErrorUtil.showError(binding.root, resource.errorMsg) }
            }
        }catch (e: Exception){}}

    private fun setAdapter(isAccountVehicle: Boolean = false) {
        binding.rvVehiclesList.visibility = View.VISIBLE
        binding.noVehiclesAdded.visibility = View.GONE
        binding.addVehiclesTxt.text = requireContext().getString(R.string.txt_your_vehicle)

        val vehicleNo = arguments?.getString("VehicleNo")
        if (isAccountVehicle) {
            val plateRes = PlateInfoResponse()
            plateRes.number = vehicleNo!!
            plateRes.country = "UK"
            val vehicleRes = VehicleResponse(PlateInfoResponse(),plateRes, VehicleInfoResponse(), false, 0, 0.0 )
            mVehicleList.add(vehicleRes)
            mAdapter.setList(mVehicleList)
            mAdapter.notifyDataSetChanged()
        }else {
            mAdapter.setList(mVehicleList)
            mAdapter.notifyDataSetChanged()
        }
        if (mVehicleList.isNotEmpty()) {
            setBtnActivated()
        }
        if (mVehicleList.size < Constants.MAX_VEHICLE_SIZE) {
            setAddBtnActivated()
        } else {
            setAddBtnDisabled()
        }
    }

    override fun onAddClick(details: VehicleResponse) {
        addDialog?.dismiss()
        if (details.plateInfo.country == Constants.UK) {
            mVehicleList.add(details)
            setAdapter()
        } else {
            val bundle = Bundle().apply {
                putParcelable(Constants.DATA, details)
            }
            findNavController().navigate(
                R.id.action_makePaymentAddVehicleFragment_to_addVehicleDetailsFragment,
                bundle
            )
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
        setAddBtnActivated()
        if (mVehicleList.size > 0) {
            mVehicleList.removeAt(pos)
            if (::mAdapter.isInitialized) {
                mAdapter.setList(mVehicleList)
                mAdapter.notifyItemRemoved(pos)
            }
        }
        if (mVehicleList.size == 0) {
            setBtnDisabled()
            binding.apply {
                rvVehiclesList.visibility = View.GONE
                noVehiclesAdded.visibility = View.VISIBLE
                addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)
            }
        }
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) { }

    private fun setBtnActivated() {
        binding.findVehicle.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.btn_color
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            isClickable = true
        }
    }

    private fun setBtnDisabled() {
        binding.findVehicle.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_C9C9C9
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.color_7D7D7D))
            isClickable = false
        }
    }

    private fun setAddBtnActivated() {
        binding.addVehicleBtn.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            setStrokeColorResource(R.color.green)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            setIconTintResource(R.color.green)
            isClickable = true
        }
    }

    private fun setAddBtnDisabled() {
        binding.addVehicleBtn.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_C9C9C9
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.color_7D7D7D))
            setIconTintResource(R.color.color_7D7D7D)
            setStrokeColorResource(R.color.color_7D7D7D)
            isClickable = false
        }
    }
}