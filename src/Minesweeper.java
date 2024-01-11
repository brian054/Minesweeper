import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper {
    // Default grid size is beginner: 9 x 9 grid with 10 bombs
    static int rowSize = 9;
    static int colSize = 9;

    // Our game board
    static Grid grid;

    // JLabel "Timer"
    static JLabel timer_text = new JLabel("Seconds: 0");
    static int startValue = 0;
    static Timer timer;

    // Number of Bombs text
    static JLabel number_of_bombs_text = new JLabel("Number of Bombs: " + 10);

    // This function will be used to repaint our game board to the frame
    public static void repaintBoard(Grid grid, JPanel main_panel, JFrame frame) {
        main_panel.add(grid, BorderLayout.CENTER);
        frame.add(main_panel);
        frame.setVisible(true);
    }

    // Starts our timer when the first Cell in the grid is clicked
    public static void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (grid.gameOver) {
                    timer.cancel();
                    startValue = 0;
                } else {
                    // Redraw text on JLabel
                    timer_text.setText("Seconds: " + startValue);
                    startValue += 1;
                }
            }
        }, 0,  1000);
    }

    public static void stopTimer() {
        timer.cancel();
    }

    public static void setBombsText(int number_of_bombs, boolean red_flag) {
        if (red_flag) {
            number_of_bombs_text.setText("Number of Bombs: " + (number_of_bombs + 1));
        } else {
            number_of_bombs_text.setText("Number of Bombs: " + (number_of_bombs - 1));
        }
    }

    public static void main(String[] args) {
        // Create Window
        JFrame frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel game_options_panel = new JPanel();
        game_options_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        game_options_panel.setPreferredSize(new Dimension(frame.getWidth(), 100)); // was 200

        JButton new_beginner = new JButton("Beginner");
        JButton new_intermediate = new JButton("Intermediate");
        JButton new_expert = new JButton("Expert");
        JButton help_button = new JButton("Help");

        game_options_panel.add(help_button);
        game_options_panel.add(new_beginner);
        game_options_panel.add(new_intermediate);
        game_options_panel.add(new_expert);
        game_options_panel.add(number_of_bombs_text);
        game_options_panel.add(timer_text);

        // Create main_panel
        JPanel main_panel = new JPanel();
        main_panel.setLayout(new BorderLayout());

        // ActionListener for Menu Items
        help_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String help_string = "To start a new game: click Beginner, Intermediate, or Expert.\n" +
                        "The numbers inside of the cells indicate how many bombs are adjacent to that cell.\n" +
                        "The game is over when you click on a bomb...good luck.";
                JOptionPane.showMessageDialog(null, help_string);
            }
        });

        new_beginner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!grid.gameOver) {
                    startValue = 0;
                    if (timer != null) {
                        stopTimer();
                    }
                }
                timer_text.setText("Seconds: " + startValue);

                // Make a new empty grid and repaint the Board
                // 9 x 9 grid, 10 bombs
                grid.removeAll();
                grid = new Grid(9, 9, 10, new Cell[9][9]);
                number_of_bombs_text.setText("Number of Bombs: " + 10);
                repaintBoard(grid, main_panel, frame);
            }
        });

        new_intermediate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!grid.gameOver) {
                    startValue = 0;
                    if (timer != null) {
                        stopTimer();
                    }
                }
                timer_text.setText("Seconds: " + startValue);

                // 16 x 16 grid, 40 bombs
                grid.removeAll();
                grid = new Grid(16, 16, 40, new Cell[16][16]);
                number_of_bombs_text.setText("Number of Bombs: " + 40);
                repaintBoard(grid, main_panel, frame);
            }
        });

        new_expert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!grid.gameOver) {
                    startValue = 0;
                    if (timer != null) {
                        stopTimer();
                    }
                }
                timer_text.setText("Seconds: " + startValue);

                // 24 x 20 grid, 99 bombs
                grid.removeAll();
                grid = new Grid(24, 20, 99, new Cell[24][20]);
                number_of_bombs_text.setText("Number of Bombs: " + 99);
                repaintBoard(grid, main_panel, frame);
            }
        });

        if (grid == null) {
            System.out.println("You should only see this once");
            grid = new Grid(rowSize, colSize, 10, new Cell[rowSize][colSize]);
        }

        // Create the main panel to contain all JPanel's
        main_panel.add(grid, BorderLayout.CENTER);
        main_panel.add(game_options_panel, BorderLayout.NORTH);

        frame.add(main_panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
