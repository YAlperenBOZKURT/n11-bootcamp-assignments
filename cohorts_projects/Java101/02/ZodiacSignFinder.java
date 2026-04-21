import java.util.Scanner;

public class ZodiacSignFinder {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int month, day;
        String zodiacSign = "";
        boolean isValid = true;

        System.out.print("Enter month (1-12): ");
        month = input.nextInt();

        System.out.print("Enter day: ");
        day = input.nextInt();

        if (month == 1) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 21) ? "Capricorn" : "Aquarius";
            } else {
                isValid = false;
            }
        } else if (month == 2) {
            if (day >= 1 && day <= 29) {
                zodiacSign = (day <= 19) ? "Aquarius" : "Pisces";
            } else {
                isValid = false;
            }
        } else if (month == 3) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 20) ? "Pisces" : "Aries";
            } else {
                isValid = false;
            }
        } else if (month == 4) {
            if (day >= 1 && day <= 30) {
                zodiacSign = (day <= 20) ? "Aries" : "Taurus";
            } else {
                isValid = false;
            }
        } else if (month == 5) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 21) ? "Taurus" : "Gemini";
            } else {
                isValid = false;
            }
        } else if (month == 6) {
            if (day >= 1 && day <= 30) {
                zodiacSign = (day <= 22) ? "Gemini" : "Cancer";
            } else {
                isValid = false;
            }
        } else if (month == 7) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 22) ? "Cancer" : "Leo";
            } else {
                isValid = false;
            }
        } else if (month == 8) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 22) ? "Leo" : "Virgo";
            } else {
                isValid = false;
            }
        } else if (month == 9) {
            if (day >= 1 && day <= 30) {
                zodiacSign = (day <= 22) ? "Virgo" : "Libra";
            } else {
                isValid = false;
            }
        } else if (month == 10) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 22) ? "Libra" : "Scorpio";
            } else {
                isValid = false;
            }
        } else if (month == 11) {
            if (day >= 1 && day <= 30) {
                zodiacSign = (day <= 21) ? "Scorpio" : "Sagittarius";
            } else {
                isValid = false;
            }
        } else if (month == 12) {
            if (day >= 1 && day <= 31) {
                zodiacSign = (day <= 21) ? "Sagittarius" : "Capricorn";
            } else {
                isValid = false;
            }
        } else {
            isValid = false;
        }

        if (isValid) {
            System.out.println("Your zodiac sign is: " + zodiacSign);
        } else {
            System.out.println("Invalid date entered.");
        }

        input.close();
    }
}