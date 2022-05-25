package com.heandroid.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.RetrievePlateInfoDetails
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.vehicle.PlateInfoResponse
import com.heandroid.data.model.vehicle.VehicleInfoResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentAddVehicleDoneBinding
import com.heandroid.ui.account.creation.step4.CreateAccountVehicleViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.vehiclelist.ItemClickListener
import com.heandroid.ui.vehicle.vehiclelist.VehicleListAdapter
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehicleDoneFragment : BaseFragment<FragmentAddVehicleDoneBinding>(), ItemClickListener {

    private var mVehicleDetails: VehicleResponse? = null
    private var mScreeType = 0
    private lateinit var mAdapter: VehicleListAdapter
    private val mList = ArrayList<VehicleResponse?>()
    private val viewModel: CreateAccountVehicleViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddVehicleDoneBinding.inflate(inflater, container, false)

    override fun observer() {
        observe(viewModel.findVehicleLiveData, ::apiResponseDVRM)
    }

    private fun getVehicleDataFromDVRM() {
        Logg.logging(
            "Testing",
            "AddVehicleDoneFragment getVehicleDataFromDVRM mVehicleDetails ${mVehicleDetails}"
        )
        val mUKVehicleDataNotFound = arguments?.getInt(Constants.UK_VEHICLE_DATA_NOT_FOUND_KEY, 0)

        if (mVehicleDetails?.newPlateInfo?.country.equals(
                "UK",
                true
            ) && mUKVehicleDataNotFound == 0
        ) {
            loader?.show(requireActivity().supportFragmentManager, "")
            viewModel.getVehicleData(mVehicleDetails?.newPlateInfo?.number, Constants.AGENCY_ID)
        } else {
            val mRetrievePlateInfoDetails = RetrievePlateInfoDetails(
                mVehicleDetails?.newPlateInfo?.number,
                VehicleClassTypeConverter.toClassCode(mVehicleDetails?.vehicleInfo?.vehicleClassDesc),
                mVehicleDetails?.vehicleInfo?.make,
                mVehicleDetails?.vehicleInfo?.model,
                mVehicleDetails?.vehicleInfo?.color
            )

            Logg.logging(
                "Testing",
                "AddVehicleDoneFragment mRetrievePlateInfoDetails ${mRetrievePlateInfoDetails}"
            )
            val it = VehicleInfoDetails(mRetrievePlateInfoDetails)
            setAdapter(it)

        }
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {
        Logg.logging("Testing", "AddVehicleDoneFragment  apiResponseDVRM")

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    loader?.dismiss()
                    setAdapter(it)
                }
                Logg.logging("Testing", "AddVehicleDoneFragment  apiResponseDVRM Success called")

            }
            is Resource.DataError -> {
                loader?.dismiss()
                Logg.logging(
                    "Testing",
                    "AddVehicleDoneFragment  apiResponseDVRM DataError called mVehicleDetails $mVehicleDetails"
                )

                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA, mVehicleDetails)
                    putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                }

                findNavController().navigate(
                    R.id.action_addVehicleDoneFragment_to_addVehicleDetailsFragment,
                    bundle
                )
                ErrorUtil.showError(binding.root, resource.errorMsg)

            }
        }

    }

    override fun init() {
        mVehicleDetails = arguments?.getParcelable(Constants.DATA)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }
        Logg.logging("testing", " AddVehicleDoneFragment mScreeType  $mScreeType")

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
            binding.tickLayout.visible()
            binding.tvYourVehicle.gone()
            binding.tickTxt.text = getString(R.string.str_new_vehicles_added_success)
            binding.conformBtn.text = getString(R.string.str_back_to_vehicles_list)
        } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
            binding.tickLayout.gone()
            binding.tvYourVehicle.visible()
        }

        getVehicleDataFromDVRM()
    }

    override fun initCtrl() {
        binding.conformBtn.setOnClickListener {
            if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
                val bundle = Bundle()
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)

                findNavController().navigate(
                    R.id.action_addVehicleDoneFragment_to_vehicleListFragment,
                    bundle
                )
            } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.DATA, ArrayList(mList))
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                findNavController().navigate(
                    R.id.action_addVehicleDoneFragment_to_makeOneOffPaymentCrossingFragment,
                    bundle
                )
            }
        }
    }

    private fun setAdapter(details: VehicleInfoDetails) {
        mList.clear()
        Logg.logging("testing", "mScreeType  $mScreeType")
        Logg.logging("testing", "details  $details")

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails?.plateInfo?.number ?: "",
                mVehicleDetails?.plateInfo?.country ?: "",
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                details.retrievePlateInfoDetails.vehicleMake ?: "",
                details.retrievePlateInfoDetails.vehicleModel ?: "",
                "",
                "",
                "",
                "",
                details.retrievePlateInfoDetails.vehicleColor ?: "",
                VehicleClassTypeConverter.toClassName(details.retrievePlateInfoDetails.vehicleClass!!)
                    ?: "",
                mVehicleDetails?.vehicleInfo?.effectiveStartDate ?: ""
            )

            val mVehicleResponse1 =
                VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp, true)
            Logg.logging("testing", "mVehicleResponse1  $mVehicleResponse1")
            mList.add(mVehicleResponse1)
        } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {
            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails?.plateInfo?.number ?: "",
                mVehicleDetails?.plateInfo?.country ?: "",
                "HE",
                "-",
                "",
                "",
                ""
            )
            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails?.vehicleInfo?.make ?: "",
                mVehicleDetails?.vehicleInfo?.model ?: "",
                "",
                "",
                "",
                "",
                mVehicleDetails?.vehicleInfo?.color ?: "",
                VehicleClassTypeConverter.toClassName(mVehicleDetails?.vehicleInfo?.vehicleClassDesc!!)
                    ?: "",
                mVehicleDetails?.vehicleInfo?.effectiveStartDate ?: ""
            )

            val mVehicleResponse1 =
                VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp, true)
            mList.add(mVehicleResponse1)
        }

        if (mList.size > 0) {
            mAdapter = VehicleListAdapter(requireContext(), this)
            mAdapter.setList(mList)
            binding.recyclerViewHeader.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewHeader.setHasFixedSize(true)
            binding.recyclerViewHeader.adapter = mAdapter
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse?, pos: Int) {

    }

    override fun onItemClick(details: VehicleResponse?, pos: Int) {
        details?.isExpanded = details?.isExpanded != true
        mList[pos]?.isExpanded = details?.isExpanded
        mAdapter.notifyItemChanged(pos)
    }

}