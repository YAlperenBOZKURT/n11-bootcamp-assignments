import java.util.Scanner;

public class TaxiMeter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter distance in km: ");
        double km = scanner.nextDouble();

        double openingFee = 10.0;
        double pricePerKm = 2.20;
        double minimumFare = 20.0;

        double totalFare = openingFee + (km * pricePerKm);
        totalFare = (totalFare < minimumFare) ? minimumFare : totalFare;

        System.out.println("Total fare: " + totalFare + " TL");

        scanner.close();
    }
}