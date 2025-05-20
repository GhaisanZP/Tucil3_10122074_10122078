package util;

import java.io.*;
import model.Position;

public class Parser {
    // Menyimpan posisi pintu keluar (ditandai dengan 'K' di papan)
    public static Position exit;

    // Menyimpan jumlah baris dan kolom papan
    public static int rows, cols;

    /**
     * Mem-parsing input dari file untuk membentuk papan permainan.
     * @param filename Nama file input.
     * @param dimensions Array yang digunakan untuk menyimpan dimensi papan [rows, cols].
     * @return Matriks karakter yang merepresentasikan papan permainan.
    */
    public static char[][] parseInput(String filename, int[] dimensions) throws IOException {
        int n_line = countLines(filename);  // Hitung total baris dalam file
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // Baca baris pertama untuk mendapatkan ukuran papan
        String[] size = br.readLine().split(" ");
        rows = n_line - 2;  // Mengabaikan dua baris pertama (ukuran dan jumlah pieces)
        cols = Integer.parseInt(size[1]);

        dimensions[0] = rows;
        dimensions[1] = cols;

        br.readLine(); // Lewati baris jumlah pieces

        char[][] board = new char[rows][cols];

        // Baca setiap baris papan permainan
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();

            // Jika panjang baris kurang dari jumlah kolom, tambahkan spasi di akhir
            if (line.length() < cols) {
                int padding = cols - line.length();
                line = line + " ".repeat(padding);  // Tambah padding spasi
            }

            board[i] = line.toCharArray();

            // Cari posisi keluar (K)
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'K') {
                    exit = new Position(i, j);  // Simpan posisi keluar
                }
            }
        }

        br.close();
        return board;
    }

    /**
     * Menghitung jumlah baris dalam file input.
     *
     * @param filename Nama file input.
     * @return Jumlah baris dalam file.
     */
    public static int countLines(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int lines = 0;
        while (reader.readLine() != null) {
            lines++;
        }
        reader.close();
        return lines;
    }
}
