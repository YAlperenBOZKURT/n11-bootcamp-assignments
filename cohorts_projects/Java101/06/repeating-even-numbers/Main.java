public class Main {
    static boolean contains(int[] arr, int value) {
        for (int i : arr) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] list = {2, 3, 4, 2, 6, 8, 10, 6, 12, 8, 15, 3, 4};
        int[] repeated = new int[list.length];
        int index = 0;

        for (int i = 0; i < list.length; i++) {
            for (int j = i + 1; j < list.length; j++) {
                if (list[i] == list[j] && list[i] % 2 == 0) {
                    if (!contains(repeated, list[i])) {
                        repeated[index++] = list[i];
                    }
                    break;
                }
            }
        }

        System.out.println("Repeated even numbers:");
        for (int number : repeated) {
            if (number != 0) {
                System.out.println(number);
            }
        }
    }
}
