package osk;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Main"), 640, 480);

        // FileChooser fileChooser = new FileChooser();
        // fileChooser.setTitle("Load PDF");
        // fileChooser.getExtensionFilters().addAll(
        //         new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        // File selectedFile = fileChooser.showOpenDialog(scene.getWindow());

        // try (PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(selectedFile))) {
        //     System.out.println("PDF opened successfully!");
        //     System.out.println("Total Pages: " + document.getNumberOfPages());
        //     PDFRenderer renderer = new PDFRenderer(document);
        //     BufferedImage bufferedImage = renderer.renderImage(0);
        //     WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);

        //     ImageView imageView = new ImageView(writableImage);
        //     ScrollPane scrollPane = new ScrollPane(imageView);
        //     root.getChildren().add(scrollPane);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

}