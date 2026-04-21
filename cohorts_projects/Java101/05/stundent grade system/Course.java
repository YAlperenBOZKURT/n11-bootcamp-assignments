public class Course {
    Teacher teacher;
    String name;
    String code;
    String prefix;
    int examNote;
    int verbalNote;
    double verbalRate;

    public Course(String name, String code, String prefix, double verbalRate) {
        this.name = name;
        this.code = code;
        this.prefix = prefix;
        this.verbalRate = verbalRate;
        this.examNote = 0;
        this.verbalNote = 0;
    }

    public void addTeacher(Teacher t) {
        if (this.prefix.equals(t.branch)) {
            this.teacher = t;
        }
    }

    public void printTeacher() {
        if (teacher != null) {
            System.out.println(name + " teacher: " + teacher.name);
        } else {
            System.out.println("No teacher assigned for " + name);
        }
    }

    public double average() {
        return (verbalNote * verbalRate) + (examNote * (1 - verbalRate));
    }
}
