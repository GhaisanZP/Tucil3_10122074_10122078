package util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import model.Position;

public class Parser {
    public static Position exit;
    public static int rows, cols;

    /**
     * Membaca file, menambah baris/kolom jika K berada di luar grid,
     * dan mengembalikan board final beserta exit position.
     */
    public static char[][] parseInput(String filename, int[] dimensions) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));

        // baris 0: "R C"
        String[] size = lines.get(0).trim().split("\\s+");
        int origR = Integer.parseInt(size[0]);
        int origC = Integer.parseInt(size[1]);

        // baris 1: jumlah pieces (abaikan)
        List<String> gridLines = lines.subList(2, lines.size());

        // cari posisi K dalam gridLines (raw index)
        int rawR = -1, rawC = -1;
        for (int i = 0; i < gridLines.size(); i++) {
            int idx = gridLines.get(i).indexOf('K');
            if (idx != -1) {
                rawR = i;
                rawC = idx;
                break;
            }
        }
        if (rawR == -1) {
            throw new IllegalArgumentException("Tidak ditemukan huruf K di input.");
        }

        // tentukan extra top/bottom/left/right
        boolean extraTop    = rawR < 0;
        boolean extraBottom = rawR >= origR;
        boolean extraLeft   = rawC < 0;
        boolean extraRight  = rawC >= origC;

        // ukuran akhir
        rows = origR + (extraTop ? 1 : 0) + (extraBottom ? 1 : 0);
        cols = origC + (extraLeft ? 1 : 0) + (extraRight ? 1 : 0);

        // simpan ke dimensions
        dimensions[0] = rows;
        dimensions[1] = cols;

        // buat board dengan spasi
        char[][] board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(board[i], ' ');
        }

        // hitung offset kalau ada extra top/left
        int rowOff = extraTop ? 1 : 0;
        int colOff = extraLeft ? 1 : 0;

        // copy grid asli (tanpa K) ke board
        for (int i = 0; i < origR; i++) {
            String line = gridLines.get(i);
            for (int j = 0; j < origC; j++) {
                char c = (j < line.length() ? line.charAt(j) : ' ');
                if (c != 'K') {
                    board[i + rowOff][j + colOff] = c;
                }
            }
        }

        // hitung posisi exit di board akhir
        int exitR = rawR + rowOff;
        int exitC = rawC + colOff;
        board[exitR][exitC] = 'K';
        exit = new Position(exitR, exitC);

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
