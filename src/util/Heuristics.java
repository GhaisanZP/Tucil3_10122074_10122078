package util;

import model.Position;

public class Heuristics {
    public static int heuristic(char[][] board, Position exit, int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'P') {
                    return Math.abs(i - exit.row) + Math.abs(j - exit.col);
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
