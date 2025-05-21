package solver;

import java.util.*;
import model.*;

/**
 * Solver class untuk menyelesaikan puzzle Rush Hour menggunakan algoritma pencarian:
 * A*, Greedy, dan Branch and Bound (BnB).
 */
public class Solver {
    // Pengaturan algoritma yang digunakan
    public static boolean useAStar = false;
    public static boolean useGreedy = false;
    public static boolean useBnB    = false;

    // Ukuran papan
    public static int rows, cols;

    // Pengaturan warna terminal
    public static boolean enableColor = true;

    // Mode heuristik
    public enum HeuristicMode { MANHATTAN, BLOCKING, COMBINED }
    public static HeuristicMode heuristicMode = HeuristicMode.COMBINED;

    // Kode warna ANSI
    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";  // P (primary piece)
    private static final String GREEN  = "\u001B[32m";  // K (exit)
    private static final String YELLOW = "\u001B[33m";  // Piece yang aktif bergerak

    // Variabel internal
    private static Position exitPosition;
    private static long nodesExpanded;
    private static long startTime, endTime;
    private static long elapsedNanos;

    /**
     * Fungsi utama untuk menjalankan algoritma pencarian dari kondisi awal.
     */
    public static void solve(char[][] initialBoard, int n_rows, int n_cols, Position exit) {
        rows = n_rows;
        cols = n_cols;
        exitPosition = exit;
        nodesExpanded = 0;
        startTime = System.nanoTime();

        PriorityQueue<State> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        int bestCost = Integer.MAX_VALUE;
        State bestGoal = null;

        State start = new State(initialBoard, rows, cols, 0, null, exitPosition);
        pq.add(start);

        while (!pq.isEmpty()) {
            State current = pq.poll();
            String boardStr = boardToString(current.board);

            if (visited.contains(boardStr)) continue;
            visited.add(boardStr);
            nodesExpanded++;

            if (isGoal(current.board, exitPosition)) {
                // Modifikasi papan jika goal tercapai (untuk mencetak solusi)
                modifyBoardForGoal(current);

                if (useBnB) {
                    bestCost = current.cost;
                    bestGoal = current;
                    continue;
                } else {
                    endTime = System.nanoTime();
                    printSolution(current);
                    printStats(nodesExpanded, startTime, endTime);
                    return;
                }
            }

            // Tambahkan semua successor ke antrian
            for (State next : generateSuccessors(current, exitPosition)) {
                if (useBnB && next.cost >= bestCost) continue;
                pq.add(next);
            }
        }

        // Jika BnB dan goal terbaik ditemukan
        if (useBnB && bestGoal != null) {
            endTime = System.nanoTime();
            printSolution(bestGoal);
            printStats(nodesExpanded, startTime, endTime);
            return;
        }

        System.out.println("No solution found.");
    }

    /** Mengecek apakah papan saat ini adalah goal state */
    static boolean isGoal(char[][] board, Position exit) {
        return board[exit.row][exit.col] == 'P';
    }

    /** Statistik proses pencarian */
    private static void printStats(long nodes, long t0, long t1) {
        double secs = (t1 - t0) / 1_000_000_000.0;
        System.out.println("\n--- Search stats ---");
        System.out.printf("Nodes expanded : %d\n", nodes);
        System.out.printf("Elapsed time   : %.3f s\n", secs);
        System.out.println("--------------------\n");
    }

    /** Generate semua successor yang mungkin dari sebuah state */
    static List<State> generateSuccessors(State current, Position exit) {
        char[][] board = current.board;
        Set<Character> pieces = new HashSet<>();

        // Identifikasi semua pieces di papan
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                char c = board[i][j];
                if (c != '.' && c != 'K' && c != ' ') pieces.add(c);
            }

        List<State> successors = new ArrayList<>();
        for (char piece : pieces) {
            List<Position> positions = new ArrayList<>();

            // Posisi semua bagian dari piece
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    if (board[i][j] == piece) positions.add(new Position(i, j));

            boolean horizontal = positions.stream().allMatch(p -> p.row == positions.get(0).row);

            // Gerakan horizontal
            if (horizontal) {
                int r = positions.get(0).row;
                int minCol = positions.stream().mapToInt(p -> p.col).min().getAsInt();
                int maxCol = positions.stream().mapToInt(p -> p.col).max().getAsInt();
                int i = 0;

                // Geser ke kiri
                while (minCol - i > 0 && (board[r][minCol - 1 - i] == '.' || board[r][minCol - 1 - i] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    for (int j = 0; j <= i; j++) {
                        newBoard[r][maxCol - j] = '.';
                        newBoard[r][minCol - 1 - j] = piece;
                    }
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, exit));
                    i++;
                }

                // Geser ke kanan
                i = 0;
                while (maxCol + i < board[r].length - 1 && (board[r][maxCol + 1 + i] == '.' || board[r][maxCol + 1 + i] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    for (int j = 0; j <= i; j++) {
                        newBoard[r][minCol + j] = '.';
                        newBoard[r][maxCol + 1 + j] = piece;
                    }
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, exit));
                    i++;
                }

            } else {
                // Gerakan vertikal
                int c = positions.get(0).col;
                int minRow = positions.stream().mapToInt(p -> p.row).min().getAsInt();
                int maxRow = positions.stream().mapToInt(p -> p.row).max().getAsInt();
                int length = rows;

                // Geser ke atas
                int i = 0;
                while (minRow - i > 0 && (board[minRow - 1 - i][c] == '.' || board[minRow - 1 - i][c] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    for (int j = 0; j <= i; j++) {
                        newBoard[maxRow - j][c] = '.';
                        newBoard[minRow - 1 - j][c] = piece;
                    }
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, exit));
                    i++;
                }

                // Geser ke bawah
                i = 0;
                while (maxRow + i < length - 1 && (board[maxRow + 1 + i][c] == '.' || board[maxRow + 1 + i][c] == 'K')) {
                    char[][] newBoard = copyBoard(board);
                    for (int j = 0; j <= i; j++) {
                        newBoard[minRow + j][c] = '.';
                        newBoard[maxRow + 1 + j][c] = piece;
                    }
                    successors.add(new State(newBoard, rows, cols, current.cost + 1, current, exit));
                    i++;
                }
            }
        }

        return successors;
    }

    /** Menyalin papan */
    static char[][] copyBoard(char[][] board) {
        char[][] newBoard = new char[rows][cols];
        for (int i = 0; i < rows; i++) newBoard[i] = board[i].clone();
        return newBoard;
    }

    /** Konversi papan ke string untuk keperluan hash */
    private static String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) sb.append(row).append("\n");
        return sb.toString();
    }

    /** Modifikasi papan untuk menampilkan tujuan */
    private static void modifyBoardForGoal(State current) {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (current.board[i][j] == 'P') positions.add(new Position(i, j));

        boolean horizontal = positions.stream().allMatch(p -> p.row == positions.get(0).row);
        if (horizontal) {
            if (exitPosition.col == 0) current.board[exitPosition.row][exitPosition.col + 1] = '.';
            else current.board[exitPosition.row][exitPosition.col - 1] = '.';
        } else {
            if (exitPosition.row == 0) current.board[exitPosition.row + 1][exitPosition.col] = '.';
            else current.board[exitPosition.row - 1][exitPosition.col] = '.';
        }
        current.board[exitPosition.row][exitPosition.col] = 'K';
    }

    /** Cetak solusi langkah per langkah */
    private static void printSolution(State goal) {
        List<State> path = new ArrayList<>();
        for (State s = goal; s != null; s = s.parent) path.add(s);
        Collections.reverse(path);

        System.out.println("Papan Awal");
        printBoard(path.get(0).board, '\0');

        for (int i = 1; i < path.size(); i++) {
            char[][] prev = path.get(i - 1).board;
            char[][] curr = path.get(i).board;
            boolean lastMove = isGoal(curr, exitPosition);
            char moved = detectMovedPiece(prev, curr);
            if (moved == '?' && lastMove) moved = 'P';

            String dir = (moved == 'P' && lastMove)
                       ? lastMoveDirection(prev, moved)
                       : detectDirection(prev, curr, moved);

            System.out.printf("\nGerakan %d: %c-%s%n", i, moved, dir);
            printBoard(curr, moved);
        }
    }

    /** Deteksi arah gerakan terakhir menuju goal */
    private static String lastMoveDirection(char[][] prev, char piece) {
        int minR = rows, maxR = -1, minC = cols, maxC = -1;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (prev[r][c] == piece) {
                    minR = Math.min(minR, r);
                    maxR = Math.max(maxR, r);
                    minC = Math.min(minC, c);
                    maxC = Math.max(maxC, c);
                }
        if (minR == maxR) return exitPosition.col > maxC ? "kanan" : "kiri";
        else return exitPosition.row > maxR ? "bawah" : "atas";
    }

    /** Deteksi piece yang bergerak antara dua papan */
    private static char detectMovedPiece(char[][] a, char[][] b) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (a[i][j] != b[i][j] && b[i][j] != '.' && b[i][j] != 'K')
                    return b[i][j];
        return 'P';
    }

    /** Deteksi arah gerakan sebuah piece */
    private static String detectDirection(char[][] a, char[][] b, char piece) {
        Position before = findPosition(a, piece);
        Position after  = findPosition(b, piece);
        if (before == null || after == null) return piece == 'P' ? lastMoveDirection(a, piece) : "?";
        return before.row == after.row
               ? (after.col > before.col ? "kanan" : "kiri")
               : (after.row > before.row ? "bawah" : "atas");
    }

    /** Temukan posisi awal dari piece */
    private static Position findPosition(char[][] board, char piece) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][j] == piece) return new Position(i, j);
        return null;
    }

    /** Cetak papan ke layar */
    public static void printBoard(char[][] board, char activePiece) {
        for (char[] row : board) {
            for (char c : row) {
                if      (c == 'P' && enableColor) System.out.print(RED + c + RESET);
                else if (c == 'K' && enableColor) System.out.print(GREEN + c + RESET);
                else if (c == activePiece && enableColor) System.out.print(YELLOW + c + RESET);
                else System.out.print(c);
            }
            System.out.println();
        }
    }

    public static List<char[][]> solveAndReturnPath(
            char[][] initialBoard, int n_rows, int n_cols, Position exit) {
        rows = n_rows;
        cols = n_cols;
        exitPosition = exit;
        nodesExpanded = 0;
        long start = System.nanoTime();

        PriorityQueue<State> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        State goalState = null;

        pq.add(new State(initialBoard, rows, cols, 0, null, exit));

        while (!pq.isEmpty()) {
            State current = pq.poll();
            String key = boardToString(current.board);
            if (visited.contains(key)) continue;
            visited.add(key);
            nodesExpanded++;

            if (isGoal(current.board, exitPosition)) {
                goalState = current;
                break;
            }
            for (State next : generateSuccessors(current, exitPosition)) {
                pq.add(next);
            }
        }

        elapsedNanos = System.nanoTime() - start;

        if (goalState == null) return Collections.emptyList();

        LinkedList<char[][]> path = new LinkedList<>();
        for (State s = goalState; s != null; s = s.parent) {
            char[][] copy = new char[rows][cols];
            for (int r = 0; r < rows; r++) copy[r] = s.board[r].clone();
            path.addFirst(copy);
        }
        return path;
    }

    /** GUI akan memanggil ini untuk menampilkan “banyak node” */
    public static long getNodesExpanded() {
        return nodesExpanded;
    }
    /** GUI akan memanggil ini untuk menampilkan “waktu” dalam detik */
    public static double getElapsedSeconds() {
        return elapsedNanos / 1_000_000_000.0;
    }
}
