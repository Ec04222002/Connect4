import java.util.ArrayList;

abstract public class Ai {
    public String name;
    public String type;
    protected Board board;
    protected ArrayList<ArrayList<Chip>> boardData;

    public Ai(String name, String type, Board board) {
        this.name = name;
        this.type = type;
        this.board = board;
        this.boardData = this.board.getBoardData();
    }

    abstract void play();

    abstract int findBestMove();
}