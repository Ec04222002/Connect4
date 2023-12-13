
public class SmartAi extends Ai {

    public SmartAi(Board board) {
        super("Smart Ai", "SMART", board);
    }

    public void play() {
        int col = findBestMove();
        board.handleSlotClick(col);
    }

    public int findBestMove() {
        int bestScore = -1000;
        int bestMove = -1;

        for (int c = 0; c < Board.COL; c++) {
            int emptyRow = board.findEmptyRow(c);
            if (emptyRow != -1) {
                Chip newChip = new Chip(Board.AI_COLOR, Board.RADIUS, emptyRow, c);
                boardData.get(emptyRow).set(c, newChip);

                int moveScore = minmax(0, false);
                boardData.get(emptyRow).set(c, null);

                if (moveScore > bestScore) {
                    bestMove = c;
                    bestScore = moveScore;
                }
            }
        }

        return bestMove;
    }

    public int evaluate() {
        int winner = board.checkWinner();
        if (winner == 0) {
            return 10;
        }
        if (winner == 1) {
            return -10;
        }

        return 0;
    }

    public int minmax(int depth, boolean isMax) {
        int score = evaluate();
        if (score == 10 || score == -10) {
            return score;
        }
        if (Utility.log2(Board.ROW * Board.COL) == depth) {
            return score;
        }
        if (board.isMovesLeft() == false) {
            return 0;
        }

        int bestScore = isMax ? -1000 : 1000;
        for (int c = 0; c < Board.COL; c++) {
            int emptyRow = board.findEmptyRow(c);
            if (emptyRow != -1) {
                Chip newChip = new Chip((isMax ? Board.AI_COLOR : Board.PLAYER_COLOR), Board.RADIUS, emptyRow, c);
                boardData.get(emptyRow).set(c, newChip);

                bestScore = (isMax ? Math.max(bestScore, minmax(depth + 1, !isMax))
                        : Math.min(bestScore, minmax(depth + 1, !isMax)));

                boardData.get(emptyRow).set(c, null);

            }
        }
        return bestScore;

    }

}