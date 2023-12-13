
public class DumbAi extends Ai {

    public DumbAi(Board board) {
        super("Dumb Ai", "DUMB", board);
    }

    public void play() {
        int col = findBestMove();
        board.handleSlotClick(col);
    }

    public int findBestMove() {
        int min = 0, max = Board.COL;
        int col = -1;
        int row = -1;

        while (row == -1) {
            col = Utility.getRandomNumber(min, max);
            row = board.findEmptyRow(col);
        }

        return col;
    }
}