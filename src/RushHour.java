import java.io.*;
import java.nio.file.*;
import solver.Solver;
import util.Parser;

public class RushHour {
    public static void main(String[] args) throws Exception {
        // Validasi jumlah argumen
        if (args.length < 3) {
            // Jika argumen kurang dari 2, tampilkan petunjuk penggunaan
            if (args.length < 2) {
                System.out.println("Usage: java RushHour <filename> <ucs|gbfs|astar|bnb> <manhattan|blocking|combined>");
                return;
            }
            // Jika algoritma bukan ucs/bnb, tapi heuristik belum diberikan
            if (!args[1].equals("ucs") && !args[1].equals("bnb")) {
                System.out.println("Usage: java RushHour <filename> <ucs|gbfs|astar|bnb> <manhattan|blocking|combined>");
                return;
            }
        }

        // Siapkan nama output berdasarkan input dan parameter
        String inputName = Paths.get(args[0]).getFileName().toString();
        Path outputPath;

        if (args.length < 3) {
            // Untuk algoritma yang tidak butuh heuristik (UCS, BnB)
            outputPath = Paths.get("test", "solusi_" + args[1] + "_" + inputName);
        } else {
            // Untuk algoritma yang menggunakan heuristik
            outputPath = Paths.get("test", "solusi_" + args[1] + "_" + args[2] + "_" + inputName);
        }

        // Pastikan direktori output ada
        Files.createDirectories(outputPath.getParent());

        // Siapkan file output
        PrintStream fileOut = new PrintStream(new FileOutputStream(outputPath.toFile()), true);
        PrintStream console = System.out;

        // Ganti System.out agar output dikirim ke file dan terminal
        System.setOut(new PrintStream(new OutputStream() {
            private final byte ESC = 0x1B;
            private boolean inAnsi = false;

            @Override
            public void write(int b) throws IOException {
                console.write(b); // tetap tampilkan di console
                if (b == ESC) {
                    inAnsi = true;
                    return;
                }
                if (inAnsi) {
                    if ((char) b == 'm') {
                        inAnsi = false;
                    }
                    return;
                }
                fileOut.write(b); // juga tulis ke file
            }

            @Override public void flush() throws IOException {
                console.flush();
                fileOut.flush();
            }

            @Override public void close() throws IOException {
                console.close();
                fileOut.close();
            }
        }, true));

        // Atur heuristik jika diperlukan
        if (!args[1].equals("ucs") && !args[1].equals("bnb")) {
            String heu = args[2].toLowerCase();
            switch (heu) {
                case "manhattan":
                    Solver.heuristicMode = Solver.HeuristicMode.MANHATTAN;
                    break;
                case "blocking":
                    Solver.heuristicMode = Solver.HeuristicMode.BLOCKING;
                    break;
                case "combined":
                    Solver.heuristicMode = Solver.HeuristicMode.COMBINED;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown heuristic: " + heu);
            }
        }

        // Atur algoritma pencarian
        String alg = args[1].toLowerCase();
        switch (alg) {
            case "ucs":
                Solver.useAStar = false;
                Solver.useGreedy = false;
                Solver.useBnB = false;
                System.out.println("Running Uniform Cost Search...");
                break;
            case "astar":
                Solver.useAStar = true;
                Solver.useGreedy = false;
                Solver.useBnB = false;
                System.out.println("Running A* Search...");
                break;
            case "gbfs":
                Solver.useAStar = false;
                Solver.useGreedy = true;
                Solver.useBnB = false;
                System.out.println("Running Greedy Best First Search...");
                break;
            case "bnb":
                Solver.useAStar = false;
                Solver.useGreedy = false;
                Solver.useBnB = true;
                System.out.println("Running Branch and Bound Search...");
                break;
            default:
                Solver.useAStar = true;
                Solver.useGreedy = false;
                Solver.useBnB = false;
                System.out.println("Running A* Search...");
                break;
        }

        // Parse input board
        int[] dimensions = new int[2]; // [rows, cols]
        char[][] board = Parser.parseInput(args[0], dimensions);

        // Jalankan solver
        Solver.solve(board, Parser.rows, Parser.cols, Parser.exit);

        // Tutup file output
        fileOut.close();
    }
}
