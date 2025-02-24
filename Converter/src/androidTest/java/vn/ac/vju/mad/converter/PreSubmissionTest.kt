package vn.ac.vju.mad.converter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PreSubmissionTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testTitle() {
        val title = rule.activity.title.toString()
        assertEquals("Currency Converter", title)
    }

    @Test
    fun testText() {
        rule.onNodeWithText("Convert JPY to VND")
            .assertIsDisplayed()
    }
}
