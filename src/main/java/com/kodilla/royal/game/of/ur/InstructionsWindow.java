package com.kodilla.royal.game.of.ur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InstructionsWindow extends Stage {

    public static final List<String> TAGS = Arrays.asList("title", "p");

    private VBox instructionsBox;

    public InstructionsWindow() {
        setTitle("The Royal Game Of Ur - Instructions");

        instructionsBox = new VBox();
        instructionsBox.setPadding(new Insets(5d));
        instructionsBox.setSpacing(10d);
        instructionsBox.setFillWidth(true);
        instructionsBox.getStyleClass().add("border");
        ClassLoader classLoader = getClass().getClassLoader();
        instructionsBox.getStylesheets().add(Objects.requireNonNull(
                classLoader.getResource("css/instructions.css"),
                "Accessing the CSS file unsuccessful."
        ).toExternalForm());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(instructionsBox);

        Scene scene = new Scene(scrollPane, 450d, 450d);
        setScene(scene);
    }

    public void setInstructions(List<List<String>> instructions) {
        for (List<String> instruction : instructions) {
            Text lineTxt = new Text(instruction.get(1));
            lineTxt.wrappingWidthProperty().bind(instructionsBox.widthProperty().subtract(10));
            lineTxt.setTextAlignment(TextAlignment.JUSTIFY);
            lineTxt.getStyleClass().add(instruction.get(0));
            instructionsBox.getChildren().add(lineTxt);
        }
    }

}
