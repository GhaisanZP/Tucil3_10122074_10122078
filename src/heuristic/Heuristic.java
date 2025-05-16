package heuristic;

import model.Position;

public class Heuristic {
    public static int heuristic(char[][] board, Position exit) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'P') {
                    return Math.abs(i - exit.row) + Math.abs(j - exit.col);
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
