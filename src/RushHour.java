import java.io.*;
import java.nio.file.*;
import solver.Solver;
import util.Parser;

public class RushHour {
    public static void main(String[] args) throws Exception {
        // Validasi jumlah argumen yang diberikan
        if (args.length < 2) {
            System.out.println("Usage: java RushHour <filename> <ucs|gbfs|astar|bnb> <manhattan|blocking|combined>");
            return;
        }

        // Menyiapkan file output berdasarkan nama file input
        String inputName = Paths.get(args[0]).getFileName().toString();
        Path outputPath = Paths.get("test", "solusi_" + inputName);

        // Membuat direktori jika belum ada
        Files.createDirectories(outputPath.getParent());

        // Menyiapkan stream untuk menulis ke file output
        PrintStream fileOut = new PrintStream(new FileOutputStream(outputPath.toFile()), true);
        PrintStream console = System.out;

        // Mengganti System.out agar menulis ke file *dan* ke console, tetapi mengabaikan ANSI escape codes saat ke file
        System.setOut(new PrintStream(new OutputStream() {
            private final byte ESC = 0x1B;
            private boolean inAnsi = false;

            @Override
            public void write(int b) throws IOException {
                console.write(b); // tetap print ke console
                if (b == ESC) {
                    inAnsi = true;
                    return;
                }
                if (inAnsi) {
                    if ((char) b == 'm') {
                        inAnsi = false;
                    }
                    return; // abaikan escape sequence saat ke file
                }
                fileOut.write(b); // tulis ke file
            }

            @Override
            public void flush() throws IOException {
                console.flush();
                fileOut.flush();
            }

            @Override
            public void close() throws IOException {
                console.close();
                fileOut.close();
            }
        }, true));

        // Menentukan mode heuristik berdasarkan argumen ketiga
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

        // Menentukan algoritma yang digunakan berdasarkan argumen kedua
        String alg = args[1].toLowerCase();
        switch (alg) {
            case "ucs":
                Solver.useAStar  = false;
                Solver.useGreedy = false;
                Solver.useBnB    = false;
                System.out.println("Running Uniform Cost Search...");
                break;
            case "astar":
                Solver.useAStar  = true;
                Solver.useGreedy = false;
                Solver.useBnB    = false;
                System.out.println("Running A* Search...");
                break;
            case "gbfs":
                Solver.useAStar  = false;
                Solver.useGreedy = true;
                Solver.useBnB    = false;
                System.out.println("Running Greedy Best First Search...");
                break;
            case "bnb":
                Solver.useAStar  = false;
                Solver.useGreedy = false;
                Solver.useBnB    = true;
                System.out.println("Running Branch and Bound Search...");
                break;
            default:
                // Default fallback: A*
                Solver.useAStar  = true;
                Solver.useGreedy = false;
                Solver.useBnB    = false;
                System.out.println("Running A* Search...");
                break;
        }

        // Parsing input dari file untuk mendapatkan papan permainan
        int[] dimensions = new int[2]; // [rows, cols]
        char[][] board = Parser.parseInput(args[0], dimensions);

        // Memanggil metode utama untuk menyelesaikan puzzle
        Solver.solve(board, Parser.rows, Parser.cols, Parser.exit);

        // Menutup file output
        fileOut.close();
    }
}
