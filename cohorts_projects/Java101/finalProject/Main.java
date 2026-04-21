import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int rows;
        int cols;

        while (true) {
            System.out.print("Satir sayisi giriniz (min 2): ");
            rows = input.nextInt();

            System.out.print("Sutun sayisi giriniz (min 2): ");
            cols = input.nextInt();

            if (rows >= 2 && cols >= 2) {
                break;
            }

            System.out.println("Matris boyutu en az 2x2 olmalidir. Tekrar giriniz.");
        }

        MineSweeper game = new MineSweeper(rows, cols);
        game.run(input);

        input.close();
    }
}
