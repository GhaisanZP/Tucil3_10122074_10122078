package solver;

import model.*;

import java.util.*;

public class Solver {
    public static boolean useAStar = false;
    public static int rows, cols;

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
        PriorityQueue<State> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        State start = new State(initialBoard, rows, cols, 0, null, useAStar, exit);
        pq.add(start);

        while (!pq.isEmpty()) {
            State current = pq.poll();

            if (isGoal(current.board, exit)) {
                printSolution(current);
                return;
            }

            String boardStr = boardToString(current.board);
            if (visited.contains(boardStr)) continue;
            visited.add(boardStr);

            for (State next : generateSuccessors(current, exit)) {
                pq.add(next);
            }
        }

        System.out.println("No solution found.");
    }

    static boolean isGoal(char[][] board, Position exit) {
        return board[exit.row][exit.col] == 'P';
    }

    static List<State> generateSuccessors(State current, Position exit) {
        char[][] board = current.board;
        Set<Character> pieces = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = board[i][j];
                if (c != '.' && c != 'K') pieces.add(c);
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

    static String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) sb.append(row);
        return sb.toString();
    }

    static void printSolution(State goal) {
        List<State> path = new ArrayList<>();
        while (goal != null) {
            path.add(goal);
            goal = goal.parent;
        }
        Collections.reverse(path);
        System.out.println("Solved in " + (path.size() - 1) + " moves.");
        for (State s : path) printBoard(s.board);
    }

    static void printBoard(char[][] board) {
        for (char[] row : board) System.out.println(new String(row));
        System.out.println();
    }
}
