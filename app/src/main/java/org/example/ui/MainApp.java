package org.example.ui;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.Core.FileHandlerRegistry;
import org.example.Core.FileSearcher;
import org.example.Core.TextFileHandler;
import org.example.Handler.ExcelFileHandler;
import org.example.Handler.WordFileHandler;

import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea; // Tetap dibutuhkan untuk membersArea, tapi tidak untuk hasil
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight; // Untuk TextFlow bold
import javafx.scene.text.Text;      // Untuk TextFlow
import javafx.scene.text.TextFlow; // Untuk TextFlow

public class MainApp extends Application {
    private File selectedFolder;
    // private TextArea resultArea; // resultArea dihilangkan dan diganti dengan VBox
    private VBox resultsContainer; // Container untuk kartu-kartu hasil
    private TextField keywordField;
    private RadioButton wordRadio;
    private RadioButton sentenceRadio;
    private ToggleGroup searchTypeGroup;
    private Label folderPathLabel;
    private ScrollPane scrollPaneForResults;
    private Label currentSearchInfoLabel; // Label untuk "Hasil pencarian untuk: "circle""
    private Label currentFolderPathLabel; // Label untuk "Folder: C:\Path"

    public enum SearchType {
        WORD,
        SENTENCE
    }

    @Override
    public void start(Stage stage) {
        Label headerLabelText = new Label("TUGAS CBS");
        headerLabelText.getStyleClass().add("header");
        
        TextArea membersArea = new TextArea(
            "1. Mahesa Putri Lukman - H071241009\n" +
            "2. Akhmad Hidayat - H071241003"
        );
        membersArea.setEditable(false);
        membersArea.setPrefHeight(50);
        membersArea.getStyleClass().add("members");
        membersArea.setFocusTraversable(false);

        VBox headerContainer = new VBox(0, headerLabelText, membersArea);
        headerContainer.getStyleClass().add("app-header-container"); 

        initializeSearchComponents(); // Inisialisasi komponen utama

        // Inisialisasi Label untuk informasi pencarian
        currentSearchInfoLabel = new Label("Hasil pencarian:"); // Default text
        currentSearchInfoLabel.getStyleClass().add("search-info-label");

        currentFolderPathLabel = new Label("Folder: "); // Default text
        currentFolderPathLabel.getStyleClass().add("search-info-path-label");

        // Layout Utama dengan GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); 
        grid.setVgap(15); 
        grid.setHgap(12); 

        grid.add(headerContainer, 0, 0, 2, 1); 
        grid.add(new Separator(), 0, 1, 2, 1); 

        // Komponen pencarian (folder, tipe, keyword)
        Label folderPromptLabel = new Label("Folder Pencarian:");
        grid.add(folderPromptLabel, 0, 2);
        
        Button selectFolderBtn = new Button("📁 Pilih Folder");
        selectFolderBtn.getStyleClass().add("button");
        selectFolderBtn.setMaxWidth(Double.MAX_VALUE); 
        Tooltip.install(selectFolderBtn, new Tooltip("Klik untuk memilih folder target pencarian"));
        grid.add(selectFolderBtn, 1, 2);
        
        grid.add(folderPathLabel, 0, 3, 2, 1);
        folderPathLabel.getStyleClass().add("folder-path-display");

        Label searchTypePromptLabel = new Label("Tipe Pencarian:");
        grid.add(searchTypePromptLabel, 0, 4);
        HBox radioBox = new HBox(15, wordRadio, sentenceRadio);
        radioBox.setAlignment(Pos.CENTER_LEFT); 
        grid.add(radioBox, 1, 4);

        Label keywordPromptLabelInput = new Label("Kata Kunci:");
        grid.add(keywordPromptLabelInput, 0, 5);
        grid.add(keywordField, 1, 5);

        Button searchBtn = new Button("🔍 Mulai Pencarian");
        searchBtn.getStyleClass().add("button");
        searchBtn.setMaxWidth(Double.MAX_VALUE); 
        Tooltip.install(searchBtn, new Tooltip("Mulai proses pencarian berdasarkan kriteria"));
        grid.add(searchBtn, 0, 6, 2, 1);

        // Informasi hasil pencarian (Header hasil)
        grid.add(new Separator(), 0, 7, 2, 1); // Separator sebelum hasil
        grid.add(currentSearchInfoLabel, 0, 8, 2, 1); // Label "Hasil pencarian untuk:"
        grid.add(currentFolderPathLabel, 0, 9, 2, 1); // Label "Folder:"

        // ScrollPane yang sekarang membungkus resultsContainer (VBox)
        grid.add(scrollPaneForResults, 0, 10, 2, 1); // Baris disesuaikan

        setupEventHandlers(stage, selectFolderBtn, searchBtn);

        Scene scene = new Scene(grid, 700, 750); // Ukuran window disesuaikan agar lebih luas
        
        String cssPath = "/style.css"; 
        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        
        stage.setTitle("Aplikasi Pencari File Pro"); 
        stage.setScene(scene);

        stage.centerOnScreen();
        stage.iconifiedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Platform.runLater(stage::centerOnScreen);
            }
        });
        
        stage.show();
    }

    private void initializeSearchComponents() {
        // resultArea diganti dengan resultsContainer (VBox)
        resultsContainer = new VBox(10); // Spasi 10px antar kartu hasil
        resultsContainer.getStyleClass().add("results-container"); // Style class untuk VBox
        resultsContainer.setPadding(new Insets(10)); // Padding internal results container
        
        scrollPaneForResults = new ScrollPane(resultsContainer);
        scrollPaneForResults.getStyleClass().add("scroll-pane");
        scrollPaneForResults.setFitToWidth(true); 
        scrollPaneForResults.setPrefHeight(250); // Beri tinggi preferensi agar ScrollPane terlihat
        scrollPaneForResults.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Scrollbar hanya muncul jika perlu

        keywordField = new TextField();
        keywordField.setPromptText("Ketik kata atau kalimat...");
        keywordField.getStyleClass().add("text-field");
        Tooltip.install(keywordField, new Tooltip("Masukkan kata kunci yang ingin dicari"));
        
        searchTypeGroup = new ToggleGroup();
        wordRadio = new RadioButton("Kata"); 
        sentenceRadio = new RadioButton("Kalimat"); 
        wordRadio.setToggleGroup(searchTypeGroup);
        sentenceRadio.setToggleGroup(searchTypeGroup);
        wordRadio.setSelected(true);
        wordRadio.getStyleClass().add("radio-button");
        sentenceRadio.getStyleClass().add("radio-button");
        
        folderPathLabel = new Label("Folder belum dipilih"); 
    }

    private void setupEventHandlers(Stage stage, Button selectFolderBtn, Button searchBtn) {
        selectFolderBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Pilih Folder Target");
            selectedFolder = chooser.showDialog(stage);
            if (selectedFolder != null) {
                folderPathLabel.setText("Path: " + selectedFolder.getAbsolutePath());
            } else {
                folderPathLabel.setText("Folder belum dipilih");
            }
        });

        searchBtn.setOnAction(e -> {
            if (validateInput()) {
                new Thread(this::performSearch).start();
            }
        });
    }

    private boolean validateInput() {
        if (selectedFolder == null) {
            showAlert("Input Diperlukan", "Harap pilih folder terlebih dahulu untuk memulai pencarian.");
            return false;
        }
        String keyword = keywordField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Input Diperlukan", "Kata kunci pencarian tidak boleh kosong.");
            return false;
        }
        if (wordRadio.isSelected() && keyword.split("\\s+").length > 1) {
            showAlert("Format Input Salah", "Untuk mode pencarian 'Kata', harap masukkan hanya satu kata.");
            return false;
        }
        return true;
    }

    private void performSearch() {
        Platform.runLater(() -> {
            resultsContainer.getChildren().clear(); // Hapus semua hasil sebelumnya
        });
        
        FileHandlerRegistry registry = new FileHandlerRegistry();
        registry.registerHandler(new TextFileHandler(this)); 
        registry.registerHandler(new WordFileHandler(this)); 
        registry.registerHandler(new ExcelFileHandler(this)); 

        SearchType currentSearchType = wordRadio.isSelected() ? SearchType.WORD : SearchType.SENTENCE;
        String keywordToSearch = keywordField.getText().trim();
        String folderPath = selectedFolder.getAbsolutePath();

        Platform.runLater(() -> {
            currentSearchInfoLabel.setText("Hasil pencarian untuk: \"" + keywordToSearch + "\"");
            currentFolderPathLabel.setText("Folder: " + folderPath);
            // Tambahkan pesan awal ke container hasil
            Label initialMsg = new Label("Memulai pencarian...");
            initialMsg.getStyleClass().add("search-message");
            resultsContainer.getChildren().add(initialMsg);
        });

        FileSearcher searcher = new FileSearcher(
            selectedFolder, 
            keywordToSearch, 
            registry,
            currentSearchType,
            this
        ); 
        
        searcher.search();

        Platform.runLater(() -> {
            // Tambahkan pesan selesai ke container hasil
            Label finalMsg = new Label("Pencarian selesai.");
            finalMsg.getStyleClass().add("search-message");
            resultsContainer.getChildren().add(finalMsg);
        });
    }

    /**
     * Metode baru untuk menampilkan hasil pencarian sebagai kartu terpisah.
     * Dipanggil dari FileHandlers.
     * @param fileName Nama file.
     * @param filePath Path lengkap file.
     * @param contextLabel Teks konteks (misal "Baris 3", "Paragraf 5", "Sheet X, Baris Y, Kolom Z").
     * @param fullLineContent Isi baris/paragraf/sel tempat keyword ditemukan.
     * @param keyword Kata kunci yang dicari (untuk highlighting).
     */
    public void displaySearchResult(String fileName, String filePath, String contextLabel, String fullLineContent, String keyword) {
        Platform.runLater(() -> {
            VBox card = new VBox(5); // Spasi 5px antar elemen dalam kartu
            card.getStyleClass().add("result-card"); // Style class untuk kartu

            // Nama File
            Label fileNameLabel = new Label("Nama File: " + fileName);
            fileNameLabel.getStyleClass().add("result-file-name");

            // Path File
            Label filePathLabel = new Label("Path: " + filePath);
            filePathLabel.getStyleClass().add("result-file-path");
            filePathLabel.setWrapText(true); // Pastikan path bisa wrap

            // Baris/Paragraf/Sel dengan highlighting
            Label contextLabelPart = new Label(contextLabel + ":"); // Label "Baris 3:"
            contextLabelPart.getStyleClass().add("result-line-context");

            // TextFlow untuk konten baris/paragraf/sel agar bisa highlight
            TextFlow lineContentTextFlow = new TextFlow();
            lineContentTextFlow.getStyleClass().add("result-line-content-flow");

            Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(fullLineContent);

            int lastIndex = 0;
            while (matcher.find()) {
                // Tambahkan teks sebelum keyword
                if (matcher.start() > lastIndex) {
                    lineContentTextFlow.getChildren().add(new Text(fullLineContent.substring(lastIndex, matcher.start())));
                }
                // Tambahkan keyword yang di-highlight
                Text highlightedText = new Text(fullLineContent.substring(matcher.start(), matcher.end()));
                highlightedText.getStyleClass().add("highlighted-text"); // Style class untuk highlight
                lineContentTextFlow.getChildren().add(highlightedText);
                lastIndex = matcher.end();
            }
            // Tambahkan sisa teks setelah keyword terakhir
            if (lastIndex < fullLineContent.length()) {
                lineContentTextFlow.getChildren().add(new Text(fullLineContent.substring(lastIndex)));
            }

            card.getChildren().addAll(fileNameLabel, filePathLabel, contextLabelPart, lineContentTextFlow);
            resultsContainer.getChildren().add(card);
        });
    }

    // Metode appendResult lama diganti namanya menjadi appendMessage untuk pesan umum
    public void appendMessage(String text) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(text);
            msgLabel.getStyleClass().add("search-message"); // Gaya untuk pesan
            resultsContainer.getChildren().add(msgLabel);
        });
    }


    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION); 
            alert.setTitle(title);
            alert.setHeaderText(null); 
            alert.setContentText(message);
            alert.getDialogPane().setStyle("-fx-font-family: \"Segoe UI\", Arial, sans-serif;");
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}