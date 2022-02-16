package com.heandroid.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heandroid.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleFragmentTest {

    private lateinit var scenario: FragmentScenario<VehicleFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer()
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `check all views`() {
        scenario.onFragment{
//            Espresso.onView(ViewMatchers.withId(R.id.vehicle_list_lyt))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

}