package com.conduent.nationalhighways.ui.account.creation.step5

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.ui.account.creation.step6.CreateAccountPaymentSummaryFragment
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
@MediumTest
class CreateAccountPaymentSummaryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
    fun `test payment summary screen visibility`() {
        launchFragmentInHiltContainer<CreateAccountPaymentSummaryFragment>(bundle) {
            onView(withId(R.id.stageLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.emailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.accountLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.registrationLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.prePayAmountLayout)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
            onView(withId(R.id.buttonLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.pay_button)).check(matches(isDisplayed()))
            onView(withId(R.id.cancel_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test payment summary screen, navigate to edit email`() {
        launchFragmentInHiltContainer<CreateAccountPaymentSummaryFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.paymentSummaryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.stageLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.emailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.lyt_email_address)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.emailVerification
            )
        }
    }

    @Test
    fun `test payment summary screen, navigate to edit account type`() {
        launchFragmentInHiltContainer<CreateAccountPaymentSummaryFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.paymentSummaryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.stageLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.lyt_account_no)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.accountTypeSelectionFragment
            )
        }
    }

    @Test
    fun `test payment summary screen, navigate to edit vehicle number`() {
        launchFragmentInHiltContainer<CreateAccountPaymentSummaryFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.paymentSummaryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.stageLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.lyt_vrm_register_no)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.findYourVehicleFragment
            )
        }
    }

    @Test
    fun `test payment summary screen, navigate to edit payment amount`() {
        val bund = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
        }
        launchFragmentInHiltContainer<CreateAccountPaymentSummaryFragment>(bund) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.paymentSummaryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.stageLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.lyt_payment_amount)).perform(BaseActions.betterScrollTo())
                .check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.businessPrePayAutoTopUpFragment
            )
        }
    }

    @Test
    fun `test payment summary screen, navigate to next screen`() {
        launchFragmentInHiltContainer<CreateAccountPaymentSummaryFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.paymentSummaryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.stageLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.emailLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.pay_button)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())
            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.choosePaymentFragment
            )
        }
    }
}