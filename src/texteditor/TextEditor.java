/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texteditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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


    private ToolBar getToolBar(final Stage primaryStage, TextArea editingArea) {
        ToolBar toolBar = new ToolBar();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".txt", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*")
        );

        Button newFile = new Button();
        newFile.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/new.png"))));
        toolBar.getItems().add(newFile);
        newFile.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                editingArea.clear();
                currentFile = null;

                primaryStage.setTitle("Text Editor - New File");
                editingArea.requestFocus();
            }
        });
        newFile.setTooltip(new Tooltip("Create New File"));

        Button openFile = new Button();
        openFile.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/open.png"))));
        toolBar.getItems().add(openFile);
        openFile.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

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
                        editingArea.requestFocus();
                    } catch (IOException err) {
                        showExceptionDialog(err);
                        editingArea.requestFocus();
                    }
                }
            }
        });
        openFile.setTooltip(new Tooltip("Open File"));

        Button saveFile = new Button();
        saveFile.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/save.png"))));
        toolBar.getItems().add(saveFile);
        saveFile.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

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

            }
        });
        saveFile.setTooltip(new Tooltip("Save File"));

        Button saveAs = new Button();
        saveAs.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/save_as.png"))));
        toolBar.getItems().add(saveAs);
        saveAs.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                File temp = fileChooser.showSaveDialog(primaryStage);

                if(temp == null) {
                    return;
                }

                currentFile = temp;

                saveFile(editingArea.getText());

                primaryStage.setTitle("Text Editor - " + currentFile.getName());
                editingArea.requestFocus();
            }
        });
        saveAs.setTooltip(new Tooltip("Save As..."));

        return toolBar;
    }


    private TextArea getTextEditorArea() {
        TextArea textArea = new TextArea();

        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(75);
        textArea.setWrapText(true);
        textArea.setFont(new Font("Arial", 12));

        setupClipboard(textArea);

        return textArea;
    }


    private void setupClipboard(TextArea component) {

        ContextMenu contextMenu = new ContextMenu();

        component.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {
                if(e.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(component, e.getX(), e.getY());
                }
            }
        });
    }


    private void saveFile(String content) {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(currentFile)));) {
            pw.println(content);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText(null);
            alert.setContentText("File Saved!");
            alert.showAndWait();
        } catch (IOException err) {
            showExceptionDialog(err);
        }
    }


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
