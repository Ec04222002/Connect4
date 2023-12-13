
import java.util.TimerTask;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Timer;

public class Main {

    @FXML
    AnchorPane playgroundPane, optionsPane, opponentSelectorPane, winnerPane;

    @FXML
    GridPane board;

    @FXML
    Button dumbAiButton, smartAiButton;

    @FXML
    VBox opponentSelector, currentTurnAlerter;

    @FXML
    Circle currentTurnChip;
    @FXML
    Label currentTurnLabel;

    @FXML
    Circle winnerChip;
    @FXML
    Label winnerLabel;

    public static final int ALERTER_CHIP_RADIUS = 10, WINNER_ALERTER_CHIP_RADIUS = 15;
    public static final String INIT_TURN_TEXT = "You start!", USER_TURN_TEXT = "Your turn!",
            AI_TURN_TEXT = "Ai's turn!";
    public static final String DRAW_TEXT = "You tied!", USER_WON_TEXT = "You won!!!", AI_WON_TEXT = "You lost!";
    public boolean isUserTurn = true;
    private boolean isGameOver = false;
    private int winnerStatus = -1; // -1 -> tie, 0 -> ai won, 1 -> player won
    private boolean isDisablePlaygroundPane = false, isOpponentSelectorOpen = false,
            isOptionsBarOpen = false, isWinnerAlerterOpen = false;

    protected Board boardCls;
    private Timer timer = new Timer();
    private Ai opponent;

    @FXML
    void initialize() {
        this.boardCls = new Board(null, this);
    }

    @FXML
    void startGame() {
        setOpponentSelector();
        setPlayground();
        setOptionBar();
        setTurnInit();
    }

    @FXML
    void togglePlayground() {
        isDisablePlaygroundPane = !isDisablePlaygroundPane;
        setPlayground();
    }

    void setPlayground() {
        playgroundPane.setDisable(isDisablePlaygroundPane);
        playgroundPane.setOpacity(isDisablePlaygroundPane ? 0.5 : 1);
    }

    @FXML
    void toggleOpponentSelector() {
        isOpponentSelectorOpen = !isOpponentSelectorOpen;
        setOpponentSelector();
    }

    void setOpponentSelector() {
        opponentSelectorPane.setVisible(isOpponentSelectorOpen);
        opponentSelectorPane.setDisable(!isOpponentSelectorOpen);
    }

    @FXML
    void setSmartOpponent() {
        this.opponent = new SmartAi(boardCls);
    }

    @FXML
    void setDumbOpponent() {
        this.opponent = new DumbAi(boardCls);
    }

    @FXML
    void toggleCurrentTurnAlerter() {
        board.setDisable(true);
        TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), currentTurnAlerter);
        translate1.setToY(-47);
        translate1.setOnFinished(e -> toggleTurn());

        TranslateTransition translate2 = new TranslateTransition(Duration.seconds(0.5), currentTurnAlerter);
        translate2.setToY(-10);
        translate2.setOnFinished(e -> board.setDisable(!isUserTurn));
        SequentialTransition sequentialTransition = new SequentialTransition(
                translate1,
                translate2);
        sequentialTransition.play();
    }

    @FXML
    void alertError(String error) {
        currentTurnLabel.setText(error);
        currentTurnLabel.setTextFill(Color.web(Board.RED_CHIP_COLOR));
        currentTurnChip.setRadius(0);
    }

    void toggleTurn() {
        if (isGameOver)
            return;
        isUserTurn = !isUserTurn;
        setTurn();
    }

    void setTurnInit() {
        board.setDisable(!isUserTurn);
        currentTurnAlerter.setVisible(true);
        currentTurnLabel.setText(INIT_TURN_TEXT);
        currentTurnLabel.setTextFill(Color.web(Board.BLACK_CHIP_COLOR));
        currentTurnChip.setFill(Color.web(isUserTurn ? Board.PLAYER_COLOR : Board.AI_COLOR));
        currentTurnChip.setRadius(ALERTER_CHIP_RADIUS);
    }

    void setTurn() {
        currentTurnLabel.setText(isUserTurn ? USER_TURN_TEXT : AI_TURN_TEXT);
        currentTurnLabel.setTextFill(Color.web(Board.BLACK_CHIP_COLOR));
        currentTurnChip.setFill(Color.web(isUserTurn ? Board.PLAYER_COLOR : Board.AI_COLOR));
        currentTurnChip.setRadius(ALERTER_CHIP_RADIUS);

        if (!isUserTurn) {
            // board.setDisable(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        opponent.play();
                        board.setDisable(false);
                    });
                }

            }, Utility.getRandomNumber(1200, 2000));
        }
    }

    @FXML
    void toggleOptionsBar() {
        isOptionsBarOpen = !isOptionsBarOpen;
        setOptionBar();
        isDisablePlaygroundPane = isOptionsBarOpen;
        setPlayground();
    }

    void setOptionBar() {
        optionsPane.setVisible(isOptionsBarOpen);
        optionsPane.setDisable(!isOptionsBarOpen);
        currentTurnAlerter.setVisible(!isOptionsBarOpen);

    }

    @FXML
    void toggleWinnerAlerter() {
        isWinnerAlerterOpen = !isWinnerAlerterOpen;
        setWinnerAlerter();
        isDisablePlaygroundPane = isWinnerAlerterOpen;
        setPlayground();
    }

    void setWinnerAlerter() {
        winnerChip.setVisible(false);
        winnerLabel.setText(DRAW_TEXT);

        if (winnerStatus == 1) {
            winnerChip.setVisible(true);
            winnerChip.setFill(Color.web(Board.PLAYER_COLOR));
            winnerLabel.setText(USER_WON_TEXT);
        }
        if (winnerStatus == 0) {
            winnerChip.setVisible(true);
            winnerChip.setFill(Color.web(Board.AI_COLOR));
            winnerLabel.setText(AI_WON_TEXT);
        }
        winnerPane.setVisible(isWinnerAlerterOpen);
        winnerPane.setDisable(!isWinnerAlerterOpen);
        currentTurnAlerter.setVisible(!isWinnerAlerterOpen);
    }

    void setWinner(int _winnerStatus) {
        winnerStatus = _winnerStatus;
    }

    void endGame() {
        isGameOver = true;
    }

    @FXML
    void restart() {
        isUserTurn = true;
        isGameOver = false;
        winnerStatus = -1;
        isDisablePlaygroundPane = true;
        isOpponentSelectorOpen = true;
        isOptionsBarOpen = false;
        isWinnerAlerterOpen = false;
        setOpponentSelector();
        setPlayground();
        setOptionBar();
        setTurnInit();
        setWinnerAlerter();

        boardCls.clear();

        isDisablePlaygroundPane = false;
        isOpponentSelectorOpen = false;
    }

}
