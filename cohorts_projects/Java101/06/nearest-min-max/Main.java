public class Main {
    public static void main(String[] args) {
        int[] list = {15, 12, 788, 1, -1, -778, 2, 0};
        int input = 5;

        Integer nearestSmaller = null;
        Integer nearestLarger = null;

        for (int number : list) {
            if (number < input) {
                if (nearestSmaller == null || number > nearestSmaller) {
                    nearestSmaller = number;
                }
            }

            if (number > input) {
                if (nearestLarger == null || number < nearestLarger) {
                    nearestLarger = number;
                }
            }
        }

        System.out.println("Array: {15, 12, 788, 1, -1, -778, 2, 0}");
        System.out.println("Input Number: " + input);
        System.out.println("Nearest smaller number: " + nearestSmaller);
        System.out.println("Nearest larger number: " + nearestLarger);
    }
}
