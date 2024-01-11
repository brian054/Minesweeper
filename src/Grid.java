import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ThreadLocalRandom;

public class Grid extends JPanel implements ActionListener { // implements ActionListener
    private Cell[][] grid_array; // rows x cols
    public boolean gameOver;
    public boolean firstClick;

    private int rowSize;
    private int colSize;

    private int iconWidth;
    private int iconHeight;

    public int number_of_bombs;

    // ImageIcon's
    private final ImageIcon bomb_icon = new ImageIcon("src/assets/bomb.png");
    private final ImageIcon bomb_red = new ImageIcon("src/assets/bomb_red.png");
    private final ImageIcon covered_icon = new ImageIcon("src/assets/covered.png");
    private final ImageIcon flag_icon = new ImageIcon("src/assets/flag.png");
    private final ImageIcon zero_icon = new ImageIcon("src/assets/zero.png");
    private final ImageIcon one_icon = new ImageIcon("src/assets/one.png");
    private final ImageIcon two_icon = new ImageIcon("src/assets/two.png");
    private final ImageIcon three_icon = new ImageIcon("src/assets/three.png");
    private final ImageIcon four_icon = new ImageIcon("src/assets/four.png");
    private final ImageIcon five_icon = new ImageIcon("src/assets/five.png");
    private final ImageIcon six_icon = new ImageIcon("src/assets/six.png");
    private final ImageIcon seven_icon = new ImageIcon("src/assets/seven.png");
    private final ImageIcon eight_icon = new ImageIcon("src/assets/eight.png");

    // Constructor
    Grid(int rowSize, int colSize, int number_of_bombs, Cell[][] grid_array) {
        firstClick = true;
        gameOver = false;

        this.rowSize = rowSize;
        this.colSize = colSize;
        this.number_of_bombs = number_of_bombs;

        // Set layout and size
        setLayout(new GridLayout(rowSize, colSize));
        setPreferredSize(new Dimension(480, 500));

        this.grid_array = grid_array;
        fillGridArray();
        initBombLayout(grid_array, number_of_bombs);
        // At this point grid array is already shuffled

        // Name the Jbuttons accordingly before 'drawing'
        for (int i = 0; i < grid_array.length; i++) {
            for (int j = 0; j < grid_array[i].length; j++) {
                if (grid_array[i][j].getBomb()) {
                    grid_array[i][j].cell_button.setName("bomb," + i + "," + j);
                } else {
                    grid_array[i][j].cell_button.setName("blank," + i + "," + j);
                }
            }
        }
        drawBoard();
    }

    private void initBombLayout(Cell[][] grid_array, int number_of_bombs) {;
        int tempCount = number_of_bombs;
        for (int i = 0; i < grid_array.length; i++) {
            for (int j = 0; j < grid_array[i].length; j++) {
                if (tempCount > 0) {
                    grid_array[i][j].setBomb(true);
                    tempCount -= 1;
                } else {
                    break;
                }
            }
        }
        // Now that we've set enough Cell's to bomb = true, shuffle these Cells
        shuffle(grid_array);
    }

    // Assign a custom offset to be used when resizing Icons
    public int assignCustomIconOffset(int colSize) {
       if (colSize >= 9 && colSize <= 15) {
           return 50;
       } else if (colSize >= 16 && colSize <= 23) {
           return 25;
       } else if (colSize == 24) {
           return 18;
       }
       return -1; // failure
    }

    // Make buttons for Cell objects, set default Icon to be the covered icon
    public void fillGridArray() {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                grid_array[i][j] = new Cell(i, j);
                JButton this_button = grid_array[i][j].cell_button;
                // Resize icons based on game type
                if (rowSize == 9) {
                    iconWidth = this_button.getWidth() + 50;
                    iconHeight = this_button.getHeight() + 55;
                } else if (rowSize == 16) {
                    iconWidth = this_button.getWidth() + 25;
                    iconHeight = this_button.getHeight() + 25;
                } else if (rowSize == 24) {
                    iconWidth = this_button.getWidth() + 18;
                    iconHeight = this_button.getHeight() + 18;
                }
                // Resize the Icon
                grid_array[i][j].cell_button.setIcon(resizeIcon(covered_icon, iconWidth, iconHeight));

                // Add Action Listener to JButton
                grid_array[i][j].cell_button.addActionListener(this);

                // Add Mouse Listener for Red Flag
                grid_array[i][j].cell_button.addMouseListener(new RightClickHandler());
            }
        }
    }

    // Shuffle our grid array
    public void shuffle(Cell[][] grid_array) {
        for (int i = grid_array.length - 1; i > 0; i--) {
            for (int j = grid_array[i].length - 1; j > 0; j--) {
                int randomNum1 = ThreadLocalRandom.current().nextInt(i + 1); // + 1 to make it inclusive of 1
                int randomNum2 = ThreadLocalRandom.current().nextInt(j + 1);

                Cell tempCell = grid_array[i][j];
                grid_array[i][j] = grid_array[randomNum1][randomNum2];
                grid_array[randomNum1][randomNum2] = tempCell;
            }
        }
    }

    // Draw just means adding our buttons to the panel
    public void drawBoard() {
        this.removeAll(); // remove any previous buttons

        // Add JButtons to our JPanel
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                // Add button to panel
                add(this.getCellButton(i, j));
            }
        }
        this.revalidate();
        this.repaint();
    }

    // Returns the button of Cell object based on row and column
    public JButton getCellButton(int row, int col) {
        return grid_array[row][col].cell_button;
    }

    // https://stackoverflow.com/questions/36957450/fit-size-of-an-imageicon-to-a-jbutton
    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    // Switch bomb with a blank cell
    public void switchCells(Cell[][] grid_array, int xPos, int yPos) {
        int newRow = xPos;
        int newCol = yPos;
        // Keep assigning random numbers until either the row or column is a different index
        while (newRow == xPos && newCol == yPos && grid_array[newRow][newCol].getBomb()) {
            newRow = ThreadLocalRandom.current().nextInt(0, rowSize);
            newCol = ThreadLocalRandom.current().nextInt(0, colSize);
        }
        // Update Cell's row and column positions
        grid_array[xPos][yPos].setRowAndColumn(newRow, newCol);
        grid_array[newRow][newCol].setRowAndColumn(xPos, xPos);

        // Switch the array elements
        Cell temp = grid_array[xPos][yPos];
        grid_array[xPos][yPos] = grid_array[newRow][newCol];
        grid_array[newRow][newCol] = temp;

        // Change button names
        grid_array[newRow][newCol].cell_button.setName("blank," + xPos + "," + yPos); // used to be a bomb
        grid_array[xPos][yPos].cell_button.setName("bomb," + newRow + "," + newCol); // is now a bomb
    }

    // This method counts up the number of bombs adjacent to the Cell(i, j).
    public int adjCheck(int row, int col) {
        int adjCount = 0;
        // Check Left
        if (col != 0) { // if column is not 0, we can check left without index oob
            if (grid_array[row][col - 1].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Top Left
        if (col != 0 && row != 0) {
            if (grid_array[row - 1][col - 1].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Top
        if (row != 0) {
            if (grid_array[row - 1][col].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Top Right
        if (row != 0 && col != (colSize - 1)) {
            if (grid_array[row - 1][col + 1].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Right
        if (col != (colSize - 1)) {
            if (grid_array[row][col + 1].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Bottom Right
        if (row != (rowSize - 1) && col != (colSize - 1)) {
            if (grid_array[row + 1][col + 1].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Bottom
        if (row != (rowSize - 1)) {
            if (grid_array[row + 1][col].getBomb()) {
                adjCount += 1;
            }
        }
        // Check Bottom Left
        if (row != (rowSize - 1) && col != 0) {
            if (grid_array[row + 1][col - 1].getBomb()) {
                adjCount += 1;
            }
        }
        return adjCount;
    }

    public ImageIcon findIcon(int adjCount) {
        switch (adjCount) {
            case 0:
                return zero_icon;
            case 1:
                return one_icon;
            case 2:
                return two_icon;
            case 3:
                return three_icon;
            case 4:
                return four_icon;
            case 5:
                return five_icon;
            case 6:
                return six_icon;
            case 7:
                return seven_icon;
            case 8:
                return eight_icon;
            default:
                System.out.println("Error");
                break;
        }
        return null; // error
    }

    // This is how we implement the flood-fill algorithm.
    // We recursively call DFS until we reveal all cells that are not bomb cells.
    public int depthFirstSearch(Cell[][] grid_array, int row, int col) {
        if (row < 0 || row >= rowSize || col < 0 || col >= colSize || grid_array[row][col].getRevealed()) {
            return 0;
        } else if (adjCheck(row, col) != 0){
            // If the icon is not a bomb, set the icon accordingly
            if (!grid_array[row][col].getBomb()) {
                ImageIcon icon = findIcon(adjCheck(row, col));
                grid_array[row][col].cell_button.setIcon(resizeIcon(icon, iconWidth, iconHeight));
            }
        } else {
            if (adjCheck(row, col) == 0) { // if cell is blank, check all cells adjacent and call dfs on them
                // Set Icon of Button since adjCheck(row, col) == 0, then check adjacent cells
                grid_array[row][col].cell_button.setIcon(resizeIcon(zero_icon, iconWidth, iconHeight));
                // Set revealed
                grid_array[row][col].setRevealed(true);
                depthFirstSearch(grid_array, row + 1, col); // bottom
                depthFirstSearch(grid_array, row + 1, col - 1); // bottom-left
                depthFirstSearch(grid_array, row, col - 1); // left
                depthFirstSearch(grid_array, row - 1, col - 1); // top-left
                depthFirstSearch(grid_array, row - 1, col); // top
                depthFirstSearch(grid_array, row - 1, col + 1); // top-right
                depthFirstSearch(grid_array, row, col + 1); // right
                depthFirstSearch(grid_array, row + 1, col + 1); // bottom-right
            }
        }
        return -1; // error
    }

    // Iterates through board and reveals all bombs. Used when the user clicks a bomb, indicating game over.
    public void revealAllBombs(Cell[][] grid_array, int xPos, int yPos) {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                if (i != xPos || j != yPos) {
                    if (grid_array[i][j].getBomb()) {
                        grid_array[i][j].cell_button.setIcon(resizeIcon(bomb_icon, iconWidth, iconHeight));
                    }
                }
                // Set revealed to 'disable' all buttons in grid
                grid_array[i][j].setRevealed(true);
            }
        }
    }

    // If a JButton gets clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            String[] selectedButton = ((JButton) e.getSource()).getName().split(",", 3);
            String type = selectedButton[0];
            int xPos = Integer.parseInt(selectedButton[1]);
            int yPos = Integer.parseInt(selectedButton[2]);

            // If the cell hasn't been revealed and it's not covered by the red flag
            if (!grid_array[xPos][yPos].getRevealed() && !grid_array[xPos][yPos].getRedFlag()) {
                if (firstClick) { // initially true
                    firstClick = false;
                    if (type.equals("bomb")) {
                        // switch the bomb with a random empty cell in the grid_array
                        switchCells(grid_array, xPos, yPos);
                    }

                    // Perform adjacency check on grid_array[xPos][yPos]
                    int adjCount = adjCheck(xPos, yPos);

                    ImageIcon theChosenOne = findIcon(adjCount);
                    ((JButton) e.getSource()).setIcon(resizeIcon(theChosenOne, iconWidth, iconHeight));

                    if (adjCount == 0) { // no bombs adjacent
                        // Flood Fill
                        depthFirstSearch(grid_array, xPos, yPos);
                    } else {
                        grid_array[xPos][yPos].setRevealed(true);
                    }

                    // Start Timer since it's the first click
                    Minesweeper.startTimer();
                } else { // not the first click
                    if (type.equals("bomb")) {
                        // show the red_bomb
                        ((JButton) e.getSource()).setIcon(resizeIcon(bomb_red, iconWidth, iconHeight));

                        // stop the timer
                        gameOver = true;

                        // Reveal All Bombs on Board
                        revealAllBombs(grid_array, xPos, yPos);
                    } else if (type.equals("blank")) {
                        // do adjacency check
                        int adjCount = adjCheck(xPos, yPos);

                        ImageIcon theChosenOne = findIcon(adjCount);
                        ((JButton) e.getSource()).setIcon(resizeIcon(theChosenOne, iconWidth, iconHeight));

                        if (adjCount == 0) {
                            // Flood Fill
                            depthFirstSearch(grid_array, xPos, yPos);
                        } else {
                            grid_array[xPos][yPos].setRevealed(true);
                        }
                    }
                }
                grid_array[xPos][yPos].setRevealed(true);
            }
        }
    }

    //    // for testing
//    public void printBombLayout(Cell[][] grid_array) {
//        int counter = 0;
//        for (int i = 0; i < grid_array.length; i++) {
//            for (int j = 0; j < grid_array[i].length; j++) {
//                if (grid_array[i][j].getBomb()) {
//                    counter++;
//                }
//                System.out.println("# of Bombs: " + counter);
//                System.out.println(grid_array[i][j].getBomb());
//            }
//        }
//    }

    // Handles the Reg Flag option when right-clicking a cell
    private class RightClickHandler implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                String[] selectedButton = ((JButton) e.getSource()).getName().split(",", 3);
                int xPos = Integer.parseInt(selectedButton[1]);
                int yPos = Integer.parseInt(selectedButton[2]);

                // If the Cell isn't revealed, set Icon
                if (!grid_array[xPos][yPos].getRevealed()) {
                    if (grid_array[xPos][yPos].getRedFlag()) { // if red flag, remove red flag
                        ((JButton) e.getSource()).setIcon(resizeIcon(covered_icon, iconWidth, iconHeight));
                        grid_array[xPos][yPos].setRedFlag(false);

                        Minesweeper.setBombsText(number_of_bombs, true);
                        number_of_bombs += 1;
                    } else { // red flag is false
                        ((JButton) e.getSource()).setIcon(resizeIcon(flag_icon, iconWidth, iconHeight));
                        grid_array[xPos][yPos].setRedFlag(true);

                        Minesweeper.setBombsText(number_of_bombs, false);
                        number_of_bombs -= 1;
                    }
                }
            }
        }

        // We don't use these however we need them to satisfy MouseListener
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
}
