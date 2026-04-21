public class Main {
    static boolean isCounted(int[] counted, int value) {
        for (int c : counted) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] list = {10, 20, 20, 10, 10, 20, 5, 20};
        int[] counted = new int[list.length];
        int index = 0;

        System.out.println("Array: [10, 20, 20, 10, 10, 20, 5, 20]");
        System.out.println("Frequency Counts");

        for (int i = 0; i < list.length; i++) {
            if (isCounted(counted, list[i])) {
                continue;
            }

            int count = 1;
            for (int j = i + 1; j < list.length; j++) {
                if (list[i] == list[j]) {
                    count++;
                }
            }

            counted[index++] = list[i];
            System.out.println(list[i] + " repeated " + count + " times.");
        }
    }
}
