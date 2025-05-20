import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import solver.Solver;
import util.Parser;

public class RushHourGUI extends JFrame {
    private JLabel statsLabel;
    private JComboBox<String> algoBox, heuBox;
    private JButton chooseFileBtn, runBtn;
    private JPanel boardPanel;
    private File inputFile;
    private List<char[][]> solutionPath; // hasil solve: list konfigurasi board

    public RushHourGUI() {
        super("Rush Hour Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        // --- Top controls ---
        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT, 6,6));
        chooseFileBtn = new JButton("Pilih File…");
        algoBox = new JComboBox<>(new String[]{"UCS","GBFS","A*","BnB"});
        heuBox  = new JComboBox<>(new String[]{"Manhattan","Blocking","Combined"});
        runBtn   = new JButton("Jalankan");
        control.add(chooseFileBtn);
        control.add(new JLabel("Algoritma:")); control.add(algoBox);
        control.add(new JLabel("Heuristic:"));  control.add(heuBox);
        control.add(runBtn);
        add(control, BorderLayout.NORTH);

        // --- Center: tempat papan ---
        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);
        statsLabel = new JLabel(" ");
        statsLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statsLabel, BorderLayout.SOUTH);
        
        // --- Listeners ---
        chooseFileBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(".");
            if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
                inputFile = fc.getSelectedFile();
                chooseFileBtn.setText(inputFile.getName());
            }
        });

        runBtn.addActionListener(e -> {
            if (inputFile == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih file puzzle terlebih dahulu.");
                return;
            }
            runSolverAndAnimate();
        });

        setSize(600, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void runSolverAndAnimate() {
        try {
            // Atur pilihan algoritma & heuristic ke Solver
            String alg = (String)algoBox.getSelectedItem();
            Solver.useAStar  = "A*".equals(alg);
            Solver.useGreedy = "GBFS".equals(alg);
            Solver.useBnB    = "BnB".equals(alg);
            // (UCS: kedua flag false)

            String heu = ((String)heuBox.getSelectedItem()).toLowerCase();
            switch (heu) {
                case "manhattan": Solver.heuristicMode = Solver.HeuristicMode.MANHATTAN; break;
                case "blocking":  Solver.heuristicMode = Solver.HeuristicMode.BLOCKING;  break;
                default:          Solver.heuristicMode = Solver.HeuristicMode.COMBINED;  break;
            }

            // Parse dan solve
            int[] dim = new int[2];
            char[][] startBoard = Parser.parseInput(inputFile.getAbsolutePath(), dim);

            // 1) Tampilkan board awal apa adanya
            boardPanel.removeAll();
            displayBoard(startBoard, '\0');

            // 2) Jalankan solver & dapatkan path
            solutionPath = Solver.solveAndReturnPath(
                startBoard, Parser.rows, Parser.cols, Parser.exit);
            long nodes = Solver.getNodesExpanded();
            double secs = Solver.getElapsedSeconds();

            // update statsLabel
            statsLabel.setText(
                String.format("Nodes expanded: %,d    Waktu eksekusi: %.3f s", nodes, secs)
            );
            if (solutionPath != null && !solutionPath.isEmpty()) {
               char[][] last = solutionPath.get(solutionPath.size() - 1);
               for (int r = 0; r < last.length; r++) {
                   for (int c = 0; c < last[0].length; c++) {
                       if (last[r][c] == 'P') {
                           last[r][c] = '.'; 
                       }
                   }
               }
           }
            // 3) Tangani no‐solution
            if (solutionPath == null || solutionPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No solution found", "Hasil", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 4) Animasi pergerakan
            Timer timer = new Timer(700, null);
            final int[] idx = {1};
            timer.addActionListener(evt -> {
                if (idx[0] < solutionPath.size()) {
                    char[][] prev = solutionPath.get(idx[0]-1);
                    char[][] curr = solutionPath.get(idx[0]);
                    char moved = detectMovedPiece(prev, curr);
                    displayBoard(curr, moved);
                    idx[0]++;
                } else {
                    ((Timer)evt.getSource()).stop();
                }
            });
            timer.start();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Gagal membaca file: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RushHourGUI::new);
    }
    private void displayBoard(char[][] board, char activePiece) {
        boardPanel.removeAll();
        int R = board.length, C = board[0].length;
        boardPanel.setLayout(new GridLayout(R, C, 1,1));
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                char c = board[i][j];
                JLabel cell = new JLabel(String.valueOf(c), SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBorder(new LineBorder(Color.DARK_GRAY));
                if (c == 'P') {
                    cell.setBackground(Color.RED);
                } else if (c == 'K') {
                    cell.setBackground(Color.GREEN);
                } else if (c == activePiece) {
                    cell.setBackground(Color.YELLOW);
                } else if (c == '.') {
                    cell.setBackground(Color.WHITE); // jalan kosong jadi putih
                } else {
                    cell.setBackground(Color.LIGHT_GRAY); // mobil jadi abu-abu
                }
                boardPanel.add(cell);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

        /**  
     * Cari karakter piece yang pindah antara board a dan b.  
     */
    private char detectMovedPiece(char[][] a, char[][] b) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] != b[i][j] && b[i][j] != '.' && b[i][j] != 'K') {
                    return b[i][j];
                }
            }
        }
        return '\0';
    }


}
   

