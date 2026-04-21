import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Array size n: ");
        int n = input.nextInt();

        int[] list = new int[n];

        System.out.println("Enter array elements:");
        for (int i = 0; i < n; i++) {
            System.out.print((i + 1) + ". Element: ");
            list[i] = input.nextInt();
        }

        Arrays.sort(list);

        System.out.print("Sorting: ");
        for (int number : list) {
            System.out.print(number + " ");
        }

        input.close();
    }
}
