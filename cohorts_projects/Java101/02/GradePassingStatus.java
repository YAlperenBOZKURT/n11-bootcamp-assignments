import java.util.Scanner;

public class GradePassingStatus {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int math, physics, turkish, chemistry, music;
        int total = 0;
        int validCourseCount = 0;
        double average;

        System.out.print("Enter Math grade: ");
        math = input.nextInt();

        System.out.print("Enter Physics grade: ");
        physics = input.nextInt();

        System.out.print("Enter Turkish grade: ");
        turkish = input.nextInt();

        System.out.print("Enter Chemistry grade: ");
        chemistry = input.nextInt();

        System.out.print("Enter Music grade: ");
        music = input.nextInt();

        if (math >= 0 && math <= 100) {
            total += math;
            validCourseCount++;
        }

        if (physics >= 0 && physics <= 100) {
            total += physics;
            validCourseCount++;
        }

        if (turkish >= 0 && turkish <= 100) {
            total += turkish;
            validCourseCount++;
        }

        if (chemistry >= 0 && chemistry <= 100) {
            total += chemistry;
            validCourseCount++;
        }

        if (music >= 0 && music <= 100) {
            total += music;
            validCourseCount++;
        }

        if (validCourseCount > 0) {
            average = (double) total / validCourseCount;
            String result = average >= 55 ? "Passed the class" : "Failed the class";

            System.out.println("Average: " + average);
            System.out.println("Result: " + result);
        } else {
            System.out.println("No valid grades were entered.");
        }

        input.close();
    }
}