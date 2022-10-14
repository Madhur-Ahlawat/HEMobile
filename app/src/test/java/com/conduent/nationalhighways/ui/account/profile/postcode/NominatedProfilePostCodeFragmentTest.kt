package com.conduent.nationalhighways.ui.account.profile.postcode

import android.os.Bundle
import android.os.Looper
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
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
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
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class NominatedProfilePostCodeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CreateAccountPostCodeViewModel>(relaxed = true)

    private val addressesLiveData = MutableLiveData<Resource<List<DataAddress?>?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                Constants.DATA, ProfileDetailModel(
                    null, null, null,
                    null, null, "", ""
                )
            )
        }
        every { viewModel.addresses } returns addressesLiveData
    }

    @Test
    fun `test nominated post code screen, navigate to next screen`() {
        launchFragmentInHiltContainer<NominatedProfilePostCodeFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.nominatedPostCodeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
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
            onData(anything()).atPosition(1).perform(BaseActions.forceClick())
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.nominatedProfilePasswordFragment
            )
        }
    }

    @Test
    fun `test nominated post code screen, get address list for error api call`() {
        launchFragmentInHiltContainer<NominatedProfilePostCodeFragment>(bundle) {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.nominatedPostCodeFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPostCode)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPassword)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePostCode)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("cm111aa"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnFindAddress)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            addressesLiveData.postValue(Resource.DataError("unknown error"))
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }


}