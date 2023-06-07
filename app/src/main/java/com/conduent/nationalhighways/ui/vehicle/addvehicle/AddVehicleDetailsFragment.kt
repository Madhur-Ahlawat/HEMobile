package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentNewAddVehicleDetailsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentNewAddVehicleDetailsBinding>(),
    View.OnClickListener, DropDownItemSelectListener {

    private var mScreeType = 0
    private var mVehicleDetails: VehicleResponse? = null
    private var typeOfVehicle: MutableList<String> = ArrayList()



    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {}

    override fun init() {
        typeOfVehicle.add("Motorcycle")
        typeOfVehicle.add("moped or quad bike")
        typeOfVehicle.add("Car, van or minibus < 8 seats")
        typeOfVehicle.add("Bus, coach or other goods vehicle with 2 axles")
        typeOfVehicle.add("Vehicle with more than 2 axles")

        binding.apply {
            typeVehicle.dataSet.addAll(typeOfVehicle)
        }



        binding.model = false
        mVehicleDetails = arguments?.getParcelable(DATA) as? VehicleResponse?

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }
        /*binding.title.text = getString(
            R.string.vehicle_reg_num,
            mVehicleDetails?.plateInfo?.number
        )//"Vehicle registration number: ${mVehicleDetails?.plateInfo?.number}"
        binding.subTitle.text = getString(
            R.string.country_reg,
            mVehicleDetails?.plateInfo?.country
        )*///"Country of registration ${mVehicleDetails?.plateInfo?.country}"



        AdobeAnalytics.setScreenTrack(
            "one of  payment:vehicle details manual entry",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of  payment:vehicle details manual entry",
            sessionManager.getLoggedInUser()
        )

        binding.editVehicle.setOnClickListener(this)

        if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
            binding.vehiclePlateNumber.text = NewCreateAccountRequestModel.plateNumber
            binding.vehicleRegisteredLayout.visibility = View.VISIBLE
        } else {
            binding.vehicleRegisteredLayout.visibility = View.GONE

            if (NewCreateAccountRequestModel.plateCountry == Constants.COUNTRY_TYPE_UK) {
                binding.typeVehicle.visibility = View.GONE
            } else {
                binding.typeVehicle.visibility = View.VISIBLE


            }

        }

        binding.nextBtn.setOnClickListener(this)
    }

    override fun initCtrl() {
        binding.makeInputLayout.editText.onTextChanged {
            checkButton()
        }
        binding.modelInputLayout.editText.onTextChanged {
            checkButton()
        }
        binding.colorInputLayout.editText.onTextChanged {
            checkButton()
        }

        binding.nextBtn.setOnClickListener {

            AdobeAnalytics.setActionTrack(
                "next",
                "one of  payment:vehicle details manual entry",
                "vehicle",
                "english",
                "one of payment",
                "home",
                sessionManager.getLoggedInUser()
            )

            if (binding.makeInputLayout.editText.toString().trim().isNotEmpty()
                && binding.modelInputLayout.editText.text.toString().trim().isNotEmpty()
                && binding.colorInputLayout.editText.text.toString().trim().isNotEmpty()
            ) {

                mVehicleDetails?.vehicleInfo?.color =
                    binding.colorInputLayout.editText.text.toString().trim()
                mVehicleDetails?.vehicleInfo?.make =
                    binding.makeInputLayout.editText.toString().trim()
                mVehicleDetails?.vehicleInfo?.model =
                    binding.modelInputLayout.editText.text.toString().trim()

                val bundle = Bundle().apply {

                    putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                    putParcelable(DATA, mVehicleDetails)
                }
                findNavController().navigate(R.id.addVehicleClassesFragment, bundle)

            }
        }
    }

    private fun checkButton() {
        if (binding.makeInputLayout.editText.toString().trim().isNotEmpty()
            && binding.modelInputLayout.editText.text.toString().trim().isNotEmpty()
            && binding.colorInputLayout.editText.text.toString().trim().isNotEmpty()
        ) {
            setBtnActivated()
        } else {
            setBtnDisabled()
        }
    }

    private fun setBtnActivated() {
        binding.nextBtn.isEnabled = true
    }

    private fun setBtnDisabled() {
        binding.nextBtn.isEnabled = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editVehicle -> {
                findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_CreateAccountFindVehicleFragment)
            }
            R.id.nextBtn->{
                findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_vehicleListFragment)
            }

        }
    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
    }

}