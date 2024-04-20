package com.parth.pestotest.ui.addTask.view

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import com.parth.pestotest.R
import com.parth.pestotest.ui.addTask.viewmodel.AddTaskViewModel
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AddTaskActivityTest {

    @get:Rule
    val scenario = activityScenarioRule<AddTaskActivity>()

    private var addTaskActivity: AddTaskActivity? = null
    private var viewModel: AddTaskViewModel? = null
    private var decorView: View? = null

    @Before
    fun setUp() {
        scenario.scenario.onActivity {
            addTaskActivity = it
        }
        viewModel = addTaskActivity?.viewModel
        scenario.scenario.onActivity { activity -> decorView = activity.window.decorView }
    }

    private fun check(msg: String) {
        onView(withText(msg))
            .inRoot(withDecorView(Matchers.not(decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyTitle() {
        onView(withId(R.id.btnSubmit)).perform(click())
        check("Please enter task title")
    }

    @Test
    fun testEmptyStatus() {
        onView(withId(R.id.etTitle)).perform(replaceText("Test Title"))
        onView(withId(R.id.btnSubmit)).perform(click())
        check("Please select task status")
    }

    @Test
    fun testEmptyDueDate() {
        onView(withId(R.id.etTitle)).perform(replaceText("Test Title"))
        val latch = CountDownLatch(1)
        scenario.scenario.onActivity { activity ->
            viewModel?.taskStatusList?.observe(activity) {
                it?.let {
                    latch.countDown()
                }
            }
        }
        latch.await(2, TimeUnit.SECONDS)
        onView(withId(R.id.boxTaskStatus)).perform(click())
        onView(withText("Done"))
            .inRoot(withDecorView(Matchers.not(decorView)))
            .perform(click())
        onView(withId(R.id.etDueDates)).perform(replaceText(""))
        viewModel?.selectedTask?.dueDate = 0
        onView(withId(R.id.btnSubmit)).perform(click())
        check("Please select task due date")
    }

    @Test
    fun testValidData() {
        onView(withId(R.id.etTitle)).perform(replaceText("Test Title"))
        val latch = CountDownLatch(1)
        scenario.scenario.onActivity { activity ->
            viewModel?.taskStatusList?.observe(activity) {
                it?.let {
                    latch.countDown()
                }
            }
        }
        latch.await(2, TimeUnit.SECONDS)
        onView(withId(R.id.boxTaskStatus)).perform(click())
        onView(withText("Done"))
            .inRoot(withDecorView(Matchers.not(decorView)))
            .perform(click())
        onView(withId(R.id.btnSubmit)).perform(click())
        check("Reminder set successfully")
    }
}