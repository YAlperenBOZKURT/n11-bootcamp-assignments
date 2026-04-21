import java.util.Scanner;

public class FlightTicketCalculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        double distance, normalPrice, ageDiscountRate = 0, ageDiscountAmount, discountedPrice, roundTripDiscountAmount, totalPrice;
        int age, tripType;

        System.out.print("Enter distance in km: ");
        distance = input.nextDouble();

        System.out.print("Enter your age: ");
        age = input.nextInt();

        System.out.print("Enter trip type (1 => One Way, 2 => Round Trip): ");
        tripType = input.nextInt();

        if (distance <= 0 || age <= 0 || (tripType != 1 && tripType != 2)) {
            System.out.println("Invalid Input!");
        } else {
            normalPrice = distance * 0.10;

            if (age < 12) {
                ageDiscountRate = 0.50;
            } else if (age <= 24) {
                ageDiscountRate = 0.10;
            } else if (age > 65) {
                ageDiscountRate = 0.30;
            }

            ageDiscountAmount = normalPrice * ageDiscountRate;
            discountedPrice = normalPrice - ageDiscountAmount;

            if (tripType == 2) {
                roundTripDiscountAmount = discountedPrice * 0.20;
                totalPrice = (discountedPrice - roundTripDiscountAmount) * 2;
            } else {
                totalPrice = discountedPrice;
            }

            System.out.println("Total Price: " + totalPrice + " TL");
        }

        input.close();
    }
}