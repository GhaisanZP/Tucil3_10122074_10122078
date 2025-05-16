package util;

import java.util.*;
import model.Position;

/**
 * Heuristics for A*: admissible heuristic combining
 * distance of primary piece to exit and number of blocking cars.
 */
public class Heuristics {
    public static int heuristic(char[][] board, Position exit, int rows, int cols) {
        // Find bounding box of primary piece 'P'
        int minR = rows, maxR = -1, minC = cols, maxC = -1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'P') {
                    minR = Math.min(minR, r);
                    maxR = Math.max(maxR, r);
                    minC = Math.min(minC, c);
                    maxC = Math.max(maxC, c);
                }
            }
        }
        // If no primary piece found, heuristic = 0
        if (maxR < minR || maxC < minC) return 0;

        boolean horizontal = (minR == maxR);
        int distance;
        Set<Character> blockers = new HashSet<>();

        if (horizontal) {
            int row = minR;
            // moving right
            if (exit.col > maxC) {
                for (int c = maxC + 1; c < exit.col && c < board[row].length; c++) {
                    char cell = board[row][c];
                    if (cell != '.' && cell != 'K') blockers.add(cell);
                }
                distance = exit.col - maxC;
            } else {
                // moving left
                for (int c = exit.col + 1; c < minC && c < board[row].length; c++) {
                    char cell = board[row][c];
                    if (cell != '.' && cell != 'K') blockers.add(cell);
                }
                distance = minC - exit.col;
            }
        } else {
            int col = minC;
            // moving down
            if (exit.row > maxR) {
                for (int r = maxR + 1; r < exit.row && col < board[r].length; r++) {
                    char cell = board[r][col];
                    if (cell != '.' && cell != 'K') blockers.add(cell);
                }
                distance = exit.row - maxR;
            } else {
                // moving up
                for (int r = exit.row + 1; r < minR && col < board[r].length; r++) {
                    char cell = board[r][col];
                    if (cell != '.' && cell != 'K') blockers.add(cell);
                }
                distance = minR - exit.row;
            }
        }
        return distance + blockers.size();
    }
}
