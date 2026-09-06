package com.example.data.model

object ChallengesData {

  val challenges: List<Challenge> = listOf(
    // ---------------- EASY ----------------
    Challenge(
      id = "ch_reverse_string",
      title = "Reverse a String",
      difficulty = Difficulty.EASY,
      category = "Strings",
      description = "Write a function `reverse_string(s)` that takes a string `s` and returns the reversed string.",
      examples = listOf(
        "\"python\"" to "\"nohtyp\"",
        "\"code\"" to "\"edoc\"",
        "\"racecar\"" to "\"racecar\""
      ),
      starterCode = """def reverse_string(s):
    # Your code here
    return s
""",
      functionName = "reverse_string",
      testCases = listOf(
        TestCase("\"python\"", "\"nohtyp\"", "Reverses 'python'"),
        TestCase("\"hello\"", "\"olleh\"", "Reverses 'hello'"),
        TestCase("\"racecar\"", "\"racecar\"", "Palindrome remains identical"),
        TestCase("\"a\"", "\"a\"", "Single character string")
      ),
      hints = listOf(
        "Python string slicing allows a step argument: string[start:stop:step].",
        "A step of -1 traverses backwards!"
      ),
      solutionCode = """def reverse_string(s):
    return s[::-1]
""",
      solutionExplanation = "Using string slicing with a negative step `s[::-1]` is the idiomatic O(n) Python way to reverse any sequence.",
      xpReward = 100
    ),

    Challenge(
      id = "ch_is_palindrome",
      title = "Palindrome Checker",
      difficulty = Difficulty.EASY,
      category = "Strings",
      description = "Write a function `is_palindrome(s)` that returns `True` if the string reads the same forwards and backwards (case-insensitive), or `False` otherwise.",
      examples = listOf(
        "\"Kayak\"" to "True",
        "\"hello\"" to "False",
        "\"Madam\"" to "True"
      ),
      starterCode = """def is_palindrome(s):
    # Convert to lowercase and check symmetry
    return False
""",
      functionName = "is_palindrome",
      testCases = listOf(
        TestCase("\"Kayak\"", "True", "Checks case-insensitive 'Kayak'"),
        TestCase("\"hello\"", "False", "Checks non-palindrome 'hello'"),
        TestCase("\"Madam\"", "True", "Checks 'Madam'"),
        TestCase("\"noon\"", "True", "Checks 'noon'")
      ),
      hints = listOf(
        "First convert the string to all lowercase using .lower().",
        "Compare the lowercase string with its reversed slice [::-1]."
      ),
      solutionCode = """def is_palindrome(s):
    cleaned = s.lower()
    return cleaned == cleaned[::-1]
""",
      solutionExplanation = "Convert the string to lowercase with `s.lower()`, then check if it equals its reverse `s[::-1]`.",
      xpReward = 100
    ),

    Challenge(
      id = "ch_count_vowels",
      title = "Count the Vowels",
      difficulty = Difficulty.EASY,
      category = "Strings & Loops",
      description = "Write a function `count_vowels(text)` that returns the total count of vowels (a, e, i, o, u, case-insensitive) in the given text.",
      examples = listOf(
        "\"Python\"" to "1",
        "\"Artificial Intelligence\"" to "10",
        "\"rhythm\"" to "0"
      ),
      starterCode = """def count_vowels(text):
    count = 0
    # Your code here
    return count
""",
      functionName = "count_vowels",
      testCases = listOf(
        TestCase("\"Python\"", "1", "Vowel in 'Python' ('o')"),
        TestCase("\"hello\"", "2", "Vowels in 'hello' ('e', 'o')"),
        TestCase("\"sky\"", "0", "Zero vowels in 'sky'"),
        TestCase("\"AEIOU\"", "5", "All capital vowels")
      ),
      hints = listOf(
        "You can define a string of vowels: `vowels = 'aeiou'`.",
        "Loop through each character in `text.lower()` and check `if char in vowels`."
      ),
      solutionCode = """def count_vowels(text):
    vowels = "aeiou"
    count = 0
    for char in text.lower():
        if char in vowels:
            count += 1
    return count
""",
      solutionExplanation = "Iterate through the lowercase string and increment a counter whenever a character is found in `'aeiou'`.",
      xpReward = 100
    ),

    Challenge(
      id = "ch_fizzbuzz",
      title = "FizzBuzz Classic",
      difficulty = Difficulty.EASY,
      category = "Conditionals",
      description = "Write a function `fizzbuzz(n)` that returns a list of strings from 1 to `n`: multiples of 3 are \"Fizz\", multiples of 5 are \"Buzz\", multiples of both are \"FizzBuzz\", otherwise the number as a string.",
      examples = listOf(
        "5" to "[\"1\", \"2\", \"Fizz\", \"4\", \"Buzz\"]"
      ),
      starterCode = """def fizzbuzz(n):
    result = []
    # Loop from 1 to n
    return result
""",
      functionName = "fizzbuzz",
      testCases = listOf(
        TestCase("5", "[\"1\", \"2\", \"Fizz\", \"4\", \"Buzz\"]", "First 5 numbers"),
        TestCase("15", "[\"1\", \"2\", \"Fizz\", \"4\", \"Buzz\", \"Fizz\", \"7\", \"8\", \"Fizz\", \"Buzz\", \"11\", \"Fizz\", \"13\", \"14\", \"FizzBuzz\"]", "Up to 15 (includes FizzBuzz)")
      ),
      hints = listOf(
        "Check for multiples of both 3 and 5 first (or i % 15 == 0) before checking 3 or 5 individually."
      ),
      solutionCode = """def fizzbuzz(n):
    result = []
    for i in range(1, n + 1):
        if i % 15 == 0:
            result.append("FizzBuzz")
        elif i % 3 == 0:
            result.append("Fizz")
        elif i % 5 == 0:
            result.append("Buzz")
        else:
            result.append(str(i))
    return result
""",
      solutionExplanation = "By checking `i % 15 == 0` first, we handle common multiples before individual branches.",
      xpReward = 120
    ),

    // ---------------- MEDIUM ----------------
    Challenge(
      id = "ch_two_sum",
      title = "Two Sum",
      difficulty = Difficulty.MEDIUM,
      category = "Algorithms & Dicts",
      description = "Given a list of integers `nums` and an integer `target`, return the indices of the two numbers that add up to `target`. Assume exactly one solution exists.",
      examples = listOf(
        "[2, 7, 11, 15], 9" to "[0, 1]",
        "[3, 2, 4], 6" to "[1, 2]"
      ),
      starterCode = """def two_sum(nums, target):
    # Use a dictionary for O(n) lookup
    seen = {}
    return []
""",
      functionName = "two_sum",
      testCases = listOf(
        TestCase("[2, 7, 11, 15], 9", "[0, 1]", "Adds 2 and 7 to reach 9"),
        TestCase("[3, 2, 4], 6", "[1, 2]", "Adds 2 and 4 to reach 6"),
        TestCase("[3, 3], 6", "[0, 1]", "Duplicate elements")
      ),
      hints = listOf(
        "For each number `n`, calculate its complement `comp = target - n`.",
        "Store the index of each visited number in a hash map `seen[n] = index`."
      ),
      solutionCode = """def two_sum(nums, target):
    seen = {}
    for i, n in enumerate(nums):
        diff = target - n
        if diff in seen:
            return [seen[diff], i]
        seen[n] = i
    return []
""",
      solutionExplanation = "Using a hash table enables finding the complementary number in O(1) time, yielding total O(n) runtime.",
      xpReward = 200
    ),

    Challenge(
      id = "ch_find_second_largest",
      title = "Find Second Largest",
      difficulty = Difficulty.MEDIUM,
      category = "Lists & Logic",
      description = "Write a function `second_largest(nums)` that returns the second largest distinct number in a list of integers.",
      examples = listOf(
        "[10, 20, 4, 45, 99]" to "45",
        "[5, 5, 4, 2]" to "4"
      ),
      starterCode = """def second_largest(nums):
    # Remove duplicates and find second max
    return 0
""",
      functionName = "second_largest",
      testCases = listOf(
        TestCase("[10, 20, 4, 45, 99]", "45", "Standard list"),
        TestCase("[5, 5, 4, 2]", "4", "Handles duplicate maximums"),
        TestCase("[100, 200]", "100", "Two items")
      ),
      hints = listOf(
        "Convert the list to a `set()` to remove duplicates, then sort the unique values."
      ),
      solutionCode = """def second_largest(nums):
    unique = list(set(nums))
    unique.sort()
    return unique[-2]
""",
      solutionExplanation = "`set(nums)` drops duplicates. After sorting ascending, index `[-2]` points directly to the second largest distinct number.",
      xpReward = 200
    ),

    Challenge(
      id = "ch_frequency_counter",
      title = "Word Frequency Counter",
      difficulty = Difficulty.MEDIUM,
      category = "Dictionaries",
      description = "Write a function `word_frequencies(words)` that takes a list of words and returns a dictionary mapping each word to its occurrence count.",
      examples = listOf(
        "[\"apple\", \"banana\", \"apple\", \"apple\", \"banana\"]" to "{\"apple\": 3, \"banana\": 2}"
      ),
      starterCode = """def word_frequencies(words):
    counts = {}
    # Count occurrences
    return counts
""",
      functionName = "word_frequencies",
      testCases = listOf(
        TestCase("[\"apple\", \"banana\", \"apple\", \"apple\", \"banana\"]", "{\"apple\": 3, \"banana\": 2}", "Repeated fruits"),
        TestCase("[\"code\", \"python\", \"code\"]", "{\"code\": 2, \"python\": 1}", "Tech terms")
      ),
      hints = listOf(
        "Use `counts.get(w, 0) + 1` to safely update counts in a dictionary."
      ),
      solutionCode = """def word_frequencies(words):
    counts = {}
    for w in words:
        counts[w] = counts.get(w, 0) + 1
    return counts
""",
      solutionExplanation = "`dict.get(key, 0)` provides a default of 0 when a key is first seen, allowing concise accumulation.",
      xpReward = 200
    ),

    // ---------------- HARD ----------------
    Challenge(
      id = "ch_fibonacci_recursive",
      title = "Fibonacci Generator",
      difficulty = Difficulty.HARD,
      category = "Recursion & DP",
      description = "Write a function `fibonacci(n)` that returns the n-th Fibonacci number (where fib(0)=0, fib(1)=1, fib(2)=1, fib(3)=2, ...).",
      examples = listOf(
        "0" to "0",
        "1" to "1",
        "7" to "13",
        "10" to "55"
      ),
      starterCode = """def fibonacci(n):
    # Compute n-th fibonacci number
    return 0
""",
      functionName = "fibonacci",
      testCases = listOf(
        TestCase("0", "0", "Base case 0"),
        TestCase("1", "1", "Base case 1"),
        TestCase("7", "13", "Fib(7) = 13"),
        TestCase("10", "55", "Fib(10) = 55")
      ),
      hints = listOf(
        "Use an iterative approach with two variables (a, b = 0, 1) for fast O(n) computation."
      ),
      solutionCode = """def fibonacci(n):
    if n <= 0:
        return 0
    if n == 1:
        return 1
    a, b = 0, 1
    for _ in range(2, n + 1):
        a, b = b, a + b
    return b
""",
      solutionExplanation = "Iterative tracking with two state variables computes the n-th Fibonacci in O(n) time and O(1) memory without stack overflow.",
      xpReward = 350
    ),

    Challenge(
      id = "ch_flatten_list",
      title = "Flatten Nested List",
      difficulty = Difficulty.HARD,
      category = "Recursion",
      description = "Write a function `flatten(nested)` that recursively flattens a nested list of arbitrary depth into a single flat list.",
      examples = listOf(
        "[1, [2, 3], [4, [5, 6]]]" to "[1, 2, 3, 4, 5, 6]"
      ),
      starterCode = """def flatten(nested):
    result = []
    # Handle both integers and inner lists recursively
    return result
""",
      functionName = "flatten",
      testCases = listOf(
        TestCase("[1, [2, 3], [4, [5, 6]]]", "[1, 2, 3, 4, 5, 6]", "Deeply nested lists"),
        TestCase("[[1, 2], [3, 4]]", "[1, 2, 3, 4]", "Matrix format"),
        TestCase("[1, 2, 3]", "[1, 2, 3]", "Already flat")
      ),
      hints = listOf(
        "Loop through elements. If `type(item) == list`, recursively call `flatten(item)` and extend; otherwise append."
      ),
      solutionCode = """def flatten(nested):
    res = []
    for item in nested:
        if type(item) == list:
            res.extend(flatten(item))
        else:
            res.append(item)
    return res
""",
      solutionExplanation = "Recursive branching checks if each element is a list. If so, it recurses and flattens deeper levels before merging.",
      xpReward = 350
    )
  )
}
