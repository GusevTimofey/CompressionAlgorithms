package AdaptiveHuffmanCode;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("JavaFx.fxml"));
        primaryStage.setTitle("Adaptive Huffman code");
        primaryStage.setScene(new Scene(root, 800, 450));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

