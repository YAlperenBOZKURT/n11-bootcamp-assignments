import java.util.Scanner;

public class ActivitySuggestion {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the temperature: ");
        int temperature = input.nextInt();

        if (temperature < 5) {
            System.out.println("You can go skiing.");
        } else if (temperature >= 5 && temperature < 15) {
            System.out.println("You can go to the cinema.");
        } else if (temperature >= 15 && temperature < 25) {
            System.out.println("You can go for a picnic.");
        } else {
            System.out.println("You can go swimming.");
        }

        input.close();
    }
}