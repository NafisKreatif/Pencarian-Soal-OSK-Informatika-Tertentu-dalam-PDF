package osk.app;

import java.io.File;
import java.io.IOException;

import java.awt.image.*;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class MainController {
    private PDDocument openedPDF;

    @FXML
    private VBox openPdfButton;
    @FXML
    private VBox pdfImageVBox;
    @FXML
    private Label pdfNameLabel;
    @FXML
    private Label pageLabel;
    @FXML
    private Button closePdfButton;

    @FXML
    private void loadPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load PDF File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF File", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(openPdfButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                openedPDF = Loader.loadPDF(selectedFile);
                pdfImageVBox.setVisible(true);
                pdfImageVBox.setManaged(true);
                openPdfButton.setVisible(false);
                openPdfButton.setManaged(false);
                pdfNameLabel.setText(selectedFile.getName());
                pageLabel.setText("Total Page: " + openedPDF.getNumberOfPages());
                closePdfButton.setDisable(false);
                renderPDF();
                System.out.println("Opened " + selectedFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void closePDF() {
        try {
            openedPDF.close();
            openedPDF = null;
            pdfImageVBox.getChildren().clear();
            pdfImageVBox.setVisible(false);
            pdfImageVBox.setManaged(false);
            openPdfButton.setVisible(true);
            openPdfButton.setManaged(true);
            pdfNameLabel.setText("No file opened");
            pageLabel.setText("Total Page: 0");
            closePdfButton.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderPDF() {
        if (openedPDF == null)
            return;
        pdfImageVBox.getChildren().clear();

        new Thread(() -> {
            PDFRenderer renderer = new PDFRenderer(openedPDF);
            int n = openedPDF.getNumberOfPages();
            for (int i = 0; i < n; i++) {
                if (openPdfButton == null) break;
                try {
                    BufferedImage bufferedImage = renderer.renderImage(i);
                    WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                    ImageView imageView = new ImageView(image);
                    Platform.runLater(() -> {
                        pdfImageVBox.getChildren().add(imageView);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void searchSoalWithMateri() {

    }

    private void showError() {

    }
}
