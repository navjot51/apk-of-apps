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
    assertEquals("Accountability Tasks", appName)
  }

  @Test
  fun `task item overdue detection`() {
    val pastDueTask = com.example.data.TaskItem(
      title = "Urgent Report",
      description = "Submit by deadline",
      dueDateTime = System.currentTimeMillis() - 10_000,
      priority = com.example.data.Priority.HIGH,
      isCompleted = false
    )
    val isOverdue = !pastDueTask.isCompleted && pastDueTask.dueDateTime < System.currentTimeMillis()
    assertEquals(true, isOverdue)
  }
}
