import java.util.Random;
import java.util.Scanner;

public class MineSweeper {
    private final int rows;
    private final int cols;
    private final int mineCount;
    private final String[][] mineMap;
    private final String[][] playerMap;
    private int safeOpened;

    public MineSweeper(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = (rows * cols) / 4;
        this.mineMap = new String[rows][cols];
        this.playerMap = new String[rows][cols];
        this.safeOpened = 0;
        initializeMaps();
        placeMines();
    }

    private void initializeMaps() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mineMap[i][j] = "-";
                playerMap[i][j] = "-";
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int placed = 0;

        while (placed < mineCount) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (!mineMap[r][c].equals("*")) {
                mineMap[r][c] = "*";
                placed++;
            }
        }
    }

    private void printMap(String[][] map) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    private boolean isValidCoordinate(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private boolean isSelectedBefore(int r, int c) {
        return !playerMap[r][c].equals("-");
    }

    private int countAdjacentMines(int r, int c) {
        int count = 0;

        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                if (i == r && j == c) {
                    continue;
                }

                if (isValidCoordinate(i, j) && mineMap[i][j].equals("*")) {
                    count++;
                }
            }
        }

        return count;
    }

    public void run(Scanner input) {
        System.out.println("Mayinlarin Konumu");
        printMap(mineMap);
        System.out.println("===========================");
        System.out.println("Mayin Tarlasi Oyununa Hosgeldiniz !");
        printMap(playerMap);

        while (safeOpened < (rows * cols - mineCount)) {
            System.out.print("Satir Giriniz : ");
            int row = input.nextInt();

            System.out.print("Sutun Giriniz : ");
            int col = input.nextInt();

            if (!isValidCoordinate(row, col)) {
                System.out.println("Gecersiz koordinat, tekrar deneyin.");
                continue;
            }

            if (isSelectedBefore(row, col)) {
                System.out.println("Bu koordinat daha once secildi, baska bir koordinat girin.");
                continue;
            }

            if (mineMap[row][col].equals("*")) {
                System.out.println("Game Over!!");
                System.out.println("===========================");
                return;
            }

            int adjacentMines = countAdjacentMines(row, col);
            playerMap[row][col] = String.valueOf(adjacentMines);
            safeOpened++;

            System.out.println("===========================");
            printMap(playerMap);
        }

        System.out.println("Oyunu Kazandiniz !");
        printMap(playerMap);
        System.out.println("===========================");
    }
}
