package tracker;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Action ac = new Action(scanner);

        System.out.println("Learning Progress Tracker");

        while (true) {
            String input = scanner.nextLine();
            if ("exit".equals(input)) {
                System.out.println("Bye!");
                break;
            }
            if ("back".equals(input)) {
                System.out.println("Enter 'exit' to exit the program.");
                continue;
            }
            
            String input2 = input.replace(" ", "").replace("\t", "");
            if ("".equals(input2)) {
                System.out.println("No input.");
                continue;
            }
            switch (input) {
                case "add students":
                    ac.addStudent();;
                    break;
                case "list":
                    ac.list();
                    break;
                case "add points":
                    ac.addPoints();
                    break;
                case "find":
                    ac.find();
                    break;
                case "statistics":
                    ac.statistics();
                    break;
                case "notify":
                    ac.anotify();
                    break;
                default:
                    System.out.println("Error: unknown command!");
                    break;
            }
        }

        scanner.close();
    }
}

class Action {
    Scanner scanner;
    List<Student> students;

    Action(Scanner scanner) {
        this.scanner = scanner;
        students = new ArrayList<Student>();
    }

    void anotify() {
        int counter = 0;
        for (Student student: students) {
            boolean flag = false;
            if (!student.javaCompleted && student.java >= 600) {
                student.javaCompleted = true;
                notifyEmail(student, "Java");
                flag = true;
            }
            if (!student.dsaCompleted && student.dsa >= 400) {
                student.dsaCompleted = true;
                notifyEmail(student, "DSA");
                flag = true;
            }
            if (!student.dbCompleted && student.db >= 480) {
                student.dbCompleted = true;
                notifyEmail(student, "Database");
                flag = true;
            }
            if (!student.springCompleted && student.spring >= 550) {
                student.springCompleted = true;
                notifyEmail(student, "Spring");
                flag = true;
            }
            if (flag) {
                counter++;
            }
        }
        System.out.println("Total " + counter + " students have been notified.");
    }

    void notifyEmail(Student student, String course) {
        System.out.println("To: " + student.email);
        System.out.println("Re: Your Learning Progress");
        System.out.println("Hello, " + student.firstName + " " + student.lastName + "! You have accomplished our " + course + " course!");
    }

    void statistics() {
        System.out.println("Type the name of a course to see details or 'back' to quit:");
        printStatistics();
        while (true) {
            String input = scanner.nextLine();
            if ("back".equals(input)) {
                return;
            }
            String input2 = input.replace(" ", "").replace("\t", "");
            if ("".equals(input2)) {
                System.out.println("Unknown course.");
                continue;
            }

            switch(input) {
                case "Java":
                case "DSA":
                case "Databases":
                case "Spring":
                    printCourseDetail(input);
                    break;
                default:
                    System.out.println("Unknown course.");
                    break; 
            }
        }
    }

    void printCourseDetail(String course) {
        System.out.println(course);
        System.out.println("id points completed");
        List<Track> tracks = new ArrayList<>();
        for (Student student: students) {
            switch (course) {
                case "Java":
                    if (student.java > 0) {
                        tracks.add(new Track(student.id, student.java, calcRate(student.java, 600)));
                    }
                    break;
                case "DSA":
                    if (student.dsa > 0) {
                        tracks.add(new Track(student.id, student.dsa, calcRate(student.dsa, 400)));
                    }
                    break;
                case "Databases":
                    if (student.db > 0) {
                        tracks.add(new Track(student.id, student.db, calcRate(student.db, 480)));
                    }
                    break;
                case "Spring":
                    if (student.spring > 0) {
                        tracks.add(new Track(student.id, student.spring, calcRate(student.spring, 550)));
                    }
                    break;
            }
        }
        
        Comparator<Track> comparator = Comparator.comparing(Track::getPoint).reversed().thenComparing(Track::getId);
        tracks.stream().sorted(comparator).forEach(s -> System.out.println(s.id + " " + s.point + " " + s.rate + "%"));
    }

    double calcRate(int point, int completePoint) {
        double rate = (double) point * 100 / (double) completePoint;
        return ((double)Math.round(rate * 10)) / 10;
    }

    void printStatistics() {
        double[] p = getPopularity();
        double[] a = getActivity();
        double[] e = getEase(p);

        String mp = getMaxCourse(p);
        String lp = getMinCourse(p, mp);
        String ha = getMaxCourse(a);
        String la = getMinCourse(a, ha);
        String ec = getMaxCourse(e);
        String hc = getMinCourse(e, ec);

        System.out.println("Most popular: " + mp);
        System.out.println("Least popular: " + lp);
        System.out.println("Highest activity: " + ha);
        System.out.println("Lowest activity: " + la);
        System.out.println("Easiest course: " + ec);
        System.out.println("Hardest course: " + hc);        
    }

    String getMinCourse(double[] p, String mp) {
        if ("n/a".equals(mp)) {
            return "n/a";
        }
        double minp = Double.MAX_VALUE;
        for (double e: p) {
            if (e < minp) {
                minp = e;
            }
        }
        if (minp == Double.MAX_VALUE) {
            return "n/a";
        }

        List<String> mpList = Arrays.asList(mp.split(", "));
        String[] courseName = {"Java", "DSA", "Databases", "Spring"}; 
        List<String> lp = new ArrayList<>();
        for (int i = 0; i < p.length; i++) {
            if (p[i] == minp && !mpList.contains(courseName[i])) {
                lp.add(courseName[i]);
            }
        }

        if (lp.isEmpty()) {
            return "n/a";
        }

        return String.join(", ", lp);
    }

    String getMaxCourse(double[] p) {
        double maxp = -1;
        for (double e: p) {
            if (e > maxp) {
                maxp = e;
            }
        }
        if (maxp == 0) {
            return "n/a";
        }
        String[] courseName = {"Java", "DSA", "Databases", "Spring"}; 
        List<String> mp = new ArrayList<>();
        for (int i = 0; i < p.length; i++) {
            if (p[i] == maxp) {
                mp.add(courseName[i]);
            }
        }

        return String.join(", ", mp);
    }

    double[] getPopularity() {
        double[] p = new double[4];
        p[0] = 0;
        p[1] = 0;
        p[2] = 0;
        p[3] = 0;
        for (Student student: students) {
            p[0] += student.java > 0 ? 1 : 0; 
            p[1] += student.dsa > 0 ? 1 : 0;
            p[2] += student.db > 0 ? 1 : 0;
            p[3] += student.spring > 0 ? 1 : 0;
        }
        return p;
    }

    double[] getActivity() {
        double[] a = new double[4];
        a[0] = 0;
        a[1] = 0;
        a[2] = 0;
        a[3] = 0;
        for (Student student: students) {
            a[0] += student.javaTask;
            a[1] += student.dsaTask;
            a[2] += student.dbTask;
            a[3] += student.springTask;
        }
        return a;
    }

    double[] getEase(double[] p) {
        double[] e = new double[4];
        e[0] = 0;
        e[1] = 0;
        e[2] = 0;
        e[3] = 0;
        for (Student student: students) {
            e[0] += student.java;
            e[1] += student.dsa;
            e[2] += student.db;
            e[3] += student.spring;
        }
        for (int i = 0; i < 4; i++) {
            if (p[i] == 0) {
                e[i] = 0;
            } else {
                e[i] /= p[i];
            }
        }        
        return e;
    }

    void find() {
        System.out.println("Enter an id or 'back' to return.");
        while (true) {
            String input = scanner.nextLine();
            if ("back".equals(input)) {
                return;
            }
            String input2 = input.replace(" ", "").replace("\t", "");
            if ("".equals(input2)) {
                System.out.println("Incorrect points format.");
                continue;
            }

            if (!input.matches("[0-9]+")) {
                System.out.println("No student is found for id=" + input);
                continue;
            }

            int id = Integer.parseInt(input);
            Student student = getStudent(id);
            if (student == null) {
                System.out.println("No student is found for id=" + id);
                continue;
            }
            System.out.printf("%d points: Java=%d; DSA=%d; Databases=%d; Spring=%d\n", id, student.java, student.dsa, student.db, student.spring);
        }
    }

    void addPoints() {
        System.out.println("Enter an id and points or 'back' to return.");
        while (true) {
            String input = scanner.nextLine();
            if ("back".equals(input)) {
                return;
            }
            String input2 = input.replace(" ", "").replace("\t", "");
            if ("".equals(input2)) {
                System.out.println("Incorrect points format.");
                continue;
            }
            String result = registPoint(input);
            if ("".equals(result)) {
                System.out.println("Points updated.");
            } else {
                System.out.println(result);
            }
        }
    }
    
    String registPoint(String input) {
        String[] elms = input.split(" ");
        if (elms.length != 5) {
            return "Incorrect points format.";
        }
 
        int[] nelms = new int[5];
        for (int i = 0; i < elms.length; i++) {
            if (!elms[i].matches("[0-9]+")) {
                if (i == 0) {
                    return "No student is found for id=" + elms[0];
                } else {
                    return "Incorrect points format.";
                }
            }
            nelms[i] = Integer.parseInt(elms[i]);
        }

        int id = nelms[0];
        int java = nelms[1];
        int dsa = nelms[2];
        int db = nelms[3];
        int spring = nelms[4];

        Student student = getStudent(id);
        if (student == null) {
            return "No student is found for id=" + id;
        }

        student.update(java, dsa, db, spring);
        return "";
    }

    Student getStudent(int id) {
        for (Student student: students) {
            if (student.id == id) {
                return student;
            }
        }
        return null;
    }

    void list() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("Students:");
        students.forEach(s -> System.out.println(s.id));
    }

    void addStudent() {
        System.out.println("Enter student credentials or 'back' to return:");
        int counter = 0;
        while (true) {
            String input = scanner.nextLine();
            if ("back".equals(input)) {
                System.out.print("Total ");
                System.out.print(counter);
                System.out.println(" students have been added.");
                return;
            }
            String input2 = input.replace(" ", "").replace("\t", "");
            if ("".equals(input2)) {
                System.out.println("Incorrect credentials.");
                continue;
            }
            String result = registStudent(input);
            if ("".equals(result)) {
                System.out.println("The student has been added.");
                counter++;
            } else {
                System.out.println(result);
            }
        }
    }

    String registStudent(String input) {
        String[] elms = input.split(" ");
        if (elms.length < 3) {
            return "Incorrect credentials.";
        }
        String email = elms[elms.length - 1];
        String firstName = elms[0];
        String lastName = "";
        for (int i = 1; i < elms.length - 1; i++) {
            if (!checkUserName(elms[i])) {
                return "Incorrect last name.";
            }
            lastName += elms[i];
        }
        if (!checkUserName(firstName)) {
            return "Incorrect first name.";
        }
        if (!checkEmail(email)) {
            return "Incorrect email.";
        }
        if (isDupEmail(email)) {
            return "This email is already taken.";
        }
        Student student = new Student(firstName, lastName, email);
        students.add(student);
        return "";
    }

    boolean checkUserName(String userName) {
        if (!userName.matches("[a-zA-Z-']{2,}")) {
            return false;
        }
        if (userName.matches("[-'].*")) {
            return false;
        }
        if (userName.matches(".*[-']$")) {
            return false;
        }
        if (userName.matches("[a-zA-Z]+(\\-'|'\\-|\\-\\-|'')[a-zA-Z]+")) {
            return false;
        }

        return true;
    }

    boolean checkEmail(String email) {
        if (email.matches("[a-zA-z0-9-.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")) {
            return true;
        }
        return false;
    }

    boolean isDupEmail(String email) {
        for (Student student: students) {
            if (email.equals(student.email)) {
                return true;
            }
        }
        return false;
    }
}

class Student {
    static int idCounter = 0;
    int id;
    String firstName;
    String lastName;
    String email;
    int java;
    int dsa;
    int db;
    int spring;
    int javaTask;
    int dsaTask;
    int dbTask;
    int springTask;
    boolean javaCompleted;
    boolean dsaCompleted;
    boolean dbCompleted;
    boolean springCompleted;

    Student(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = ++idCounter;
        this.java = 0;
        this.dsa = 0;
        this.db = 0;
        this.spring = 0;
        this.javaTask = 0;
        this.dsaTask = 0;
        this.dbTask = 0;
        this.springTask = 0;
        this.javaCompleted = false;
        this.dsaCompleted = false;
        this.dbCompleted = false;
        this.springCompleted = false;
    }

    void update(int java, int dsa, int db, int spring) {
        this.java += java;
        this.dsa += dsa;
        this.db += db;
        this.spring += spring;
        this.javaTask += java > 0 ? 1 : 0;
        this.dsaTask += dsa > 0 ? 1 : 0;
        this.dbTask += db > 0 ? 1 : 0;
        this.springTask += spring > 0 ? 1 : 0; 
    }
}

class Track {
    int id;
    int point;
    double rate;

    Track(int id, int point, double rate) {
        this.id = id;
        this.point = point;
        this.rate = rate;
    }

    int getId() {
        return id;
    }

    int getPoint() {
        return point;
    }
}