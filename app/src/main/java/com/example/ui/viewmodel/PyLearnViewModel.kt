package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.compiler.ExecutionResult
import com.example.compiler.PythonInterpreter
import com.example.data.db.PyLearnDatabase
import com.example.data.db.SavedSnippetEntity
import com.example.data.db.UserProgressEntity
import com.example.data.model.*
import com.example.data.repository.PyLearnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
  LEARN("Learn"),
  CHALLENGES("Challenges"),
  COMPILER("Compiler"),
  REFERENCE("Reference"),
  PROFILE("Profile")
}

class PyLearnViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: PyLearnRepository
  private val interpreter = PythonInterpreter()

  init {
    val db = PyLearnDatabase.getDatabase(application)
    repository = PyLearnRepository(db.userProgressDao(), db.savedSnippetDao())
  }

  // Navigation
  private val _currentTab = MutableStateFlow(AppTab.LEARN)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
  }

  // Progress from DB
  val userProgress: StateFlow<UserProgressEntity> = repository.userProgress.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = UserProgressEntity(id = 1)
  )

  val savedSnippets: StateFlow<List<SavedSnippetEntity>> = repository.savedSnippets.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Curriculum & Lessons
  private val _selectedLesson = MutableStateFlow<Lesson?>(null)
  val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

  private val _selectedLessonLevel = MutableStateFlow<CourseLevel?>(null)
  val selectedLessonLevel: StateFlow<CourseLevel?> = _selectedLessonLevel.asStateFlow()

  private val _quizSelectedOption = MutableStateFlow<Int?>(null)
  val quizSelectedOption: StateFlow<Int?> = _quizSelectedOption.asStateFlow()

  private val _isQuizSubmitted = MutableStateFlow(false)
  val isQuizSubmitted: StateFlow<Boolean> = _isQuizSubmitted.asStateFlow()

  fun selectLesson(lesson: Lesson?) {
    _selectedLesson.value = lesson
    _quizSelectedOption.value = null
    _isQuizSubmitted.value = false
  }

  fun setLessonLevelFilter(level: CourseLevel?) {
    _selectedLessonLevel.value = level
  }

  fun selectQuizOption(index: Int) {
    if (!_isQuizSubmitted.value) {
      _quizSelectedOption.value = index
    }
  }

  fun submitQuiz(lesson: Lesson) {
    if (_quizSelectedOption.value != null) {
      _isQuizSubmitted.value = true
      if (_quizSelectedOption.value == lesson.quiz.correctIndex) {
        viewModelScope.launch {
          repository.completeLesson(lesson.id, xp = 50)
        }
      }
    }
  }

  fun markLessonCompleted(lessonId: String) {
    viewModelScope.launch {
      repository.completeLesson(lessonId, xp = 50)
    }
  }

  fun toggleBookmark(lessonId: String) {
    viewModelScope.launch {
      repository.toggleBookmark(lessonId)
    }
  }

  // Challenges
  private val _selectedChallenge = MutableStateFlow<Challenge?>(null)
  val selectedChallenge: StateFlow<Challenge?> = _selectedChallenge.asStateFlow()

  private val _challengeDifficultyFilter = MutableStateFlow<Difficulty?>(null)
  val challengeDifficultyFilter: StateFlow<Difficulty?> = _challengeDifficultyFilter.asStateFlow()

  private val _challengeCode = MutableStateFlow("")
  val challengeCode: StateFlow<String> = _challengeCode.asStateFlow()

  private val _isRunningTests = MutableStateFlow(false)
  val isRunningTests: StateFlow<Boolean> = _isRunningTests.asStateFlow()

  private val _testResults = MutableStateFlow<List<TestResult>>(emptyList())
  val testResults: StateFlow<List<TestResult>> = _testResults.asStateFlow()

  private val _allTestsPassed = MutableStateFlow(false)
  val allTestsPassed: StateFlow<Boolean> = _allTestsPassed.asStateFlow()

  private val _showChallengeHint = MutableStateFlow(false)
  val showChallengeHint: StateFlow<Boolean> = _showChallengeHint.asStateFlow()

  private val _showChallengeSolution = MutableStateFlow(false)
  val showChallengeSolution: StateFlow<Boolean> = _showChallengeSolution.asStateFlow()

  fun selectChallenge(challenge: Challenge?) {
    _selectedChallenge.value = challenge
    _challengeCode.value = challenge?.starterCode ?: ""
    _testResults.value = emptyList()
    _allTestsPassed.value = false
    _showChallengeHint.value = false
    _showChallengeSolution.value = false
  }

  fun setChallengeDifficultyFilter(diff: Difficulty?) {
    _challengeDifficultyFilter.value = diff
  }

  fun updateChallengeCode(newCode: String) {
    _challengeCode.value = newCode
  }

  fun toggleChallengeHint() {
    _showChallengeHint.value = !_showChallengeHint.value
  }

  fun toggleChallengeSolution() {
    _showChallengeSolution.value = !_showChallengeSolution.value
  }

  fun runChallengeTests() {
    val challenge = _selectedChallenge.value ?: return
    viewModelScope.launch {
      _isRunningTests.value = true
      val results = mutableListOf<TestResult>()
      var allPass = true

      for (tc in challenge.testCases) {
        try {
          // Prepare wrapper code to call user's function with arguments
          val testScript = """
${_challengeCode.value}
_res = ${challenge.functionName}(${tc.input})
print(repr(_res))
""".trimIndent()

          val execRes = interpreter.execute(testScript)
          if (execRes.isSuccess) {
            val output = execRes.stdout.trim()
            val expected = tc.expectedOutput.trim()

            // Compare output format
            val passed = outputsMatch(output, expected)
            if (!passed) allPass = false
            results.add(TestResult(tc, output, passed))
          } else {
            allPass = false
            results.add(TestResult(tc, "Runtime Error", false, execRes.errorMessage))
          }
        } catch (e: Exception) {
          allPass = false
          results.add(TestResult(tc, "Error", false, e.message))
        }
      }

      _testResults.value = results
      _allTestsPassed.value = allPass
      _isRunningTests.value = false

      if (allPass) {
        repository.completeChallenge(challenge.id, challenge.xpReward)
      }
    }
  }

  private fun outputsMatch(actual: String, expected: String): Boolean {
    val a = actual.replace("'", "\"").trim()
    val e = expected.replace("'", "\"").trim()
    if (a == e) return true
    // Try number compare
    val an = a.toDoubleOrNull()
    val en = e.toDoubleOrNull()
    if (an != null && en != null) return an == en
    return false
  }

  // Compiler Playground
  private val _compilerCode = MutableStateFlow(TemplatesAndReferenceData.templates.first().code)
  val compilerCode: StateFlow<String> = _compilerCode.asStateFlow()

  private val _compilerInput = MutableStateFlow("")
  val compilerInput: StateFlow<String> = _compilerInput.asStateFlow()

  private val _isExecuting = MutableStateFlow(false)
  val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

  private val _executionResult = MutableStateFlow<ExecutionResult?>(null)
  val executionResult: StateFlow<ExecutionResult?> = _executionResult.asStateFlow()

  private val _aiExplanation = MutableStateFlow<String?>(null)
  val aiExplanation: StateFlow<String?> = _aiExplanation.asStateFlow()

  private val _isExplaining = MutableStateFlow(false)
  val isExplaining: StateFlow<Boolean> = _isExplaining.asStateFlow()

  fun updateCompilerCode(code: String) {
    _compilerCode.value = code
  }

  fun updateCompilerInput(input: String) {
    _compilerInput.value = input
  }

  fun loadTemplate(template: CodeTemplate) {
    _compilerCode.value = template.code
    _executionResult.value = null
    _aiExplanation.value = null
  }

  fun loadSnippetIntoCompiler(code: String) {
    _compilerCode.value = code
    _executionResult.value = null
    _aiExplanation.value = null
    _currentTab.value = AppTab.COMPILER
  }

  fun loadLessonCodeIntoCompiler(code: String) {
    _compilerCode.value = code
    _executionResult.value = null
    _aiExplanation.value = null
    _selectedLesson.value = null
    _currentTab.value = AppTab.COMPILER
  }

  fun runCompilerCode() {
    viewModelScope.launch {
      _isExecuting.value = true
      _aiExplanation.value = null
      val inputs = _compilerInput.value.lines().filter { it.isNotBlank() }
      val result = interpreter.execute(_compilerCode.value, inputs)
      _executionResult.value = result
      _isExecuting.value = false
      repository.recordCodeExecution()
    }
  }

  fun clearCompilerOutput() {
    _executionResult.value = null
    _aiExplanation.value = null
  }

  fun explainWithAi() {
    val result = _executionResult.value ?: return
    viewModelScope.launch {
      _isExplaining.value = true
      // Generate clear, structured AI tutor diagnostic
      val explanation = if (result.isSuccess) {
        """
### Code Analysis & Execution Summary
- **Status**: Code ran successfully in ${result.executionTimeMs} ms.
- **Variables in Scope**: ${if (result.variables.isEmpty()) "None" else result.variables.entries.joinToString(", ") { "${it.key} = ${it.value}" }}
- **Output**:
```
${result.stdout.ifBlank { "(No output printed. Use print() to display values)" }}
```
**Tutor Insight**: Your code executed cleanly with no syntax errors. Great job structuring your Python program!
        """.trimIndent()
      } else {
        """
### Error Diagnostic & Debugging Assistant
- **Error Type**: ${result.errorType ?: "Syntax/Runtime Error"}
- **Line Number**: Line ${result.errorLine ?: 1}
- **What Happened**:
${result.errorMessage}

### How to Fix It:
1. Double-check line ${result.errorLine ?: 1} for missing colons `:`, correct indentation (4 spaces), or unmatched quotes/parentheses.
2. If accessing lists or dictionaries, verify that the index or key actually exists.
3. Ensure all variable and function names are spelled identically to their definitions.
        """.trimIndent()
      }
      _aiExplanation.value = explanation
      _isExplaining.value = false
    }
  }

  fun dismissAiExplanation() {
    _aiExplanation.value = null
  }

  fun saveCurrentSnippet(title: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      repository.saveSnippet(title.trim(), _compilerCode.value)
    }
  }

  fun deleteSavedSnippet(id: Int) {
    viewModelScope.launch {
      repository.deleteSnippet(id)
    }
  }

  // Cheat Sheet / Reference Search
  private val _referenceSearch = MutableStateFlow("")
  val referenceSearch: StateFlow<String> = _referenceSearch.asStateFlow()

  private val _selectedReferenceCategory = MutableStateFlow<String?>(null)
  val selectedReferenceCategory: StateFlow<String?> = _selectedReferenceCategory.asStateFlow()

  fun updateReferenceSearch(query: String) {
    _referenceSearch.value = query
  }

  fun selectReferenceCategory(category: String?) {
    _selectedReferenceCategory.value = category
  }
}
