package com.example.data.model

object TemplatesAndReferenceData {

  val templates: List<CodeTemplate> = listOf(
    CodeTemplate(
      id = "tmpl_hello",
      title = "Hello Python World",
      category = "Basics",
      description = "Simple greeting script demonstrating variables and f-strings.",
      code = """# Welcome to PyLearn Built-in Compiler!
name = "Pythonista"
version = "3.12"
print(f"Hello, {name}!")
print(f"Running on Python {version} Interactive Sandbox")

# Calculate arithmetic
numbers = [1, 2, 3, 4, 5]
print("Sum of numbers:", sum(numbers))
print("Max value:", max(numbers))
"""
    ),

    CodeTemplate(
      id = "tmpl_fibonacci",
      title = "Fibonacci Sequence",
      category = "Algorithms",
      description = "Generate Fibonacci numbers using iterative state tracking.",
      code = """# Fibonacci Sequence Generator
def generate_fibonacci(count):
    series = []
    a, b = 0, 1
    for _ in range(count):
        series.append(a)
        a, b = b, a + b
    return series

terms = 10
result = generate_fibonacci(terms)
print(f"First {terms} Fibonacci numbers:")
print(result)
"""
    ),

    CodeTemplate(
      id = "tmpl_binary_search",
      title = "Binary Search",
      category = "Algorithms",
      description = "Efficient O(log n) search in a sorted array.",
      code = """# Binary Search Algorithm O(log n)
def binary_search(arr, target):
    left = 0
    right = len(arr) - 1

    while left <= right:
        mid = (left + right) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1

data = [3, 9, 14, 19, 25, 33, 42, 56, 78, 90]
search_target = 42

index = binary_search(data, search_target)
print("Sorted Array:", data)
print(f"Target {search_target} found at index: {index}")
"""
    ),

    CodeTemplate(
      id = "tmpl_oop_bank",
      title = "OOP Bank Account",
      category = "Object-Oriented",
      description = "Object-Oriented Programming with encapsulation and methods.",
      code = """# Object-Oriented Programming (OOP)
class BankAccount:
    def __init__(self, owner, balance=0.0):
        self.owner = owner
        self.balance = balance

    def deposit(self, amount):
        if amount > 0:
            self.balance += amount
            print(f"Deposited ${'$'}{amount}. New balance: ${'$'}{self.balance}")
        return self.balance

    def withdraw(self, amount):
        if amount <= self.balance:
            self.balance -= amount
            print(f"Withdrew ${'$'}{amount}. Remaining: ${'$'}{self.balance}")
            return True
        else:
            print(f"Declined! Insufficient funds for ${'$'}{amount} withdrawal.")
            return False

# Test the class
account = BankAccount("Ada Lovelace", 500.0)
account.deposit(250.0)
account.withdraw(120.0)
account.withdraw(900.0) # Insufficient funds
"""
    ),

    CodeTemplate(
      id = "tmpl_matrix",
      title = "Matrix Transposition",
      category = "Data Structures",
      description = "Transpose a 2D matrix using list comprehension.",
      code = """# Matrix Transposition with Comprehensions
matrix = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9]
]

print("Original Matrix:")
for row in matrix:
    print(row)

# Transpose matrix: rows become columns
transposed = [[row[i] for row in matrix] for i in range(len(matrix[0]))]

print("\nTransposed Matrix:")
for row in transposed:
    print(row)
"""
    ),

    CodeTemplate(
      id = "tmpl_prime_sieve",
      title = "Prime Numbers Sieve",
      category = "Math & Logic",
      description = "Find all prime numbers up to N.",
      code = """# Prime Numbers Finder
def find_primes(limit):
    primes = []
    for num in range(2, limit + 1):
        is_prime = True
        for divisor in range(2, int(num ** 0.5) + 1):
            if num % divisor == 0:
                is_prime = False
                break
        if is_prime:
            primes.append(num)
    return primes

n = 50
result = find_primes(n)
print(f"Prime numbers up to {n}:")
print(result)
print(f"Total count: {len(result)}")
"""
    )
  )

  val cheatSheetItems: List<CheatSheetItem> = listOf(
    CheatSheetItem(
      id = "cs_str_split",
      category = "Strings",
      title = "str.split(sep)",
      syntax = "text.split(delimiter)",
      example = "\"a,b,c\".split(\",\") # ['a', 'b', 'c']",
      explanation = "Splits a string by delimiter and returns a list of tokens."
    ),
    CheatSheetItem(
      id = "cs_str_join",
      category = "Strings",
      title = "str.join(iterable)",
      syntax = "delimiter.join(list)",
      example = "\"-\".join(['2026', '09', '15']) # '2026-09-15'",
      explanation = "Concatenates elements of an iterable using the string as separator."
    ),
    CheatSheetItem(
      id = "cs_str_slice",
      category = "Strings",
      title = "String Slicing",
      syntax = "s[start:stop:step]",
      example = "s = 'Python'\ns[0:2] # 'Py'\ns[::-1] # 'nohtyP'",
      explanation = "Extracts sub-strings using index boundaries and optional step direction."
    ),
    CheatSheetItem(
      id = "cs_list_comp",
      category = "Lists",
      title = "List Comprehension",
      syntax = "[expr for item in iterable if cond]",
      example = "[x**2 for x in range(5) if x % 2 == 0] # [0, 4, 16]",
      explanation = "Compact syntax to filter and transform iterables into a new list."
    ),
    CheatSheetItem(
      id = "cs_list_append",
      category = "Lists",
      title = "list.append(x)",
      syntax = "my_list.append(item)",
      example = "items = [1, 2]\nitems.append(3) # [1, 2, 3]",
      explanation = "Appends an element to the end of the list in O(1) time."
    ),
    CheatSheetItem(
      id = "cs_list_pop",
      category = "Lists",
      title = "list.pop([i])",
      syntax = "my_list.pop()",
      example = "nums = [10, 20, 30]\nval = nums.pop() # val=30, nums=[10, 20]",
      explanation = "Removes and returns item at index (defaulting to the last element)."
    ),
    CheatSheetItem(
      id = "cs_dict_get",
      category = "Dictionaries",
      title = "dict.get(key, default)",
      syntax = "my_dict.get(key, default_value)",
      example = "user = {'name': 'Sam'}\nage = user.get('age', 18) # 18",
      explanation = "Safely returns value for key, or fallback default if key does not exist."
    ),
    CheatSheetItem(
      id = "cs_dict_items",
      category = "Dictionaries",
      title = "dict.items()",
      syntax = "for k, v in my_dict.items():",
      example = "for key, val in {'a': 1, 'b': 2}.items():\n    print(key, val)",
      explanation = "Returns key-value tuple pairs suitable for unpacked dictionary iteration."
    ),
    CheatSheetItem(
      id = "cs_set_ops",
      category = "Sets",
      title = "Set Deduplication",
      syntax = "unique_items = set(list_with_dupes)",
      example = "set([1, 2, 2, 3, 1]) # {1, 2, 3}",
      explanation = "Converts iterable to a set, removing all duplicate values instantly."
    ),
    CheatSheetItem(
      id = "cs_func_lambda",
      category = "Functions",
      title = "Lambda Expression",
      syntax = "lambda arg1, arg2: expression",
      example = "multiply = lambda x, y: x * y\nmultiply(3, 4) # 12",
      explanation = "Creates an anonymous inline single-expression function."
    ),
    CheatSheetItem(
      id = "cs_oop_init",
      category = "OOP",
      title = "Class Constructor",
      syntax = "def __init__(self, ...):",
      example = "class Point:\n    def __init__(self, x, y):\n        self.x = x\n        self.y = y",
      explanation = "Constructor hook called when a new object instance is created."
    ),
    CheatSheetItem(
      id = "cs_builtins_enumerate",
      category = "Built-ins",
      title = "enumerate(iterable)",
      syntax = "for index, item in enumerate(list):",
      example = "for i, char in enumerate(['a', 'b']):\n    print(i, char)",
      explanation = "Yields (index, value) tuples from an iterable, eliminating manual counters."
    )
  )

  val achievements: List<Achievement> = listOf(
    Achievement("ach_first_run", "First Byte", "Execute your first script in the built-in compiler", "Terminal", 25),
    Achievement("ach_first_lesson", "Python Scholar", "Complete your first learning lesson", "School", 50),
    Achievement("ach_streak_3", "Consistency Champ", "Maintain a 3-day learning streak", "Whatshot", 100),
    Achievement("ach_beginner_complete", "Foundations Certified", "Complete all Beginner Python lessons", "WorkspacePremium", 200),
    Achievement("ach_first_challenge", "Bug Squasher", "Solve your first interactive code challenge", "BugReport", 75),
    Achievement("ach_challenge_master", "Code Ninja", "Solve 5 or more coding challenges", "SportsMartialArts", 250),
    Achievement("ach_advanced_graduate", "Python Guru", "Complete the Advanced Python curriculum", "MilitaryTech", 350)
  )
}
