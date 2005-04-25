/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.main;

import javax.swing.JOptionPane;

public class InfVisException extends Exception implements java.io.Serializable {
    private String msg = "";
    private String title = "";
    
    public InfVisException() {
        
    }
    
    /*
     * Fetches exception
     */
    public InfVisException(String _msg, boolean show){
        msg = _msg;
        
        if (show){
            showDialogMessage();
        }
    }
    
    /*
     * Fetches exception
     */
    public InfVisException(String _title, String _msg, boolean show){
        msg = _msg;
        title = _title;
        
        if (show){
            showDialogMessage();
        }
    }
    
    /*
     * Displays exception as JDialog message
     */
    public void showDialogMessage(){
        JOptionPane.showMessageDialog(null,msg, title, JOptionPane.ERROR_MESSAGE );
    }
    
    /*
     * Displays exception as JDialog info message
     */
    public void showInfoDialogMessage(){
        JOptionPane.showMessageDialog(null,msg, title, JOptionPane.INFORMATION_MESSAGE );
    }
}
