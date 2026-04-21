import java.util.Scanner;

public class GroceryCashRegister {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double pearPrice = 2.14;
        double applePrice = 3.67;
        double tomatoPrice = 1.11;
        double bananaPrice = 0.95;
        double eggplantPrice = 5.00;

        System.out.print("How many kilos of pears? : ");
        double pearKg = scanner.nextDouble();

        System.out.print("How many kilos of apples? : ");
        double appleKg = scanner.nextDouble();

        System.out.print("How many kilos of tomatoes? : ");
        double tomatoKg = scanner.nextDouble();

        System.out.print("How many kilos of bananas? : ");
        double bananaKg = scanner.nextDouble();

        System.out.print("How many kilos of eggplants? : ");
        double eggplantKg = scanner.nextDouble();

        double total =
                (pearKg * pearPrice) +
                (appleKg * applePrice) +
                (tomatoKg * tomatoPrice) +
                (bananaKg * bananaPrice) +
                (eggplantKg * eggplantPrice);

        System.out.println("Total Price: " + total + " TL");

        scanner.close();
    }
}