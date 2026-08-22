package gui;

import algorithms.Algorithms;
import algorithms.PathfindingAlgorithm;
import maze.Cell;
import maze.CellType;
import maze.Maze;
import maze.MazeGenerator;
import maze.MazeParser;
import renderer.MazeRenderer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

//The main window: a toolbar with the algorithm, speed and run controls,
//the maze in the middle and a status line at the bottom. The maze can be
//edited with the mouse: the left button uses the selected tool, the right
//button always erases.
public class VisualizerWindow extends JFrame {

    private static final long SLOWEST_DELAY = 200;

    private final MazePanel mazePanel;
    private final JComboBox<PathfindingAlgorithm> algorithmBox =
            new JComboBox<>(Algorithms.all().toArray(new PathfindingAlgorithm[0]));
    private final JButton runButton = new JButton("Run");
    private final JButton clearButton = new JButton("Clear path");
    private final JComboBox<EditTool> toolBox = new JComboBox<>(EditTool.values());
    private final JSlider speedSlider = new JSlider(0, 100, 75);
    private final JLabel statusLabel =
            new JLabel("Draw on the maze, pick an algorithm and press Run (Space). Right-click erases.");

    //Read by the search thread, so it must not touch the slider itself
    private volatile long delayMilliseconds;

    //Menu items that would change the maze, disabled while a search runs
    private final List<JMenuItem> mazeMenuItems = new ArrayList<>();
    private final JFileChooser fileChooser = new JFileChooser("mazes");

    private SearchWorker runningSearch;

    public VisualizerWindow(Maze maze) {
        super("AI Pathfinding Visualizer");

        mazePanel = new MazePanel(maze);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setLayout(new BorderLayout());
        add(createToolbar(), BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        runButton.addActionListener(event -> toggleSearch());
        clearButton.addActionListener(event -> clearSearch());
        speedSlider.addChangeListener(event -> updateDelay());
        updateDelay();

        bindKey("SPACE", "toggleSearch", this::toggleSearch);
        bindKey("ESCAPE", "clearSearch", () -> {
            if (!isSearching()) {
                clearSearch();
            }
        });

        fileChooser.setFileFilter(new FileNameExtensionFilter("Maze files (*.txt)", "txt"));

        MouseAdapter editor = new MazeEditor();
        mazePanel.addMouseListener(editor);
        mazePanel.addMouseMotionListener(editor);

        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenu mazeMenu = new JMenu("Maze");
        mazeMenu.add(menuItem("New random maze...", KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcut), this::newRandomMaze));
        mazeMenu.add(menuItem("New empty maze", null, this::newEmptyMaze));
        mazeMenu.addSeparator();
        mazeMenu.add(menuItem("Open...", KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcut), this::openMaze));
        mazeMenu.add(menuItem("Save as...", KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut), this::saveMaze));

        JMenu analysisMenu = new JMenu("Analysis");
        analysisMenu.add(menuItem("Compare all algorithms", KeyStroke.getKeyStroke(KeyEvent.VK_K, shortcut),
                () -> ComparisonDialog.show(this, mazePanel.getMaze())));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(mazeMenu);
        menuBar.add(analysisMenu);
        return menuBar;
    }

    private JMenuItem menuItem(String label, KeyStroke shortcut, Runnable action) {
        JMenuItem item = new JMenuItem(label);
        item.setAccelerator(shortcut);
        item.addActionListener(event -> action.run());
        mazeMenuItems.add(item);
        return item;
    }

    private void bindKey(String key, String name, Runnable action) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), name);
        getRootPane().getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                action.run();
            }
        });
    }

    private void newRandomMaze() {
        Maze current = mazePanel.getMaze();
        JSpinner rows = new JSpinner(new SpinnerNumberModel(current.getRows(), MazeGenerator.MINIMUM_SIZE, 151, 2));
        JSpinner columns = new JSpinner(new SpinnerNumberModel(current.getColumns(), MazeGenerator.MINIMUM_SIZE, 251, 2));
        JSpinner mud = new JSpinner(new SpinnerNumberModel(15, 0, 80, 5));
        JTextField seed = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        form.add(new JLabel("Rows:"));
        form.add(rows);
        form.add(new JLabel("Columns:"));
        form.add(columns);
        form.add(new JLabel("Mud (%):"));
        form.add(mud);
        form.add(new JLabel("Seed (blank = random):"));
        form.add(seed);

        int choice = JOptionPane.showConfirmDialog(
                this, form, "New random maze", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        long mazeSeed;

        try {
            mazeSeed = seed.getText().isBlank()
                    ? new Random().nextInt(1_000_000)
                    : Long.parseLong(seed.getText().trim());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "The seed must be a whole number.");
            return;
        }

        Maze maze = new MazeGenerator(mazeSeed, (Integer) mud.getValue() / 100.0)
                .generate((Integer) rows.getValue(), (Integer) columns.getValue());

        showMaze(maze, "New maze from seed " + mazeSeed + ".");
    }

    private void newEmptyMaze() {
        Maze current = mazePanel.getMaze();
        Maze maze = new Maze(current.getRows(), current.getColumns());

        maze.setStart(current.getRows() / 2, 1);
        maze.setEnd(current.getRows() / 2, current.getColumns() - 2);

        showMaze(maze, "Empty maze: draw some walls and mud.");
    }

    private void openMaze() {
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            Maze maze = MazeParser.load(fileChooser.getSelectedFile().toPath());
            showMaze(maze, "Opened " + fileChooser.getSelectedFile().getName() + ".");
        } catch (IOException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "Could not open maze:\n" + exception.getMessage());
        }
    }

    private void saveMaze() {
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            Files.writeString(fileChooser.getSelectedFile().toPath(), MazeRenderer.toText(mazePanel.getMaze().copy()));
            setStatus("Saved " + fileChooser.getSelectedFile().getName() + ".");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Could not save maze:\n" + exception.getMessage());
        }
    }

    void showMaze(Maze maze, String status) {
        mazePanel.setMaze(maze);
        setStatus(status);
        pack();
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        toolbar.add(new JLabel("Algorithm:"));
        toolbar.add(algorithmBox);
        toolbar.add(runButton);
        toolbar.add(clearButton);
        toolbar.add(new JLabel("Draw:"));
        toolbar.add(toolBox);
        toolbar.add(new JLabel("Speed:"));
        toolbar.add(speedSlider);

        return toolbar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 10, 6, 10));
        statusBar.add(statusLabel, BorderLayout.CENTER);
        return statusBar;
    }

    //Speed 100 runs without pauses; lower speeds slow down quadratically,
    //which gives finer control at the fast end of the slider
    private void updateDelay() {
        double slowness = 1 - speedSlider.getValue() / 100.0;
        delayMilliseconds = Math.round(SLOWEST_DELAY * slowness * slowness);
    }

    MazePanel getMazePanel() {
        return mazePanel;
    }

    boolean isSearching() {
        return runningSearch != null;
    }

    void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void toggleSearch() {
        if (isSearching()) {
            runningSearch.cancel(true);
        } else {
            startSearch();
        }
    }

    private void startSearch() {
        PathfindingAlgorithm algorithm = (PathfindingAlgorithm) algorithmBox.getSelectedItem();

        mazePanel.getMaze().clearSearchResults();
        runningSearch = new SearchWorker(algorithm, mazePanel.getMaze());

        setControlsRunning(true);
        setStatus("Running " + algorithm.getName() + "...");
        runningSearch.execute();
    }

    void clearSearch() {
        mazePanel.getMaze().clearSearchResults();
        mazePanel.repaint();
        setStatus(" ");
    }

    //Everything except the run button and the speed slider is locked during a search
    protected void setControlsRunning(boolean running) {
        runButton.setText(running ? "Stop" : "Run");
        algorithmBox.setEnabled(!running);
        clearButton.setEnabled(!running);
        toolBox.setEnabled(!running);
        mazeMenuItems.forEach(item -> item.setEnabled(!running));
    }

    //Applies a tool to the cell under the mouse, then repaints.
    //Old search results are cleared first, since they no longer match the maze.
    void edit(EditTool tool, Cell cell) {
        if (isSearching() || cell == null) {
            return;
        }

        mazePanel.getMaze().clearSearchResults();
        tool.apply(mazePanel.getMaze(), cell);
        mazePanel.repaint();
    }

    private final class MazeEditor extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            paint(event);
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            paint(event);
        }

        private void paint(MouseEvent event) {
            EditTool tool = SwingUtilities.isRightMouseButton(event)
                    ? EditTool.ERASE
                    : (EditTool) toolBox.getSelectedItem();

            edit(tool, mazePanel.cellAt(event.getX(), event.getY()));
        }
    }

    private record SearchSummary(List<Cell> path, int cellsExplored) {
    }

    //Runs the search off the Swing thread and repaints after every step
    private final class SearchWorker extends SwingWorker<SearchSummary, Void> {

        private final PathfindingAlgorithm algorithm;
        private final Maze maze;

        SearchWorker(PathfindingAlgorithm algorithm, Maze maze) {
            this.algorithm = algorithm;
            this.maze = maze;
        }

        @Override
        protected SearchSummary doInBackground() {
            int[] explored = new int[1];

            List<Cell> path = algorithm.findPath(maze, (searchedMaze, cell) -> {
                explored[0]++;
                mazePanel.repaint();
                pause(delayMilliseconds);
            });

            //Draw the final path one cell at a time
            for (Cell cell : path) {
                if (!cell.isStart() && !cell.isEnd()) {
                    cell.setType(CellType.PATH);
                    mazePanel.repaint();
                    pause(delayMilliseconds / 2);
                }
            }

            return new SearchSummary(path, explored[0]);
        }

        private void pause(long milliseconds) {
            if (isCancelled()) {
                throw new CancellationException();
            }

            if (milliseconds == 0) {
                return;
            }

            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException exception) {
                throw new CancellationException();
            }
        }

        @Override
        protected void done() {
            runningSearch = null;
            setControlsRunning(false);
            mazePanel.repaint();

            try {
                SearchSummary summary = get();
                setStatus(describe(summary));
            } catch (CancellationException exception) {
                setStatus(algorithm.getName() + " stopped.");
            } catch (InterruptedException | ExecutionException exception) {
                setStatus(algorithm.getName() + " failed: " + exception.getMessage());
            }
        }

        private String describe(SearchSummary summary) {
            if (summary.path().isEmpty()) {
                return algorithm.getName() + " found no path after exploring "
                        + summary.cellsExplored() + " cells.";
            }

            return String.format(
                    "%s: path of %d moves, cost %d, %d cells explored.",
                    algorithm.getName(),
                    summary.path().size() - 1,
                    Maze.pathCost(summary.path()),
                    summary.cellsExplored()
            );
        }
    }
}
