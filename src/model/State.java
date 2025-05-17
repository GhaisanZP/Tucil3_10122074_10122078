package model;

import solver.Solver;
import util.Heuristics;

public class State implements Comparable<State> {
    public char[][] board;
    public int cost;
    public int estimatedCost;
    public State parent;
    public int rows, cols;

    public State(char[][] board, int n_rows, int n_cols,
                 int cost, State parent, Position exit) {
        this.board = board;
        this.cost  = cost;
        this.parent= parent;
        this.rows  = n_rows;
        this.cols  = n_cols;

        // hitung h(n) jika A* atau GBFS
        if (Solver.useAStar || Solver.useGreedy) {
            this.estimatedCost =
                Heuristics.heuristic(board, exit, rows, cols);
        } else {
            this.estimatedCost = 0;
        }
    }

    @Override
    public int compareTo(State other) {
        if (Solver.useAStar) {
            // f(n) = g(n) + h(n)
            return Integer.compare(this.cost + this.estimatedCost,
                                   other.cost + other.estimatedCost);
        }
        if (Solver.useGreedy) {
            // purely h(n)
            return Integer.compare(this.estimatedCost,
                                   other.estimatedCost);
        }
        // Uniform Cost Search: purely g(n)
        return Integer.compare(this.cost, other.cost);
    }
}
