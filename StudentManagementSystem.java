import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * StudentManagementSystem.java
 *
 * A console-based, single-file Java application for managing students,
 * courses and enrollments at an educational institution. Supports full
 * CRUD operations, searching, sorting, GPA statistics, department-wise
 * reports and simple text-file persistence.
 *
 * Compile: javac StudentManagementSystem.java
 * Run:     java StudentManagementSystem
 */
public class StudentManagementSystem {

    public static void main(String[] args) {
        StudentManager studentManager = new StudentManager();
        CourseManager courseManager = new CourseManager();
        EnrollmentManager enrollmentManager = new EnrollmentManager(studentManager, courseManager);

        seedSampleData(studentManager, courseManager, enrollmentManager);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=================================================");
        System.out.println("       STUDENT MANAGEMENT SYSTEM (SMS)          ");
        System.out.println("=================================================");

        while (running) {
            printMainMenu();
            int choice = readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1:
                    studentMenu(scanner, studentManager);
                    break;
                case 2:
                    courseMenu(scanner, courseManager);
                    break;
                case 3:
                    enrollmentMenu(scanner, enrollmentManager);
                    break;
                case 4:
                    reportsMenu(scanner, studentManager, courseManager, enrollmentManager);
                    break;
                case 5:
                    fileMenu(scanner, studentManager);
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using the Student Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid menu option.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n-------------------- MAIN MENU --------------------");
        System.out.println("1. Student Management");
        System.out.println("2. Course Management");
        System.out.println("3. Enrollment Management");
        System.out.println("4. Reports & Statistics");
        System.out.println("5. Save / Load Data");
        System.out.println("0. Exit");
        System.out.println("-----------------------------------------------------");
    }

    // =====================================================================
    // STUDENT MENU
    // =====================================================================

    private static void studentMenu(Scanner scanner, StudentManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- STUDENT MANAGEMENT -----");
            System.out.println("1. Add Student");
            System.out.println("2. Update Student");
            System.out.println("3. Remove Student");
            System.out.println("4. Search Student by ID");
            System.out.println("5. Search Student by Name");
            System.out.println("6. List All Students");
            System.out.println("7. Sort Students by Name");
            System.out.println("8. Sort Students by GPA (Descending)");
            System.out.println("9. List Students by Department");
            System.out.println("0. Back to Main Menu");
            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudentFlow(scanner, manager);
                    break;
                case 2:
                    updateStudentFlow(scanner, manager);
                    break;
                case 3:
                    removeStudentFlow(scanner, manager);
                    break;
                case 4:
                    searchByIdFlow(scanner, manager);
                    break;
                case 5:
                    searchByNameFlow(scanner, manager);
                    break;
                case 6:
                    manager.listAll();
                    break;
                case 7:
                    manager.sortByName();
                    System.out.println("Students sorted by name.");
                    manager.listAll();
                    break;
                case 8:
                    manager.sortByGpaDescending();
                    System.out.println("Students sorted by GPA (descending).");
                    manager.listAll();
                    break;
                case 9:
                    listByDepartmentFlow(scanner, manager);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addStudentFlow(Scanner scanner, StudentManager manager) {
        System.out.println("\n--- Add New Student ---");
        int id = readInt(scanner, "Student ID: ");
        if (manager.findById(id) != null) {
            System.out.println("Error: A student with ID " + id + " already exists.");
            return;
        }
        String name = readNonEmptyString(scanner, "Full Name: ");
        int age = readInt(scanner, "Age: ");
        String gender = readNonEmptyString(scanner, "Gender (M/F/O): ");
        String department = readNonEmptyString(scanner, "Department: ");
        int semester = readInt(scanner, "Current Semester (1-8): ");
        double gpa = readDouble(scanner, "GPA (0.0 - 4.0): ");
        String phone = readNonEmptyString(scanner, "Phone Number: ");
        String email = readNonEmptyString(scanner, "Email Address: ");
        String address = readNonEmptyString(scanner, "Address: ");

        Student student = new Student(id, name, age, gender, department, semester, gpa, phone, email, address);
        manager.addStudent(student);
        System.out.println("Student added successfully:\n" + student);
    }

    private static void updateStudentFlow(Scanner scanner, StudentManager manager) {
        int id = readInt(scanner, "Enter Student ID to update: ");
        Student student = manager.findById(id);
        if (student == null) {
            System.out.println("No student found with ID " + id);
            return;
        }
        System.out.println("Current details:\n" + student);
        System.out.println("Leave blank to keep the current value.");

        String name = readOptionalString(scanner, "New Name [" + student.getName() + "]: ");
        if (!name.isEmpty()) student.setName(name);

        String ageStr = readOptionalString(scanner, "New Age [" + student.getAge() + "]: ");
        if (!ageStr.isEmpty()) {
            try {
                student.setAge(Integer.parseInt(ageStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid age, keeping previous value.");
            }
        }

        String dept = readOptionalString(scanner, "New Department [" + student.getDepartment() + "]: ");
        if (!dept.isEmpty()) student.setDepartment(dept);

        String semStr = readOptionalString(scanner, "New Semester [" + student.getSemester() + "]: ");
        if (!semStr.isEmpty()) {
            try {
                student.setSemester(Integer.parseInt(semStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid semester, keeping previous value.");
            }
        }

        String gpaStr = readOptionalString(scanner, "New GPA [" + student.getGpa() + "]: ");
        if (!gpaStr.isEmpty()) {
            try {
                student.setGpa(Double.parseDouble(gpaStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid GPA, keeping previous value.");
            }
        }

        String phone = readOptionalString(scanner, "New Phone [" + student.getPhone() + "]: ");
        if (!phone.isEmpty()) student.setPhone(phone);

        String email = readOptionalString(scanner, "New Email [" + student.getEmail() + "]: ");
        if (!email.isEmpty()) student.setEmail(email);

        String address = readOptionalString(scanner, "New Address [" + student.getAddress() + "]: ");
        if (!address.isEmpty()) student.setAddress(address);

        System.out.println("Student updated successfully:\n" + student);
    }

    private static void removeStudentFlow(Scanner scanner, StudentManager manager) {
        int id = readInt(scanner, "Enter Student ID to remove: ");
        boolean removed = manager.removeStudentById(id);
        if (removed) {
            System.out.println("Student with ID " + id + " removed successfully.");
        } else {
            System.out.println("No student found with ID " + id);
        }
    }

    private static void searchByIdFlow(Scanner scanner, StudentManager manager) {
        int id = readInt(scanner, "Enter Student ID to search: ");
        Student student = manager.findById(id);
        if (student != null) {
            System.out.println("Student found:\n" + student);
        } else {
            System.out.println("No student found with ID " + id);
        }
    }

    private static void searchByNameFlow(Scanner scanner, StudentManager manager) {
        String query = readNonEmptyString(scanner, "Enter name (or part of name) to search: ");
        List<Student> results = manager.findByNameContains(query);
        if (results.isEmpty()) {
            System.out.println("No students found matching \"" + query + "\"");
        } else {
            System.out.println("Found " + results.size() + " matching student(s):");
            for (Student s : results) {
                System.out.println(s);
            }
        }
    }

    private static void listByDepartmentFlow(Scanner scanner, StudentManager manager) {
        String dept = readNonEmptyString(scanner, "Enter Department name: ");
        List<Student> results = manager.findByDepartment(dept);
        if (results.isEmpty()) {
            System.out.println("No students found in department \"" + dept + "\"");
        } else {
            System.out.println("Students in " + dept + " (" + results.size() + "):");
            for (Student s : results) {
                System.out.println(s);
            }
        }
    }

    // =====================================================================
    // COURSE MENU
    // =====================================================================

    private static void courseMenu(Scanner scanner, CourseManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- COURSE MANAGEMENT -----");
            System.out.println("1. Add Course");
            System.out.println("2. Update Course");
            System.out.println("3. Remove Course");
            System.out.println("4. Search Course by ID");
            System.out.println("5. List All Courses");
            System.out.println("6. Sort Courses by Credits");
            System.out.println("0. Back to Main Menu");
            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    addCourseFlow(scanner, manager);
                    break;
                case 2:
                    updateCourseFlow(scanner, manager);
                    break;
                case 3:
                    removeCourseFlow(scanner, manager);
                    break;
                case 4:
                    searchCourseFlow(scanner, manager);
                    break;
                case 5:
                    manager.listAll();
                    break;
                case 6:
                    manager.sortByCredits();
                    System.out.println("Courses sorted by credits.");
                    manager.listAll();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addCourseFlow(Scanner scanner, CourseManager manager) {
        System.out.println("\n--- Add New Course ---");
        String courseId = readNonEmptyString(scanner, "Course ID (e.g. CS101): ");
        if (manager.findById(courseId) != null) {
            System.out.println("Error: A course with ID " + courseId + " already exists.");
            return;
        }
        String name = readNonEmptyString(scanner, "Course Name: ");
        int credits = readInt(scanner, "Credits: ");
        String department = readNonEmptyString(scanner, "Offering Department: ");
        Course course = new Course(courseId, name, credits, department);
        manager.addCourse(course);
        System.out.println("Course added successfully:\n" + course);
    }

    private static void updateCourseFlow(Scanner scanner, CourseManager manager) {
        String courseId = readNonEmptyString(scanner, "Enter Course ID to update: ");
        Course course = manager.findById(courseId);
        if (course == null) {
            System.out.println("No course found with ID " + courseId);
            return;
        }
        String name = readOptionalString(scanner, "New Name [" + course.getName() + "]: ");
        if (!name.isEmpty()) course.setName(name);
        String creditsStr = readOptionalString(scanner, "New Credits [" + course.getCredits() + "]: ");
        if (!creditsStr.isEmpty()) {
            try {
                course.setCredits(Integer.parseInt(creditsStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid credits, keeping previous value.");
            }
        }
        String dept = readOptionalString(scanner, "New Department [" + course.getDepartment() + "]: ");
        if (!dept.isEmpty()) course.setDepartment(dept);
        System.out.println("Course updated successfully:\n" + course);
    }

    private static void removeCourseFlow(Scanner scanner, CourseManager manager) {
        String courseId = readNonEmptyString(scanner, "Enter Course ID to remove: ");
        boolean removed = manager.removeCourseById(courseId);
        if (removed) {
            System.out.println("Course " + courseId + " removed successfully.");
        } else {
            System.out.println("No course found with ID " + courseId);
        }
    }

    private static void searchCourseFlow(Scanner scanner, CourseManager manager) {
        String courseId = readNonEmptyString(scanner, "Enter Course ID to search: ");
        Course course = manager.findById(courseId);
        if (course != null) {
            System.out.println("Course found:\n" + course);
        } else {
            System.out.println("No course found with ID " + courseId);
        }
    }

    // =====================================================================
    // ENROLLMENT MENU
    // =====================================================================

    private static void enrollmentMenu(Scanner scanner, EnrollmentManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- ENROLLMENT MANAGEMENT -----");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Assign / Update Grade");
            System.out.println("3. Drop Enrollment");
            System.out.println("4. List Enrollments for a Student");
            System.out.println("5. List Enrollments for a Course");
            System.out.println("6. List All Enrollments");
            System.out.println("0. Back to Main Menu");
            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    enrollFlow(scanner, manager);
                    break;
                case 2:
                    assignGradeFlow(scanner, manager);
                    break;
                case 3:
                    dropEnrollmentFlow(scanner, manager);
                    break;
                case 4:
                    listByStudentFlow(scanner, manager);
                    break;
                case 5:
                    listByCourseFlow(scanner, manager);
                    break;
                case 6:
                    manager.listAll();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void enrollFlow(Scanner scanner, EnrollmentManager manager) {
        int studentId = readInt(scanner, "Student ID: ");
        String courseId = readNonEmptyString(scanner, "Course ID: ");
        String result = manager.enroll(studentId, courseId);
        System.out.println(result);
    }

    private static void assignGradeFlow(Scanner scanner, EnrollmentManager manager) {
        int studentId = readInt(scanner, "Student ID: ");
        String courseId = readNonEmptyString(scanner, "Course ID: ");
        String grade = readNonEmptyString(scanner, "Grade (A/B/C/D/F): ");
        String result = manager.assignGrade(studentId, courseId, grade);
        System.out.println(result);
    }

    private static void dropEnrollmentFlow(Scanner scanner, EnrollmentManager manager) {
        int studentId = readInt(scanner, "Student ID: ");
        String courseId = readNonEmptyString(scanner, "Course ID: ");
        boolean dropped = manager.drop(studentId, courseId);
        if (dropped) {
            System.out.println("Enrollment dropped successfully.");
        } else {
            System.out.println("No matching enrollment found.");
        }
    }

    private static void listByStudentFlow(Scanner scanner, EnrollmentManager manager) {
        int studentId = readInt(scanner, "Student ID: ");
        manager.listByStudent(studentId);
    }

    private static void listByCourseFlow(Scanner scanner, EnrollmentManager manager) {
        String courseId = readNonEmptyString(scanner, "Course ID: ");
        manager.listByCourse(courseId);
    }

    // =====================================================================
    // REPORTS MENU
    // =====================================================================

    private static void reportsMenu(Scanner scanner, StudentManager sm, CourseManager cm, EnrollmentManager em) {
        boolean back = false;
        while (!back) {
            System.out.println("\n----- REPORTS & STATISTICS -----");
            System.out.println("1. Overall Class Average GPA");
            System.out.println("2. Highest GPA Student");
            System.out.println("3. Lowest GPA Student");
            System.out.println("4. Department-wise Student Count");
            System.out.println("5. Course Enrollment Counts");
            System.out.println("6. Full Summary Report");
            System.out.println("0. Back to Main Menu");
            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1:
                    System.out.printf("Overall average GPA: %.2f%n", sm.averageGpa());
                    break;
                case 2:
                    Student highest = sm.highestGpaStudent();
                    System.out.println(highest == null ? "No students available." : "Highest GPA:\n" + highest);
                    break;
                case 3:
                    Student lowest = sm.lowestGpaStudent();
                    System.out.println(lowest == null ? "No students available." : "Lowest GPA:\n" + lowest);
                    break;
                case 4:
                    sm.printDepartmentCounts();
                    break;
                case 5:
                    em.printCourseEnrollmentCounts(cm);
                    break;
                case 6:
                    printFullSummary(sm, cm, em);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void printFullSummary(StudentManager sm, CourseManager cm, EnrollmentManager em) {
        System.out.println("\n=========== FULL SUMMARY REPORT ===========");
        System.out.println("Total Students   : " + sm.count());
        System.out.println("Total Courses    : " + cm.count());
        System.out.println("Total Enrollments: " + em.count());
        System.out.printf("Average GPA      : %.2f%n", sm.averageGpa());
        Student highest = sm.highestGpaStudent();
        if (highest != null) {
            System.out.println("Top Student      : " + highest.getName() + " (GPA " + highest.getGpa() + ")");
        }
        sm.printDepartmentCounts();
        em.printCourseEnrollmentCounts(cm);
        System.out.println("=============================================");
    }

    // =====================================================================
    // FILE MENU
    // =====================================================================

    private static void fileMenu(Scanner scanner, StudentManager manager) {
        System.out.println("\n----- SAVE / LOAD DATA -----");
        System.out.println("1. Save Students to File");
        System.out.println("2. Load Students from File");
        System.out.println("0. Back to Main Menu");
        int choice = readInt(scanner, "Enter your choice: ");
        switch (choice) {
            case 1:
                String saveFile = readNonEmptyString(scanner, "Enter file name to save to (e.g. students.txt): ");
                manager.saveToFile(saveFile);
                break;
            case 2:
                String loadFile = readNonEmptyString(scanner, "Enter file name to load from: ");
                manager.loadFromFile(loadFile);
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // =====================================================================
    // SAMPLE DATA SEEDING
    // =====================================================================

    private static void seedSampleData(StudentManager sm, CourseManager cm, EnrollmentManager em) {
        sm.addStudent(new Student(1001, "Aditi Sharma", 20, "F", "Computer Science", 4, 3.8,
                "9876543210", "aditi.sharma@example.com", "12 MG Road, Bengaluru"));
        sm.addStudent(new Student(1002, "Rohan Verma", 21, "M", "Mechanical Engineering", 5, 3.2,
                "9876500001", "rohan.verma@example.com", "45 Park Street, Pune"));
        sm.addStudent(new Student(1003, "Sneha Iyer", 19, "F", "Electronics", 2, 3.9,
                "9876500002", "sneha.iyer@example.com", "7 Anna Salai, Chennai"));
        sm.addStudent(new Student(1004, "Kabir Khan", 22, "M", "Computer Science", 6, 2.9,
                "9876500003", "kabir.khan@example.com", "88 Civil Lines, Delhi"));
        sm.addStudent(new Student(1005, "Meera Nair", 20, "F", "Civil Engineering", 3, 3.5,
                "9876500004", "meera.nair@example.com", "3 Marine Drive, Mumbai"));

        cm.addCourse(new Course("CS101", "Introduction to Programming", 4, "Computer Science"));
        cm.addCourse(new Course("CS201", "Data Structures", 4, "Computer Science"));
        cm.addCourse(new Course("ME101", "Engineering Mechanics", 3, "Mechanical Engineering"));
        cm.addCourse(new Course("EC101", "Basic Electronics", 3, "Electronics"));
        cm.addCourse(new Course("CE101", "Structural Analysis", 4, "Civil Engineering"));

        em.enroll(1001, "CS101");
        em.enroll(1001, "CS201");
        em.enroll(1002, "ME101");
        em.enroll(1003, "EC101");
        em.enroll(1004, "CS101");
        em.enroll(1005, "CE101");
        em.assignGrade(1001, "CS101", "A");
        em.assignGrade(1002, "ME101", "B");
        em.assignGrade(1003, "EC101", "A");
    }

    // =====================================================================
    // INPUT HELPER UTILITIES
    // =====================================================================

    static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
            System.out.println("This field cannot be empty.");
        }
    }

    static String readOptionalString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        return line == null ? "" : line.trim();
    }
}

// =========================================================================
// DOMAIN CLASS: Student
// =========================================================================
class Student {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String department;
    private int semester;
    private double gpa;
    private String phone;
    private String email;
    private String address;

    public Student(int id, String name, int age, String gender, String department, int semester,
                   double gpa, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.department = department;
        this.semester = semester;
        this.gpa = gpa;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format(
                "ID: %-6d Name: %-20s Age: %-3d Gender: %-2s Dept: %-22s Sem: %-2d GPA: %-4.2f Phone: %-12s Email: %-28s Address: %s",
                id, name, age, gender, department, semester, gpa, phone, email, address);
    }

    public String toCsvLine() {
        return id + "," + name + "," + age + "," + gender + "," + department + "," + semester + ","
                + gpa + "," + phone + "," + email + "," + address;
    }

    public static Student fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 10) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            int age = Integer.parseInt(parts[2].trim());
            String gender = parts[3].trim();
            String department = parts[4].trim();
            int semester = Integer.parseInt(parts[5].trim());
            double gpa = Double.parseDouble(parts[6].trim());
            String phone = parts[7].trim();
            String email = parts[8].trim();
            String address = parts[9].trim();
            return new Student(id, name, age, gender, department, semester, gpa, phone, email, address);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

// =========================================================================
// DOMAIN CLASS: Course
// =========================================================================
class Course {
    private String courseId;
    private String name;
    private int credits;
    private String department;

    public Course(String courseId, String name, int credits, String department) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.department = department;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return String.format("Course ID: %-8s Name: %-30s Credits: %-3d Department: %s",
                courseId, name, credits, department);
    }
}

// =========================================================================
// DOMAIN CLASS: Enrollment
// =========================================================================
class Enrollment {
    private int studentId;
    private String courseId;
    private String grade;

    public Enrollment(int studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = "N/A";
    }

    public int getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return String.format("Student ID: %-6d Course ID: %-8s Grade: %s", studentId, courseId, grade);
    }
}

// =========================================================================
// MANAGER CLASS: StudentManager
// =========================================================================
class StudentManager {
    private List<Student> students = new ArrayList<>();

    public void addStudent(Student s) {
        students.add(s);
    }

    public boolean removeStudentById(int id) {
        return students.removeIf(s -> s.getId() == id);
    }

    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public List<Student> findByNameContains(String query) {
        List<Student> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(lower)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Student> findByDepartment(String department) {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getDepartment().equalsIgnoreCase(department)) {
                results.add(s);
            }
        }
        return results;
    }

    public void listAll() {
        if (students.isEmpty()) {
            System.out.println("No students on record.");
            return;
        }
        System.out.println("\n--- Student List (" + students.size() + ") ---");
        for (Student s : students) {
            System.out.println(s);
        }
    }

    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public void sortByGpaDescending() {
        students.sort(Comparator.comparingDouble(Student::getGpa).reversed());
    }

    public void sortById() {
        students.sort(Comparator.comparingInt(Student::getId));
    }

    public double averageGpa() {
        if (students.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Student s : students) {
            sum += s.getGpa();
        }
        return sum / students.size();
    }

    public Student highestGpaStudent() {
        Student best = null;
        for (Student s : students) {
            if (best == null || s.getGpa() > best.getGpa()) {
                best = s;
            }
        }
        return best;
    }

    public Student lowestGpaStudent() {
        Student worst = null;
        for (Student s : students) {
            if (worst == null || s.getGpa() < worst.getGpa()) {
                worst = s;
            }
        }
        return worst;
    }

    public void printDepartmentCounts() {
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        for (Student s : students) {
            counts.merge(s.getDepartment(), 1, Integer::sum);
        }
        System.out.println("\n--- Students per Department ---");
        for (java.util.Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public int count() {
        return students.size();
    }

    public List<Student> getAll() {
        return students;
    }

    public void saveToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Student s : students) {
                writer.write(s.toCsvLine());
                writer.newLine();
            }
            System.out.println("Saved " + students.size() + " student record(s) to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public void loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return;
        }
        List<Student> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Student s = Student.fromCsvLine(line);
                if (s != null) {
                    loaded.add(s);
                }
            }
            students = loaded;
            System.out.println("Loaded " + students.size() + " student record(s) from " + fileName);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}

// =========================================================================
// MANAGER CLASS: CourseManager
// =========================================================================
class CourseManager {
    private List<Course> courses = new ArrayList<>();

    public void addCourse(Course c) {
        courses.add(c);
    }

    public boolean removeCourseById(String courseId) {
        return courses.removeIf(c -> c.getCourseId().equalsIgnoreCase(courseId));
    }

    public Course findById(String courseId) {
        for (Course c : courses) {
            if (c.getCourseId().equalsIgnoreCase(courseId)) return c;
        }
        return null;
    }

    public void listAll() {
        if (courses.isEmpty()) {
            System.out.println("No courses on record.");
            return;
        }
        System.out.println("\n--- Course List (" + courses.size() + ") ---");
        for (Course c : courses) {
            System.out.println(c);
        }
    }

    public void sortByCredits() {
        courses.sort(Comparator.comparingInt(Course::getCredits).reversed());
    }

    public int count() {
        return courses.size();
    }

    public List<Course> getAll() {
        return courses;
    }
}

// =========================================================================
// MANAGER CLASS: EnrollmentManager
// =========================================================================
class EnrollmentManager {
    private List<Enrollment> enrollments = new ArrayList<>();
    private StudentManager studentManager;
    private CourseManager courseManager;

    public EnrollmentManager(StudentManager studentManager, CourseManager courseManager) {
        this.studentManager = studentManager;
        this.courseManager = courseManager;
    }

    public String enroll(int studentId, String courseId) {
        if (studentManager.findById(studentId) == null) {
            return "Error: No student found with ID " + studentId;
        }
        if (courseManager.findById(courseId) == null) {
            return "Error: No course found with ID " + courseId;
        }
        for (Enrollment e : enrollments) {
            if (e.getStudentId() == studentId && e.getCourseId().equalsIgnoreCase(courseId)) {
                return "Error: Student " + studentId + " is already enrolled in " + courseId;
            }
        }
        enrollments.add(new Enrollment(studentId, courseId));
        return "Enrolled student " + studentId + " in course " + courseId + " successfully.";
    }

    public String assignGrade(int studentId, String courseId, String grade) {
        Enrollment target = findEnrollment(studentId, courseId);
        if (target == null) {
            return "Error: No enrollment found for student " + studentId + " in course " + courseId;
        }
        target.setGrade(grade.toUpperCase());
        return "Grade " + grade.toUpperCase() + " assigned for student " + studentId + " in course " + courseId;
    }

    public boolean drop(int studentId, String courseId) {
        return enrollments.removeIf(e -> e.getStudentId() == studentId && e.getCourseId().equalsIgnoreCase(courseId));
    }

    private Enrollment findEnrollment(int studentId, String courseId) {
        for (Enrollment e : enrollments) {
            if (e.getStudentId() == studentId && e.getCourseId().equalsIgnoreCase(courseId)) {
                return e;
            }
        }
        return null;
    }

    public void listByStudent(int studentId) {
        boolean found = false;
        System.out.println("\n--- Enrollments for Student " + studentId + " ---");
        for (Enrollment e : enrollments) {
            if (e.getStudentId() == studentId) {
                System.out.println(e);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No enrollments found for this student.");
        }
    }

    public void listByCourse(String courseId) {
        boolean found = false;
        System.out.println("\n--- Enrollments for Course " + courseId + " ---");
        for (Enrollment e : enrollments) {
            if (e.getCourseId().equalsIgnoreCase(courseId)) {
                System.out.println(e);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No enrollments found for this course.");
        }
    }

    public void listAll() {
        if (enrollments.isEmpty()) {
            System.out.println("No enrollments on record.");
            return;
        }
        System.out.println("\n--- All Enrollments (" + enrollments.size() + ") ---");
        for (Enrollment e : enrollments) {
            System.out.println(e);
        }
    }

    public void printCourseEnrollmentCounts(CourseManager courseManager) {
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        for (Course c : courseManager.getAll()) {
            counts.put(c.getCourseId(), 0);
        }
        for (Enrollment e : enrollments) {
            counts.merge(e.getCourseId(), 1, Integer::sum);
        }
        System.out.println("\n--- Enrollment Counts per Course ---");
        for (java.util.Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " student(s)");
        }
    }

    public int count() {
        return enrollments.size();
    }
}
