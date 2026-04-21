public class Student {
    String name;
    String stuNo;
    int classes;
    Course course1;
    Course course2;
    Course course3;
    double average;
    boolean isPass;

    public Student(String name, int classes, String stuNo, Course course1, Course course2, Course course3) {
        this.name = name;
        this.classes = classes;
        this.stuNo = stuNo;
        this.course1 = course1;
        this.course2 = course2;
        this.course3 = course3;
        this.average = 0.0;
        this.isPass = false;
    }

    public void addBulkExamNote(int n1, int n2, int n3) {
        if (n1 >= 0 && n1 <= 100) course1.examNote = n1;
        if (n2 >= 0 && n2 <= 100) course2.examNote = n2;
        if (n3 >= 0 && n3 <= 100) course3.examNote = n3;
    }

    public void addBulkVerbalNote(int n1, int n2, int n3) {
        if (n1 >= 0 && n1 <= 100) course1.verbalNote = n1;
        if (n2 >= 0 && n2 <= 100) course2.verbalNote = n2;
        if (n3 >= 0 && n3 <= 100) course3.verbalNote = n3;
    }

    public void calcAverage() {
        average = (course1.average() + course2.average() + course3.average()) / 3.0;
    }

    public void isPass() {
        calcAverage();
        isPass = average >= 55;
        printNote();
        System.out.printf("Average: %.2f%n", average);
        System.out.println(isPass ? "Passed" : "Failed");
    }

    public void printNote() {
        System.out.println("Student: " + name);
        System.out.printf("Math: Exam=%d Verbal=%d CourseAvg=%.2f%n", course1.examNote, course1.verbalNote, course1.average());
        System.out.printf("Physics: Exam=%d Verbal=%d CourseAvg=%.2f%n", course2.examNote, course2.verbalNote, course2.average());
        System.out.printf("Chemistry: Exam=%d Verbal=%d CourseAvg=%.2f%n", course3.examNote, course3.verbalNote, course3.average());
    }
}
