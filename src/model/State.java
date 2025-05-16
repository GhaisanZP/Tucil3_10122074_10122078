package model;

import java.util.*;
import solver.Solver;
import util.Heuristics;

public class State implements Comparable<State> {
    public char[][] board;
    public int cost;
    public int estimatedCost;
    public State parent;
    public int rows, cols;

    public State(char[][] board, int n_rows, int n_cols, int cost, State parent, boolean useAStar, Position exit) {
        this.board = board;
        this.cost = cost;
        this.parent = parent;
        this.rows = n_rows;
        this.cols = n_cols;
        if (useAStar) {
            this.estimatedCost = Heuristics.heuristic(board, exit, rows, cols);
        }
    }

    @Override
    public int compareTo(State other) {
        if (Solver.useAStar) {
            return Integer.compare(this.cost + this.estimatedCost, other.cost + other.estimatedCost);
        } else {
            return Integer.compare(this.cost, other.cost);
        }
    }
}
