package org.example.ui;

import java.io.File;

import org.example.Core.FileHandlerRegistry;
import org.example.Core.FileSearcher;
import org.example.Core.TextFileHandler;
import org.example.Handler.ExcelFileHandler;
import org.example.Handler.WordFileHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainApp extends Application {
    // Deklarasi semua komponen UI sebagai field
    private File selectedFolder;
    private TextArea resultArea;
    private TextField keywordField;
    private RadioButton wordRadio;
    private RadioButton sentenceRadio;
    private ToggleGroup searchTypeGroup;
    private Label folderPathLabel;

    @Override
    public void start(Stage stage) {
        // Inisialisasi komponen
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        
        keywordField = new TextField();
        
        searchTypeGroup = new ToggleGroup();
        wordRadio = new RadioButton("Kata");
        sentenceRadio = new RadioButton("Kalimat");
        wordRadio.setToggleGroup(searchTypeGroup);
        sentenceRadio.setToggleGroup(searchTypeGroup);
        wordRadio.setSelected(true);
        
        folderPathLabel = new Label("Belum ada folder dipilih.");

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);

        // Tambahkan komponen ke grid
        grid.add(new Label("Pilih Folder:"), 0, 0);
        Button selectFolderBtn = new Button("Browse");
        grid.add(selectFolderBtn, 1, 0);
        grid.add(folderPathLabel, 0, 1, 2, 1);

        grid.add(new Label("Jenis Pencarian:"), 0, 2);
        HBox radioBox = new HBox(10, wordRadio, sentenceRadio);
        grid.add(radioBox, 1, 2);

        grid.add(new Label("Kata/Kalimat:"), 0, 3);
        grid.add(keywordField, 1, 3);

        Button searchBtn = new Button("Cari Sekarang");
        grid.add(searchBtn, 0, 4, 2, 1);

        grid.add(new Label("Hasil:"), 0, 5);
        grid.add(new ScrollPane(resultArea), 0, 6, 2, 1);

        // Event Handlers
        selectFolderBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            selectedFolder = chooser.showDialog(stage);
            if (selectedFolder != null) {
                folderPathLabel.setText(selectedFolder.getAbsolutePath());
            }
        });

        searchBtn.setOnAction(e -> {
            if (validateInput()) {
                new Thread(this::performSearch).start();
            }
        });

        // Setup stage
        Scene scene = new Scene(grid, 600, 500);
        stage.setTitle("File Searcher");
        stage.setScene(scene);
        stage.show();
    }

    private boolean validateInput() {
        if (selectedFolder == null) {
            showAlert("Error", "Pilih folder terlebih dahulu!");
            return false;
        }

        String keyword = keywordField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Error", "Masukkan kata/kalimat!");
            return false;
        }

        if (wordRadio.isSelected() && keyword.split("\\s+").length > 1) {
            showAlert("Error", "Mode 'Kata' hanya untuk satu kata!");
            return false;
        }

        return true;
    }

    private void performSearch() {
        Platform.runLater(() -> resultArea.clear());
        
        FileHandlerRegistry registry = new FileHandlerRegistry();
        registry.registerHandler(new TextFileHandler(this));
        registry.registerHandler(new ExcelFileHandler(this));
        registry.registerHandler(new WordFileHandler(this));
        // Tambahkan handler lainnya

        FileSearcher searcher = new FileSearcher(
            selectedFolder, 
            keywordField.getText().trim(), 
            registry
        );
        
        searcher.search();
    }

    public void appendResult(String text) {
        Platform.runLater(() -> resultArea.appendText(text));
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}