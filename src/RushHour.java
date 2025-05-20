import java.io.*;
import java.nio.file.*;
import solver.Solver;
import util.Parser;

public class RushHour {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            if (args.length < 2) {
                System.out.println("Usage: java RushHour <filename> <ucs|gbfs|astar|bnb> <manhattan|blocking|combined>");
                return;    
            }
            if (!args[1].equals("ucs") && !args[1].equals("bnb")) {
                System.out.println("Usage: java RushHour <filename> <ucs|gbfs|astar|bnb> <manhattan|blocking|combined>");
                return;    
            }
        }

        String inputName = Paths.get(args[0]).getFileName().toString();
        Path outputPath;
        if (args.length < 3) {
            outputPath = Paths.get("test", "solusi_" + args[1] + "_" + inputName) ;
        } else {
            outputPath = Paths.get("test", "solusi_" + "_" + args[1] + "_" + args[2] + "_" + inputName);
        }
        Files.createDirectories(outputPath.getParent());
        PrintStream fileOut = new PrintStream(new FileOutputStream(outputPath.toFile()), true);
        PrintStream console = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            private final byte ESC = 0x1B;
            private boolean inAnsi = false;

            @Override public void write(int b) throws IOException {
                console.write(b);
                if (b == ESC) {
                    inAnsi = true;
                    return;    
                }
                if (inAnsi) {
                    if ((char)b == 'm') {
                        inAnsi = false;  
                    }
                    return; 
                }
                fileOut.write(b);
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
                Solver.useAStar  = true;
                Solver.useGreedy = false;
                Solver.useBnB    = false;
                System.out.println("Running A* Search...");
                break;
        }
        
        int[] dimensions = new int[2]; // [rows, cols]
        char[][] board = Parser.parseInput(args[0], dimensions);
        Solver.solve(board, Parser.rows, Parser.cols, Parser.exit);

        fileOut.close();
    }
}
