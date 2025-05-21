package util;

import model.Position;

/**
 * Kelas Heuristics menyediakan fungsi-fungsi heuristik yang digunakan untuk
 * algoritma pencarian seperti A*, Greedy Best First Search, dan Combined heuristic.
 * 
 * Terdapat tiga jenis heuristik:
 * 1. Manhattan Distance
 * 2. Jumlah kendaraan yang menghalangi
 * 3. Kombinasi dari keduanya
 */
public class Heuristics {

    /**
     * Menghitung jarak Manhattan antara kendaraan utama ('P') dan posisi keluar ('K').
     * Jarak ini merupakan jumlah langkah horizontal dan vertikal minimal untuk mencapai pintu keluar.
     *
     * @param board papan permainan
     * @param exit  posisi keluar
     * @param rows  jumlah baris papan
     * @param cols  jumlah kolom papan
     * @return jarak Manhattan dari 'P' ke 'K'
     */
    public static int manhattan(char[][] board, Position exit, int rows, int cols) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (board[i][j] == 'P')
                    return Math.abs(i - exit.row) + Math.abs(j - exit.col);
        return 0; // fallback jika 'P' tidak ditemukan (seharusnya tidak terjadi)
    }

    /**
     * Menghitung jumlah kendaraan yang menghalangi jalur kendaraan utama ('P') menuju keluar ('K').
     * Heuristik ini memperkirakan hambatan aktual tanpa memperhitungkan jarak.
     *
     * @param board papan permainan
     * @param exit  posisi keluar
     * @param rows  jumlah baris papan
     * @param cols  jumlah kolom papan
     * @return jumlah kendaraan yang menghalangi jalur
     */
    public static int blockingCount(char[][] board, Position exit, int rows, int cols) {
        int blockers = 0;
        int minR = rows, maxR = -1, minC = cols, maxC = -1;

        // Cari rentang posisi kendaraan utama ('P') di papan
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

        if (minR == maxR) {
            // Kendaraan utama bergerak horizontal
            for (int c = Math.min(exit.col, minC); c <= Math.max(exit.col, maxC); c++) {
                char cell = board[minR][c];
                if (cell != 'P' && cell != '.' && cell != 'K') {
                    blockers++;
                }
            }
        } else {
            // Kendaraan utama bergerak vertikal
            for (int r = Math.min(exit.row, minR); r <= Math.max(exit.row, maxR); r++) {
                char cell = board[r][minC];
                if (cell != 'P' && cell != '.' && cell != 'K') {
                    blockers++;
                }
            }
        }

        return blockers;
    }

    /**
     * Kombinasi dari heuristik Manhattan dan blocking count.
     * Biasanya digunakan untuk mendapatkan estimasi yang lebih informatif.
     *
     * @param board papan permainan
     * @param exit  posisi keluar
     * @param rows  jumlah baris papan
     * @param cols  jumlah kolom papan
     * @return kombinasi heuristik: jarak + jumlah blocker
     */
    public static int combined(char[][] board, Position exit, int rows, int cols) {
        return manhattan(board, exit, rows, cols)
             + blockingCount(board, exit, rows, cols);
    }
}
