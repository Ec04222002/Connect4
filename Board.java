import java.util.ArrayList;
import java.util.Arrays;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class Board {

    // TODO: make dynamic with gridpane
    public static final int ROW = 6, COL = 7;
    public static final int WIDTH = 640, HEIGHT = 412;
    public static final int RADIUS = 25;
    public static final int GAP = 10;
    public static final String EMPTY_SLOT_COLOR = "#f2f2f2", RED_CHIP_COLOR = "red", BLACK_CHIP_COLOR = "black";
    public static final String PLAYER_COLOR = RED_CHIP_COLOR, AI_COLOR = BLACK_CHIP_COLOR;

    private ArrayList<ArrayList<Chip>> boardData = new ArrayList<ArrayList<Chip>>(ROW);

    private final Main mainController;
    private GridPane board;

    public Board(ArrayList<ArrayList<Chip>> boardData, Main mainController) {
        this.mainController = mainController;
        this.board = mainController.board;
        if (boardData == null) {
            // create empty board
            for (int r = 0; r < ROW; r++) {
                ArrayList<Chip> slotRow = new ArrayList<Chip>(COL);
                for (int c = 0; c < COL; c++) {
                    slotRow.add(null);
                }
                this.boardData.add(slotRow);
            }
        } else {
            this.boardData = boardData;
        }
        createBoardSlots();
    }

    public void createBoardSlots() {
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                Chip chip = boardData.get(r).get(c);
                Button slot = chip == null ? createEmptySlot() : chip.getChip();
                GridPane.setHalignment(slot, HPos.CENTER);
                GridPane.setValignment(slot, VPos.CENTER);
                final int colClicked = c;
                slot.setOnMouseClicked(e -> handleSlotClick(colClicked));
                board.add(slot, c, r);
            }
        }
    }

    public void clear() {
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                boardData.get(r).set(c, null);

            }
        }
        board.getChildren().clear();
        createBoardSlots();
    }

    public Button createEmptySlot() {
        Button slot = new Button();
        int width = RADIUS * 2, height = RADIUS * 2;
        String style = String.format("""
                -fx-background-radius: %dpx;
                -fx-background-color: %s;
                -fx-min-width: %dpx;
                -fx-min-height: %dpx;
                -fx-max-width: %dpx;
                -fx-max-height: %dpx;
                -fx-cursor: hand;
                """, RADIUS, EMPTY_SLOT_COLOR, width, height, width, height);
        slot.setStyle(style);
        return slot;
    }

    public double getAvailableSlotWidth() {
        double totalGap = (COL + 1) * GAP;
        double totalAvailableWidth = WIDTH - totalGap;
        return totalAvailableWidth / COL;
    }

    public double getAvailableSlotHeight() {
        double totalGap = (ROW + 1) * GAP;
        double totalAvailableHeight = HEIGHT - totalGap;
        return totalAvailableHeight / ROW;
    }

    public void handleSlotClick(int col) {
        int emptyRow = findEmptyRow(col);
        if (emptyRow == -1) {
            mainController.alertError("Column is filled!");
            return;
        }
        chipDrop(emptyRow, col);

        int winner = checkWinner();
        if (winner == 1 || winner == 0 || !isMovesLeft()) {
            mainController.setWinner(winner);
            mainController.endGame();
            mainController.toggleWinnerAlerter();
        }
        mainController.toggleCurrentTurnAlerter();

    }

    public boolean isMovesLeft() {
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                if (boardData.get(r).get(c) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public int findEmptyRow(int col) {
        Chip topSlot = boardData.get(0).get(col);
        if (topSlot != null)
            return -1;

        // finding empty row to put chip
        for (int r = 0; r < boardData.size() - 1; r++) {
            Chip nextSlot = boardData.get(r + 1).get(col);
            if (nextSlot != null) {
                return r;
            }
        }
        return (boardData.size() - 1);
    }

    public void chipDrop(int row, int col) {
        double initialX = (getAvailableSlotWidth() - (RADIUS * 2)) / 2;
        double initialY = -HEIGHT;
        double finalY = 0;
        Chip newChip = new Chip(mainController.isUserTurn ? PLAYER_COLOR : AI_COLOR, RADIUS, row, col);
        Button chipButton = newChip.getChip();
        chipButton.setTranslateX(initialX);
        chipButton.setTranslateY(initialY);
        chipButton.setOnMouseClicked(e -> handleSlotClick(col));
        board.add(chipButton, col, row);
        boardData.get(row).set(col, newChip);

        newChip.animateStraightFall(finalY);

    }

    // 0 -> Ai won, 1 -> Player won, -1 -> continue
    public int checkWinner() {
        int res = -1;
        // horizontalCheck
        for (int j = 0; j < COL - 3; j++) {
            for (int i = 0; i < ROW; i++) {
                Chip[] horizontal = { boardData.get(i).get(j), boardData.get(i).get(j + 1),
                        boardData.get(i).get(j + 2), boardData.get(i).get(j + 3) };

                boolean isWinnerExist = Arrays.stream(horizontal)
                        .allMatch(s -> s != null && s.color.equals(horizontal[0].color));
                if (isWinnerExist) {
                    res = horizontal[0].color == PLAYER_COLOR ? 1 : 0;
                }

            }
        }
        // verticalCheck
        for (int i = 0; i < ROW - 3; i++) {
            for (int j = 0; j < COL; j++) {
                Chip[] vertical = { boardData.get(i).get(j), boardData.get(i + 1).get(j),
                        boardData.get(i + 2).get(j), boardData.get(i + 3).get(j) };

                boolean isWinnerExist = Arrays.stream(vertical)
                        .allMatch(s -> s != null && s.color.equals(vertical[0].color));
                if (isWinnerExist) {
                    res = vertical[0].color == PLAYER_COLOR ? 1 : 0;
                }
            }
        }

        // ascendingDiagonalCheck
        for (int i = 3; i < ROW; i++) {
            for (int j = 0; j < COL - 3; j++) {
                Chip[] ascendDiagonal = { boardData.get(i).get(j), boardData.get(i - 1).get(j + 1),
                        boardData.get(i - 2).get(j + 2), boardData.get(i - 3).get(j + 3) };
                boolean isWinnerExist = Arrays.stream(ascendDiagonal)
                        .allMatch(s -> s != null && s.color.equals(ascendDiagonal[0].color));

                if (isWinnerExist) {
                    res = ascendDiagonal[0].color == PLAYER_COLOR ? 1 : 0;
                }
            }
        }
        // descendingDiagonalCheck
        for (int i = 3; i < ROW; i++) {
            for (int j = 3; j < COL; j++) {
                Chip[] descendDiagonal = { boardData.get(i).get(j), boardData.get(i -
                        1).get(j - 1),
                        boardData.get(i - 2).get(j - 2), boardData.get(i - 3).get(j - 3) };
                boolean isWinnerExist = Arrays.stream(descendDiagonal)
                        .allMatch(s -> s != null && s.color.equals(descendDiagonal[0].color));

                if (isWinnerExist) {
                    res = descendDiagonal[0].color == PLAYER_COLOR ? 1 : 0;
                }
            }
        }
        return res;
    }

    public ArrayList<ArrayList<Chip>> getBoardData() {
        return boardData;
    }
}