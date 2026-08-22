package gui;

import algorithms.Algorithms;
import analysis.AlgorithmComparison;
import maze.Maze;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Locale;

//Shows a table comparing every algorithm on the maze currently on screen
final class ComparisonDialog {

    static final String[] COLUMNS = {"Algorithm", "Path length", "Path cost", "Cells explored", "Time (ms)"};

    private ComparisonDialog() {
    }

    static void show(JFrame owner, Maze maze) {
        JDialog dialog = new JDialog(owner, "Compare algorithms", true);

        JTable table = new JTable(createModel(maze));
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        JLabel note = new JLabel("Each algorithm ran on its own copy of the maze; times are the fastest of five runs.");
        note.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(note, BorderLayout.SOUTH);
        dialog.setSize(640, 260);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    static DefaultTableModel createModel(Maze maze) {
        List<AlgorithmComparison.Result> results = AlgorithmComparison.run(Algorithms.all(), maze::copy);

        DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (AlgorithmComparison.Result result : results) {
            model.addRow(new Object[]{
                    result.algorithm(),
                    result.foundPath() ? result.moves() : "no path",
                    result.foundPath() ? result.cost() : "-",
                    result.cellsExplored(),
                    String.format(Locale.ROOT, "%.3f", result.milliseconds())
            });
        }

        return model;
    }
}
