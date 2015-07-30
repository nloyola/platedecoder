package org.biobank.platedecoder;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlateDecoder extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TilePane title = createTitle();
        title.prefTileWidthProperty().bind(Bindings.selectDouble(title.parentProperty(), "width").divide(2));
        primaryStage.show();
    }

    private TilePane createTitle() {
        StackPane left = new StackPane();
        left.setStyle("-fx-background-color: black");
        Text text = new Text("Plate decoder");
        text.setFont(Font.font(null, FontWeight.BOLD, 18));
        // text.setFill(Color.BLACK);
        StackPane.setAlignment(text, Pos.CENTER_RIGHT);
        left.getChildren().add(text);
        Text right = new Text("Decoder");
        right.setFont(Font.font(null, FontWeight.BOLD, 18));
        TilePane tiles = new TilePane();
        tiles.setSnapToPixel(false);
        TilePane.setAlignment(right, Pos.CENTER_LEFT);
        tiles.getChildren().addAll(left, right);
        tiles.setPrefTileHeight(40);
        return tiles;
    }

}
