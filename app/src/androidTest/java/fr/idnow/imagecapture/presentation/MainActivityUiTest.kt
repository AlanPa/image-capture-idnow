package fr.idnow.imagecapture.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import fr.idnow.imagecapture.MainActivity
import fr.idnow.imagecapture.R
import org.junit.Rule
import org.junit.Test

class MainActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun takePictureButtonClickStartsCamera() {
        onView(withId(R.id.takePictureBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.takePictureBtn)).perform(click())
        onView(withId(R.id.imagePreview)).check(matches(isDisplayed()))
    }

    @Test
    fun afterPictureTakenDownloadButtonAppears() {
        onView(withId(R.id.downloadBtn)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.takePictureBtn)).perform(click())
        onView(withId(R.id.downloadBtn)).check(matches(isDisplayed()))
    }
}