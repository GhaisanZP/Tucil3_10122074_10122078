package util;

import java.io.*;
import model.Position;
import solver.Solver;

public class Parser {
    public static Position exit;
    public static int rows, cols;
    public static char[][] parseInput(String filename, int[] dimensions) throws IOException {
        int n_line = countLines(filename);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] size = br.readLine().split(" ");
        rows = n_line - 2;
        cols = Integer.parseInt(size[1]);
        br.readLine(); // Skip piece count

        char[][] board = new char[n_line-2][cols];
        for (int i = 0; i < n_line-2; i++) {
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

    public static int countLines(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int lines = 0;
        while (reader.readLine() != null) {
            lines++;
        }
        reader.close();
        return lines;
    }
}
