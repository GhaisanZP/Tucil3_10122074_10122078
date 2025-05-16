import util.Parser;
import solver.Solver;
import model.Position;

public class RushHour {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java RushHour <filename> <ucs|astar>");
            return;
        }

        if (args[1].equalsIgnoreCase("astar")) {
            Solver.useAStar = true;
            System.out.println("Running A* Search...");
        } else {
            System.out.println("Running Uniform Cost Search...");
        }

        int[] dimensions = new int[2]; // [rows, cols]
        char[][] board = Parser.parseInput(args[0], dimensions);
        Solver.solve(board, Parser.rows, Parser.cols, Parser.exit);
    }
}
