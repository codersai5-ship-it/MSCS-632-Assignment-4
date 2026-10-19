# Employee Shift Scheduling System

**MSCS-632-M30 — Advanced Programming Languages**  
**Assignment 4: Implementing Control Structures**  
**Student:** Sai Jeswanth Reddy Pochana  
**University:** University of the Cumberlands  
**Instructor:** Dr. Jay Thom

## Project Overview

This assignment implements an employee shift scheduling application in **Python** and **Java**. The program collects employees' preferred shifts for each day, assigns employees to a seven-day schedule, resolves preferences that cannot be accommodated, and prints the completed schedule.

Both implementations use control structures including **loops, conditionals, and branching**, demonstrating similar scheduling decisions in two languages with different syntax and type systems.

## Requirements and Scheduling Rules

- Operates **Monday through Sunday**.
- Supports three daily shifts: **morning, afternoon, and evening**.
- Accepts employee names and a preferred shift for **each day of the week**, or a built-in demonstration dataset.
- Assigns **no more than one shift to an employee per day**.
- Limits each employee to **five working days per week**.
- Requires **at least two employees per shift per day**.
- Uses a capacity of **two employees per shift** in these implementations, making it possible to detect when a preferred shift is full.
- When a requested shift is full, attempts another open shift that day; when the day is full, a request may be carried to the following day.
- Includes random selection of eligible employees if a shift is understaffed, subject to the daily and weekly limits.
- Prints the final schedule, employee workday counts, conflict/adjustment messages, and validation status.

**Feasibility note:** The weekly minimum is 7 days × 3 shifts × 2 employees = **42 employee-shifts**. Because each employee can work no more than five days, at least **nine employees** are necessary. The demo includes 12 employees. Some datasets or preference arrangements may not be schedulable by the current greedy approach; the programs report a scheduling failure if they cannot fill a shift without violating the limits.

## Repository Structure

```text
MSCS-632-Assignment-4/
├── README.md
├── python/
│   └── employee_scheduler.py
└── java/
    └── EmployeeScheduler.java
```

## How to Run

### Python

**Requirement:** Python 3

From the repository root:

```bash
python python/employee_scheduler.py
```

### Java

**Requirement:** Java Development Kit (JDK), with `javac` and `java` available

From the repository root:

```bash
javac java/EmployeeScheduler.java
java -cp java EmployeeScheduler
```

For **either** program, respond to:

```text
Use demo employees? [Y/n]:
```

- Enter **Y** or press Enter to generate a schedule using the built-in 12-employee dataset.
- Enter **N** to input the number of employees, each employee's name, and their preferred shift for every day.

Use the shift names `morning`, `afternoon`, or `evening` for manual input.

## Scheduling Approach

1. **Collect data:** Store each employee's seven daily preferences.
2. **Initialize:** Create a weekly schedule with three shifts per day and track days worked.
3. **Assign preferences:** For each day, iterate through employee requests; assign an open preferred shift only when the employee has no assignment that day and is below the weekly limit.
4. **Resolve conflicts:** Try another available shift on the same day or carry the request forward when the day is full.
5. **Fill shortages:** Where fewer than two staff are assigned, select an eligible employee at random, prioritizing the employees with the fewest assigned days.
6. **Validate and print:** Check daily and weekly assignment constraints, display the schedule and workday totals, and show any conflict adjustments.

### Python vs. Java

| Aspect | Python | Java |
|---|---|---|
| Employee preferences | Dictionary of lists | `LinkedHashMap<String, String[]>` |
| Weekly schedule | Nested dictionaries and lists | Nested maps and `ArrayList` |
| Track daily assignments | `set` | `HashSet` |
| Control flow | `for`, `while`, `if/else` | `for`, `while`, `if/else` |
| Random choices | `random.Random` | `java.util.Random` |
| Validation | Assertions | Explicit exceptions |

## Sample Run and Validation

The demonstration dataset contains **12 employees**, covers all seven days and all three shifts, and generates a readable employee schedule. In the demonstrated Python and Java runs, each shift had two employees, no employee worked twice in a day, and no employee exceeded five days.

The programs print:

```text
VALIDATION PASSED: 2 per shift, 1 per day, maximum 5 days/week.
```

The schedules may differ between Python and Java because the languages use different random-number generators. The optional priority-ranking and GUI bonus features are **not implemented**.

## Source Code

- [Python implementation](python/employee_scheduler.py)
- [Java implementation](java/EmployeeScheduler.java)

## Assignment Submission

The submission document includes screenshots of the weekly schedules and validation output for both programming languages. The source code is available in this public GitHub repository.
