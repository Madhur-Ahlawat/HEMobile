package com.heandroid.pages

import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import org.hamcrest.*
import org.hamcrest.core.AnyOf
import java.lang.Exception

open class BasePage {
    /*fun scrollToTheBottomByText(lastElement: String?) {
        Espresso.onView(withId(R.id.mcvAddVehicle)).perform(customScrollTo)
        for (i in 0..4) {
            Espresso.onView(ViewMatchers.withText(lastElement)).perform(ViewActions.swipeUp())
        }
    }

    fun closeConfirmationPage() {
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(3000))
        okButton.perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(4000))
    }

    fun confirmNotificationPopup() {
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(4000))
        Espresso.onView(ViewMatchers.withText("YES")).perform(ViewActions.click())
    }

    companion object {
        var okButton: ViewInteraction = Espresso.onView(ViewMatchers.withText("OK"))
        var customScrollTo: ViewAction = object : ViewAction {
            val constraints: Matcher<View>
                get() = CoreMatchers.allOf<View>(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                    ViewMatchers.isDescendantOfA(
                        AnyOf.anyOf<View>(
                            ViewMatchers.isAssignableFrom(ScrollView::class.java),
                            ViewMatchers.isAssignableFrom(HorizontalScrollView::class.java),
                            ViewMatchers.isAssignableFrom(NestedScrollView::class.java)))
                )
            val description: String
                get() = null

            override fun perform(uiController: UiController, view: View) {
                ScrollToAction().perform(uiController, view)
            }
        }

        fun nestedScrollTo(): ViewAction {
            return object : ViewAction {
                val constraints: Matcher<View>
                    get() = Matchers.allOf(
                        ViewMatchers.isDescendantOfA(ViewMatchers.isAssignableFrom(NestedScrollView::class.java)),
                        ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
                val description: String
                    get() = null

                override fun perform(uiController: UiController, view: View) {
                    try {
                        val nestedScrollView: NestedScrollView? = findFirstParentLayoutOfClass(view,
                            NestedScrollView::class.java) as NestedScrollView?
                        if (nestedScrollView != null) {
                            nestedScrollView.scrollTo(0, view.top)
                        } else {
                            throw Exception("Unable to find NestedScrollView parent.")
                        }
                    } catch (e: Exception) {
                        throw PerformException.Builder()
                            .withActionDescription(description)
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(e)
                            .build()
                    }
                    uiController.loopMainThreadUntilIdle()
                }
            }
        }

        private fun findFirstParentLayoutOfClass(view: View, parentClass: Class<out View>): View? {
            var parent: ViewParent? = FrameLayout(view.context)
            var incrementView: ViewParent? = null
            var i = 0
            while (parent != null && parent.javaClass != parentClass) {
                if (i == 0) {
                    parent = findParent(view)
                } else {
                    parent = findParent(incrementView)
                }
                incrementView = parent
                i++
            }
            return parent
        }

        private fun findParent(view: View): ViewParent {
            return view.parent
        }

        private fun findParent(view: ViewParent): ViewParent {
            return view.getParent()
        }

        fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View> {
            return object : TypeSafeMatcher<View?>() {
                var currentIndex = 0
                override fun describeTo(description: Description) {
                    description.appendText("with index: ")
                    description.appendValue(index)
                    matcher.describeTo(description)
                }

                public override fun matchesSafely(view: View?): Boolean {
                    return matcher.matches(view) && currentIndex++ == index
                }
            }
        }

        fun waitFor(millis: Long): ViewAction {
           *//* return object : ViewAction {
                val constraints: Matcher<View>
                    get() = ViewMatchers.isRoot()
                val description: String
                    get() = "Wait for $millis milliseconds."


                override fun perform(uiController: UiController, view: View) {
                    uiController.loopMainThreadForAtLeast(millis)
                }
            }*//*
        }
    }*/
}