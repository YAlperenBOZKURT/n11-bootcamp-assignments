import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Random rand = new Random();
        int number = rand.nextInt(100);

        Scanner input = new Scanner(System.in);
        int right = 0;
        int selected;
        int[] wrong = new int[5];
        boolean isWin = false;
        boolean isWrong = false;

        while (right < 5) {
            System.out.print("Please enter your guess: ");
            selected = input.nextInt();

            if (selected < 0 || selected > 99) {
                System.out.println("Please enter a value between 0 and 99.");
                if (isWrong) {
                    right++;
                    System.out.println("Too many invalid inputs. Remaining attempts: " + (5 - right));
                } else {
                    isWrong = true;
                    System.out.println("Next invalid input will reduce your remaining attempts.");
                }
                continue;
            }

            if (selected == number) {
                System.out.println("Congratulations, correct guess! Number: " + number);
                isWin = true;
                break;
            } else {
                System.out.println("Wrong guess!");
                if (selected > number) {
                    System.out.println(selected + " is greater than the hidden number.");
                } else {
                    System.out.println(selected + " is less than the hidden number.");
                }

                wrong[right++] = selected;
                System.out.println("Remaining attempts: " + (5 - right));
            }
        }

        if (!isWin) {
            System.out.println("You lost!");
            if (!isWrong) {
                System.out.println("Your guesses: " + Arrays.toString(wrong));
            }
        }

        input.close();
    }
}
