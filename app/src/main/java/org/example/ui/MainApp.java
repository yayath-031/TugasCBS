package org.example.ui;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox; // Import ColumnConstraints
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser; // Import Priority
import javafx.stage.Stage;

public class MainApp extends Application {
    private File selectedFolder;
    private VBox resultsContainer;
    private TextField keywordField;
    private RadioButton wordRadio;
    private RadioButton sentenceRadio;
    private ToggleGroup searchTypeGroup;
    private Label folderPathLabel;
    private ScrollPane scrollPaneForResults;
    private Label currentSearchInfoLabel;
    private Label currentFolderPathLabel;

    public enum SearchType {
        WORD,
        SENTENCE
    }

    @Override
    public void start(Stage stage) {
        Label headerLabelText = new Label("TUGAS CBS");
        headerLabelText.getStyleClass().add("header-label"); // Menggunakan style class CSS
        
        TextArea membersArea = new TextArea(
            "1. Mahesa Putri Lukman - H071241009\n" +
            "2. Akhmad Hidayat - H071241003"
        );
        membersArea.setEditable(false);
        membersArea.setPrefHeight(50); 
        membersArea.getStyleClass().add("members-area"); // Menggunakan style class CSS
        membersArea.setFocusTraversable(false);

        VBox headerContainer = new VBox(0, headerLabelText, membersArea);
        headerContainer.getStyleClass().add("app-header-container"); 

        initializeSearchComponents();

        currentSearchInfoLabel = new Label("Hasil pencarian:");
        currentSearchInfoLabel.getStyleClass().add("search-info-label");

        currentFolderPathLabel = new Label("Folder: ");
        currentFolderPathLabel.getStyleClass().add("search-info-path-label");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); 
        grid.setVgap(15); 
        grid.setHgap(12); 

        // --- Penambahan ColumnConstraints ---
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.SOMETIMES); // Kolom label bisa tumbuh jika perlu
        // col0.setPercentWidth(35); // Opsional: atur persentase jika ingin lebih presisi

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS); // Kolom input/tombol akan mengambil sisa ruang
        // col1.setPercentWidth(65); // Opsional

        grid.getColumnConstraints().addAll(col0, col1);
        // --- Akhir Penambahan ColumnConstraints ---

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

        grid.add(new Separator(), 0, 7, 2, 1);
        grid.add(currentSearchInfoLabel, 0, 8, 2, 1);
        grid.add(currentFolderPathLabel, 0, 9, 2, 1);
        grid.add(scrollPaneForResults, 0, 10, 2, 1);

        setupEventHandlers(stage, selectFolderBtn, searchBtn);

        Scene scene = new Scene(grid, 700, 750); 
        
        String cssPath = "/style.css"; 
        java.net.URL cssUrl = getClass().getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS Path TIDAK ditemukan: " + cssPath);
            // Anda bisa tambahkan Alert di sini jika perlu
            // showAlert("CSS Error", "File stylesheet tidak ditemukan: " + cssPath);
        }
        
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
        resultsContainer = new VBox(10); 
        resultsContainer.getStyleClass().add("results-container");
        resultsContainer.setPadding(new Insets(10));
        
        scrollPaneForResults = new ScrollPane(resultsContainer);
        scrollPaneForResults.getStyleClass().add("scroll-pane");
        scrollPaneForResults.setFitToWidth(true); 
        scrollPaneForResults.setPrefHeight(250); 
        scrollPaneForResults.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

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
            resultsContainer.getChildren().clear();
        });
        
        FileHandlerRegistry registry = new FileHandlerRegistry();
        // Pastikan handler Anda sudah benar dan dikonfigurasi untuk menggunakan 'this' jika memanggil metode MainApp
        registry.registerHandler(new TextFileHandler(this)); 
        registry.registerHandler(new WordFileHandler(this)); 
        registry.registerHandler(new ExcelFileHandler(this)); 
        // Jika Anda mengaktifkan TextBasedFileHandler, pastikan konstruktornya sesuai
        // registry.registerHandler(new TextBasedFileHandler(this));


        SearchType currentSearchType = wordRadio.isSelected() ? SearchType.WORD : SearchType.SENTENCE;
        String keywordToSearch = keywordField.getText().trim();
        String folderPathString = (selectedFolder != null) ? selectedFolder.getAbsolutePath() : "N/A";


        Platform.runLater(() -> {
            currentSearchInfoLabel.setText("Hasil pencarian untuk: \"" + keywordToSearch + "\"");
            currentFolderPathLabel.setText("Folder: " + folderPathString);
            Label initialMsg = new Label("Memulai pencarian...");
            initialMsg.getStyleClass().add("search-message");
            resultsContainer.getChildren().add(initialMsg);
        });

        FileSearcher searcher = new FileSearcher(
            selectedFolder, 
            keywordToSearch, 
            registry,
            currentSearchType,
            this // Meneruskan instance MainApp ke FileSearcher
        ); 
        
        searcher.search();

        Platform.runLater(() -> {
            Label finalMsg = new Label("Pencarian selesai.");
            finalMsg.getStyleClass().add("search-message");
            resultsContainer.getChildren().add(finalMsg);
        });
    }

    public void displaySearchResult(String fileName, String filePath, String contextLabel, String fullLineContent, String keyword) {
        Platform.runLater(() -> {
            VBox card = new VBox(5); 
            card.getStyleClass().add("result-card");

            Label fileNameLabel = new Label("Nama File: " + (fileName != null ? fileName : "N/A"));
            fileNameLabel.getStyleClass().add("result-file-name");

            Label filePathLabelCard = new Label("Path: " + (filePath != null ? filePath : "N/A")); // Variabel dibedakan
            filePathLabelCard.getStyleClass().add("result-file-path");
            filePathLabelCard.setWrapText(true);

            Label contextLabelPart = new Label((contextLabel != null ? contextLabel : "") + ":");
            contextLabelPart.getStyleClass().add("result-line-context");

            TextFlow lineContentTextFlow = new TextFlow();
            lineContentTextFlow.getStyleClass().add("result-line-content-flow");

            if (fullLineContent != null && keyword != null && !keyword.isEmpty()) {
                Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(fullLineContent);

                int lastIndex = 0;
                while (matcher.find()) {
                    if (matcher.start() > lastIndex) {
                        lineContentTextFlow.getChildren().add(new Text(fullLineContent.substring(lastIndex, matcher.start())));
                    }
                    Text highlightedText = new Text(fullLineContent.substring(matcher.start(), matcher.end()));
                    highlightedText.getStyleClass().add("highlighted-text");
                    lineContentTextFlow.getChildren().add(highlightedText);
                    lastIndex = matcher.end();
                }
                if (lastIndex < fullLineContent.length()) {
                    lineContentTextFlow.getChildren().add(new Text(fullLineContent.substring(lastIndex)));
                }
            } else if (fullLineContent != null) { // Jika tidak ada keyword (atau keyword kosong), tampilkan konten biasa
                 lineContentTextFlow.getChildren().add(new Text(fullLineContent));
            }


            card.getChildren().addAll(fileNameLabel, filePathLabelCard, contextLabelPart, lineContentTextFlow);
            resultsContainer.getChildren().add(card);
        });
    }

    public void appendMessage(String text) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(text);
            msgLabel.getStyleClass().add("search-message");
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