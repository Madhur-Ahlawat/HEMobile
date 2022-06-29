package com.heandroid.ui.account.creation.step3

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
import androidx.test.filters.LargeTest
import com.google.common.base.Predicates.instanceOf
import com.heandroid.R
import com.heandroid.data.model.address.DataAddress
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.common.Resource
import com.heandroid.utils.data.DataFile
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.EnumSet.allOf

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class CreateAccountPostCodeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountPostCodeViewModel>(relaxed = true)

    private val addressesLiveData = MutableLiveData<Resource<List<DataAddress>?>?>()

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
        every { viewModel.addresses } returns addressesLiveData
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundl) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.mcvContainer)).check(matches(isDisplayed()))
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
        every { viewModel.addresses } returns addressesLiveData
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundl) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.mcvContainer)).check(matches(isDisplayed()))
            onView(withId(R.id.tilPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
            onView(withId(R.id.switch_view_business)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account post code screen visibility for pay as you go`() {
        every { viewModel.addresses } returns addressesLiveData
        launchFragmentInHiltContainer<CreateAccountPostCodeFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.mcvContainer)).check(matches(isDisplayed()))
            onView(withId(R.id.tilPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account post code screen, find address`() {
        every { viewModel.addresses } returns addressesLiveData
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
                .perform(ViewActions.click())
            onData(anything()).atPosition(1).perform(ViewActions.click())
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        zipCode1 = ""
                        countryType = "UK"
                        city = ""
                        stateType = "HE"
                        zipCode1 = "1234"
                        address1 = ""
                        planType = null
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account post code screen, find address, edit account type for personal`() {
        bundle.apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        every { viewModel.addresses } returns addressesLiveData
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
                .perform(ViewActions.click())
            onData(anything()).atPosition(1).perform(ViewActions.click())
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        zipCode1 = ""
                        countryType = "UK"
                        city = ""
                        stateType = "HE"
                        zipCode1 = "1234"
                        address1 = ""
                        planType = null
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account post code screen, find address, edit account type for business`() {
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
        every { viewModel.addresses } returns addressesLiveData
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
                .perform(ViewActions.click())
            onData(anything()).atPosition(1).perform(ViewActions.click())
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPasswordFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                        zipCode1 = ""
                        countryType = "UK"
                        city = ""
                        stateType = "HE"
                        zipCode1 = "1234"
                        address1 = ""
                        planType = "BUSINESS"
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }
}