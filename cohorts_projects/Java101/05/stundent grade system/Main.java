public class Main {
    public static void main(String[] args) {
        Course math = new Course("Mathematics", "MAT101", "MAT", 0.20);
        Course physics = new Course("Physics", "PHY101", "PHY", 0.20);
        Course chemistry = new Course("Chemistry", "CHE101", "CHE", 0.30);

        Teacher t1 = new Teacher("Mahmut", "90550000000", "MAT");
        Teacher t2 = new Teacher("Fatma", "90550000001", "PHY");
        Teacher t3 = new Teacher("Ali", "90550000002", "CHE");

        math.addTeacher(t1);
        physics.addTeacher(t2);
        chemistry.addTeacher(t3);

        Student s1 = new Student("Student One", 4, "140144015", math, physics, chemistry);
        s1.addBulkExamNote(50, 20, 40);
        s1.addBulkVerbalNote(90, 90, 80);
        s1.isPass();
    }
}
