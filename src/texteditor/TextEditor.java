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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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


    /*
        The start method will create the scene and add the components to it.
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

        Scene scene = new Scene(borderPane, 500, 500);

        primaryStage.setTitle("Text Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
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
     * @return  The tool bar that contains the new, open, save,save as, and exit
     *          buttons.
     */
    private ToolBar getToolBar() {
        ToolBar toolBar = new ToolBar();

        // Create the new file button.
        Button newFile = new Button();
        newFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/new.png"))));
        toolBar.getItems().add(newFile);
        newFile.setOnAction((ActionEvent e) -> {
            addNewTab();
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

                EditingArea editingArea = getActiveEditingArea();

                editingArea.clear();

                editingArea.setCurrentFile(temp);

                try {
                    BufferedReader br = new BufferedReader(new FileReader(editingArea.getCurrentFile()));
                    String line;

                    while ((line = br.readLine()) != null) {
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
        });
        openFile.setTooltip(new Tooltip("Open File"));

        // Create the save file button.
        Button saveFile = new Button();
        saveFile.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/save.png"))));
        toolBar.getItems().add(saveFile);
        saveFile.setOnAction((ActionEvent e) -> {
            EditingArea editingArea = getActiveEditingArea();

            if (editingArea.getCurrentFile() == null) {
                File temp = fileChooser.showSaveDialog(primaryStage);
                if (temp == null) {
                    return;
                }

                editingArea.setCurrentFile(temp);
            }

            saveFile(editingArea.getText(), editingArea.getCurrentFile(), editingArea);

            tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());
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

            EditingArea editingArea = getActiveEditingArea();
            editingArea.setCurrentFile(temp);

            saveFile(editingArea.getText(), editingArea.getCurrentFile(), editingArea);

            tabPane.getSelectionModel().getSelectedItem().setText(editingArea.getCurrentFile().getName());
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

            // TODO implement the design below.
            
            /*
                Design Plan:
                
                File Name | Save button | Discard changes button
            
                When the user clicks on the save button, a save file dialog 
                appears and the user either cancels it, or saves the file. If the
                user saves the file, the list item (or table row) containing the
                file will be removed and so on and so forth and what not. If the
                user cancels the save file dialog, the item remains until the user
                clicks the discard button. After all items are gone, the program
                exits. 
                
                There will also be a discard all button, save all button, and a 
                cancel button like usual.
            
                This should be completed tomorrow if there is time.
            */
            
            if(tabPane.getTabs().isEmpty()) {
                System.exit(0);
            }

            ObservableList<Tab> tabs = tabPane.getTabs();
            boolean canExit = true;

            for(Tab tab : tabs) {
                EditingArea editingArea = (EditingArea)tab.getContent();

                if(!showPossibleDataLossDialog(fileChooser, primaryStage, editingArea)) {
                    canExit = false;
                }
            }

            if(canExit) {
                System.exit(0);
            }
        });
        exit.setTooltip(new Tooltip("Quit The Program"));

        return toolBar;
    }

    
    /**
     * Returns the active editing area.
     * 
     * @return the active editing area.
     */
    private EditingArea getActiveEditingArea() {
        return (EditingArea)(tabPane.getSelectionModel().getSelectedItem().getContent());
    }

    
    /**
     * Adds a new tab to the tabbed pane.
     */
    private void addNewTab() {
        Tab tab = new Tab("New File");

        EditingArea editingArea = new EditingArea();

        tab.setOnCloseRequest((Event e) -> {
            showPossibleDataLossDialog(fileChooser, primaryStage, editingArea);
        });
        
        tab.setContent(editingArea);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        editingArea.requestFocus();
    }


    /**
     * Saves the text the user typed to the current file.
     *
     * @param content   The text to be saved to the file.
     */
    private void saveFile(final String content, final File file, final EditingArea editingArea) {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));) {
            pw.println(content);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText(null);
            alert.setContentText("File Saved!");
            alert.showAndWait();
            editingArea.resetHasBeenEdited();
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
                                            final EditingArea editingArea) {

        if(editingArea.getHasBeenEdited()) {

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
                if(editingArea.getCurrentFile() == null || !(new File(editingArea.getCurrentFile().getAbsolutePath()).exists())) {
                    File temp = fileChooser.showSaveDialog(primaryStage);
                    if (temp == null) {
                        return false;
                    }
                    editingArea.setCurrentFile(temp);
                }

                saveFile(editingArea.getText(), editingArea.getCurrentFile(), editingArea);
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