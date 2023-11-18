package practica5_6;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class VProgressBar extends JFrame{
    private static JProgressBar pb;

	public VProgressBar() {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 500, 75 );
		setLocationRelativeTo( null );
		setLayout(new BorderLayout());
		setTitle("CARGANDO DATOS");
		setVisible(true);
		
		pb = new JProgressBar(0, 40000);
        pb.setValue(0);
        add(pb);
	}
	
	public static void setProgressBar(int value) {
		int progreso = value; //Que linea del fichero esta procesando
		pb.setValue(progreso);
		
	}
	
}
