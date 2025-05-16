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
            board[i] = br.readLine().toCharArray();
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
