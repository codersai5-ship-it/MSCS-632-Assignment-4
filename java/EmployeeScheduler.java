import java.util.*;

/** MSCS-632 Assignment 4: Employee shift scheduler (Java implementation). */
public class EmployeeScheduler {
    static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    static final String[] SHIFTS = {"morning", "afternoon", "evening"};
    static final int MIN_PER_SHIFT = 2, MAX_PER_SHIFT = 2, MAX_DAYS = 5;
    static final Random RANDOM = new Random(632);

    static class Request {
        String name, preferred;
        boolean carried;
        Request(String name, String preferred, boolean carried) {
            this.name = name; this.preferred = preferred; this.carried = carried;
        }
    }

    static Map<String, String[]> demoEmployees() {
        String[] names = {"Alex", "Bailey", "Casey", "Drew", "Emery", "Finley",
                "Gray", "Harper", "Jordan", "Kai", "Logan", "Morgan"};
        Map<String, String[]> employees = new LinkedHashMap<>();
        for (int i = 0; i < names.length; i++) {
            String[] prefs = new String[7];
            for (int d = 0; d < 7; d++) prefs[d] = SHIFTS[(i + d / 2) % 3];
            employees.put(names[i], prefs);
        }
        return employees;
    }

    static Map<String, String[]> collectEmployees(Scanner input) {
        System.out.println("Enter at least 9 employees (12 recommended).");
        System.out.print("Number of employees: ");
        int count = Integer.parseInt(input.nextLine().trim());
        Map<String, String[]> employees = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            String name;
            do {
                System.out.print("Employee " + (i + 1) + " name: ");
                name = input.nextLine().trim();
                if (name.isEmpty() || employees.containsKey(name))
                    System.out.println("Name must be unique and nonempty.");
            } while (name.isEmpty() || employees.containsKey(name));
            String[] prefs = new String[7];
            for (int d = 0; d < 7; d++) {
                while (true) {
                    System.out.print("  " + DAYS[d] + " preferred shift (morning/afternoon/evening): ");
                    String shift = input.nextLine().trim().toLowerCase(Locale.ROOT);
                    if (Arrays.asList(SHIFTS).contains(shift)) {
                        prefs[d] = shift;
                        break;
                    }
                    System.out.println("Please enter morning, afternoon, or evening.");
                }
            }
            employees.put(name, prefs);
        }
        return employees;
    }

    static void validate(Map<String, Map<String, List<String>>> schedule, Map<String, Integer> worked) {
        for (String day : DAYS) {
            Set<String> assigned = new HashSet<>();
            for (String shift : SHIFTS) {
                List<String> staff = schedule.get(day).get(shift);
                if (staff.size() < MIN_PER_SHIFT || staff.size() > MAX_PER_SHIFT)
                    throw new IllegalStateException("Incorrect staffing: " + day + " " + shift);
                for (String name : staff)
                    if (!assigned.add(name)) throw new IllegalStateException("Double shift: " + name);
            }
        }
        for (int count : worked.values())
            if (count > MAX_DAYS) throw new IllegalStateException("Exceeded five-day limit");
    }

    static void scheduleWeek(Map<String, String[]> employees) {
        if (employees.size() < 9)
            throw new IllegalArgumentException("At least 9 employees required for 42 shifts with a 5-day limit.");

        Map<String, Map<String, List<String>>> schedule = new LinkedHashMap<>();
        Map<String, Integer> worked = new LinkedHashMap<>();
        List<String> events = new ArrayList<>();
        List<Request> deferred = new ArrayList<>();
        for (String name : employees.keySet()) worked.put(name, 0);

        for (int d = 0; d < DAYS.length; d++) {
            String day = DAYS[d];
            Map<String, List<String>> shiftMap = new LinkedHashMap<>();
            for (String shift : SHIFTS) shiftMap.put(shift, new ArrayList<>());
            schedule.put(day, shiftMap);
            Set<String> assignedToday = new HashSet<>();

            List<Request> requests = new ArrayList<>(deferred);
            deferred = new ArrayList<>();
            List<String> names = new ArrayList<>(employees.keySet());
            Collections.shuffle(names, RANDOM);
            for (String name : names) requests.add(new Request(name, employees.get(name)[d], false));

            for (Request request : requests) {
                String name = request.name, preferred = request.preferred;
                if (assignedToday.contains(name) || worked.get(name) >= MAX_DAYS) continue;
                String target = null;
                if (shiftMap.get(preferred).size() < MAX_PER_SHIFT) target = preferred;
                else {
                    for (String alternative : SHIFTS) {
                        if (shiftMap.get(alternative).size() < MAX_PER_SHIFT) {
                            target = alternative;
                            events.add(day + ": " + name + "'s " + preferred
                                    + " preference was full; assigned " + alternative + ".");
                            break;
                        }
                    }
                }
                if (target != null) {
                    shiftMap.get(target).add(name);
                    assignedToday.add(name);
                    worked.put(name, worked.get(name) + 1);
                } else if (d < DAYS.length - 1 && !request.carried) {
                    deferred.add(new Request(name, preferred, true));
                    events.add(day + ": " + name + "'s " + preferred
                            + " preference was full; request carried to next day.");
                }
            }

            for (String shift : SHIFTS) {
                while (shiftMap.get(shift).size() < MIN_PER_SHIFT) {
                    List<String> eligible = new ArrayList<>();
                    for (String name : employees.keySet())
                        if (!assignedToday.contains(name) && worked.get(name) < MAX_DAYS)
                            eligible.add(name);
                    if (eligible.isEmpty())
                        throw new IllegalStateException("Unable to staff " + day + " " + shift
                                + "; add employees or change constraints.");
                    int leastWorked = Integer.MAX_VALUE;
                    for (String name : eligible) leastWorked = Math.min(leastWorked, worked.get(name));
                    List<String> candidates = new ArrayList<>();
                    for (String name : eligible)
                        if (worked.get(name) == leastWorked) candidates.add(name);
                    String chosen = candidates.get(RANDOM.nextInt(candidates.size()));
                    shiftMap.get(shift).add(chosen);
                    assignedToday.add(chosen);
                    worked.put(chosen, worked.get(chosen) + 1);
                    events.add(day + ": shortage in " + shift + "; randomly assigned " + chosen + ".");
                }
            }
        }

        validate(schedule, worked);
        System.out.println("\nFINAL WEEKLY SCHEDULE");
        System.out.println("==============================================================");
        for (String day : DAYS) {
            System.out.println("\n" + day);
            for (String shift : SHIFTS)
                System.out.printf("  %-10s: %s%n",
                        Character.toUpperCase(shift.charAt(0)) + shift.substring(1),
                        String.join(", ", schedule.get(day).get(shift)));
        }
        System.out.println("\nWORKING DAYS PER EMPLOYEE");
        for (String name : new TreeSet<>(worked.keySet()))
            System.out.printf("  %-10s: %d%n", name, worked.get(name));
        System.out.println("\nCONFLICTS AND STAFFING ADJUSTMENTS");
        for (String event : events) System.out.println(" - " + event);
        System.out.println("\nVALIDATION PASSED: 2 per shift, 1 per day, maximum 5 days/week.");
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("EMPLOYEE SHIFT SCHEDULING SYSTEM (JAVA)");
        System.out.print("Use demo employees? [Y/n]: ");
        String mode = input.nextLine().trim();
        try {
            Map<String, String[]> employees = mode.equalsIgnoreCase("n")
                    ? collectEmployees(input) : demoEmployees();
            scheduleWeek(employees);
        } catch (IllegalArgumentException | IllegalStateException error) {
            System.out.println("Scheduling failed: " + error.getMessage());
        }
    }
}
