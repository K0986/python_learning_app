package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.compiler.syntax.PythonSyntaxHighlighter
import com.example.ui.compiler.syntax.PythonVisualTransformation
import com.example.ui.theme.*

@Composable
fun CodeEditorView(
  code: String,
  onCodeChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  minHeight: androidx.compose.ui.unit.Dp = 220.dp,
  readOnly: Boolean = false
) {
  val lines = code.lines()
  val lineCount = lines.size.coerceAtLeast(1)
  val horizontalScrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(CodeEditorBg)
      .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
  ) {
    // Top editor header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CodeGutterBg)
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFFF5F56)))
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFFFBD2E)))
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFF27C93F)))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "main.py",
          color = TextSecondaryDark,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      Text(
        text = "$lineCount lines",
        color = TextMutedDark,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    // Code area with line numbers gutter
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = minHeight)
        .padding(vertical = 8.dp)
    ) {
      // Line numbers gutter
      Column(
        modifier = Modifier
          .width(42.dp)
          .padding(end = 8.dp),
        horizontalAlignment = Alignment.End
      ) {
        for (i in 1..lineCount) {
          Text(
            text = "$i",
            color = TextMutedDark,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 22.sp
          )
        }
      }

      // Divider between line numbers and code
      Box(
        modifier = Modifier
          .width(1.dp)
          .fillMaxHeight()
          .background(SurfaceCardBorder)
      )

      // Code editor area
      Box(
        modifier = Modifier
          .weight(1f)
          .padding(start = 8.dp, end = 8.dp)
          .horizontalScroll(horizontalScrollState)
      ) {
        BasicTextField(
          value = code,
          onValueChange = onCodeChange,
          readOnly = readOnly,
          textStyle = TextStyle(
            color = TextPrimaryDark,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 22.sp
          ),
          cursorBrush = SolidColor(PythonYellow),
          visualTransformation = PythonVisualTransformation(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("code_editor_field")
        )
      }
    }
  }
}

@Composable
fun QuickSymbolBar(
  onInsert: (String) -> Unit,
  onIndent: () -> Unit,
  onUnindent: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  val symbols = listOf("def ", "return ", "print(", ":", "(", ")", "[", "]", "{", "}", "\"", "'", "==", "!=", "=", "+", "-", "*", "/", "in ", "for ", "if ")

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(SurfaceDark)
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .horizontalScroll(scrollState),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Indent +4
    Surface(
      onClick = onIndent,
      shape = RoundedCornerShape(6.dp),
      color = SurfaceCardDark,
      modifier = Modifier.testTag("btn_indent")
    ) {
      Text(
        text = "Tab ⇥",
        color = PythonBlue,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
      )
    }

    // Unindent
    Surface(
      onClick = onUnindent,
      shape = RoundedCornerShape(6.dp),
      color = SurfaceCardDark,
      modifier = Modifier.testTag("btn_unindent")
    ) {
      Text(
        text = "⇤",
        color = PythonYellow,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
      )
    }

    symbols.forEach { sym ->
      Surface(
        onClick = { onInsert(sym) },
        shape = RoundedCornerShape(6.dp),
        color = SurfaceCardDark,
        modifier = Modifier.testTag("btn_sym_${sym.trim()}")
      ) {
        Text(
          text = sym,
          color = TextPrimaryDark,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
      }
    }
  }
}

@Composable
fun CodeSnippetCard(
  code: String,
  title: String? = null,
  onRunClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  val horizontalScroll = rememberScrollState()

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = CodeEditorBg),
    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
  ) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CodeGutterBg)
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title ?: "Python Code",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = PythonBlue,
          fontFamily = FontFamily.Monospace
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = { clipboardManager.setText(AnnotatedString(code)) },
            modifier = Modifier.size(24.dp).testTag("copy_snippet_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy code",
              tint = TextSecondaryDark,
              modifier = Modifier.size(14.dp)
            )
          }

          if (onRunClick != null) {
            Button(
              onClick = onRunClick,
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.height(24.dp).testTag("run_snippet_btn")
            ) {
              Text(
                text = "Run ▶",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BackgroundDark
              )
            }
          }
        }
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
          .horizontalScroll(horizontalScroll)
      ) {
        Text(
          text = PythonSyntaxHighlighter.highlight(code),
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace,
          lineHeight = 20.sp
        )
      }
    }
  }
}
