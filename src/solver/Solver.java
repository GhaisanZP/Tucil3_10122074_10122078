package solver;

import java.util.*;
import model.*;

public class Solver {
    public static boolean useAStar = false;
    public static int rows, cols;

    // ANSI color codes
    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";  // primary piece P
    private static final String GREEN  = "\u001B[32m";  // exit K
    private static final String YELLOW = "\u001B[33m";  // moving piece
    private static Position exitPosition;
    private static long nodesExpanded;
    private static long startTime, endTime;

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

    public static void solve(char[][] initialBoard, int n_rows, int n_cols, Position exit) {
        rows = n_rows;
        cols = n_cols;
        exitPosition = exit;
        nodesExpanded = 0;
        startTime = System.nanoTime();
        PriorityQueue<State> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        State start = new State(initialBoard, rows, cols, 0, null, useAStar, exitPosition);
        pq.add(start);

        while (!pq.isEmpty()) {
            State current = pq.poll();
            nodesExpanded++;
            //printBoard(current.board);

            if (isGoal(current.board, exitPosition)) {
                endTime = System.nanoTime();
                printSolution(current);
                printStats(nodesExpanded, startTime, endTime);
                return;
            }

            String boardStr = boardToString(current.board);
            if (visited.contains(boardStr)) continue;
            visited.add(boardStr);

            for (State next : generateSuccessors(current, exitPosition)) {
                pq.add(next);
            }
        }

        System.out.println("No solution found.");
    }

    static boolean isGoal(char[][] board, Position exit) {
        return board[exit.row][exit.col] == 'P';
    }

    private static void printStats(long nodes, long t0, long t1) {
        double secs = (t1 - t0) / 1_000_000_000.0;
        System.out.println("\n--- Search stats ---");
        System.out.printf("Nodes expanded : %d\n", nodes);
        System.out.printf("Elapsed time   : %.3f s\n", secs);
        System.out.println("--------------------\n");
    }

    static List<State> generateSuccessors(State current, Position exit) {
        char[][] board = current.board;
        Set<Character> pieces = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = board[i][j];
                if (c != '.' && c != 'K' && c != ' ') pieces.add(c);
            }
        }

        List<State> successors = new ArrayList<>();
        for (char piece : pieces) {
            List<Position> positions = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] == piece) positions.add(new Position(i, j));
                }
            }
            //System.out.println(piece);

            boolean horizontal = positions.stream().allMatch(p -> p.row == positions.get(0).row);

            if (horizontal) {
                // Move left
                int r = positions.get(0).row;
                int minCol = positions.stream().mapToInt(p -> p.col).min().getAsInt();
                int maxCol = positions.stream().mapToInt(p -> p.col).max().getAsInt();
                if (minCol > 0 && (board[r][minCol - 1] == '.' || board[r][minCol - 1] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    // for (int i = positions.size() - 1; i >= 0; i--) {
                    //     newBoard[r][positions.get(i).col + 1] = '.';
                    // }
                    newBoard[r][maxCol] = '.';
                    newBoard[r][minCol - 1] = piece;
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, useAStar, exit));
                }

                // Move right
                if (maxCol < board[r].length - 1 && (board[r][maxCol + 1] == '.' || board[r][maxCol + 1] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    // for (int i = 0; i < positions.size(); i++) {
                    //     newBoard[r][positions.get(i).col] = '.';
                    // }
                    newBoard[r][minCol] = '.';
                    newBoard[r][maxCol + 1] = piece;
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, useAStar, exit));
                }
            } else {
                // Vertical
                int c = positions.get(0).col;
                int minRow = positions.stream().mapToInt(p -> p.row).min().getAsInt();
                int maxRow = positions.stream().mapToInt(p -> p.row).max().getAsInt();

                int length = 0;

                for (int i = 0; i < board.length; i++) {
                    if (c < board[i].length) {
                        length++;
                    }
                }

                // Move up
                if (minRow > 0 && (board[minRow - 1][c] == '.' || board[minRow - 1][c] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    // for (int i = positions.size() - 1; i >= 0; i--) {
                    //     newBoard[positions.get(i).row + 1][c] = '.';
                    // }
                    newBoard[maxRow][c] = '.';
                    newBoard[minRow - 1][c] = piece;
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, useAStar, exit));
                }

                // Move down
                if (maxRow < length - 1 && (board[maxRow + 1][c] == '.' || board[maxRow + 1][c] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    // for (int i = 0; i < positions.size(); i++) {
                    //     newBoard[positions.get(i).row][c] = '.';
                    // }
                    newBoard[minRow][c] = '.';
                    //System.out.println(maxRow);
                    newBoard[maxRow + 1][c] = piece;
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, useAStar, exit));
                }
            }
        }
        return successors;
    }

    static char[][] copyBoard(char[][] board) {
        char[][] newBoard = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            newBoard[i] = board[i].clone();
        }
        return newBoard;
    }

    private static String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) sb.append(row).append("\n");
        return sb.toString();
    }

    private static void printSolution(State goal) {
        List<State> path = new ArrayList<>();
        for (State s = goal; s != null; s = s.parent) path.add(s);
        Collections.reverse(path);

        // Papan Awal
        System.out.println("Papan Awal");
        printBoard(path.get(0).board, '\0');

        for (int i = 1; i < path.size(); i++) {
            char[][] prev = path.get(i-1).board;
            char[][] curr = path.get(i).board;
            boolean lastMove = isGoal(curr, exitPosition);
            char moved = detectMovedPiece(prev, curr);
            if (moved == '?' && lastMove) {
                // P piece moved into exit
                moved = 'P';
            }
            String dir;
            if (moved == 'P' && lastMove) {
                dir = lastMoveDirection(prev, moved);
            } else {
                dir = detectDirection(prev, curr, moved);
            }

            System.out.printf("\nGerakan %d: %c-%s%n", i, moved, dir);
            printBoard(curr, moved);
        }
    }

    private static String lastMoveDirection(char[][] prev, char piece) {
        // determine orientation of P in prev
        int minR = rows, maxR = -1, minC = cols, maxC = -1;
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++)
            if (prev[r][c] == piece) {
                minR = Math.min(minR, r);
                maxR = Math.max(maxR, r);
                minC = Math.min(minC, c);
                maxC = Math.max(maxC, c);
            }
        if (minR == maxR) {
            // horizontal
            return exitPosition.col > maxC ? "kanan" : "kiri";
        } else {
            return exitPosition.row > maxR ? "bawah" : "atas";
        }
    }

    private static char detectMovedPiece(char[][] a, char[][] b) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (a[i][j] != b[i][j] && b[i][j] != '.' && b[i][j] != 'K') {
                    return b[i][j];
                }
            }
        }
        return '?';
    }

    private static String detectDirection(char[][] a, char[][] b, char piece) {
        Position before = findPosition(a, piece);
        Position after  = findPosition(b, piece);
        if (before == null || after == null) {
            // fallback for unexpected case: use lastMoveDirection logic only for P
            if (piece == 'P') return lastMoveDirection(a, piece);
            return "?";
        }
        if (before.row == after.row) {
            return after.col > before.col ? "kanan" : "kiri";
        } else {
            return after.row > before.row ? "bawah" : "atas";
        }
    }

    private static Position findPosition(char[][] board, char piece) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == piece) return new Position(i, j);
            }
        }
        return null;
    }

    public static void printBoard(char[][] board, char activePiece) {
        for (char[] row : board) {
            for (char c : row) {
                if (c == 'P')      System.out.print(RED   + c + RESET);
                else if (c == 'K') System.out.print(GREEN + c + RESET);
                else if (c == activePiece) System.out.print(YELLOW + c + RESET);
                else System.out.print(c);
            }
            System.out.println();
        }
    }
}
