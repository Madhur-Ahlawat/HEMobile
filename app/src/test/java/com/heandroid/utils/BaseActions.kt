package com.heandroid.utils

import android.graphics.Rect
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import java.lang.RuntimeException
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.matcher.ViewMatchers.*

import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers.anyOf
import android.os.Bundle
import android.widget.*


object BaseActions {

    fun forceClick(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.anyOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.isClickable(),
                    ViewMatchers.isEnabled()
                )
            }

            override fun getDescription(): String {
                return "force click"
            }

            override fun perform(uiController: UiController, view: View) {
                view.performClick()
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    fun clickClickableSpan(textToClick: CharSequence): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> {
                return Matchers.instanceOf(TextView::class.java)
            }

            override fun getDescription(): String {
                return "clicking on a ClickableSpan"
            }

            override fun perform(uiController: UiController, view: View) {
                val textView = view as TextView
                val spannableString = textView.text as SpannableString

                if (spannableString.isEmpty()) {
                    throw NoMatchingViewException.Builder()
                        .includeViewHierarchy(true)
                        .withRootView(textView)
                        .build()
                }
                val spans =
                    spannableString.getSpans(0, spannableString.length, ClickableSpan::class.java)
                if (spans.isNotEmpty()) {
                    var spanCandidate: ClickableSpan
                    for (span: ClickableSpan in spans) {
                        spanCandidate = span
                        val start = spannableString.getSpanStart(spanCandidate)
                        val end = spannableString.getSpanEnd(spanCandidate)
                        val sequence = spannableString.subSequence(start, end)
                        if (textToClick.toString() == (sequence.toString())) {
                            span.onClick(textView)
                            return
                        }
                    }
                }

                throw NoMatchingViewException.Builder()
                    .includeViewHierarchy(true)
                    .withRootView(textView)
                    .build()

            }
        }
    }

    fun withCustomConstraints(
        action: ViewAction,
        constraints: Matcher<View>
    ): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return constraints
            }

            override fun getDescription(): String {
                return action.description
            }

            override fun perform(uiController: UiController, view: View) {
                action.perform(uiController, view)
            }
        }
    }

    fun isPopupWindow(): Matcher<Root?>? {
        return RootMatchers.isPlatformPopup()
    }

    fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return CoreMatchers.allOf(ViewMatchers.isClickable(), ViewMatchers.isDisplayed())
        }

        override fun getDescription() = "Click on a child view with specified id."
        override fun perform(uiController: UiController, view: View) =
            ViewActions.click().perform(uiController, view.findViewById(viewId))
    }

    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    fun betterScrollTo(): ViewAction? {
        return ViewActions.actionWithAssertions(NestedScrollToAction())
    }

    class NestedScrollToAction : ViewAction {
        override fun getConstraints(): Matcher<View?>? {
            return allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isDescendantOfA(
                    anyOf(
                        isAssignableFrom(ScrollView::class.java),
                        isAssignableFrom(HorizontalScrollView::class.java),
                        isAssignableFrom(
                            NestedScrollView::class.java
                        )
                    )
                )
            )
        }

        override fun perform(uiController: UiController, view: View) {
            if (ViewMatchers.isDisplayingAtLeast(90).matches(view)) {
                Log.i(TAG, "View is already displayed. Returning.")
                return
            }
            val rect = Rect()
            view.getDrawingRect(rect)
            if (!view.requestRectangleOnScreen(rect, true /* immediate */)) {
                Log.w(TAG, "Scrolling to view was requested, but none of the parents scrolled.")
            }
            uiController.loopMainThreadUntilIdle()
            if (!ViewMatchers.isDisplayingAtLeast(90).matches(view)) {
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(
                        RuntimeException(
                            "Scrolling to view was attempted, but the view is not displayed"
                        )
                    )
                    .build()
            }
        }

        override fun getDescription(): String {
            return "scroll to"
        }

        companion object {
            private val TAG = ScrollToAction::class.java.simpleName
        }
    }

    fun equalBundles(one: Bundle?, two: Bundle?): Boolean {
        one?.let {
            two?.let {
                if (one.size() != two.size()) return false
                val setOne: MutableSet<String> = HashSet(one.keySet())
                setOne.addAll(two.keySet())
                var valueOne: Any?
                var valueTwo: Any?
                for (key in setOne) {
                    if (!one.containsKey(key) || !two.containsKey(key)) return false
                    valueOne = one[key]
                    valueTwo = two[key]
                    if (valueOne is Bundle && valueTwo is Bundle &&
                        !equalBundles(valueOne, valueTwo)
                    ) {
                        return false
                    } else if (valueOne == null) {
                        if (valueTwo != null) return false
                    } else if (valueOne != valueTwo) return false
                }
                return true
            }
        }
        return false
    }

    fun forceTypeText(text: String): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "force type text"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(isEnabled())
            }

            override fun perform(uiController: UiController?, view: View?) {
                (view as? EditText)?.append(text)
                uiController?.loopMainThreadUntilIdle()
            }
        }
    }
}