package texteditor;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;


/**
 * The EditingArea is a TextArea that the user can type in.
 *
 * @author Jordan Hartwick
 * Jul 2, 2016
 */
public class EditingArea extends TextArea {
    
    
    /**
     * A boolean containing whether or not this TextArea has been edited or not.
     */
    private boolean hasBeenEdited = false;
    
    
    /**
     * The current file associated with this TextArea.
     */
    private File currentFile = null;
    
    
    /**
     * Constructor for the EditingArea sets all needed settings and adds all needed
     * event filters.
     */
    public EditingArea() {
        setPrefRowCount(20);
        setPrefColumnCount(75);
        setWrapText(true);
        setFont(new Font("Arial", 12));
        
        ContextMenu contextMenu = new ContextMenu();
        
        /*
         * A listener for if the document associated with this text area has been
         * edited.
         */
        this.textProperty().addListener((final ObservableValue<? extends String> observable, 
                                        final String oldValue, 
                                        final String newValue) -> {
            hasBeenEdited = true;
        });
        
        /*
         * Listen for mouse clicks within this text area and if the click is the
         * secondary mouse button (right click), display the context menu.
         */
        addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, e.getX(), e.getY());
            }
        });
    }
    
    
    /**
     * Sets the current file for this TextArea.
     * 
     * @param file  The active file for this TextArea.
     */
    public void setCurrentFile(File file) {
        currentFile = file;
    }
    
    
    /**
     * Returns the current file for this text area.
     * 
     * @return  The current file for this text area.
     */
    public File getCurrentFile() {
        return currentFile;
    }
    
    
    /**
     * Returns whether or not this text area has been edited.
     * 
     * @return  Whether or not this text area has been edited.
     */
    public boolean getHasBeenEdited() {
        return hasBeenEdited;
    }
    
    
    /**
     * Sets the hasBeenEdited variable in this text area to false.
     */
    public void resetHasBeenEdited() {
        hasBeenEdited = false;
    }
}