package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.compiler.PythonInterpreter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals("PyLearn", appName)
  }

  @Test
  fun `python interpreter executes arithmetic and prints`() = runBlocking {
    val interpreter = PythonInterpreter()
    val script = """
a = 10
b = 20
print(a + b)
""".trimIndent()
    val res = interpreter.execute(script)
    assertTrue(res.isSuccess)
    assertEquals("30\n", res.stdout)
  }
}
