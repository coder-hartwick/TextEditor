package texteditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * TextEditor is a very simple text editing application that allows the user to
 * create, open, save, edit, and print files.
 * 
 * @author Jordan Hartwick
 */
public class TextEditor extends Application {


    /**
     * The TabPane that will hold tabs.
     */
    private TabPane tabPane;


    /**
     * The File chooser used when saving, opening, and closing files.
     */
    private FileChooser fileChooser;


    /**
     * The stage of the application.
     */
    private Stage primaryStage;


    /**
     * The amount of documents created.
     */
    private int documentIndex = 1;


    /**
     * Buttons for the tool bar.
     */
    private Button newFile, openFile, saveFile, saveAs, print, exit;

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
    

    /*
     * The start method will create the scene and add the components to it.
     */
    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        BorderPane borderPane = new BorderPane();

        tabPane = new TabPane();

        ToolBar tb = getToolBar();

        borderPane.setTop(tb);
        borderPane.setCenter(tabPane);

        addNewTab();

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*")
        );

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            if(!exitProgram()) {
                e.consume();
            }
        });

        Scene scene = new Scene(borderPane, 500, 500);

        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        getActiveEditingArea().requestFocus();
    }


    /**
     * Returns a tool bar that contains the following buttons:
     * New File, Open File, Save File, Save As, Print File, Exit Program.
     * 
     * @return  The tool bar containing the mentioned buttons.
     */
    private ToolBar getToolBar() {
        ToolBar toolBar = new ToolBar();

        // Create the new file button.
        newFile = new Button();
        newFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/new.png"))));
        toolBar.getItems().add(newFile);
        newFile.setOnAction((ActionEvent e) -> {
            addNewTab();
        });
        newFile.setTooltip(new Tooltip("Create New File - CTRL + N"));

        // Create the open file button.
        openFile = new Button();
        openFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/open.png"))));
        toolBar.getItems().add(openFile);
        openFile.setOnAction((ActionEvent e) -> {
            openFile();
        });
        openFile.setTooltip(new Tooltip("Open File - CTRL + O"));

        // Create the save file button.
        saveFile = new Button();
        saveFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/save.png"))));
        toolBar.getItems().add(saveFile);
        saveFile.setOnAction((ActionEvent e) -> {
            if(tabPane.getTabs().isEmpty()) {
                return;
            }

            EditingArea editingArea = getActiveEditingArea();

            if(saveFile(editingArea, false)) {
                tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());
            }

            editingArea.requestFocus();
        });
        saveFile.setTooltip(new Tooltip("Save File - CTRL + S"));

        // Create the save as button.
        saveAs = new Button();
        saveAs.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/save_as.png"))));
        toolBar.getItems().add(saveAs);
        saveAs.setOnAction((ActionEvent e) -> {
            if(tabPane.getTabs().isEmpty()) {
                return;
            }

            EditingArea editingArea = getActiveEditingArea();
            if(saveFile(editingArea, true)) {
                tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());
            }

            editingArea.requestFocus();
        });
        saveAs.setTooltip(new Tooltip("Save File As - CTRL + Shift + S"));

        // Create the print button.
        print = new Button();
        print.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/print.png"))));
        toolBar.getItems().add(print);
        print.setOnAction((ActionEvent e) -> {
            if(tabPane.getTabs().isEmpty()) {
                return;
            }

            EditingArea editingArea = getActiveEditingArea();
            new PrinterWorker(editingArea).print();
        });
        print.setTooltip(new Tooltip("Print the current document - CTRL + P"));

        // Create exit button.
        exit = new Button();
        exit.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/exit.png"))));
        toolBar.getItems().add(exit);
        exit.setOnAction((ActionEvent e) -> {
            exitProgram();
        });
        exit.setTooltip(new Tooltip("Quit The Program - CTRL + Q"));

        return toolBar;
    }


    /**
     * Returns the currently selected editing area.
     * 
     * @return  The currently selected editing area.
     */
    private EditingArea getActiveEditingArea() {
        return (EditingArea)(tabPane.getSelectionModel().getSelectedItem().getContent());
    }


    /**
     * Adds a new tab to the Tab Pane.
     */
    private void addNewTab() {
        Tab tab = new Tab("Unsaved Document " + documentIndex);

        documentIndex++;

        EditingArea editingArea = new EditingArea();

        tab.setOnCloseRequest((Event e) -> {
            if(showPossibleDataLossDialog(editingArea)) {
                documentIndex--;
            } else {
                e.consume();
            }
        });

        tab.setContent(editingArea);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        // Add key combinations for keyboard shortcuts.
        KeyCodeCombination saveKC = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination saveAsKC = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        KeyCodeCombination openKC = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination quitKC = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination newKC = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination printKC = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
        
        editingArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if(saveAsKC.match(e)) {
                if(saveFile(getActiveEditingArea(), true)) {
                    tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());
                }
            } else if(saveKC.match(e)) {
                if(saveFile(getActiveEditingArea(), false)) {
                    tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());
                }
            } else if(openKC.match(e)) {
                openFile();
            } else if(quitKC.match(e)) {
                exitProgram();
            } else if(newKC.match(e)) {
                addNewTab();
            } else if(printKC.match(e)) {
                new PrinterWorker(editingArea).print();
            }
        });

        editingArea.resetHasBeenEdited();
        editingArea.requestFocus();
    }


    /**
     * Shows a dialog telling the user that they may lose any unsaved changes if
     * the close the current tab, if the content in the current tab needs to
     * be saved.
     * 
     * @param editingArea   The EditingArea that contains the content that may 
     *                      need to be save.
     * @return  Whether or not it is safe to close the current tab.
     */
    private boolean showPossibleDataLossDialog(final EditingArea editingArea) {

        if(isFileSaveNeeded(editingArea)) {

            Alert warning = new Alert(AlertType.WARNING);
            warning.setTitle("Warning!");
            warning.setHeaderText(null);
            warning.setContentText("You have unsaved changes. Would you like to save them?");

            ButtonType yes = new ButtonType("Yes", ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonData.NO);
            ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            warning.getButtonTypes().setAll(no, yes, cancel);

            Optional<ButtonType> result = warning.showAndWait();

            if(result.get() == yes) {
                return saveFile(editingArea, false);
            } else if(result.get() == cancel) {
                return false;
            }
        }
        return true;
    }


    /**
     * Used to aid in the opening of multiple files.
     */
    private void openFile() {
        fileChooser.setTitle("Open File");
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

        if(files != null) {

            for(File temp : files) {

                addNewTab();
                EditingArea editingArea = getActiveEditingArea();
                editingArea.setCurrentFile(temp);

                try (BufferedReader br = new BufferedReader(new FileReader(editingArea.getCurrentFile()))) {
                    
                    String line;

                    while((line = br.readLine()) != null) {
                        editingArea.appendText(line + "\n");
                    }

                    tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());

                    br.close();

                    editingArea.resetHasBeenEdited();
                    editingArea.requestFocus();
                } catch (IOException err) {
                    showExceptionDialog(err);
                    editingArea.requestFocus();
                }
            }
        }
    }


    /**
     * Returns whether or not the file has been saved.
     * 
     * @param editingArea   The editingArea that contains the content that needs
     *                      to be saved.
     * @param saveAs        Whether or not the file chooser should automatically
     *                      be saved.
     * @return  Whether or not the content in the editingArea has been saved.
     */
    private boolean saveFile(final EditingArea editingArea, final boolean saveAs) {

        fileChooser.setTitle("Save File");

        if(saveAs) {
            File temp = fileChooser.showSaveDialog(primaryStage);
            if(temp == null) {
                return false;
            }
            editingArea.setCurrentFile(temp);
        } else if(isFileSaveNeeded(editingArea)) {

            File temp = fileChooser.showSaveDialog(primaryStage);
            if(temp == null) {
                return false;
            }
            editingArea.setCurrentFile(temp);
        }

        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(editingArea.getCurrentFile())));) {
            pw.println(editingArea.getText());
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText(null);
            alert.setContentText("File Saved!");
            alert.showAndWait();
            editingArea.resetHasBeenEdited();
            return true;
        } catch (IOException err) {
            showExceptionDialog(err);
            return false;
        }
    }


    /**
     * Returns whether or not it is safe to exit the program.
     * 
     * @return  true if it is save to exit the program; false if not.
     */
    private boolean exitProgram() {
        ObservableList<Tab> tabs = tabPane.getTabs();
        if(tabs.isEmpty()) {
            System.exit(0);
        }

        List<EditingArea> unsavedEditingAreas = new ArrayList();
        ObservableList<String> unsavedDocuments = FXCollections.observableArrayList();

        // Get the unsaved documents.
        for(Tab tab : tabs) {
            EditingArea editingArea = (EditingArea)tab.getContent();

            if(isFileSaveNeeded(editingArea)) {
                unsavedDocuments.add(tab.getText());
                unsavedEditingAreas.add(editingArea);
            }
        }

        if(unsavedDocuments.isEmpty()) {
            System.exit(0);
        }

        // Create the unsaved documents list.
        ListView<String> unsaved = new ListView(unsavedDocuments);
        unsaved.setMaxWidth(Double.MAX_VALUE);
        unsaved.setMaxHeight(Double.MAX_VALUE);

        final int rowHeight = 24;

        unsaved.setPrefHeight(unsavedDocuments.size() * rowHeight + 2);

        // Set up the alert dialog.
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText(null);

        ButtonType discardAll = new ButtonType("Discard All");
        ButtonType save = new ButtonType("Save");
        ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(discardAll, save, cancel);

        Label label = new Label("You have files with unsaved changes");

        GridPane.setVgrow(unsaved, Priority.ALWAYS);
        GridPane.setHgrow(unsaved, Priority.ALWAYS);

        GridPane gp = new GridPane();
        gp.setMaxWidth(Double.MAX_VALUE);
        gp.add(label, 0, 0);
        gp.add(unsaved, 0, 1);

        alert.getDialogPane().setContent(gp);

        unsaved.getSelectionModel().selectFirst();

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == discardAll) {
            System.exit(0);
        } else if(result.get() == save) {
            EditingArea editingArea = unsavedEditingAreas.get(unsaved.getSelectionModel().getSelectedIndex());

            if(saveFile(editingArea, false)) {
                unsavedEditingAreas.remove(editingArea);
                unsaved.getItems().remove(unsaved.getSelectionModel().getSelectedIndex());
            }

            exitProgram();
        } else {
            return false;
        }
        return true;
    }

    
    /**
     * Returns whether or not the content in the editing area needs to be saved.
     * 
     * @param editingArea   The EditingArea that contains the content that might
     *                      need to be saved.
     * @return  Whether or not the content in editingArea needs to be saved.
     */
    private boolean isFileSaveNeeded(final EditingArea editingArea) {
        if(editingArea.getCurrentFile() != null 
                && !(new File(editingArea.getCurrentFile().getAbsolutePath()).exists())) {
            return true;
        } else {
            return editingArea.getHasBeenEdited();
        }
    }
    

    /**
     * Shows a dialog with the exception message.
     *
     * @param e     The exception that was thrown.
     */
    private void showExceptionDialog(final Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String text = sw.toString();

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("An Error Occurred");
        alert.setHeaderText(e.getMessage());

        Label label = new Label("The Exception Was:");

        TextArea ta = new TextArea(text);
        ta.setEditable(false);
        ta.setWrapText(true);

        ta.setMaxWidth(Double.MAX_VALUE);
        ta.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(ta, Priority.ALWAYS);
        GridPane.setHgrow(ta, Priority.ALWAYS);

        GridPane gp = new GridPane();
        gp.setMaxWidth(Double.MAX_VALUE);
        gp.add(label, 0, 0);
        gp.add(ta, 0, 1);

        alert.getDialogPane().setExpandableContent(ta);
        alert.showAndWait();
    }
}