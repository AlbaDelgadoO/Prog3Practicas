package practica6;

import java.awt.BorderLayout;

import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;


public class Practica6 {
	private static JFrame ventana = new JFrame( "Práctica 6.3" );
	private static VentanaDatos ventanaDatos;
	private static DataSetMunicipios dataset;
	
	public static void main(String[] args) {
		ventana.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		ventana.setLocationRelativeTo( null );
		ventana.setSize( 200, 80 );

		JButton bCargaMunicipios = new JButton( "Carga municipios > 100k" );
		ventana.add( bCargaMunicipios );
		
		bCargaMunicipios.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cargaMunicipios();
			}
		});
		
		ventana.setVisible( true );
	}
	
	private static void cargaMunicipios() {
		try {
			dataset = new DataSetMunicipios( "municipios100k.txt" );
			System.out.println( "Cargados municipios:" );
			for (Municipio m : dataset.getListaMunicipios() ) {
				System.out.println( "\t" + m );
			}
			// TODO Resolver el ejercicio 6.3
			ventanaDatos = new VentanaDatos(ventana);
			ventanaDatos.setDatosTree(dataset);
			ventanaDatos.setVisible( true );
		
		} catch (IOException e) {
			System.err.println( "Error en carga de municipios" );
		}
	}
}


