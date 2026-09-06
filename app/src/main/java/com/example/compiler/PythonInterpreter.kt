package com.example.compiler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.*

data class ExecutionResult(
  val stdout: String,
  val executionTimeMs: Long,
  val isSuccess: Boolean,
  val errorMessage: String? = null,
  val errorType: String? = null,
  val errorLine: Int? = null,
  val variables: Map<String, Any?> = emptyMap()
)

class PythonInterpreter {

  private var stepCount = 0
  private val MAX_STEPS = 50_000

  suspend fun execute(code: String, inputFeed: List<String> = emptyList()): ExecutionResult = withContext(Dispatchers.Default) {
    val startTime = System.currentTimeMillis()
    val stdout = StringBuilder()
    var inputIndex = 0
    stepCount = 0

    val globalScope = mutableMapOf<String, Any?>()
    setupBuiltins(globalScope, stdout, {
      if (inputIndex < inputFeed.size) inputFeed[inputIndex++] else ""
    })

    try {
      val lines = code.lines()
      executeBlock(lines, 0, lines.size, 0, globalScope)
      val time = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdout.toString(),
        executionTimeMs = max(1L, time),
        isSuccess = true,
        variables = globalScope.filterKeys { !it.startsWith("__") && !it.contains("builtin_") }
      )
    } catch (e: PythonException) {
      val time = System.currentTimeMillis() - startTime
      val trace = "Traceback (most recent call last):\n  File \"<stdin>\", line ${e.lineNumber}\n${e.type}: ${e.message}"
      ExecutionResult(
        stdout = stdout.toString(),
        executionTimeMs = max(1L, time),
        isSuccess = false,
        errorMessage = trace,
        errorType = e.type,
        errorLine = e.lineNumber
      )
    } catch (e: Exception) {
      val time = System.currentTimeMillis() - startTime
      ExecutionResult(
        stdout = stdout.toString(),
        executionTimeMs = max(1L, time),
        isSuccess = false,
        errorMessage = "Error: ${e.message ?: e.javaClass.simpleName}",
        errorType = "RuntimeError",
        errorLine = 1
      )
    }
  }

  suspend fun runFunction(
    code: String,
    functionName: String,
    args: List<Any?>
  ): Any? = withContext(Dispatchers.Default) {
    stepCount = 0
    val stdout = StringBuilder()
    val globalScope = mutableMapOf<String, Any?>()
    setupBuiltins(globalScope, stdout, { "" })

    val lines = code.lines()
    executeBlock(lines, 0, lines.size, 0, globalScope)

    val func = globalScope[functionName]
      ?: throw PythonException("NameError", "name '$functionName' is not defined", 1)

    if (func is UserFunction) {
      func.call(args, this@PythonInterpreter)
    } else {
      throw PythonException("TypeError", "'$functionName' is not callable", 1)
    }
  }

  private fun setupBuiltins(
    scope: MutableMap<String, Any?>,
    stdout: StringBuilder,
    inputSupplier: () -> String
  ) {
    scope["print"] = BuiltinFunction("print") { args ->
      val text = args.joinToString(" ") { formatPyValue(it) }
      stdout.append(text).append("\n")
      null
    }
    scope["len"] = BuiltinFunction("len") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "len() takes exactly one argument (0 given)")
      when (val target = args[0]) {
        is String -> target.length
        is List<*> -> target.size
        is Map<*, *> -> target.size
        is Set<*> -> target.size
        else -> throw PythonException("TypeError", "object of type '${pyTypeName(target)}' has no len()")
      }
    }
    scope["range"] = BuiltinFunction("range") { args ->
      when (args.size) {
        1 -> (0 until toInt(args[0])).toList()
        2 -> (toInt(args[0]) until toInt(args[1])).toList()
        3 -> {
          val start = toInt(args[0])
          val stop = toInt(args[1])
          val step = toInt(args[2])
          if (step == 0) throw PythonException("ValueError", "range() arg 3 must not be zero")
          val list = mutableListOf<Int>()
          if (step > 0) {
            var curr = start
            while (curr < stop) {
              list.add(curr)
              curr += step
            }
          } else {
            var curr = start
            while (curr > stop) {
              list.add(curr)
              curr += step
            }
          }
          list
        }
        else -> throw PythonException("TypeError", "range expected at most 3 arguments, got ${args.size}")
      }
    }
    scope["sum"] = BuiltinFunction("sum") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "sum() takes at least 1 argument")
      val list = args[0] as? List<*> ?: throw PythonException("TypeError", "first argument to sum() must be iterable")
      var total = 0.0
      var isFloat = false
      for (item in list) {
        when (item) {
          is Number -> {
            if (item is Double || item is Float) isFloat = true
            total += item.toDouble()
          }
          else -> throw PythonException("TypeError", "unsupported operand type for sum: '${pyTypeName(item)}'")
        }
      }
      if (isFloat) total else total.toLong()
    }
    scope["min"] = BuiltinFunction("min") { args ->
      val items = if (args.size == 1 && args[0] is List<*>) (args[0] as List<*>) else args
      if (items.isEmpty()) throw PythonException("ValueError", "min() arg is an empty sequence")
      items.minWithOrNull { a, b -> comparePy(a, b) }
    }
    scope["max"] = BuiltinFunction("max") { args ->
      val items = if (args.size == 1 && args[0] is List<*>) (args[0] as List<*>) else args
      if (items.isEmpty()) throw PythonException("ValueError", "max() arg is an empty sequence")
      items.maxWithOrNull { a, b -> comparePy(a, b) }
    }
    scope["abs"] = BuiltinFunction("abs") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "abs() takes exactly one argument")
      val num = args[0] as? Number ?: throw PythonException("TypeError", "bad operand type for abs(): '${pyTypeName(args[0])}'")
      if (num is Double || num is Float) abs(num.toDouble()) else abs(num.toLong())
    }
    scope["round"] = BuiltinFunction("round") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "round() takes at least 1 argument")
      val num = (args[0] as? Number)?.toDouble() ?: throw PythonException("TypeError", "a float is required")
      val digits = if (args.size > 1) toInt(args[1]) else 0
      if (digits == 0) round(num).toLong() else {
        val factor = 10.0.pow(digits.toDouble())
        round(num * factor) / factor
      }
    }
    scope["str"] = BuiltinFunction("str") { args ->
      if (args.isEmpty()) "" else formatPyValue(args[0])
    }
    scope["int"] = BuiltinFunction("int") { args ->
      if (args.isEmpty()) 0L else {
        when (val v = args[0]) {
          is Number -> v.toLong()
          is String -> v.trim().toDoubleOrNull()?.toLong() ?: throw PythonException("ValueError", "invalid literal for int(): '$v'")
          is Boolean -> if (v) 1L else 0L
          else -> throw PythonException("TypeError", "int() argument must be a string, a bytes-like object or a real number")
        }
      }
    }
    scope["float"] = BuiltinFunction("float") { args ->
      if (args.isEmpty()) 0.0 else {
        when (val v = args[0]) {
          is Number -> v.toDouble()
          is String -> v.trim().toDoubleOrNull() ?: throw PythonException("ValueError", "could not convert string to float: '$v'")
          is Boolean -> if (v) 1.0 else 0.0
          else -> throw PythonException("TypeError", "float() argument must be a string or a real number")
        }
      }
    }
    scope["bool"] = BuiltinFunction("bool") { args ->
      if (args.isEmpty()) false else isTruthy(args[0])
    }
    scope["type"] = BuiltinFunction("type") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "type() takes 1 or 3 arguments")
      "<class '${pyTypeName(args[0])}'>"
    }
    scope["sorted"] = BuiltinFunction("sorted") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "sorted expected at least 1 argument")
      val list = when (val v = args[0]) {
        is List<*> -> v.toList()
        is String -> v.map { it.toString() }
        else -> throw PythonException("TypeError", "'${pyTypeName(v)}' object is not iterable")
      }
      list.sortedWith { a, b -> comparePy(a, b) }
    }
    scope["reversed"] = BuiltinFunction("reversed") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "reversed() missing 1 required positional argument")
      when (val v = args[0]) {
        is List<*> -> v.reversed()
        is String -> v.reversed().map { it.toString() }
        else -> throw PythonException("TypeError", "'${pyTypeName(v)}' object is not reversible")
      }
    }
    scope["enumerate"] = BuiltinFunction("enumerate") { args ->
      if (args.isEmpty()) throw PythonException("TypeError", "enumerate expected at least 1 argument")
      val list = when (val v = args[0]) {
        is List<*> -> v
        is String -> v.map { it.toString() }
        else -> emptyList<Any?>()
      }
      list.mapIndexed { idx, item -> listOf(idx, item) }
    }
    scope["zip"] = BuiltinFunction("zip") { args ->
      if (args.size < 2) return@BuiltinFunction emptyList<Any?>()
      val l1 = args[0] as? List<*> ?: emptyList<Any?>()
      val l2 = args[1] as? List<*> ?: emptyList<Any?>()
      val minSize = min(l1.size, l2.size)
      (0 until minSize).map { listOf(l1[it], l2[it]) }
    }
    scope["input"] = BuiltinFunction("input") { args ->
      if (args.isNotEmpty()) {
        stdout.append(formatPyValue(args[0]))
      }
      inputSupplier()
    }
    // Math constants & functions
    val mathModule = mutableMapOf<String, Any?>(
      "pi" to Math.PI,
      "e" to Math.E,
      "sqrt" to BuiltinFunction("math.sqrt") { args -> sqrt(toDouble(args[0])) },
      "pow" to BuiltinFunction("math.pow") { args -> toDouble(args[0]).pow(toDouble(args[1])) },
      "floor" to BuiltinFunction("math.floor") { args -> floor(toDouble(args[0])).toLong() },
      "ceil" to BuiltinFunction("math.ceil") { args -> ceil(toDouble(args[0])).toLong() },
      "sin" to BuiltinFunction("math.sin") { args -> sin(toDouble(args[0])) },
      "cos" to BuiltinFunction("math.cos") { args -> cos(toDouble(args[0])) }
    )
    scope["math"] = mathModule
  }

  fun executeBlock(
    lines: List<String>,
    startIndex: Int,
    endIndex: Int,
    baseIndent: Int,
    scope: MutableMap<String, Any?>
  ): ControlSignal {
    var i = startIndex
    while (i < endIndex) {
      val rawLine = lines[i]
      val lineNum = i + 1

      if (rawLine.trim().isEmpty() || rawLine.trim().startsWith("#")) {
        i++
        continue
      }

      val indent = getIndentation(rawLine)
      if (indent < baseIndent) {
        // Block ended
        return ControlSignal.None
      }

      val trimmed = rawLine.trim()

      checkSteps(lineNum)

      // Function definition: def func(a, b):
      if (trimmed.startsWith("def ") && trimmed.endsWith(":")) {
        val defHeader = trimmed.removePrefix("def ").removeSuffix(":").trim()
        val parenIdx = defHeader.indexOf("(")
        if (parenIdx == -1 || !defHeader.endsWith(")")) {
          throw PythonException("SyntaxError", "invalid syntax in function definition", lineNum)
        }
        val funcName = defHeader.substring(0, parenIdx).trim()
        val paramsRaw = defHeader.substring(parenIdx + 1, defHeader.length - 1).trim()
        val params = if (paramsRaw.isEmpty()) emptyList() else paramsRaw.split(",").map { it.trim() }

        // Gather function body lines
        val bodyLines = mutableListOf<String>()
        val bodyIndent = baseIndent + 1
        var j = i + 1
        while (j < endIndex) {
          val nextRaw = lines[j]
          if (nextRaw.trim().isEmpty() || nextRaw.trim().startsWith("#")) {
            bodyLines.add(nextRaw)
            j++
            continue
          }
          val nextIndent = getIndentation(nextRaw)
          if (nextIndent <= indent) break
          bodyLines.add(nextRaw)
          j++
        }

        scope[funcName] = UserFunction(funcName, params, bodyLines, scope)
        i = j
        continue
      }

      // Return statement
      if (trimmed == "return" || trimmed.startsWith("return ")) {
        val expr = trimmed.removePrefix("return").trim()
        val retVal = if (expr.isEmpty()) null else evaluateExpression(expr, scope, lineNum)
        return ControlSignal.Return(retVal)
      }

      // Break / Continue
      if (trimmed == "break") return ControlSignal.Break
      if (trimmed == "continue") return ControlSignal.Continue
      if (trimmed == "pass") {
        i++
        continue
      }

      // If / Elif / Else structure
      if (trimmed.startsWith("if ") && trimmed.endsWith(":")) {
        val condExpr = trimmed.removePrefix("if ").removeSuffix(":").trim()
        val branches = collectConditionalBranches(lines, i, endIndex, indent)
        var executed = false

        for (branch in branches) {
          val shouldRun = if (branch.conditionExpr == null) {
            !executed // else
          } else {
            !executed && isTruthy(evaluateExpression(branch.conditionExpr, scope, lineNum))
          }

          if (shouldRun) {
            executed = true
            val signal = executeBlock(lines, branch.startLine, branch.endLine, indent + 1, scope)
            if (signal !is ControlSignal.None) return signal
          }
        }

        i = branches.last().endLine
        continue
      }

      // While loop
      if (trimmed.startsWith("while ") && trimmed.endsWith(":")) {
        val condExpr = trimmed.removePrefix("while ").removeSuffix(":").trim()
        val bodyRange = getBlockRange(lines, i + 1, endIndex, indent)

        while (isTruthy(evaluateExpression(condExpr, scope, lineNum))) {
          checkSteps(lineNum)
          val signal = executeBlock(lines, bodyRange.first, bodyRange.second, indent + 1, scope)
          if (signal is ControlSignal.Break) break
          if (signal is ControlSignal.Return) return signal
        }

        i = bodyRange.second
        continue
      }

      // For loop: for var in iterable:
      if (trimmed.startsWith("for ") && trimmed.endsWith(":")) {
        val header = trimmed.removePrefix("for ").removeSuffix(":").trim()
        val inIdx = header.indexOf(" in ")
        if (inIdx == -1) throw PythonException("SyntaxError", "invalid syntax in for loop", lineNum)

        val loopVar = header.substring(0, inIdx).trim()
        val iterExpr = header.substring(inIdx + 4).trim()
        val iterableVal = evaluateExpression(iterExpr, scope, lineNum)
        val bodyRange = getBlockRange(lines, i + 1, endIndex, indent)

        val items: List<Any?> = when (iterableVal) {
          is List<*> -> iterableVal
          is String -> iterableVal.map { it.toString() }
          is Map<*, *> -> iterableVal.keys.toList()
          is Set<*> -> iterableVal.toList()
          else -> throw PythonException("TypeError", "'${pyTypeName(iterableVal)}' object is not iterable", lineNum)
        }

        for (item in items) {
          checkSteps(lineNum)
          // assign loop variable
          if (loopVar.contains(",")) {
            // tuple unpacking
            val vars = loopVar.split(",").map { it.trim() }
            if (item is List<*> && item.size >= vars.size) {
              for (k in vars.indices) scope[vars[k]] = item[k]
            }
          } else {
            scope[loopVar] = item
          }

          val signal = executeBlock(lines, bodyRange.first, bodyRange.second, indent + 1, scope)
          if (signal is ControlSignal.Break) break
          if (signal is ControlSignal.Return) return signal
        }

        i = bodyRange.second
        continue
      }

      // Normal single or multi assignment / expression statement
      executeStatement(trimmed, scope, lineNum)
      i++
    }
    return ControlSignal.None
  }

  private fun executeStatement(stmt: String, scope: MutableMap<String, Any?>, lineNum: Int) {
    // Check for compound assignments +=, -=, *=, /=
    val compoundOps = listOf("+=", "-=", "*=", "/=", "//=", "%=")
    for (op in compoundOps) {
      val idx = findOperatorIndex(stmt, op)
      if (idx != -1) {
        val target = stmt.substring(0, idx).trim()
        val expr = stmt.substring(idx + op.length).trim()
        val baseOp = op.removeSuffix("=")
        val currVal = evaluateExpression(target, scope, lineNum)
        val deltaVal = evaluateExpression(expr, scope, lineNum)
        val newVal = applyBinaryOp(currVal, deltaVal, baseOp, lineNum)
        assignToTarget(target, newVal, scope, lineNum)
        return
      }
    }

    // Standard assignment: target = expr
    val eqIdx = findOperatorIndex(stmt, "=")
    if (eqIdx != -1 && !stmt.startsWith("==") && (eqIdx == 0 || stmt[eqIdx - 1] !in listOf('!', '<', '>', '='))) {
      val target = stmt.substring(0, eqIdx).trim()
      val expr = stmt.substring(eqIdx + 1).trim()
      val value = evaluateExpression(expr, scope, lineNum)
      assignToTarget(target, value, scope, lineNum)
      return
    }

    // Expression call or method call (e.g. print(...), list.append(...))
    evaluateExpression(stmt, scope, lineNum)
  }

  private fun assignToTarget(target: String, value: Any?, scope: MutableMap<String, Any?>, lineNum: Int) {
    // List/dict indexing assignment: a[i] = x
    if (target.endsWith("]") && target.contains("[")) {
      val openBracket = target.indexOf("[")
      val containerName = target.substring(0, openBracket).trim()
      val indexExpr = target.substring(openBracket + 1, target.length - 1).trim()
      val container = scope[containerName] ?: throw PythonException("NameError", "name '$containerName' is not defined", lineNum)
      val key = evaluateExpression(indexExpr, scope, lineNum)

      if (container is MutableList<*>) {
        @Suppress("UNCHECKED_CAST")
        val list = container as MutableList<Any?>
        var idx = toInt(key)
        if (idx < 0) idx += list.size
        if (idx < 0 || idx >= list.size) throw PythonException("IndexError", "list assignment index out of range", lineNum)
        list[idx] = value
      } else if (container is MutableMap<*, *>) {
        @Suppress("UNCHECKED_CAST")
        val map = container as MutableMap<Any?, Any?>
        map[key] = value
      }
      return
    }

    // Multiple assignment: a, b = 1, 2
    if (target.contains(",") && !target.contains("[") && !target.contains("(")) {
      val vars = target.split(",").map { it.trim() }
      val values = if (value is List<*>) value else listOf(value)
      if (values.size < vars.size) throw PythonException("ValueError", "not enough values to unpack", lineNum)
      for (i in vars.indices) {
        scope[vars[i]] = values[i]
      }
      return
    }

    scope[target] = value
  }

  fun evaluateExpression(expr: String, scope: Map<String, Any?>, lineNum: Int): Any? {
    val trimmed = expr.trim()
    if (trimmed.isEmpty()) return null

    // Literals
    if (trimmed == "None") return null
    if (trimmed == "True") return true
    if (trimmed == "False") return false
    trimmed.toLongOrNull()?.let { return it }
    trimmed.toDoubleOrNull()?.let { return it }

    // Strings
    if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
      if (trimmed.length >= 2) {
        return trimmed.substring(1, trimmed.length - 1)
          .replace("\\n", "\n")
          .replace("\\t", "\t")
          .replace("\\\"", "\"")
          .replace("\\'", "'")
      }
    }

    // f-strings: f"Hello {name}"
    if ((trimmed.startsWith("f\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("f'") && trimmed.endsWith("'"))) {
      val raw = trimmed.substring(2, trimmed.length - 1)
      val sb = StringBuilder()
      var idx = 0
      while (idx < raw.length) {
        if (raw[idx] == '{') {
          val closeIdx = raw.indexOf('}', idx)
          if (closeIdx != -1) {
            val innerExpr = raw.substring(idx + 1, closeIdx)
            val valResult = evaluateExpression(innerExpr, scope, lineNum)
            sb.append(formatPyValue(valResult))
            idx = closeIdx + 1
            continue
          }
        }
        sb.append(raw[idx])
        idx++
      }
      return sb.toString()
    }

    // List comprehension: [x * 2 for x in items if x > 2]
    if (trimmed.startsWith("[") && trimmed.endsWith("]") && trimmed.contains(" for ")) {
      val inner = trimmed.substring(1, trimmed.length - 1).trim()
      val forIdx = inner.indexOf(" for ")
      val mapExpr = inner.substring(0, forIdx).trim()
      val remainder = inner.substring(forIdx + 5).trim()
      val inIdx = remainder.indexOf(" in ")
      if (inIdx != -1) {
        val loopVar = remainder.substring(0, inIdx).trim()
        val afterIn = remainder.substring(inIdx + 4).trim()
        val ifIdx = afterIn.indexOf(" if ")
        val iterExpr = if (ifIdx != -1) afterIn.substring(0, ifIdx).trim() else afterIn
        val conditionExpr = if (ifIdx != -1) afterIn.substring(ifIdx + 4).trim() else null

        val iterableVal = evaluateExpression(iterExpr, scope, lineNum)
        val items = when (iterableVal) {
          is List<*> -> iterableVal
          is String -> iterableVal.map { it.toString() }
          else -> emptyList<Any?>()
        }

        val resultList = mutableListOf<Any?>()
        for (item in items) {
          val localScope = scope.toMutableMap()
          localScope[loopVar] = item
          if (conditionExpr == null || isTruthy(evaluateExpression(conditionExpr, localScope, lineNum))) {
            resultList.add(evaluateExpression(mapExpr, localScope, lineNum))
          }
        }
        return resultList
      }
    }

    // List literal: [1, 2, 3]
    if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
      val inner = trimmed.substring(1, trimmed.length - 1).trim()
      if (inner.isEmpty()) return mutableListOf<Any?>()
      val elements = splitTopLevel(inner, ',')
      return elements.map { evaluateExpression(it, scope, lineNum) }.toMutableList()
    }

    // Dict literal: {"a": 1, "b": 2}
    if (trimmed.startsWith("{") && trimmed.endsWith("}") && !trimmed.contains(" for ")) {
      val inner = trimmed.substring(1, trimmed.length - 1).trim()
      if (inner.isEmpty()) return mutableMapOf<Any?, Any?>()
      val entries = splitTopLevel(inner, ',')
      val map = mutableMapOf<Any?, Any?>()
      for (entry in entries) {
        val colonIdx = findTopLevelColon(entry)
        if (colonIdx != -1) {
          val k = evaluateExpression(entry.substring(0, colonIdx), scope, lineNum)
          val v = evaluateExpression(entry.substring(colonIdx + 1), scope, lineNum)
          map[k] = v
        }
      }
      return map
    }

    // Logical 'or'
    val orIdx = findLogicalOperator(trimmed, "or")
    if (orIdx != -1) {
      val left = evaluateExpression(trimmed.substring(0, orIdx), scope, lineNum)
      if (isTruthy(left)) return left
      return evaluateExpression(trimmed.substring(orIdx + 2), scope, lineNum)
    }

    // Logical 'and'
    val andIdx = findLogicalOperator(trimmed, "and")
    if (andIdx != -1) {
      val left = evaluateExpression(trimmed.substring(0, andIdx), scope, lineNum)
      if (!isTruthy(left)) return left
      return evaluateExpression(trimmed.substring(andIdx + 3), scope, lineNum)
    }

    // Logical 'not'
    if (trimmed.startsWith("not ") && trimmed.length > 4) {
      val operand = evaluateExpression(trimmed.substring(4), scope, lineNum)
      return !isTruthy(operand)
    }

    // Comparisons: ==, !=, <=, >=, <, >, in, not in
    val compOps = listOf("==", "!=", "<=", ">=", "<", ">", " not in ", " in ")
    for (op in compOps) {
      val idx = findTopLevelString(trimmed, op)
      if (idx != -1) {
        val left = evaluateExpression(trimmed.substring(0, idx), scope, lineNum)
        val right = evaluateExpression(trimmed.substring(idx + op.length), scope, lineNum)
        return compareValues(left, right, op.trim())
      }
    }

    // Add / Subtract (+, -)
    val addSubIdx = findBinaryOp(trimmed, listOf("+", "-"))
    if (addSubIdx != -1) {
      val op = trimmed[addSubIdx].toString()
      val left = evaluateExpression(trimmed.substring(0, addSubIdx), scope, lineNum)
      val right = evaluateExpression(trimmed.substring(addSubIdx + 1), scope, lineNum)
      return applyBinaryOp(left, right, op, lineNum)
    }

    // Multiply / Divide / Modulo (*, /, //, %)
    val mulDivIdx = findBinaryOp(trimmed, listOf("//", "*", "/", "%"))
    if (mulDivIdx != -1) {
      val isDoubleSlash = trimmed.regionMatches(mulDivIdx, "//", 0, 2)
      val op = if (isDoubleSlash) "//" else trimmed[mulDivIdx].toString()
      val left = evaluateExpression(trimmed.substring(0, mulDivIdx), scope, lineNum)
      val right = evaluateExpression(trimmed.substring(mulDivIdx + op.length), scope, lineNum)
      return applyBinaryOp(left, right, op, lineNum)
    }

    // Power (**)
    val powIdx = findTopLevelString(trimmed, "**")
    if (powIdx != -1) {
      val left = evaluateExpression(trimmed.substring(0, powIdx), scope, lineNum)
      val right = evaluateExpression(trimmed.substring(powIdx + 2), scope, lineNum)
      return applyBinaryOp(left, right, "**", lineNum)
    }

    // Method calls or indexing: target[index] or target.method(...)
    if (trimmed.endsWith("]") && trimmed.contains("[")) {
      val openBracket = findMatchingBracketStart(trimmed)
      if (openBracket != -1 && openBracket > 0) {
        val targetExpr = trimmed.substring(0, openBracket).trim()
        val sliceExpr = trimmed.substring(openBracket + 1, trimmed.length - 1).trim()
        val target = evaluateExpression(targetExpr, scope, lineNum)
        return applySliceOrIndex(target, sliceExpr, scope, lineNum)
      }
    }

    // Function or Method Call: name(arg1, arg2)
    if (trimmed.endsWith(")") && trimmed.contains("(")) {
      val openParen = findMatchingParenStart(trimmed)
      if (openParen != -1 && openParen > 0) {
        val callerExpr = trimmed.substring(0, openParen).trim()
        val argsExpr = trimmed.substring(openParen + 1, trimmed.length - 1).trim()
        val parsedArgs = if (argsExpr.isEmpty()) emptyList() else splitTopLevel(argsExpr, ',').map { evaluateExpression(it, scope, lineNum) }

        // Method call on object: obj.method(...)
        if (callerExpr.contains(".")) {
          val dotIdx = callerExpr.lastIndexOf(".")
          val objExpr = callerExpr.substring(0, dotIdx).trim()
          val methodName = callerExpr.substring(dotIdx + 1).trim()
          val obj = evaluateExpression(objExpr, scope, lineNum)
          return invokeMethod(obj, methodName, parsedArgs, lineNum)
        }

        val callable = scope[callerExpr] ?: throw PythonException("NameError", "name '$callerExpr' is not defined", lineNum)
        return when (callable) {
          is BuiltinFunction -> callable.block(parsedArgs)
          is UserFunction -> callable.call(parsedArgs, this)
          else -> throw PythonException("TypeError", "'$callerExpr' is not callable", lineNum)
        }
      }
    }

    // Dot attribute lookup: obj.attr
    if (trimmed.contains(".")) {
      val dotIdx = trimmed.lastIndexOf(".")
      val objExpr = trimmed.substring(0, dotIdx).trim()
      val attr = trimmed.substring(dotIdx + 1).trim()
      val obj = evaluateExpression(objExpr, scope, lineNum)
      if (obj is Map<*, *>) return obj[attr]
    }

    // Variable lookup
    if (scope.containsKey(trimmed)) {
      return scope[trimmed]
    }

    throw PythonException("NameError", "name '$trimmed' is not defined", lineNum)
  }

  private fun applySliceOrIndex(target: Any?, sliceExpr: String, scope: Map<String, Any?>, lineNum: Int): Any? {
    if (sliceExpr.contains(":")) {
      // Slicing: [start:stop:step]
      val parts = sliceExpr.split(":")
      val start = if (parts.isNotEmpty() && parts[0].isNotBlank()) toInt(evaluateExpression(parts[0], scope, lineNum)) else null
      val stop = if (parts.size > 1 && parts[1].isNotBlank()) toInt(evaluateExpression(parts[1], scope, lineNum)) else null
      val step = if (parts.size > 2 && parts[2].isNotBlank()) toInt(evaluateExpression(parts[2], scope, lineNum)) else 1

      if (target is String) {
        return sliceString(target, start, stop, step)
      } else if (target is List<*>) {
        return sliceList(target, start, stop, step)
      }
      throw PythonException("TypeError", "'${pyTypeName(target)}' object is not subscriptable", lineNum)
    } else {
      // Single index or dict key lookup
      val key = evaluateExpression(sliceExpr, scope, lineNum)
      if (target is List<*>) {
        var idx = toInt(key)
        if (idx < 0) idx += target.size
        if (idx < 0 || idx >= target.size) throw PythonException("IndexError", "list index out of range", lineNum)
        return target[idx]
      } else if (target is String) {
        var idx = toInt(key)
        if (idx < 0) idx += target.length
        if (idx < 0 || idx >= target.length) throw PythonException("IndexError", "string index out of range", lineNum)
        return target[idx].toString()
      } else if (target is Map<*, *>) {
        if (!target.containsKey(key)) throw PythonException("KeyError", "$key", lineNum)
        return target[key]
      }
      throw PythonException("TypeError", "'${pyTypeName(target)}' object is not subscriptable", lineNum)
    }
  }

  private fun sliceString(s: String, start: Int?, stop: Int?, step: Int): String {
    if (step == -1 && start == null && stop == null) return s.reversed()
    val len = s.length
    var st = start ?: if (step > 0) 0 else len - 1
    var sp = stop ?: if (step > 0) len else -1
    if (st < 0) st += len
    if (sp < 0 && stop != null) sp += len
    st = max(0, min(st, len))
    sp = max(-1, min(sp, len))

    val sb = StringBuilder()
    var curr = st
    if (step > 0) {
      while (curr < sp && curr < len) {
        sb.append(s[curr])
        curr += step
      }
    } else {
      while (curr > sp && curr >= 0) {
        sb.append(s[curr])
        curr += step
      }
    }
    return sb.toString()
  }

  private fun sliceList(list: List<*>, start: Int?, stop: Int?, step: Int): List<*> {
    if (step == -1 && start == null && stop == null) return list.reversed()
    val len = list.size
    var st = start ?: if (step > 0) 0 else len - 1
    var sp = stop ?: if (step > 0) len else -1
    if (st < 0) st += len
    if (sp < 0 && stop != null) sp += len
    st = max(0, min(st, len))
    sp = max(-1, min(sp, len))

    val result = mutableListOf<Any?>()
    var curr = st
    if (step > 0) {
      while (curr < sp && curr < len) {
        result.add(list[curr])
        curr += step
      }
    } else {
      while (curr > sp && curr >= 0) {
        result.add(list[curr])
        curr += step
      }
    }
    return result
  }

  private fun invokeMethod(obj: Any?, method: String, args: List<Any?>, lineNum: Int): Any? {
    if (obj is String) {
      return when (method) {
        "lower" -> obj.lowercase(Locale.ROOT)
        "upper" -> obj.uppercase(Locale.ROOT)
        "strip" -> obj.trim()
        "replace" -> {
          val old = args.getOrNull(0)?.toString() ?: ""
          val new = args.getOrNull(1)?.toString() ?: ""
          obj.replace(old, new)
        }
        "split" -> {
          val delim = args.getOrNull(0)?.toString() ?: " "
          if (delim.isEmpty()) obj.map { it.toString() } else obj.split(delim).filter { it.isNotEmpty() }
        }
        "join" -> {
          val iterable = args.getOrNull(0) as? List<*> ?: emptyList<Any?>()
          iterable.joinToString(obj) { it?.toString() ?: "" }
        }
        "startswith" -> obj.startsWith(args.getOrNull(0)?.toString() ?: "")
        "endswith" -> obj.endsWith(args.getOrNull(0)?.toString() ?: "")
        "find" -> obj.indexOf(args.getOrNull(0)?.toString() ?: "")
        "count" -> {
          val sub = args.getOrNull(0)?.toString() ?: ""
          if (sub.isEmpty()) 0 else (obj.length - obj.replace(sub, "").length) / sub.length
        }
        else -> throw PythonException("AttributeError", "'str' object has no attribute '$method'", lineNum)
      }
    }

    if (obj is MutableList<*>) {
      @Suppress("UNCHECKED_CAST")
      val list = obj as MutableList<Any?>
      return when (method) {
        "append" -> {
          list.add(args.getOrNull(0))
          null
        }
        "pop" -> {
          if (list.isEmpty()) throw PythonException("IndexError", "pop from empty list", lineNum)
          val idx = if (args.isNotEmpty()) toInt(args[0]) else list.size - 1
          list.removeAt(idx)
        }
        "remove" -> {
          val target = args.getOrNull(0)
          val removed = list.remove(target)
          if (!removed) throw PythonException("ValueError", "list.remove(x): x not in list", lineNum)
          null
        }
        "insert" -> {
          val idx = toInt(args.getOrNull(0) ?: 0)
          val item = args.getOrNull(1)
          list.add(min(max(0, idx), list.size), item)
          null
        }
        "reverse" -> {
          list.reverse()
          null
        }
        "sort" -> {
          list.sortWith { a, b -> comparePy(a, b) }
          null
        }
        "extend" -> {
          val other = args.getOrNull(0) as? List<*> ?: emptyList<Any?>()
          list.addAll(other)
          null
        }
        "count" -> list.count { it == args.getOrNull(0) }
        else -> throw PythonException("AttributeError", "'list' object has no attribute '$method'", lineNum)
      }
    }

    if (obj is MutableMap<*, *>) {
      @Suppress("UNCHECKED_CAST")
      val map = obj as MutableMap<Any?, Any?>
      return when (method) {
        "get" -> {
          val key = args.getOrNull(0)
          val defaultVal = args.getOrNull(1)
          map.getOrDefault(key, defaultVal)
        }
        "keys" -> map.keys.toList()
        "values" -> map.values.toList()
        "items" -> map.entries.map { listOf(it.key, it.value) }
        else -> throw PythonException("AttributeError", "'dict' object has no attribute '$method'", lineNum)
      }
    }

    throw PythonException("AttributeError", "'${pyTypeName(obj)}' object has no attribute '$method'", lineNum)
  }

  private fun applyBinaryOp(left: Any?, right: Any?, op: String, lineNum: Int): Any? {
    if (op == "+" && (left is String || right is String)) {
      return formatPyValue(left) + formatPyValue(right)
    }
    if (op == "+" && left is List<*> && right is List<*>) {
      return (left + right).toMutableList()
    }
    if (op == "*" && left is String && right is Number) {
      return left.repeat(max(0, right.toInt()))
    }
    if (op == "*" && left is List<*> && right is Number) {
      val times = max(0, right.toInt())
      val res = mutableListOf<Any?>()
      repeat(times) { res.addAll(left) }
      return res
    }

    val ln = (left as? Number)?.toDouble()
    val rn = (right as? Number)?.toDouble()
    if (ln == null || rn == null) {
      throw PythonException("TypeError", "unsupported operand type(s) for $op: '${pyTypeName(left)}' and '${pyTypeName(right)}'", lineNum)
    }

    return when (op) {
      "+" -> if (left !is Double && left !is Float && right !is Double && right !is Float) (left.toLong() + right.toLong()) else (ln + rn)
      "-" -> if (left !is Double && left !is Float && right !is Double && right !is Float) (left.toLong() - right.toLong()) else (ln - rn)
      "*" -> if (left !is Double && left !is Float && right !is Double && right !is Float) (left.toLong() * right.toLong()) else (ln * rn)
      "/" -> {
        if (rn == 0.0) throw PythonException("ZeroDivisionError", "division by zero", lineNum)
        ln / rn
      }
      "//" -> {
        if (rn == 0.0) throw PythonException("ZeroDivisionError", "integer division or modulo by zero", lineNum)
        floor(ln / rn).toLong()
      }
      "%" -> {
        if (rn == 0.0) throw PythonException("ZeroDivisionError", "integer division or modulo by zero", lineNum)
        if (left !is Double && right !is Double) (left.toLong() % right.toLong()) else (ln % rn)
      }
      "**" -> ln.pow(rn)
      else -> throw PythonException("SyntaxError", "unknown operator $op", lineNum)
    }
  }

  private fun compareValues(left: Any?, right: Any?, op: String): Boolean {
    return when (op) {
      "==" -> valuesEqual(left, right)
      "!=" -> !valuesEqual(left, right)
      "<" -> comparePy(left, right) < 0
      "<=" -> comparePy(left, right) <= 0
      ">" -> comparePy(left, right) > 0
      ">=" -> comparePy(left, right) >= 0
      "in" -> {
        when (right) {
          is String -> right.contains(left.toString())
          is List<*> -> right.contains(left)
          is Map<*, *> -> right.containsKey(left)
          else -> false
        }
      }
      "not in" -> !compareValues(left, right, "in")
      else -> false
    }
  }

  private fun valuesEqual(a: Any?, b: Any?): Boolean {
    if (a == null && b == null) return true
    if (a == null || b == null) return false
    if (a is Number && b is Number) return a.toDouble() == b.toDouble()
    if (a is Boolean && b is Boolean) return a == b
    if (a is List<*> && b is List<*>) {
      if (a.size != b.size) return false
      for (i in a.indices) {
        if (!valuesEqual(a[i], b[i])) return false
      }
      return true
    }
    return a == b
  }

  fun comparePy(a: Any?, b: Any?): Int {
    if (a is Number && b is Number) return a.toDouble().compareTo(b.toDouble())
    if (a is String && b is String) return a.compareTo(b)
    if (a is Boolean && b is Boolean) return a.compareTo(b)
    return a.toString().compareTo(b.toString())
  }

  private fun checkSteps(lineNum: Int) {
    stepCount++
    if (stepCount > MAX_STEPS) {
      throw PythonException("TimeoutError", "Execution exceeded step limit ($MAX_STEPS steps). Check for infinite loops!", lineNum)
    }
  }

  // Helpers
  private fun getIndentation(line: String): Int {
    var spaces = 0
    for (ch in line) {
      if (ch == ' ') spaces++
      else if (ch == '\t') spaces += 4
      else break
    }
    return spaces / 4
  }

  private fun isTruthy(v: Any?): Boolean {
    return when (v) {
      null -> false
      is Boolean -> v
      is Number -> v.toDouble() != 0.0
      is String -> v.isNotEmpty()
      is List<*> -> v.isNotEmpty()
      is Map<*, *> -> v.isNotEmpty()
      else -> true
    }
  }

  private fun toInt(v: Any?): Int = when (v) {
    is Number -> v.toInt()
    is String -> v.toIntOrNull() ?: 0
    else -> 0
  }

  private fun toDouble(v: Any?): Double = when (v) {
    is Number -> v.toDouble()
    is String -> v.toDoubleOrNull() ?: 0.0
    else -> 0.0
  }

  private fun pyTypeName(v: Any?): String = when (v) {
    null -> "NoneType"
    is Int, is Long -> "int"
    is Float, is Double -> "float"
    is String -> "str"
    is Boolean -> "bool"
    is List<*> -> "list"
    is Map<*, *> -> "dict"
    is Set<*> -> "set"
    is UserFunction, is BuiltinFunction -> "function"
    else -> v.javaClass.simpleName.lowercase(Locale.ROOT)
  }

  fun formatPyValue(v: Any?): String = when (v) {
    null -> "None"
    is Boolean -> if (v) "True" else "False"
    is Number -> if (v.toDouble() == v.toLong().toDouble()) v.toLong().toString() else v.toString()
    is List<*> -> "[" + v.joinToString(", ") { formatPyValue(it) } + "]"
    is Map<*, *> -> "{" + v.entries.joinToString(", ") { "${formatPyValue(it.key)}: ${formatPyValue(it.value)}" } + "}"
    else -> v.toString()
  }

  // Parsing helper utilities
  private fun findOperatorIndex(line: String, op: String): Int {
    var inQuotes = false
    var quoteChar = ' '
    var depth = 0
    var i = 0
    while (i <= line.length - op.length) {
      val c = line[i]
      if (inQuotes) {
        if (c == quoteChar) inQuotes = false
      } else {
        if (c == '\'' || c == '"') {
          inQuotes = true
          quoteChar = c
        } else if (c in "([{") depth++
        else if (c in ")]}") depth--
        else if (depth == 0 && line.regionMatches(i, op, 0, op.length)) {
          return i
        }
      }
      i++
    }
    return -1
  }

  private fun findBinaryOp(expr: String, ops: List<String>): Int {
    var inQuotes = false
    var quoteChar = ' '
    var depth = 0
    var i = expr.length - 1
    while (i >= 0) {
      val c = expr[i]
      if (inQuotes) {
        if (c == quoteChar) inQuotes = false
      } else {
        if (c == '\'' || c == '"') {
          inQuotes = true
          quoteChar = c
        } else if (c in ")]}") depth++
        else if (c in "([{") depth--
        else if (depth == 0) {
          for (op in ops) {
            if (expr.regionMatches(i - op.length + 1, op, 0, op.length)) {
              val start = i - op.length + 1
              var prevIdx = start - 1
              while (prevIdx >= 0 && expr[prevIdx].isWhitespace()) prevIdx--
              if (prevIdx >= 0 && expr[prevIdx] !in "+-*/%<>=!([{,") {
                return start
              }
            }
          }
        }
      }
      i--
    }
    return -1
  }

  private fun findLogicalOperator(expr: String, op: String): Int {
    val search = " $op "
    val idx = findTopLevelString(expr, search)
    return if (idx != -1) idx + 1 else -1
  }

  private fun findTopLevelString(str: String, target: String): Int {
    var inQuotes = false
    var quoteChar = ' '
    var depth = 0
    var i = 0
    while (i <= str.length - target.length) {
      val c = str[i]
      if (inQuotes) {
        if (c == quoteChar) inQuotes = false
      } else {
        if (c == '\'' || c == '"') {
          inQuotes = true
          quoteChar = c
        } else if (c in "([{") depth++
        else if (c in ")]}") depth--
        else if (depth == 0 && str.regionMatches(i, target, 0, target.length)) {
          return i
        }
      }
      i++
    }
    return -1
  }

  private fun findTopLevelColon(str: String): Int {
    var inQuotes = false
    var quoteChar = ' '
    var depth = 0
    for (i in str.indices) {
      val c = str[i]
      if (inQuotes) {
        if (c == quoteChar) inQuotes = false
      } else {
        if (c == '\'' || c == '"') {
          inQuotes = true
          quoteChar = c
        } else if (c in "([{") depth++
        else if (c in ")]}") depth--
        else if (depth == 0 && c == ':') return i
      }
    }
    return -1
  }

  private fun splitTopLevel(str: String, delimiter: Char): List<String> {
    val result = mutableListOf<String>()
    var inQuotes = false
    var quoteChar = ' '
    var depth = 0
    var start = 0
    for (i in str.indices) {
      val c = str[i]
      if (inQuotes) {
        if (c == quoteChar) inQuotes = false
      } else {
        if (c == '\'' || c == '"') {
          inQuotes = true
          quoteChar = c
        } else if (c in "([{") depth++
        else if (c in ")]}") depth--
        else if (depth == 0 && c == delimiter) {
          result.add(str.substring(start, i).trim())
          start = i + 1
        }
      }
    }
    if (start < str.length) {
      result.add(str.substring(start).trim())
    }
    return result
  }

  private fun findMatchingBracketStart(str: String): Int {
    var depth = 0
    for (i in str.indices.reversed()) {
      val c = str[i]
      if (c == ']') depth++
      else if (c == '[') {
        depth--
        if (depth == 0) return i
      }
    }
    return -1
  }

  private fun findMatchingParenStart(str: String): Int {
    var depth = 0
    for (i in str.indices.reversed()) {
      val c = str[i]
      if (c == ')') depth++
      else if (c == '(') {
        depth--
        if (depth == 0) return i
      }
    }
    return -1
  }

  private fun getBlockRange(lines: List<String>, startIdx: Int, maxIdx: Int, parentIndent: Int): Pair<Int, Int> {
    var end = startIdx
    while (end < maxIdx) {
      val line = lines[end]
      if (line.trim().isEmpty() || line.trim().startsWith("#")) {
        end++
        continue
      }
      val ind = getIndentation(line)
      if (ind <= parentIndent) break
      end++
    }
    return Pair(startIdx, end)
  }

  private fun collectConditionalBranches(
    lines: List<String>,
    startIdx: Int,
    maxIdx: Int,
    baseIndent: Int
  ): List<ConditionalBranch> {
    val branches = mutableListOf<ConditionalBranch>()
    var curr = startIdx
    var first = true

    while (curr < maxIdx) {
      val line = lines[curr]
      if (line.trim().isEmpty() || line.trim().startsWith("#")) {
        curr++
        continue
      }
      val ind = getIndentation(line)
      if (ind != baseIndent && !first) break

      val trimmed = line.trim()
      if (first && trimmed.startsWith("if ") && trimmed.endsWith(":")) {
        val cond = trimmed.removePrefix("if ").removeSuffix(":").trim()
        val range = getBlockRange(lines, curr + 1, maxIdx, baseIndent)
        branches.add(ConditionalBranch(cond, range.first, range.second))
        curr = range.second
        first = false
      } else if (!first && trimmed.startsWith("elif ") && trimmed.endsWith(":")) {
        val cond = trimmed.removePrefix("elif ").removeSuffix(":").trim()
        val range = getBlockRange(lines, curr + 1, maxIdx, baseIndent)
        branches.add(ConditionalBranch(cond, range.first, range.second))
        curr = range.second
      } else if (!first && trimmed == "else:") {
        val range = getBlockRange(lines, curr + 1, maxIdx, baseIndent)
        branches.add(ConditionalBranch(null, range.first, range.second))
        curr = range.second
        break
      } else {
        break
      }
    }
    return branches
  }
}

data class ConditionalBranch(
  val conditionExpr: String?,
  val startLine: Int,
  val endLine: Int
)

sealed class ControlSignal {
  object None : ControlSignal()
  object Break : ControlSignal()
  object Continue : ControlSignal()
  data class Return(val value: Any?) : ControlSignal()
}

class PythonException(
  val type: String,
  override val message: String,
  val lineNumber: Int = 1
) : Exception(message)

class BuiltinFunction(val name: String, val block: (List<Any?>) -> Any?)

class UserFunction(
  val name: String,
  val params: List<String>,
  val bodyLines: List<String>,
  val closureScope: Map<String, Any?>
) {
  fun call(args: List<Any?>, interpreter: PythonInterpreter): Any? {
    val localScope = closureScope.toMutableMap()
    for (i in params.indices) {
      localScope[params[i]] = args.getOrNull(i)
    }
    val signal = interpreter.executeBlock(bodyLines, 0, bodyLines.size, 1, localScope)
    return if (signal is ControlSignal.Return) signal.value else null
  }
}
