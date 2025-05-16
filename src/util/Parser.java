package util;

import java.io.*;
import model.Position;

public class Parser {
    public static Position exit;
    public static int rows, cols;
    public static char[][] parseInput(String filename, int[] dimensions) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] size = br.readLine().split(" ");
        rows = Integer.parseInt(size[0]);
        cols = Integer.parseInt(size[1]);
        br.readLine(); // Skip piece count

        char[][] board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();
            if (line.length() < cols) {
                // Isi titik '.' di awal baris hingga panjangnya sama dengan jumlah kolom
                int padding = cols - line.length();
                line = line + " ".repeat(padding);
            }
            board[i] = line.toCharArray();
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'K') {
                    exit = new Position(i, j);
                }
            }
        }

        br.close();
        return board;
    }
}
