package model;

/**
 * Kelas Position merepresentasikan posisi suatu objek
 * di dalam papan permainan sebagai koordinat (baris, kolom).
 */
public class Position {
    public int row; // Baris dari posisi
    public int col; // Kolom dari posisi

    /**
     * Konstruktor untuk membuat posisi baru dengan koordinat tertentu.
     *
     * @param row Baris (y) dari posisi.
     * @param col Kolom (x) dari posisi.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
