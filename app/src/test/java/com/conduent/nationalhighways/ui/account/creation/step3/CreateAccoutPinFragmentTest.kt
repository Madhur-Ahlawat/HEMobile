package com.conduent.nationalhighways.ui.account.creation.step3

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
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
@LargeTest
class CreateAccoutPinFragmentTest {

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
    fun `test create account pin screen visibility`() {
        launchFragmentInHiltContainer<CreateAccoutPinFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.mcvContainer)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account pin screen, navigate to next screen for pre pay`() {
        launchFragmentInHiltContainer<CreateAccoutPinFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutPinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.desc2)).check(matches(isDisplayed()))

            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createCommunicationPrefsFragment
            )
        }
    }

    @Test
    fun `test create account pin screen, navigate to next screen for pay-g`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = Constants.PAYG
                }
            )
        }
        launchFragmentInHiltContainer<CreateAccoutPinFragment>(bundl) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutPinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.desc2)).check(matches(isDisplayed()))

            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createCommunicationPrefsFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = Constants.PAYG
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account pin screen, navigate to next screen for business account`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                    planType = Constants.BUSINESS_ACCOUNT
                }
            )
        }
        launchFragmentInHiltContainer<CreateAccoutPinFragment>(bundl) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutPinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.desc2)).check(matches(isDisplayed()))

            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createCommunicationPrefsFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                        planType = Constants.BUSINESS_ACCOUNT
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account pin screen, navigate to next screen for business account, edit account type`() {
        val bundl = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccoutPinFragment>(bundl) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutPinFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.desc2)).check(matches(isDisplayed()))

            runTest {
                onView(withId(R.id.tvPinOne)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("1"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinTwo)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("2"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinThree)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("3"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tvPinFour)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("4"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(BaseActions.forceClick())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.paymentSummaryFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }


}