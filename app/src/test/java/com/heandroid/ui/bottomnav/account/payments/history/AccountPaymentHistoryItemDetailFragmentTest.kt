package com.heandroid.ui.bottomnav.account.payments.history

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.heandroid.R
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.data.DataFile
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class AccountPaymentHistoryItemDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test payment history detail screen visibility`() {
        val bundle = Bundle().apply {
            putParcelable(ConstantsTest.DATA, DataFile.getPaymentHistoryTransactionData())
        }
        launchFragmentInHiltContainer<AccountPaymentHistoryItemDetailFragment>(
            bundle
        ) {
            onView(withId(R.id.transactionId)).check(matches(isDisplayed()))
                .check(matches(withText("12345678")))
            onView(withId(R.id.paymentDate)).check(matches(isDisplayed()))
            onView(withId(R.id.paymentMethod)).check(matches(isDisplayed()))
                .check(matches(withText("VISA")))
            onView(withId(R.id.amount)).check(matches(isDisplayed()))
                .check(matches(withText(containsString("100"))))
            onView(withId(R.id.downloadReceiptBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.backBtn)).check(matches(isDisplayed()))

        }
    }

    @Test
    fun `test payment history detail screen, cancel button click`() {
        val bundle = Bundle().apply {
            putParcelable(ConstantsTest.DATA, DataFile.getPaymentHistoryTransactionData())
        }
        launchFragmentInHiltContainer<AccountPaymentHistoryItemDetailFragment>(
            bundle
        ) {
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.transactionId)).check(matches(isDisplayed()))
                .check(matches(withText("12345678")))
            onView(withId(R.id.paymentDate)).check(matches(isDisplayed()))
            onView(withId(R.id.paymentMethod)).check(matches(isDisplayed()))
                .check(matches(withText("VISA")))
            onView(withId(R.id.amount)).check(matches(isDisplayed()))
                .check(matches(withText(containsString("100"))))
            onView(withId(R.id.downloadReceiptBtn)).check(matches(isDisplayed()))
            onView(withId(R.id.backBtn)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Mockito.verify(navController).popBackStack()

        }
    }


}