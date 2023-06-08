package com.conduent.nationalhighways.ui.vehicle.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.FragmentNewAddVehicleDetailsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.DATA
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleDetailsFragment : BaseFragment<FragmentNewAddVehicleDetailsBinding>(),
    View.OnClickListener, DropDownItemSelectListener {

    private var mScreeType = 0
    private var mVehicleDetails: VehicleResponse? = null
    private var typeOfVehicle: MutableList<String> = ArrayList()
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null
    private var radioButtonChecked:Boolean=false
    private var typeOfVehicleChecked:Boolean=false
    private var checkBoxChecked:Boolean=false


    @Inject
    lateinit var sessionManager: SessionManager


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewAddVehicleDetailsBinding.inflate(inflater, container, false)

    override fun observer() {}

    override fun init() {
        typeOfVehicle.clear()
        typeOfVehicle.add("Motorcycle, moped or quad bike")
        typeOfVehicle.add("Car, van or minibus < 8 seats")
        typeOfVehicle.add("Bus, coach or other goods vehicle with 2 axles")
        typeOfVehicle.add("Vehicle with more than 2 axles")
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)
        binding.apply {
            typeVehicle.dataSet.addAll(typeOfVehicle)
        }


        binding.typeVehicle.dropDownItemSelectListener=this

        binding.model = false
        mVehicleDetails = arguments?.getParcelable(DATA) as? VehicleResponse?

        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            radioButtonChecked = R.id.radioButtonYes==checkedId||R.id.radioButtonNo==checkedId
        }

        binding.checkBoxTerms.setOnCheckedChangeListener{_,checkedId->
            checkBoxChecked=true
        }



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
            binding.vehiclePlateNumber.text = nonUKVehicleModel?.plateNumber
            binding.makeInputLayout.setText(nonUKVehicleModel?.vehicleMake.toString())
            binding.modelInputLayout.setText(nonUKVehicleModel?.vehicleModel.toString())
            binding.colorInputLayout.setText(nonUKVehicleModel?.vehicleColor.toString())
            binding.typeVehicle.setSelectedValue(Utils.getVehicleType(nonUKVehicleModel?.vehicleClass.toString()))



            if (NewCreateAccountRequestModel.plateCountry == Constants.COUNTRY_TYPE_UK) {
                binding.typeVehicle.visibility = View.GONE
                binding.typeOfVehicleInputLayout.visibility=View.GONE
                binding.cardView.visibility=View.VISIBLE
            } else {
                binding.typeVehicle.visibility = View.VISIBLE
                binding.typeOfVehicleInputLayout.visibility=View.VISIBLE
                binding.cardView.visibility=View.GONE


                binding.typeOfVehicleInputLayout.setText(nonUKVehicleModel?.plateNumber.toString())




            }

        }

        binding.nextBtn.setOnClickListener(this)
    }

    override fun initCtrl() {
        checkButton()
        binding.makeInputLayout.editText.onTextChanged {
            checkButton()
        }
        binding.modelInputLayout.editText.onTextChanged {
            checkButton()
        }
        binding.colorInputLayout.editText.onTextChanged {
            checkButton()
        }

/*
        binding.nextBtn.setOnClickListener {

            AdobeAnalytics.setActionTrack(
                "next",
                "one of payment:vehicle details manual entry",
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
*/
    }

    private fun checkButton() {
        if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
            if (binding.makeInputLayout.editText.toString().trim().isNotEmpty()
                && binding.modelInputLayout.editText.text.toString().trim().isNotEmpty()
                && binding.colorInputLayout.editText.text.toString().trim().isNotEmpty()&&radioButtonChecked&&typeOfVehicleChecked
            ) {
                setBtnActivated()
            } else {
                setBtnDisabled()
            }
        }else{
            if (binding.makeInputLayout.getText().toString().trim().isNotEmpty()
                && binding.modelInputLayout.getText().toString().trim().isNotEmpty()
                && binding.colorInputLayout.getText().toString().trim().isNotEmpty()
            ) {
                setBtnActivated()
            } else {
                setBtnDisabled()
            }
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
            R.id.next_btn->{

                val accountData = NewCreateAccountRequestModel
                val vehicleList = accountData.vehicleList

                nonUKVehicleModel?.let {
                    if(vehicleList.contains(nonUKVehicleModel)){
                        ErrorUtil.showError(binding.root, getString(R.string.the_vehicle_has_already_been_added))
                    }else{
                        vehicleList.add(it)

                        findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_vehicleListFragment)
                    }

                }
                if (NewCreateAccountRequestModel.plateNumberIsNotInDVLA && NewCreateAccountRequestModel.plateNumber.isNotEmpty()) {
                    val newVehicleInfoDetails=NewVehicleInfoDetails()
                    if(vehicleList.size>0){
                        for(i in 0 until vehicleList.size){
                            if (vehicleList[i].plateNumber==binding.vehiclePlateNumber.text.toString()){
                                ErrorUtil.showError(binding.root, getString(R.string.the_vehicle_has_already_been_added))

                            }
                        }

                    }else{
                        newVehicleInfoDetails.vehicleMake=binding.makeInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleModel=binding.modelInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleColor=binding.colorInputLayout.getText().toString()
                        newVehicleInfoDetails.vehicleClass=binding.typeVehicle.getSelectedValue()
                        newVehicleInfoDetails.plateNumber=binding.vehiclePlateNumber.text.toString()
                        vehicleList.add(newVehicleInfoDetails)

                        findNavController().navigate(R.id.action_addVehicleDetailsFragment_to_vehicleListFragment)

                    }



/*
                    accountData.vehicleList[0].vehicleMake=binding.makeInputLayout.getText().toString()
                    accountData.vehicleList[0].vehicleModel=binding.modelInputLayout.getText().toString()
                    accountData.vehicleList[0].vehicleColor=binding.colorInputLayout.getText().toString()
                    accountData.vehicleList[0].vehicleClass=binding.typeVehicle.getSelectedValue()
                    accountData.vehicleList[0].plateNumber=binding.vehiclePlateNumber.text.toString()
*/

                }




                }

        }
    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        typeOfVehicleChecked=true
    }

}