package com.example.ui.compiler.syntax

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.ui.theme.*

object PythonSyntaxHighlighter {

  private val KEYWORDS = setOf(
    "def", "return", "if", "elif", "else", "for", "while", "break", "continue",
    "class", "import", "from", "as", "try", "except", "finally", "raise", "with",
    "pass", "lambda", "yield", "global", "nonlocal", "assert", "del", "async", "await"
  )

  private val LITERALS = setOf("True", "False", "None")

  private val BUILTINS = setOf(
    "print", "len", "range", "sum", "min", "max", "abs", "round",
    "str", "int", "float", "bool", "list", "dict", "set", "tuple",
    "type", "sorted", "reversed", "enumerate", "zip", "map", "filter",
    "input", "open", "isinstance", "issubclass", "help", "id"
  )

  fun highlight(code: String): AnnotatedString {
    return buildAnnotatedString {
      append(code)
      val length = code.length
      var i = 0

      while (i < length) {
        val c = code[i]

        // 1. Comments: # until end of line
        if (c == '#') {
          val start = i
          while (i < length && code[i] != '\n') {
            i++
          }
          addStyle(
            SpanStyle(color = SyntaxComment, fontWeight = FontWeight.Normal),
            start,
            i
          )
          continue
        }

        // 2. Strings: "..." or '...' (or f"...")
        if (c == '"' || c == '\'') {
          val quote = c
          val start = if (i > 0 && code[i - 1] == 'f') i - 1 else i
          i++
          var escaped = false
          while (i < length) {
            val curr = code[i]
            if (escaped) {
              escaped = false
            } else if (curr == '\\') {
              escaped = true
            } else if (curr == quote) {
              i++
              break
            } else if (curr == '\n') {
              break
            }
            i++
          }
          addStyle(
            SpanStyle(color = SyntaxString, fontWeight = FontWeight.Medium),
            start,
            i
          )
          continue
        }

        // 3. Numbers
        if (c.isDigit() || (c == '.' && i + 1 < length && code[i + 1].isDigit())) {
          val start = i
          while (i < length && (code[i].isDigit() || code[i] == '.' || code[i] == 'e' || code[i] == 'E' || code[i] == 'x' || code[i] == 'X')) {
            i++
          }
          addStyle(
            SpanStyle(color = SyntaxNumber, fontWeight = FontWeight.Bold),
            start,
            i
          )
          continue
        }

        // 4. Identifiers, Keywords, Builtins
        if (c.isLetter() || c == '_') {
          val start = i
          while (i < length && (code[i].isLetterOrDigit() || code[i] == '_')) {
            i++
          }
          val word = code.substring(start, i)

          when {
            KEYWORDS.contains(word) -> {
              addStyle(
                SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Bold),
                start,
                i
              )
            }
            LITERALS.contains(word) -> {
              addStyle(
                SpanStyle(color = SyntaxNumber, fontWeight = FontWeight.SemiBold),
                start,
                i
              )
            }
            BUILTINS.contains(word) -> {
              addStyle(
                SpanStyle(color = SyntaxBuiltin, fontWeight = FontWeight.SemiBold),
                start,
                i
              )
            }
            i < length && code[i] == '(' -> {
              // Function call or def
              addStyle(
                SpanStyle(color = SyntaxFunction, fontWeight = FontWeight.Medium),
                start,
                i
              )
            }
          }
          continue
        }

        // 5. Operators and Symbols
        if (c in "=+-*/%<>!&|^~:") {
          val start = i
          while (i < length && code[i] in "=+-*/%<>!&|^~:") {
            i++
          }
          addStyle(
            SpanStyle(color = SyntaxOperator, fontWeight = FontWeight.Normal),
            start,
            i
          )
          continue
        }

        i++
      }
    }
  }
}

class PythonVisualTransformation : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val highlighted = PythonSyntaxHighlighter.highlight(text.text)
    return TransformedText(highlighted, OffsetMapping.Identity)
  }
}
