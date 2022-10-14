package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.RetrievePlateInfoDetails
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentAddVehicleDoneBinding
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog.ItemClickListener
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.adapter.VehicleListAdapter
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
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

    override fun init() {
        mVehicleDetails = arguments?.getParcelable(Constants.DATA)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.tickTxt.text = getString(R.string.str_vehicle_added_successfully)
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD ||
            mScreeType == Constants.VEHICLE_SCREEN_TYPE_LIST) {
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
            if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {

                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.DATA, ArrayList(mList))
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                findNavController().navigate(
                    R.id.action_addVehicleDoneFragment_to_makeOneOffPaymentCrossingFragment,
                    bundle
                )

            } else {
                val bundle = Bundle()
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)

                findNavController().navigate(
                    R.id.action_addVehicleDoneFragment_to_vehicleListFragment,
                    bundle
                )
            }
        }
    }

    private fun getVehicleDataFromDVRM() {
        val mUKVehicleDataNotFound = arguments?.getInt(Constants.UK_VEHICLE_DATA_NOT_FOUND_KEY, 0)

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD
            || mScreeType == Constants.VEHICLE_SCREEN_TYPE_LIST
        ) {
            setAdapter()
        } else if (mVehicleDetails?.newPlateInfo?.country.equals(
                "UK",
                true
            ) && mUKVehicleDataNotFound == 0
        ) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.getVehicleData(
                mVehicleDetails?.newPlateInfo?.number,
                Constants.AGENCY_ID.toInt()
            )
        } else {
            val mRetrievePlateInfoDetails = RetrievePlateInfoDetails(
                mVehicleDetails?.newPlateInfo?.number,
                VehicleClassTypeConverter.toClassCode(mVehicleDetails?.vehicleInfo?.vehicleClassDesc),
                mVehicleDetails?.vehicleInfo?.make,
                mVehicleDetails?.vehicleInfo?.model,
                mVehicleDetails?.vehicleInfo?.color
            )

            val it = VehicleInfoDetails(mRetrievePlateInfoDetails)
            setAdapter(it)

        }
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    setAdapter(it)
                }
            }
            is Resource.DataError -> {
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
            else -> {
            }
        }

    }

    private fun setAdapter(details: VehicleInfoDetails? = null) {
        mList.clear()
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
                details?.retrievePlateInfoDetails?.vehicleMake ?: "",
                details?.retrievePlateInfoDetails?.vehicleModel ?: "",
                "",
                "",
                "",
                "",
                details?.retrievePlateInfoDetails?.vehicleColor ?: "",
                details?.retrievePlateInfoDetails?.vehicleClass?.let {
                    VehicleClassTypeConverter.toClassName(
                        it
                    )
                },
                mVehicleDetails?.vehicleInfo?.effectiveStartDate ?: ""
            )

            val mVehicleResponse1 =
                VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp, true)
            Logg.logging("testing", "mVehicleResponse1  $mVehicleResponse1")
            mList.add(mVehicleResponse1)
        } else if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD
            || mScreeType == Constants.VEHICLE_SCREEN_TYPE_LIST
        ) {
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
                mVehicleDetails?.vehicleInfo?.vehicleClassDesc?.let {
                    VehicleClassTypeConverter.toClassName(
                        it
                    )
                },
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