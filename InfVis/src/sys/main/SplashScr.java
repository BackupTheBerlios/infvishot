/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.main;

import javax.swing.JWindow;
import javax.swing.JLabel;
import java.awt.*;

public class SplashScr extends JWindow {
    private SysCore sysCore = null;
    
	private javax.swing.JPanel jContentPane = null;

	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	/**
	 * This is the default constructor
	 */
	public SplashScr(SysCore _sysCore) {
		super();
		sysCore = _sysCore;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(399, 200);
		this.setContentPane(getJContentPane());
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (d.width - getSize().width ) / 2,(d.height- getSize().height) / 2 );
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(null);
			jLabel.setText("Loading Plugins and Co");
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel.setBounds(0, 0, 390, 126);
			jLabel1.setText("InfVis Project by Harald Meyer, Tobias Schleser, Oliver Hörbinger");
			jLabel1.setBounds(0, 140, 388, 60);
			jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
		}
		return jContentPane;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
