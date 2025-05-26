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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup; // Pastikan TextArea diimpor
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainApp extends Application {
    private File selectedFolder;
    private TextArea resultArea;
    private TextField keywordField;
    private RadioButton wordRadio;
    private RadioButton sentenceRadio;
    private ToggleGroup searchTypeGroup;
    private Label folderPathLabel;
    private ScrollPane scrollPaneForResults;

    public enum SearchType {
        WORD,
        SENTENCE
    }

    @Override
    public void start(Stage stage) {
        Label headerLabelText = new Label("TUGAS CBS");
        headerLabelText.getStyleClass().add("header");
        
        // Mengembalikan penggunaan TextArea untuk daftar anggota seperti awal
        TextArea membersArea = new TextArea(
            "1. Mahesa Putri Lukman - H071241009\n" +
            "2. Akhmad Hidayat - H071241003"
            // Tambahkan nama anggota lain di sini jika ada, masing-masing di baris baru
        );
        membersArea.setEditable(false);
        // Kembalikan pengaturan tinggi seperti yang Anda inginkan (misalnya, 45 atau 50)
        // Sesuaikan nilai ini jika Anda ingin menampilkan lebih banyak baris tanpa scroll
        // atau biarkan nilai awal jika itu yang Anda maksud "seperti awal"
        membersArea.setPrefHeight(50); // Atau nilai awal Anda jika berbeda
        // membersArea.setMinHeight(USE_PREF_SIZE); // Jika ingin tinggi minimal mengikuti preferensi
        membersArea.getStyleClass().add("members"); // Pastikan class .members di CSS sesuai untuk TextArea
        membersArea.setFocusTraversable(false);

        VBox headerContainer = new VBox(0, headerLabelText, membersArea); // Menggunakan TextArea kembali
        headerContainer.getStyleClass().add("app-header-container"); 

        initializeSearchComponents();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); 
        grid.setVgap(15); 
        grid.setHgap(12); 

        grid.add(headerContainer, 0, 0, 2, 1); 
        grid.add(new Separator(), 0, 1, 2, 1); 

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

        Label resultPromptLabel = new Label("Hasil:");
        grid.add(resultPromptLabel, 0, 7);
        grid.add(scrollPaneForResults, 0, 8, 2, 1);

        setupEventHandlers(stage, selectFolderBtn, searchBtn);

        Scene scene = new Scene(grid, 700, 650); 
        
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
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.getStyleClass().add("text-area");
        
        scrollPaneForResults = new ScrollPane(resultArea);
        scrollPaneForResults.getStyleClass().add("scroll-pane");
        scrollPaneForResults.setFitToWidth(true); 
        scrollPaneForResults.setFitToHeight(true); 

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
            resultArea.clear();
        });
        
        FileHandlerRegistry registry = new FileHandlerRegistry();
        registry.registerHandler(new TextFileHandler(this)); 
        registry.registerHandler(new WordFileHandler(this)); 
        registry.registerHandler(new ExcelFileHandler(this)); 

        SearchType currentSearchType = wordRadio.isSelected() ? SearchType.WORD : SearchType.SENTENCE;
        String keywordToSearch = keywordField.getText().trim();

        String initialMessage = String.format("Mencari %s '%s' dalam folder '%s'...\n============================================\n",
                                (currentSearchType == SearchType.WORD ? "kata" : "kalimat"),
                                keywordToSearch,
                                selectedFolder.getName());
        Platform.runLater(() -> appendResult(initialMessage));

        FileSearcher searcher = new FileSearcher(
            selectedFolder, 
            keywordToSearch, 
            registry,
            currentSearchType
        ); 
        
        searcher.search();

        Platform.runLater(() -> {
            appendResult("============================================\nPencarian selesai untuk '" + keywordToSearch + "'.\n");
        });
    }

    public void appendResult(String text) {
        Platform.runLater(() -> resultArea.appendText(text));
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