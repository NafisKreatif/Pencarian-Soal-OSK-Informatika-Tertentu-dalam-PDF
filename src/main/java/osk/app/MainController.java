package osk.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import osk.App;
import osk.pdf.PDFSoalSearcher;
import osk.pdf.SoalToTagSearchResult;
import osk.pdf.TagToSoalSearchResult;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class MainController {
    private PDDocument openedPDF;
    private PDFSoalSearcher soalSearcher;

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
    private VBox tagVBox;
    @FXML
    private VBox resultVBox;

    @FXML
    private void initialize() {
        soalSearcher = new PDFSoalSearcher();
        soalSearcher.loadKeywords(App.class.getResourceAsStream("keyword.json"));
        soalSearcher.loadRegex(App.class.getResourceAsStream("regex.json"));
    }

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
                if (openPdfButton == null)
                    break;
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

    @FXML
    private void searchSoal() {
        List<String> tags = new ArrayList<>();
        for (Node node : tagVBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox tagCheckBox = (CheckBox) node;
                if (tagCheckBox.isSelected()) {
                    tags.add(tagCheckBox.getText());
                }
            }
        }
        var searchResults = soalSearcher.searchSoalWithTags(openedPDF, tags);
        resultVBox.getChildren().clear();
        for (TagToSoalSearchResult searchResult : searchResults.getValue()) {
            Label soalLabel = new Label(searchResult.tag + ": ");
            VBox soalVBox = new VBox(soalLabel);
            soalVBox.setSpacing(5);
            StringBuilder numberText = new StringBuilder();
            boolean first = true;
            for (Integer number : searchResult.numbers) {
                if (!first) numberText.append(", ");
                else first = false;
                numberText.append("Soal " + number);
            }
            Label numberLabel = new Label(numberText.toString());
            numberLabel.setWrapText(true);
            soalVBox.getChildren().add(numberLabel);
            resultVBox.getChildren().add(soalVBox);
        }
    }
}
