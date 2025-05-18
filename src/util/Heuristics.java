package util;

import model.Position;

/**
 * Heuristics for A*: admissible heuristic combining
 * distance of primary piece to exit and number of blocking cars.
 */
public class Heuristics {
    // 1) Manhattan distance saja
    public static int manhattan(char[][] board, Position exit, int rows, int cols) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (board[i][j] == 'P')
                    return Math.abs(i - exit.row) + Math.abs(j - exit.col);
        return 0;
    }

    // 2) Jumlah blocker saja
    public static int blockingCount(char[][] board, Position exit, int rows, int cols) {
        int blockers = 0;
        int minR = rows, maxR = -1, minC = cols, maxC = -1;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (board[r][c] == 'P') {
                    minR = Math.min(minR, r);
                    maxR = Math.max(maxR, r);
                    minC = Math.min(minC, c);
                    maxC = Math.max(maxC, c);
                }

        if (minR == maxR) {
            // horizontal → cek di baris itu, antara P dan exit
            for (int c = Math.min(exit.col, minC); c <= Math.max(exit.col, maxC); c++)
                if (board[minR][c] != 'P' && board[minR][c] != '.' && board[minR][c] != 'K')
                    blockers++;
        } else {
            // vertical → cek di kolom itu
            for (int r = Math.min(exit.row, minR); r <= Math.max(exit.row, maxR); r++)
                if (board[r][minC] != 'P' && board[r][minC] != '.' && board[r][minC] != 'K')
                    blockers++;
        }
        return blockers;
    }

    // 3) Combined: distance + blockers
    public static int combined(char[][] board, Position exit, int rows, int cols) {
        return manhattan(board, exit, rows, cols)
             + blockingCount(board, exit, rows, cols);
    }
}
