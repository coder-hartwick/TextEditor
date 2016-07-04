package texteditor;

import javafx.concurrent.Task;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * Adds printing functionality to the text editor.
 *
 * @author Jordan Hartwick
 * Jul 3, 2016
 */
public class PrinterWorker {


    /**
     * The TextFlow that will contain the text that needs to be printed.
     */
    private final TextFlow node;
    
    
    /**
     * The boolean that contains the value for if the printer has finished printing.
     */
    private boolean printerFinished = false;


    /**
     * The PrintWorker constructor method will set the
     * @param node
     */
    public PrinterWorker(EditingArea node) {
        Text text = new Text(node.getText());
        text.setFont(new Font("Arial", 12));
        this.node = new TextFlow(text);
    }


    /**
     * Creates a PrinterJob and prints the page the user typed.
     */
    public void print() {
        PrinterJob job = PrinterJob.createPrinterJob();
        
        Task task = new Task<Void>() {

            @Override
            public Void call() {
                if(job != null && job.showPrintDialog(null)) {
                    PageLayout pageLayout = job.getJobSettings().getPageLayout();
                    node.setMaxWidth(pageLayout.getPrintableWidth());

                    boolean success = job.printPage(node);
                    if(success) {
                        job.endJob();
                        printerFinished = true;
                    }
                }
                return null;
            }
        };
        
        // Display a message for if the printer printed the page.
        task.setOnSucceeded(e -> {
            if(printerFinished) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Printer");
                alert.setHeaderText(null);
                alert.setContentText("Finished Printing!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Printing Document!");
                alert.setHeaderText(null);
                alert.setContentText("There was an error printing the document!");
                alert.showAndWait();
            }
        });
        
        // Display a message if the task failed.
        task.setOnFailed(e -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The printing task encountered an error.");
        });

        // Start the printing task.
        new Thread(task).start();
    }
}
