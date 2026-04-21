import java.util.Scanner;

public class VatCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the price: ");
        double price = scanner.nextDouble();

        double vatRate = (price > 0 && price < 1000) ? 0.18 : 0.08;
        double vatAmount = price * vatRate;
        double totalPrice = price + vatAmount;

        System.out.println("Price without VAT: " + price);
        System.out.println("VAT rate: " + (vatRate * 100) + "%");
        System.out.println("VAT amount: " + vatAmount);
        System.out.println("Price with VAT: " + totalPrice);

        scanner.close();
    }
}