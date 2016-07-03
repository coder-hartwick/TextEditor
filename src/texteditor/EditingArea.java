package texteditor;

import java.io.File;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
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
        
        addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent e) -> {
            hasBeenEdited = true;
        });
        
        ContextMenu contextMenu = new ContextMenu();
        
        addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, e.getX(), e.getY());
            }
        });
    }
    
    
    public void setCurrentFile(File file) {
        currentFile = file;
    }
    
    public File getCurrentFile() {
        return currentFile;
    }
    
    public boolean getHasBeenEdited() {
        return hasBeenEdited;
    }
    
    public void resetHasBeenEdited() {
        hasBeenEdited = false;
    }
}