package com.imagedownloader


import android.view.inputmethod.EditorInfo.IME_ACTION_GO
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.imagedownloader.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.imagedownloader", appContext.packageName)
    }

    @Test
    fun openWebSite() {
        onView(withId(R.id.etSearch)).perform(typeText("http://www.cricbuzz.com"))
        onView(withId(R.id.etSearch)).perform(ViewActions.pressKey(IME_ACTION_GO))
        closeSoftKeyboard()

    }

    @Test fun testWebViewInteraction() {
        onWebView(withId(R.id.webVw)).forceJavascriptEnabled()
      //  onWebView().check(webMatches(getText(), containsString("cricbuzz")))
    }

}
