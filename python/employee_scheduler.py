"""MSCS-632 Assignment 4 - Employee Shift Scheduling System (Python)."""
import random

DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
SHIFTS = ["morning", "afternoon", "evening"]
MIN_PER_SHIFT = 2
MAX_PER_SHIFT = 2  # Defined capacity so a preferred shift can become full
MAX_DAYS = 5
RNG = random.Random(632)


def demo_employees():
    names = ["Alex", "Bailey", "Casey", "Drew", "Emery", "Finley",
             "Gray", "Harper", "Jordan", "Kai", "Logan", "Morgan"]
    return {name: [SHIFTS[(i + day // 2) % 3] for day in range(7)]
            for i, name in enumerate(names)}


def collect_employees():
    print("Enter at least 9 employees (12 recommended).")
    count = int(input("Number of employees: "))
    employees = {}
    for i in range(count):
        while True:
            name = input(f"Employee {i + 1} name: ").strip()
            if name and name not in employees:
                break
            print("Name must be unique and nonempty.")
        preferences = []
        for day in DAYS:
            while True:
                shift = input(f"  {day} preferred shift (morning/afternoon/evening): ").strip().lower()
                if shift in SHIFTS:
                    preferences.append(shift)
                    break
                print("Please enter morning, afternoon, or evening.")
        employees[name] = preferences
    return employees


def schedule_week(employees):
    if len(employees) < 9:
        raise ValueError("At least 9 employees are required for 42 shifts with a 5-day limit.")
    schedule = {day: {shift: [] for shift in SHIFTS} for day in DAYS}
    days_worked = {name: 0 for name in employees}
    events = []
    deferred = []

    for day_index, day in enumerate(DAYS):
        assigned_today = set()
        shift_map = schedule[day]

        # Handle requests carried forward from a full previous day first.
        requests = [(name, preferred, True) for name, preferred in deferred]
        deferred = []
        names = list(employees)
        RNG.shuffle(names)
        requests += [(name, employees[name][day_index], False) for name in names]

        for name, preferred, carried in requests:
            if name in assigned_today or days_worked[name] >= MAX_DAYS:
                continue
            if len(shift_map[preferred]) < MAX_PER_SHIFT:
                shift_map[preferred].append(name)
                assigned_today.add(name)
                days_worked[name] += 1
            else:
                other = next((s for s in SHIFTS if len(shift_map[s]) < MAX_PER_SHIFT), None)
                if other is not None:
                    shift_map[other].append(name)
                    assigned_today.add(name)
                    days_worked[name] += 1
                    events.append(f"{day}: {name}'s {preferred} preference was full; assigned {other}.")
                elif day_index < len(DAYS) - 1 and not carried:
                    deferred.append((name, preferred))
                    events.append(f"{day}: {name}'s {preferred} preference was full; request carried to next day.")

        # Fill short shifts by randomly selecting eligible employees.
        for shift in SHIFTS:
            while len(shift_map[shift]) < MIN_PER_SHIFT:
                eligible = [name for name in employees
                            if name not in assigned_today and days_worked[name] < MAX_DAYS]
                if not eligible:
                    raise ValueError(f"Unable to staff {day} {shift}; add employees or change constraints.")
                # Prefer employees with fewer days, randomizing ties.
                lowest = min(days_worked[name] for name in eligible)
                candidates = [name for name in eligible if days_worked[name] == lowest]
                chosen = RNG.choice(candidates)
                shift_map[shift].append(chosen)
                assigned_today.add(chosen)
                days_worked[chosen] += 1
                events.append(f"{day}: shortage in {shift}; randomly assigned {chosen}.")

    validate(schedule, days_worked)
    return schedule, days_worked, events


def validate(schedule, days_worked):
    for day in DAYS:
        assigned = [name for shift in SHIFTS for name in schedule[day][shift]]
        assert len(assigned) == len(set(assigned)), f"Double shift on {day}"
        for shift in SHIFTS:
            assert MIN_PER_SHIFT <= len(schedule[day][shift]) <= MAX_PER_SHIFT
    assert all(workdays <= MAX_DAYS for workdays in days_worked.values())


def main():
    print("EMPLOYEE SHIFT SCHEDULING SYSTEM (PYTHON)")
    mode = input("Use demo employees? [Y/n]: ").strip().lower()
    employees = demo_employees() if mode != "n" else collect_employees()
    try:
        schedule, days_worked, events = schedule_week(employees)
    except ValueError as error:
        print(f"Scheduling failed: {error}")
        return
    print("\nFINAL WEEKLY SCHEDULE")
    print("=" * 62)
    for day in DAYS:
        print(f"\n{day}")
        for shift in SHIFTS:
            print(f"  {shift.title():10}: {', '.join(schedule[day][shift])}")
    print("\nWORKING DAYS PER EMPLOYEE")
    for name, count in sorted(days_worked.items()):
        print(f"  {name:10}: {count}")
    print("\nCONFLICTS AND STAFFING ADJUSTMENTS")
    for event in events:
        print(" - " + event)
    print("\nVALIDATION PASSED: 2 per shift, 1 per day, maximum 5 days/week.")


if __name__ == "__main__":
    main()
