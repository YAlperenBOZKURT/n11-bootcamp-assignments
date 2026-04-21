public class Main {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5};

        double harmonicSeries = 0.0;
        for (int number : numbers) {
            harmonicSeries += 1.0 / number;
        }

        double harmonicAverage = numbers.length / harmonicSeries;
        System.out.println("Harmonic Average: " + harmonicAverage);
    }
}
