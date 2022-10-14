package com.conduent.nationalhighways.ui.account.creation.step3

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.anything
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class CreateAccountPostCodeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountPostCodeViewModel>(relaxed = true)

    private val addressesLiveData = MutableLiveData<Resource<List<DataAddress?>?>?>()
    private val countriesLiveData = MutableLiveData<Resource<List<CountriesModel?>?>?>()
    private val countryCodeLiveData = MutableLiveData<Resource<List<CountryCodes?>?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = null
                }
            )
        }
        every { viewModel.addresses } returns addressesLiveData
        every { viewModel.countriesList } returns countriesLiveData
        every { viewModel.countriesCodeList } returns countryCodeLiveData
    }

    @Test
    fun `test create account post code screen visibility for pre-pay`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = Constants.PAYG
                }
            )
        }

        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundl) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tilPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account post code screen visibility for business`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                    planType = null
                }
            )
        }

        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundl) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tilPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
            onView(withId(R.id.switch_view_business)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account post code screen visibility for pay as you go`() {

        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tilPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account post code screen, add address for non UK`() {
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.postcodeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.switch_view_business)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            val c1 = CountriesModel("1234", "11", "India")
            val c2 = CountriesModel("4567", "67", "China")

            countriesLiveData.postValue(Resource.Success(listOf(c1, c2)))
            runTest {
                onView(withId(R.id.tieHouseNumber)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieStreetName)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("street"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieCity)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("city"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieNonUkPostCode)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
        }
    }

    @Test
    fun `test create account post code screen, add address for UK`() {
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.postcodeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePostCode)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("cm111aa"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val list1 = DataAddress(postcode = "1234")
            val list2 = DataAddress(postcode = "4567")
            addressesLiveData.postValue(Resource.Success(listOf(list1, list2)))
            onView(withId(R.id.tieAddress)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onData(anything()).atPosition(1).perform(ViewActions.click())
            runTest {
                onView(withId(R.id.tieHouseNumber)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieStreetName)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("street"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieCity)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("city"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
            }

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
        }
    }

    @Test
    fun `test create account post code screen, add address for non UK with edit account type for business`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                    planType = null
                }
            )
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundl) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.postcodeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.switch_view_business)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            val c1 = CountriesModel("1234", "11", "India")
            val c2 = CountriesModel("4567", "67", "China")

            countriesLiveData.postValue(Resource.Success(listOf(c1, c2)))
            runTest {
                onView(withId(R.id.tieHouseNumber)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieStreetName)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("street"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieCity)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("city"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieNonUkPostCode)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
            }
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
        }
    }

    @Test
    fun `test create account post code screen, add address for UK with edit account type for business`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                    planType = null
                }
            )
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundl) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.postcodeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePostCode)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("cm111aa"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            val list1 = DataAddress(postcode = "1234")
            val list2 = DataAddress(postcode = "4567")
            addressesLiveData.postValue(Resource.Success(listOf(list1, list2)))
            onView(withId(R.id.tieAddress)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            onData(anything()).atPosition(1).perform(ViewActions.click())
            runTest {
                onView(withId(R.id.tieHouseNumber)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("1234"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieStreetName)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("street"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieCity)).perform(BaseActions.betterScrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("city"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(ViewActions.click())
            }

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
        }
    }
}