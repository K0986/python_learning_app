package com.example.data.model

object CurriculumData {

  val lessons: List<Lesson> = listOf(
    // ---------------- BEGINNER ----------------
    Lesson(
      id = "py_intro",
      title = "Python Essentials & Syntax",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 6,
      summary = "Understand Python's philosophy, indentation-based syntax, comments, and printing output.",
      contentMarkdown = """
Python is a readable, high-level programming language designed for clarity and productivity.

### Key Syntax Rules
1. **No Semicolons**: Python statements end with a newline.
2. **Indentation is Significant**: Blocks of code (loops, functions, ifs) are defined by 4 spaces (or tabs), not curly braces `{}`.
3. **Comments**: Start with a `#` symbol and are ignored by the Python interpreter.
4. **Dynamic Typing**: You don't need to specify types when declaring variables.

```python
# This is a comment
message = "Welcome to Python!"
print(message)
```
      """.trimIndent(),
      codeExample = """# Python Basics
greeting = "Hello, Pythonista!"
print(greeting)

# Dynamic variables
year = 2026
print("Year is:", year)
print("Type of year is:", type(year))
""",
      quiz = QuizQuestion(
        question = "How does Python define blocks of code like functions or loops?",
        options = listOf("Curly braces { }", "Indentation (4 spaces)", "BEGIN and END tags", "Semicolons ;"),
        correctIndex = 1,
        explanation = "Python uses consistent indentation (typically 4 spaces) rather than curly braces to define code blocks."
      ),
      keyTakeaways = listOf(
        "Indentation determines block scope in Python.",
        "# denotes single-line comments.",
        "print() prints values to the console."
      )
    ),

    Lesson(
      id = "py_variables",
      title = "Variables & Data Types",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 8,
      summary = "Master integers, floats, strings, booleans, and type conversion.",
      contentMarkdown = """
Variables are containers for storing data values. In Python, a variable is created the moment you first assign a value to it.

### Core Primitive Types:
- **`int`**: Whole numbers like `42`, `-7`, `0`
- **`float`**: Numbers with decimals like `3.14159`, `-0.5`
- **`str`**: Text surrounded by single or double quotes
- **`bool`**: `True` or `False` (capitalized!)
- **`None`**: Represents the absence of a value

### Type Conversion (Casting):
```python
x = int("100")    # 100
y = float("3.14") # 3.14
s = str(42)       # "42"
```
      """.trimIndent(),
      codeExample = """# Variables & Type Conversion
score = 95
temperature = 98.6
is_online = True
username = "AdaLovelace"

print("Score:", score, type(score))
print("Temp:", temperature, type(temperature))
print("Online status:", is_online)

# Type casting
age_str = "24"
age_num = int(age_str)
print("Next year age:", age_num + 1)
""",
      quiz = QuizQuestion(
        question = "Which of the following is the correct boolean value in Python?",
        options = listOf("true", "TRUE", "True", "boolean(true)"),
        correctIndex = 2,
        explanation = "In Python, boolean literals are strictly capitalized: True and False."
      ),
      keyTakeaways = listOf(
        "Python has 4 primary scalar types: int, float, str, bool.",
        "Use type(x) to inspect the runtime type of any value.",
        "Use int(), float(), str(), bool() for type conversions."
      )
    ),

    Lesson(
      id = "py_strings",
      title = "Strings & F-Strings",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 10,
      summary = "Manipulate text, slice sequences, and master Python f-strings formatting.",
      contentMarkdown = """
Strings in Python are immutable sequences of characters.

### F-Strings (Formatted String Literals)
Prefix string with `f` and embed Python expressions in curly braces `{}`:
```python
name = "Guido"
age = 68
print(f"Name: {name}, in 5 years: {age + 5}")
```

### String Slicing: `string[start:stop:step]`
- `s[0]`: First character
- `s[-1]`: Last character
- `s[1:4]`: Characters from index 1 up to 3
- `s[::-1]`: Reverse the string!

### Common String Methods:
- `.lower()`, `.upper()`
- `.strip()` removes leading/trailing whitespace
- `.replace(old, new)`
- `.split(delimiter)` splits text into a list
- `.join(list)` joins elements into a single string
      """.trimIndent(),
      codeExample = """# F-Strings & String Slicing
first = "Python"
last = "Programming"
full = f"{first} {last}"

print("Full title:", full)
print("Uppercase:", full.upper())
print("First 3 chars:", full[:3])
print("Reversed:", first[::-1])

# String split and join
tags = "ai,python,mobile,coding"
tag_list = tags.split(",")
print("Tags list:", tag_list)
print("Joined with pipe:", " | ".join(tag_list))
""",
      quiz = QuizQuestion(
        question = "What does the slice expression word[::-1] do in Python?",
        options = listOf("Takes every 2nd letter", "Reverses the string", "Extracts the last letter only", "Throws an IndexError"),
        correctIndex = 1,
        explanation = "A step of -1 in slicing steps backwards from end to beginning, reversing the string."
      ),
      keyTakeaways = listOf(
        "F-strings f'...' allow inline expressions inside {}.",
        "Strings are immutable; methods return a modified copy.",
        "Negative indexing s[-1] counts from the right."
      )
    ),

    Lesson(
      id = "py_conditionals",
      title = "Conditionals & Logic",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 8,
      summary = "Control code execution using if, elif, else, and logical operators.",
      contentMarkdown = """
Control flow lets your program make decisions based on conditions.

### Conditional Syntax:
```python
if condition:
    # do something
elif another_condition:
    # do alternative
else:
    # fallback
```

### Logical Operators:
- `and`: Both conditions must be True
- `or`: At least one condition must be True
- `not`: Inverts boolean value

### Truthy & Falsy Values:
Falsy values: `0`, `0.0`, `""`, `[]`, `{}`, `None`, `False`. Everything else is truthy.
      """.trimIndent(),
      codeExample = """# Conditionals Example
grade = 88

if grade >= 90:
    letter = "A"
elif grade >= 80:
    letter = "B"
elif grade >= 70:
    letter = "C"
else:
    letter = "F"

print(f"Grade {grade} is letter: {letter}")

# Logical operators
is_member = True
cart_total = 45

if is_member and cart_total > 40:
    print("Eligible for Free Express Shipping!")
""",
      quiz = QuizQuestion(
        question = "Which keyword is used in Python for 'else if'?",
        options = listOf("else if", "elseif", "elif", "elsif"),
        correctIndex = 2,
        explanation = "Python uses the keyword 'elif' to chain secondary conditional checks."
      ),
      keyTakeaways = listOf(
        "Python uses 'if', 'elif', and 'else' for branching.",
        "Logical operators are written as English words: 'and', 'or', 'not'.",
        "Empty sequences and 0 evaluate to False (falsy)."
      )
    ),

    Lesson(
      id = "py_lists_tuples",
      title = "Lists & Tuples",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 10,
      summary = "Explore ordered collections: mutable lists vs immutable tuples.",
      contentMarkdown = """
### Lists: Mutable Ordered Collections
Created with square brackets `[]`.
- `.append(x)`: Adds `x` to the end
- `.pop()`: Removes and returns the last element
- `.insert(i, x)`: Inserts at index `i`
- `.sort()`: Sorts the list in-place

```python
numbers = [10, 20, 30]
numbers.append(40)
print(numbers[0]) # 10
```

### Tuples: Immutable Ordered Collections
Created with parentheses `()`.
Once created, elements cannot be modified, added, or removed. Useful for fixed records and dictionary keys.
```python
coordinates = (37.7749, -122.4194)
```
      """.trimIndent(),
      codeExample = """# Lists & Tuples Demo
fruits = ["apple", "banana", "cherry"]
fruits.append("mango")
print("All fruits:", fruits)
print("Total count:", len(fruits))

# Modification
fruits[1] = "blueberry"
print("After update:", fruits)

# Tuple example (immutable)
screen_res = (1920, 1080)
width, height = screen_res # unpacking
print(f"Resolution: {width} x {height}")
""",
      quiz = QuizQuestion(
        question = "What is the primary difference between a list and a tuple?",
        options = listOf("Lists are faster", "Lists are mutable, tuples are immutable", "Tuples cannot hold numbers", "Lists can only hold strings"),
        correctIndex = 1,
        explanation = "Lists can be modified after creation (mutable), while tuples cannot be altered once created (immutable)."
      ),
      keyTakeaways = listOf(
        "Lists use [] and are mutable (items can be updated/added).",
        "Tuples use () and are immutable.",
        "Both support indexing, slicing, and tuple unpacking."
      )
    ),

    Lesson(
      id = "py_loops",
      title = "Loops & Iteration",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 9,
      summary = "Automate repeated tasks with for loops, while loops, and range().",
      contentMarkdown = """
Python provides two main looping constructs:

### 1. `for` Loops
Iterate directly over collections (lists, strings, ranges):
```python
for item in ["a", "b", "c"]:
    print(item)
```

### 2. `range()` Function
Generates an arithmetic progression:
- `range(5)`: 0, 1, 2, 3, 4
- `range(1, 10, 2)`: 1, 3, 5, 7, 9

### 3. Loop Control
- `break`: Exits the loop immediately
- `continue`: Skips the rest of current iteration and moves to next
      """.trimIndent(),
      codeExample = """# For and While loops
print("Counting with range:")
for i in range(1, 6):
    print(f"Step {i}")

# Summing items
nums = [12, 45, 7, 23, 56]
total = 0
for n in nums:
    total += n
print("Total sum:", total)

# While loop with break
countdown = 5
while countdown > 0:
    print(f"T-minus {countdown}...")
    countdown -= 1
print("Liftoff!")
""",
      quiz = QuizQuestion(
        question = "What values does range(2, 8, 2) generate?",
        options = listOf("2, 3, 4, 5, 6, 7, 8", "2, 4, 6", "2, 4, 6, 8", "4, 6, 8"),
        correctIndex = 1,
        explanation = "range(start, stop, step) starts at 2, steps by 2, and stops before 8, producing: 2, 4, 6."
      ),
      keyTakeaways = listOf(
        "For loops in Python are 'for-each' loops over iterables.",
        "range(stop) stops one number before stop.",
        "Use break to terminate loops and continue to jump to the next cycle."
      )
    ),

    Lesson(
      id = "py_dicts_sets",
      title = "Dictionaries & Sets",
      level = CourseLevel.BEGINNER,
      estimatedMinutes = 10,
      summary = "Store key-value pairs with dictionaries and unique collections with sets.",
      contentMarkdown = """
### Dictionaries (`dict`)
Key-value stores optimized for fast lookups.
```python
student = {"name": "Sara", "gpa": 3.9, "major": "CS"}
print(student["name"])
student["year"] = 2026 # Add key
```

### Safe Access with `.get()`
`student.get("minor", "Undeclared")` avoids `KeyError` if key is missing!

### Sets (`set`)
Unordered collections of **unique** elements.
```python
unique_ids = {101, 102, 103, 101} # Duplicates removed: {101, 102, 103}
```
      """.trimIndent(),
      codeExample = """# Dictionary and Set demonstration
hero = {
    "name": "Tony Stark",
    "alias": "Iron Man",
    "armors": 85
}

print(f"{hero['alias']} is {hero['name']}")
hero["armors"] += 1
print("Updated armor count:", hero["armors"])

# Safe lookup
city = hero.get("city", "Malibu")
print("City:", city)

# Sets for deduplication
numbers = [1, 2, 2, 3, 4, 4, 4, 5]
unique_set = set(numbers)
print("Original:", numbers)
print("Unique values:", unique_set)
""",
      quiz = QuizQuestion(
        question = "What happens when you access a non-existent key with dict['missing'] versus dict.get('missing')?",
        options = listOf("Both return None", "dict['missing'] raises KeyError, .get() returns None", "Both raise KeyError", ".get() creates the key automatically"),
        correctIndex = 1,
        explanation = "Square bracket syntax raises a KeyError for missing keys, while .get() safely returns None (or a specified default value)."
      ),
      keyTakeaways = listOf(
        "Dictionaries map unique keys to values using hash tables.",
        "Use .get(key, default) for safe retrieval.",
        "Sets automatically deduplicate elements."
      )
    ),

    // ---------------- INTERMEDIATE ----------------
    Lesson(
      id = "py_functions",
      title = "Functions & Variable Scope",
      level = CourseLevel.INTERMEDIATE,
      estimatedMinutes = 12,
      summary = "Define reusable functions, parameters, return values, and understand local vs global scope.",
      contentMarkdown = """
Functions are blocks of code that run when called.

### Defining Functions:
```python
def greet(name, greeting="Hello"):
    return f"{greeting}, {name}!"
```

### Parameters & Default Values:
Default parameters must come **after** positional parameters.

### Returning Multiple Values:
Python functions can return multiple values as a tuple:
```python
def min_and_max(nums):
    return min(nums), max(nums)

lowest, highest = min_and_max([5, 2, 9, 1])
```
      """.trimIndent(),
      codeExample = """# Functions & Default Parameters
def calculate_price(amount, tax_rate=0.08, discount=0.0):
    subtotal = amount - discount
    total = subtotal * (1 + tax_rate)
    return round(total, 2)

p1 = calculate_price(100.0)
p2 = calculate_price(100.0, discount=10.0)
print("Standard price:", p1)
print("Discounted price:", p2)

# Multiple returns
def stats(values):
    return sum(values), len(values), round(sum(values) / len(values), 2)

total, count, avg = stats([80, 90, 100])
print(f"Total: {total}, Count: {count}, Average: {avg}")
""",
      quiz = QuizQuestion(
        question = "Can a Python function return more than one value?",
        options = listOf("No, only one value can be returned", "Yes, they are packed and returned as a tuple", "Only by using global variables", "Only in Python 3.12+"),
        correctIndex = 1,
        explanation = "Python packs multiple comma-separated return values into a tuple, which can be easily unpacked by the caller."
      ),
      keyTakeaways = listOf(
        "Use def to define functions and return to return results.",
        "Default parameter values provide fallbacks.",
        "Multiple returns are packed into a tuple."
      )
    ),

    Lesson(
      id = "py_comprehensions",
      title = "List & Dict Comprehensions",
      level = CourseLevel.INTERMEDIATE,
      estimatedMinutes = 11,
      summary = "Write concise, idiomatic Python expressions to create transformed lists and dictionaries.",
      contentMarkdown = """
Comprehensions provide a concise way to create lists and dictionaries from existing iterables.

### List Comprehension Syntax:
`[expression for item in iterable if condition]`

Example:
```python
# Square only even numbers
evens_squared = [x**2 for x in range(10) if x % 2 == 0]
# [0, 4, 16, 36, 64]
```

### Dictionary Comprehension:
`{key_expr: val_expr for item in iterable}`
```python
squares = {x: x**2 for x in range(5)}
# {0: 0, 1: 1, 2: 4, 3: 9, 4: 16}
```
      """.trimIndent(),
      codeExample = """# Comprehensions Showcase
names = ["alice", "bob", "charlie", "david", "eve"]

# Capitalize names longer than 3 characters
long_names = [name.capitalize() for name in names if len(name) > 3]
print("Long names:", long_names)

# Number transformations
squares = [n * n for n in range(1, 8)]
print("Squares 1 to 7:", squares)

# Filter words
words = ["Python", "C++", "Java", "Kotlin", "Go"]
py_related = [w for w in words if "t" in w.lower()]
print("Languages with 't':", py_related)
""",
      quiz = QuizQuestion(
        question = "What does [x * 2 for x in [1, 2, 3] if x > 1] evaluate to?",
        options = listOf("[2, 4, 6]", "[4, 6]", "[2, 4]", "[6]"),
        correctIndex = 1,
        explanation = "Only elements > 1 (2 and 3) pass the filter, so they are doubled to produce [4, 6]."
      ),
      keyTakeaways = listOf(
        "List comprehensions replace verbose for-loops with one readable line.",
        "Filter conditions go at the end: if condition.",
        "Keep comprehensions clean and readable."
      )
    ),

    Lesson(
      id = "py_errors",
      title = "Exception Handling",
      level = CourseLevel.INTERMEDIATE,
      estimatedMinutes = 10,
      summary = "Gracefully handle runtime errors using try, except, else, and finally blocks.",
      contentMarkdown = """
Exceptions allow programs to catch errors and prevent sudden crashes.

### Block Structure:
```python
try:
    # Code that might raise an error
    result = 10 / divisor
except ZeroDivisionError as e:
    print("Cannot divide by zero!")
except ValueError:
    print("Invalid numeric value!")
else:
    # Runs ONLY if NO exceptions occurred
    print("Computation succeeded:", result)
finally:
    # Runs ALWAYS, clean up resources
    print("Operation complete.")
```

### Raising Exceptions:
Use `raise` keyword to trigger custom errors:
```python
if balance < 0:
    raise ValueError("Account balance cannot be negative")
```
      """.trimIndent(),
      codeExample = """# Exception Handling in Python
def safe_divide(a, b):
    try:
        val = a / b
        return val
    except ZeroDivisionError:
        print("Error: Attempted division by zero!")
        return 0.0

print("10 / 2 =", safe_divide(10, 2))
print("10 / 0 =", safe_divide(10, 0))

# Parsing safely
def parse_int_list(raw_strings):
    numbers = []
    for item in raw_strings:
        try:
            numbers.append(int(item))
        except ValueError:
            print(f"Skipping invalid number: '{item}'")
    return numbers

print("Clean numbers:", parse_int_list(["10", "abc", "42", "hello", "99"]))
""",
      quiz = QuizQuestion(
        question = "Which block in a try-except structure executes regardless of whether an exception occurred?",
        options = listOf("else", "except", "finally", "catch"),
        correctIndex = 2,
        explanation = "The 'finally' block is guaranteed to execute whether an exception was raised, caught, or not."
      ),
      keyTakeaways = listOf(
        "Catch specific exceptions rather than bare 'except:'.",
        "The 'finally' block always runs (useful for cleanup).",
        "Use 'raise ExceptionType(\"message\")' to trigger errors."
      )
    ),

    Lesson(
      id = "py_lambda_map",
      title = "Lambda, Map & Filter",
      level = CourseLevel.INTERMEDIATE,
      estimatedMinutes = 9,
      summary = "Write anonymous inline functions and use functional programming utilities.",
      contentMarkdown = """
### Lambda Functions
Small anonymous inline functions defined with `lambda`:
`lambda arguments: expression`

```python
double = lambda x: x * 2
print(double(5)) # 10
```

### Sorting with Custom Keys
Lambdas shine when sorting complex collections:
```python
users = [{"name": "Sara", "score": 90}, {"name": "Alex", "score": 98}]
users.sort(key=lambda u: u["score"], reverse=True)
```
      """.trimIndent(),
      codeExample = """# Lambdas & Custom Sorting
add = lambda a, b: a + b
print("Lambda add 4 + 7 =", add(4, 7))

# Sorting list of pairs by second element
pairs = [("Apples", 5), ("Oranges", 2), ("Bananas", 8)]
sorted_by_qty = sorted(pairs, key=lambda p: p[1])
print("Sorted by quantity:", sorted_by_qty)

# Applying transformations
nums = [1, 2, 3, 4, 5]
tripled = [x * 3 for x in nums] # idiomatic python
print("Tripled:", tripled)
""",
      quiz = QuizQuestion(
        question = "What is a lambda function in Python?",
        options = listOf("A multiline recursive function", "A single-expression anonymous function", "A function that runs asynchronously", "A deprecated Python 2 feature"),
        correctIndex = 1,
        explanation = "A lambda is a small, single-expression anonymous function commonly used for short key selectors."
      ),
      keyTakeaways = listOf(
        "Lambdas are concise single-expression functions: lambda x: x + 1.",
        "They are most commonly used with sorted(key=...).",
        "For complex logic, prefer standard 'def' functions for readability."
      )
    ),

    // ---------------- ADVANCED ----------------
    Lesson(
      id = "py_oop_classes",
      title = "OOP: Classes & Objects",
      level = CourseLevel.ADVANCED,
      estimatedMinutes = 14,
      summary = "Master Object-Oriented Programming: classes, instances, __init__, and self.",
      contentMarkdown = """
Object-Oriented Programming (OOP) models real-world entities with data (attributes) and behavior (methods).

### Defining a Class
```python
class BankAccount:
    def __init__(self, owner, balance=0.0):
        self.owner = owner
        self.balance = balance

    def deposit(self, amount):
        self.balance += amount
        return self.balance

    def withdraw(self, amount):
        if amount > self.balance:
            raise ValueError("Insufficient funds")
        self.balance -= amount
        return self.balance
```

### What is `self`?
`self` represents the instance of the class. Through `self`, methods can access and mutate the object's attributes.
      """.trimIndent(),
      codeExample = """# Object-Oriented Programming Demo
class Robot:
    def __init__(self, name, battery=100):
        self.name = name
        self.battery = battery

    def perform_task(self, cost):
        if self.battery >= cost:
            self.battery -= cost
            print(f"{self.name} completed task! Battery: {self.battery}%")
        else:
            print(f"{self.name} battery too low ({self.battery}%)!")

    def recharge(self):
        self.battery = 100
        print(f"{self.name} fully recharged to 100%!")

bot = Robot("R2D2", 50)
bot.perform_task(30)
bot.perform_task(30) # fails due to low battery
bot.recharge()
bot.perform_task(30) # succeeds now!
""",
      quiz = QuizQuestion(
        question = "What is the purpose of the __init__ method in a Python class?",
        options = listOf("To destroy an object when finished", "To initialize attributes when an instance is created (constructor)", "To import foreign packages", "To convert objects to strings"),
        correctIndex = 1,
        explanation = "__init__ is the constructor method in Python, called automatically when a new object instance is created."
      ),
      keyTakeaways = listOf(
        "Classes define blueprints; objects are instances.",
        "__init__(self, ...) initializes object state.",
        "self must be the first parameter in instance methods."
      )
    ),

    Lesson(
      id = "py_oop_inheritance",
      title = "Inheritance & Polymorphism",
      level = CourseLevel.ADVANCED,
      estimatedMinutes = 12,
      summary = "Reuse code with class inheritance, override methods, and call super().",
      contentMarkdown = """
Inheritance allows a child class to inherit attributes and methods from a parent class.

### Syntax:
```python
class Animal:
    def __init__(self, name):
        self.name = name

    def speak(self):
        pass

class Dog(Animal):
    def speak(self):
        return f"{self.name} says Woof!"
```

### Calling Parent with `super()`
`super().__init__(name)` initializes the parent class state cleanly before adding child-specific attributes.
      """.trimIndent(),
      codeExample = """# Inheritance & super()
class Vehicle:
    def __init__(self, make, model):
        self.make = make
        self.model = model

    def info(self):
        return f"{self.make} {self.model}"

class ElectricCar(Vehicle):
    def __init__(self, make, model, battery_kwh):
        super().__init__(make, model)
        self.battery_kwh = battery_kwh

    def info(self):
        base = super().info()
        return f"{base} (EV {self.battery_kwh}kWh)"

ev = ElectricCar("Tesla", "Model 3", 75)
print("Vehicle Info:", ev.info())
""",
      quiz = QuizQuestion(
        question = "How do you call a method from the parent class in Python?",
        options = listOf("parent.method()", "super().method()", "this.super.method()", "base->method()"),
        correctIndex = 1,
        explanation = "super() provides a proxy object that delegates method calls to the parent (superclass)."
      ),
      keyTakeaways = listOf(
        "Inheritance creates 'is-a' relationships between classes.",
        "Use super() to delegate to the parent class constructor and methods.",
        "Polymorphism lets different classes respond to the same method call."
      )
    ),

    Lesson(
      id = "py_dunder_methods",
      title = "Magic & Dunder Methods",
      level = CourseLevel.ADVANCED,
      estimatedMinutes = 12,
      summary = "Customize Python operators, string representation, and collection behavior.",
      contentMarkdown = """
"Dunder" methods (Double Underscore) let user-defined classes hook into Python's native operators and built-in functions.

### Key Dunder Methods:
- `__str__(self)`: Human-readable string representation (used by `print()` and `str()`)
- `__repr__(self)`: Developer representation for debugging
- `__len__(self)`: Enables `len(obj)`
- `__eq__(self, other)`: Enables `obj1 == obj2`
- `__add__(self, other)`: Enables `obj1 + obj2`
      """.trimIndent(),
      codeExample = """# Magic Dunder Methods
class Vector2D:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __str__(self):
        return f"Vector2D({self.x}, {self.y})"

    def __add__(self, other):
        return Vector2D(self.x + other.x, self.y + other.y)

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

v1 = Vector2D(3, 4)
v2 = Vector2D(1, 2)
v3 = v1 + v2 # calls __add__

print("v1:", v1)
print("v2:", v2)
print("v1 + v2 =", v3)
print("Is v1 equal to v2?", v1 == v2)
""",
      quiz = QuizQuestion(
        question = "Which dunder method is called when print(my_object) is executed?",
        options = listOf("__print__", "__str__", "__show__", "__display__"),
        correctIndex = 1,
        explanation = "print() checks for __str__ to produce an informal readable representation of the object."
      ),
      keyTakeaways = listOf(
        "Dunder methods have double underscores: __init__, __str__, __len__.",
        "They enable operator overloading like + and ==.",
        "They make custom objects behave like native Python types."
      )
    ),

    Lesson(
      id = "py_decorators",
      title = "Decorators & Closures",
      level = CourseLevel.ADVANCED,
      estimatedMinutes = 14,
      summary = "Wrap and enhance functions dynamically using the @decorator syntax.",
      contentMarkdown = """
A decorator is a function that takes another function as an argument, extends its behavior without modifying it, and returns the modified function.

### Decorator Pattern:
```python
def my_decorator(func):
    def wrapper(*args, **kwargs):
        print("Before execution")
        result = func(*args, **kwargs)
        print("After execution")
        return result
    return wrapper

@my_decorator
def greet(name):
    print(f"Hello, {name}!")
```

### Use Cases:
- Timing execution
- Logging & analytics
- Authentication & access control
- Caching / Memoization
      """.trimIndent(),
      codeExample = """# Decorator Pattern Example
def logger(func):
    def wrapper(a, b):
        print(f"Calling '{func.__name__}' with args ({a}, {b})")
        res = func(a, b)
        print(f"Result was: {res}")
        return res
    return wrapper

@logger
def multiply(x, y):
    return x * y

val = multiply(6, 7)
""",
      quiz = QuizQuestion(
        question = "What does the @decorator syntax accomplish in Python?",
        options = listOf("It comments out the function", "It passes the decorated function into the decorator function", "It compiles Python to C", "It creates a new thread"),
        correctIndex = 1,
        explanation = "@decorator is syntactic sugar for func = decorator(func)."
      ),
      keyTakeaways = listOf(
        "Functions in Python are first-class citizens (can be passed as arguments).",
        "Decorators wrap and augment functions cleanly.",
        "Syntactic sugar @decorator makes application elegant."
      )
    ),

    Lesson(
      id = "py_generators",
      title = "Generators & Iterators",
      level = CourseLevel.ADVANCED,
      estimatedMinutes = 12,
      summary = "Stream data lazily using the yield statement with minimal memory footprint.",
      contentMarkdown = """
Generators are special functions that produce values on-demand using the `yield` keyword rather than `return`.

### The `yield` Keyword
When a generator function calls `yield`, it pauses execution and remembers its state. When asked for the next value, it resumes right where it left off!

```python
def countdown(n):
    while n > 0:
        yield n
        n -= 1
```

### Memory Efficiency:
Unlike lists that allocate all elements in RAM simultaneously, generators compute each item only when requested (lazy evaluation).
      """.trimIndent(),
      codeExample = """# Generator Demo (Fibonacci)
def fib_stream(limit):
    a, b = 0, 1
    count = 0
    while count < limit:
        yield a
        a, b = b, a + b
        count += 1

print("First 8 Fibonacci numbers:")
for num in fib_stream(8):
    print(num, end=" ")
print()
""",
      quiz = QuizQuestion(
        question = "What keyword distinguishes a generator function from a regular function in Python?",
        options = listOf("generate", "stream", "yield", "lazy"),
        correctIndex = 2,
        explanation = "The presence of the 'yield' keyword turns a standard function into a generator."
      ),
      keyTakeaways = listOf(
        "yield produces a value and pauses execution state.",
        "Generators evaluate lazily, consuming minimal RAM for huge streams.",
        "Iterate over generators using standard for loops."
      )
    )
  )
}
