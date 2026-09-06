package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compiler.ExecutionResult
import com.example.data.model.TemplatesAndReferenceData
import com.example.ui.components.CodeEditorView
import com.example.ui.components.QuickSymbolBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.PyLearnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompilerScreen(
  viewModel: PyLearnViewModel,
  modifier: Modifier = Modifier
) {
  val code by viewModel.compilerCode.collectAsState()
  val inputBuffer by viewModel.compilerInput.collectAsState()
  val isExecuting by viewModel.isExecuting.collectAsState()
  val executionResult by viewModel.executionResult.collectAsState()
  val aiExplanation by viewModel.aiExplanation.collectAsState()
  val isExplaining by viewModel.isExplaining.collectAsState()

  var showSaveDialog by remember { mutableStateOf(false) }
  var snippetTitle by remember { mutableStateOf("") }
  var showInputDrawer by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  if (showSaveDialog) {
    AlertDialog(
      onDismissRequest = { showSaveDialog = false },
      title = { Text("Save Snippet", color = TextPrimaryDark) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Enter a title for this Python script:", fontSize = 13.sp, color = TextSecondaryDark)
          OutlinedTextField(
            value = snippetTitle,
            onValueChange = { snippetTitle = it },
            placeholder = { Text("e.g. My Algorithm") },
            modifier = Modifier.fillMaxWidth().testTag("save_snippet_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (snippetTitle.isNotBlank()) {
              viewModel.saveCurrentSnippet(snippetTitle)
              showSaveDialog = false
              snippetTitle = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
          modifier = Modifier.testTag("confirm_save_snippet_btn")
        ) {
          Text("Save", color = BackgroundDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showSaveDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      },
      containerColor = SurfaceDark
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundDark)
  ) {
    // Top Bar Actions
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceDark)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
          imageVector = Icons.Default.Terminal,
          contentDescription = null,
          tint = PythonBlue,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = "Python 3 Compiler",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        // Toggle input drawer
        IconButton(
          onClick = { showInputDrawer = !showInputDrawer },
          modifier = Modifier.size(36.dp).testTag("btn_toggle_stdin")
        ) {
          Icon(
            imageVector = Icons.Default.Input,
            contentDescription = "Standard Input",
            tint = if (inputBuffer.isNotBlank() || showInputDrawer) PythonYellow else TextSecondaryDark,
            modifier = Modifier.size(18.dp)
          )
        }

        // Save Snippet
        IconButton(
          onClick = { showSaveDialog = true },
          modifier = Modifier.size(36.dp).testTag("btn_open_save_snippet")
        ) {
          Icon(
            imageVector = Icons.Default.BookmarkAdd,
            contentDescription = "Save Snippet",
            tint = TextSecondaryDark,
            modifier = Modifier.size(18.dp)
          )
        }

        // Clear
        IconButton(
          onClick = { viewModel.clearCompilerOutput() },
          modifier = Modifier.size(36.dp).testTag("btn_clear_output")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Clear Output",
            tint = TextSecondaryDark,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Scrollable Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Template Pills
      Text(
        text = "Quick Starter Templates:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondaryDark
      )

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(TemplatesAndReferenceData.templates) { tmpl ->
          Surface(
            onClick = { viewModel.loadTemplate(tmpl) },
            shape = RoundedCornerShape(6.dp),
            color = SurfaceDark,
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(SurfaceCardBorder)),
            modifier = Modifier.testTag("tmpl_${tmpl.id}")
          ) {
            Text(
              text = tmpl.title,
              fontSize = 11.sp,
              color = PythonBlue,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      // Standard Input Field (when expanded)
      AnimatedVisibility(visible = showInputDrawer) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(PythonYellow.copy(alpha = 0.5f)))
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Standard Input (stdin for input()):",
              fontSize = 11.sp,
              color = PythonYellow,
              fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
              value = inputBuffer,
              onValueChange = { viewModel.updateCompilerInput(it) },
              placeholder = { Text("Enter simulated input lines...", fontSize = 12.sp) },
              modifier = Modifier.fillMaxWidth().testTag("stdin_input_field"),
              textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            )
          }
        }
      }

      // Code Editor
      CodeEditorView(
        code = code,
        onCodeChange = { viewModel.updateCompilerCode(it) },
        minHeight = 220.dp
      )

      // Quick Symbol Bar
      QuickSymbolBar(
        onInsert = { sym -> viewModel.updateCompilerCode(code + sym) },
        onIndent = { viewModel.updateCompilerCode(code + "    ") },
        onUnindent = {
          if (code.endsWith("    ")) {
            viewModel.updateCompilerCode(code.dropLast(4))
          } else if (code.isNotEmpty()) {
            viewModel.updateCompilerCode(code.dropLast(1))
          }
        }
      )

      // Primary Execution Button
      Button(
        onClick = { viewModel.runCompilerCode() },
        enabled = !isExecuting,
        colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("run_code_button")
      ) {
        if (isExecuting) {
          CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BackgroundDark, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Executing Python...", color = BackgroundDark, fontWeight = FontWeight.Bold)
        } else {
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BackgroundDark)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Run Code ▶", color = BackgroundDark, fontWeight = FontWeight.Bold)
        }
      }

      // Output Console Section
      executionResult?.let { result ->
        OutputConsole(
          result = result,
          onExplainClick = { viewModel.explainWithAi() },
          isExplaining = isExplaining
        )
      }

      // AI Tutor Diagnostic Explanation Card
      aiExplanation?.let { explanation ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_explanation_card"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(PythonYellow.copy(alpha = 0.7f)))
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = PythonYellow)
                Text("AI Python Tutor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PythonYellow)
              }
              IconButton(onClick = { viewModel.dismissAiExplanation() }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
              }
            }

            Text(
              text = explanation,
              fontSize = 13.sp,
              color = TextPrimaryDark,
              lineHeight = 20.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun OutputConsole(
  result: ExecutionResult,
  onExplainClick: () -> Unit,
  isExplaining: Boolean,
  modifier: Modifier = Modifier
) {
  val horizontalScroll = rememberScrollState()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("output_console_card"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CodeEditorBg),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = SolidColor(if (result.isSuccess) StatusSuccess.copy(alpha = 0.5f) else StatusError.copy(alpha = 0.5f))
    )
  ) {
    Column {
      // Header with execution stats
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CodeGutterBg)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(if (result.isSuccess) StatusSuccess else StatusError)
          )
          Text(
            text = if (result.isSuccess) "Output (Exit Code: 0)" else "Runtime Traceback",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (result.isSuccess) StatusSuccess else StatusError,
            fontFamily = FontFamily.Monospace
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "${result.executionTimeMs} ms",
            fontSize = 11.sp,
            color = TextMutedDark,
            fontFamily = FontFamily.Monospace
          )

          // AI Tutor Explain button
          Surface(
            onClick = onExplainClick,
            shape = RoundedCornerShape(4.dp),
            color = PythonYellow.copy(alpha = 0.2f),
            modifier = Modifier.testTag("explain_with_ai_btn")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = PythonYellow, modifier = Modifier.size(12.dp))
              Text("Explain", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PythonYellow)
            }
          }
        }
      }

      // Output body
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
          .horizontalScroll(horizontalScroll)
      ) {
        if (result.isSuccess) {
          if (result.stdout.isBlank()) {
            Text(
              text = "(Process finished with exit code 0 - No output printed. Use print() to see results)",
              fontSize = 12.sp,
              color = TextMutedDark,
              fontFamily = FontFamily.Monospace
            )
          } else {
            Text(
              text = result.stdout,
              fontSize = 13.sp,
              color = TextPrimaryDark,
              fontFamily = FontFamily.Monospace,
              lineHeight = 20.sp
            )
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Traceback (most recent call last):",
              fontSize = 12.sp,
              color = StatusError,
              fontFamily = FontFamily.Monospace
            )
            if (result.errorLine != null) {
              Text(
                text = "  File \"main.py\", line ${result.errorLine}",
                fontSize = 12.sp,
                color = TextSecondaryDark,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = "${result.errorType ?: "Error"}: ${result.errorMessage ?: "Unknown error"}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = StatusError,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // Variable Inspector Drawer (if variables exist)
      if (result.variables.isNotEmpty()) {
        HorizontalDivider(color = SurfaceCardBorder)
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
          Text(
            text = "Scope Variables (${result.variables.size}):",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PythonBlue,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.height(4.dp))
          result.variables.entries.take(8).forEach { (k, v) ->
            Text(
              text = "$k = $v",
              fontSize = 11.sp,
              color = TextSecondaryDark,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}
