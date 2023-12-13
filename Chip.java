
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class Chip {
    public final String color;
    public final int radius;
    public final int row, col;
    private Button chip;

    public Chip(String color, int radius, int row, int col) {
        this.color = color;
        this.radius = radius;
        this.row = row;
        this.col = col;
        createChip();
    }

    public void createChip() {
        Button chip = new Button();
        int width = radius * 2, height = radius * 2;
        String style = String.format("""
                -fx-background-radius: %dpx;
                -fx-background-color: %s;
                -fx-min-width: %dpx;
                -fx-min-height: %dpx;
                -fx-max-width: %dpx;
                -fx-max-height: %dpx;
                -fx-cursor: hand;
                """, radius, color, width, height, width, height);
        chip.setStyle(style);
        this.chip = chip;
    }

    public void animateStraightFall(double finalY) {
        // Create a Timeline for the animation
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        final double gravity = 8;
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(20),
                event -> {
                    // Update the position based on gravity
                    chip.setTranslateY(chip.getTranslateY() + gravity);

                    // Check if the ball has reached the bottom
                    if (chip.getTranslateY() >= finalY) {
                        chip.setTranslateY(finalY);
                        timeline.stop();
                    }
                });

        // Add the KeyFrame to the Timeline
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public Button getChip() {
        return this.chip;
    }
}