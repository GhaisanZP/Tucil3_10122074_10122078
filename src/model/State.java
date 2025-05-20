package model;

import solver.Solver;
import util.Heuristics;

/**
 * Kelas State merepresentasikan sebuah kondisi/papan dalam pencarian solusi puzzle Rush Hour.
 * Setiap state menyimpan papan, cost, parent (untuk rekonstruksi solusi), dan estimasi biaya (heuristik).
 */
public class State implements Comparable<State> {
    public char[][] board;        // Representasi papan permainan
    public int cost;              // Biaya aktual (g(n)) untuk mencapai state ini dari awal
    public int estimatedCost;     // Estimasi biaya ke goal (h(n)), tergantung algoritma
    public State parent;          // Referensi ke state sebelumnya (untuk pelacakan jalur)
    public int rows, cols;        // Ukuran papan

    /**
     * Konstruktor untuk membuat state baru.
     *
     * @param board  Papan permainan saat ini
     * @param n_rows Jumlah baris papan
     * @param n_cols Jumlah kolom papan
     * @param cost   Biaya aktual g(n)
     * @param parent State sebelumnya dalam jalur pencarian
     * @param exit   Posisi pintu keluar
     */
    public State(char[][] board, int n_rows, int n_cols,
                 int cost, State parent, Position exit) {
        this.board  = board;
        this.cost   = cost;
        this.parent = parent;
        this.rows   = n_rows;
        this.cols   = n_cols;

        // Hitung estimasi biaya h(n) hanya jika menggunakan A* atau Greedy
        if (Solver.useAStar || Solver.useGreedy) {
            switch (Solver.heuristicMode) {
                case MANHATTAN:
                    this.estimatedCost = Heuristics.manhattan(board, exit, rows, cols);
                    break;
                case BLOCKING:
                    this.estimatedCost = Heuristics.blockingCount(board, exit, rows, cols);
                    break;
                default: // COMBINED
                    this.estimatedCost = Heuristics.combined(board, exit, rows, cols);
            }
        } else {
            this.estimatedCost = 0;
        }
    }

    /**
     * Perbandingan antar state untuk keperluan antrian prioritas.
     * Bergantung pada algoritma yang digunakan (UCS, GBFS, A*).
     */
    @Override
    public int compareTo(State other) {
        if (Solver.useAStar) {
            // f(n) = g(n) + h(n)
            return Integer.compare(this.cost + this.estimatedCost,
                                   other.cost + other.estimatedCost);
        }
        if (Solver.useGreedy) {
            // h(n) saja
            return Integer.compare(this.estimatedCost, other.estimatedCost);
        }
        // UCS dan BnB: g(n) saja
        return Integer.compare(this.cost, other.cost);
    }
}
