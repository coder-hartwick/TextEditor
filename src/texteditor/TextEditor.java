package texteditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * TextEditor is a very basic text editor that allows the user to open, save, and
 * create files.
 *
 * @author Jordan Hartwick
 */
public class TextEditor extends Application {


    /**
     * The file that is currently being edited.
     */
    private File currentFile = null;


    /**
     * Keeps track for if the file has been edited.
     */
    private boolean hasBeenEdited = false;


    /*
        The start method will create the scene and add the components to it.
    */
    @Override
    public void start(Stage primaryStage) {

        BorderPane borderPane = new BorderPane();

        TextArea ta = getTextEditorArea();
        ToolBar tb = getToolBar(primaryStage, ta);

        borderPane.setTop(tb);
        borderPane.setCenter(ta);

        Scene scene = new Scene(borderPane, 500, 500);

        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

        ta.requestFocus();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Creates and returns the tool bar that appears at the top of the window.
     *
     * @param primaryStage  The stage that is the parent for the alert dialogs.
     * @param editingArea   The text area that the user types in.
     * @return  The tool bar that contains the new, open, save,save as, and exit
     *          buttons.
     */
    private ToolBar getToolBar(final Stage primaryStage, final TextArea editingArea) {
        ToolBar toolBar = new ToolBar();

        // Create the file chooser and add extension filters to it.
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*")
        );

        // Create the new file button.
        Button newFile = new Button();
        newFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/new.png"))));
        toolBar.getItems().add(newFile);
        newFile.setOnAction((ActionEvent e) -> {

            showPossibleDataLossDialog(fileChooser, primaryStage, editingArea);

            editingArea.clear();
            currentFile = null;
            hasBeenEdited = false;

            primaryStage.setTitle("Text Editor - New File");
            editingArea.requestFocus();
        });
        newFile.setTooltip(new Tooltip("Create New File"));

        // Create the open file button.
        Button openFile = new Button();
        openFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/open.png"))));
        toolBar.getItems().add(openFile);
        openFile.setOnAction((ActionEvent e) -> {
            File temp = fileChooser.showOpenDialog(primaryStage);

            if(temp != null) {

                editingArea.clear();

                currentFile = temp;

                try {
                    BufferedReader br = new BufferedReader(new FileReader(currentFile));
                    String line;

                    while ((line = br.readLine()) != null) {
                        editingArea.appendText(line + "\n");
                    }

                    primaryStage.setTitle("Text Editor - " + currentFile.getName());

                    br.close();
                    
                    hasBeenEdited = false;
                    editingArea.requestFocus();
                } catch (IOException err) {
                    showExceptionDialog(err);
                    editingArea.requestFocus();
                }
            }
        });
        openFile.setTooltip(new Tooltip("Open File"));

        // Create the save file button.
        Button saveFile = new Button();
        saveFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/save.png"))));
        toolBar.getItems().add(saveFile);
        saveFile.setOnAction((ActionEvent e) -> {
            if (currentFile == null) {
                File temp = fileChooser.showSaveDialog(primaryStage);
                if (temp == null) {
                    return;
                }

                currentFile = temp;
            }

            saveFile(editingArea.getText());

            primaryStage.setTitle("Text Editor - " + currentFile.getName());
            editingArea.requestFocus();
        });
        saveFile.setTooltip(new Tooltip("Save File"));

        // Create the save as button.
        Button saveAs = new Button();
        saveAs.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/save_as.png"))));
        toolBar.getItems().add(saveAs);
        saveAs.setOnAction((ActionEvent e) -> {
            File temp = fileChooser.showSaveDialog(primaryStage);

            if(temp == null) {
                return;
            }

            currentFile = temp;

            saveFile(editingArea.getText());
            
            primaryStage.setTitle("Text Editor - " + currentFile.getName());
            editingArea.requestFocus();
        });
        saveAs.setTooltip(new Tooltip("Save File As..."));

        // Create exit button.
        Button exit = new Button();
        exit.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/exit.png"))));
        toolBar.getItems().add(exit);
        exit.setOnAction((ActionEvent e) -> {
            if(showPossibleDataLossDialog(fileChooser, primaryStage, editingArea)) {
                System.exit(0);
            }
        });
        exit.setTooltip(new Tooltip("Quit The Program"));

        return toolBar;
    }


    /**
     * Creates the text area that the user will type in.
     *
     * @return The text area that the user will type in.
     */
    private TextArea getTextEditorArea() {
        TextArea textArea = new TextArea();

        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(75);
        textArea.setWrapText(true);
        textArea.setFont(new Font("Arial", 12));
        textArea.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent e) -> {
            hasBeenEdited = true;
        });

        setupClipboard(textArea);

        return textArea;
    }


    /**
     * Sets up the right click and show items context menu.
     *
     * @param component     The component that the context menu should be shown in.
     */
    private void setupClipboard(TextArea component) {

        ContextMenu contextMenu = new ContextMenu();

        component.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if(e.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.show(component, e.getX(), e.getY());
            }
        });
    }


    /**
     * Saves the text the user typed to the current file.
     *
     * @param content   The text to be saved to the file.
     */
    private void saveFile(String content) {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(currentFile)));) {
            pw.println(content);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText(null);
            alert.setContentText("File Saved!");
            alert.showAndWait();
            hasBeenEdited = false;
        } catch (IOException err) {
            showExceptionDialog(err);
        }
    }


    /**
     * Shows an Alert to the user saying that their file has unsaved changes and
     * asks if they want to save their changes before creating a new file or
     * exiting the program.
     *
     * @param fileChooser   The file chooser that is shown if they want to save.
     * @param primaryStage  The parent window of the file chooser.
     * @param editingArea   The text area that contains the text the user typed.
     * @return true if the application can exit; false if not.
     */
    private boolean showPossibleDataLossDialog(final FileChooser fileChooser,
                                            final Stage primaryStage,
                                            final TextArea editingArea) {

        if(hasBeenEdited) {

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
                if(currentFile == null) {
                    File temp = fileChooser.showSaveDialog(primaryStage);
                    if (temp == null) {
                        return false;
                    }
                    currentFile = temp;
                }

                saveFile(editingArea.getText());
                return true;
            } else if(result.get() == cancel) {
                return false;
            }
        }
        return true;
    }


    /**
     * Shows a dialog with the exception message.
     *
     * @param e     The exception that was thrown.
     */
    private void showExceptionDialog(Throwable e) {
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