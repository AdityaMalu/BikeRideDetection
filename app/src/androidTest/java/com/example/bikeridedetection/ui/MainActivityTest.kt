package com.example.bikeridedetection.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bikeridedetection.R
import com.example.bikeridedetection.ui.activity.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for MainActivity.
 * Tests the bike mode toggle functionality.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun titleIsDisplayed() {
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_bike_mode)))
    }

    @Test
    fun switchIsDisplayed() {
        onView(withId(R.id.switchBikeMode))
            .check(matches(isDisplayed()))
    }

    @Test
    fun statusTextIsDisplayed() {
        onView(withId(R.id.statusText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun initialStateShowsStatusOff() {
        onView(withId(R.id.statusText))
            .check(matches(withText(R.string.status_off)))
    }

    @Test
    fun switchInitiallyUnchecked() {
        onView(withId(R.id.switchBikeMode))
            .check(matches(isNotChecked()))
    }

    @Test
    fun toggleSwitchChangesStatus() {
        // Click the switch to enable bike mode
        onView(withId(R.id.switchBikeMode))
            .perform(click())

        // Verify switch is now checked
        onView(withId(R.id.switchBikeMode))
            .check(matches(isChecked()))

        // Verify status text changed to ON
        onView(withId(R.id.statusText))
            .check(matches(withText(R.string.status_on)))
    }

    @Test
    fun toggleSwitchOffAfterOn() {
        // Enable bike mode
        onView(withId(R.id.switchBikeMode))
            .perform(click())

        // Disable bike mode
        onView(withId(R.id.switchBikeMode))
            .perform(click())

        // Verify switch is unchecked
        onView(withId(R.id.switchBikeMode))
            .check(matches(isNotChecked()))

        // Verify status text changed back to OFF
        onView(withId(R.id.statusText))
            .check(matches(withText(R.string.status_off)))
    }
}
