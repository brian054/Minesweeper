import javax.swing.*;

// Represents one cell in our grid
public class Cell {
    private boolean bomb;
    private boolean revealed;
    private boolean red_flag;

    private int row;
    private int col;

    public JButton cell_button;

    private ImageIcon covered_icon = new ImageIcon("src/assets/covered.png");

    Cell(int row, int col) {
        this.row = row;
        this.col = col;

        revealed = false;
        bomb = false;
        red_flag = false;

        cell_button = new JButton();
        cell_button.setSize(1, 1);
        cell_button.setContentAreaFilled(false);
    }

    public void setBomb(boolean status) {
        bomb = status;
    }

    public boolean getBomb() {
        return bomb;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean getRevealed() {
        return revealed;
    }

    public void setRedFlag(boolean red_flag) {
        this.red_flag = red_flag;
    }

    public boolean getRedFlag() {
        return red_flag;
    }

    public void setRowAndColumn(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

