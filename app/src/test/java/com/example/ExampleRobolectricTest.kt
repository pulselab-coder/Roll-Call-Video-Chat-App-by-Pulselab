package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Roll Call", appName)
    assertEquals("ca-app-pub-3940256099942544/6300978111", com.example.ui.ads.RollCallAdIds.BANNER_AD_UNIT_ID)
    assertEquals("ca-app-pub-3940256099942544/1033173712", com.example.ui.ads.RollCallAdIds.INTERSTITIAL_AD_UNIT_ID)
    assertEquals("ca-app-pub-3940256099942544/5224354917", com.example.ui.ads.RollCallAdIds.REWARDED_AD_UNIT_ID)
  }
}
