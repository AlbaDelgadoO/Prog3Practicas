package practica1;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;

import org.mindrot.jbcrypt.BCrypt;


public class Ventana extends JFrame{
	
	static Logger log;
	
	protected JTextField txtCorreo;
	protected JPasswordField txtContrasenia;
	
	public Ventana() {
		
		//Características de la ventana principal
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setBounds(500, 150, 400, 200);
		this.setTitle("Inicio");
		
        // Creación de paneles y componentes
		JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel panelCentro = new JPanel(new GridLayout(3,2));
		add(panelSur, BorderLayout.SOUTH);
		add(panelCentro,BorderLayout.CENTER);

		JPanel pCorreo = new JPanel();
		txtCorreo = new JTextField(); 
		aplicarEstiloCampo(txtCorreo, "Correo");
		pCorreo.add(txtCorreo);
		
		JPanel pContr = new JPanel();
		txtContrasenia = new CustomPasswordField();
        aplicarEstiloCampo(txtContrasenia, "Contraseña");
        txtContrasenia.setEchoChar((char) 0);
        pContr.add(txtContrasenia);
		
		
		JButton bInicioSesion = new JButton("Iniciar Sesion");
		JLabel lCuenta = new JLabel("¿No tienes cuenta?");
		JButton bRegistro = new JButton("Registrarse");
		
		
		//Añadimos los elementos previamente creados a los paneles
		panelSur.add(lCuenta);
		panelSur.add(bRegistro);
		panelCentro.add(pCorreo);
		panelCentro.add(pContr);
		JPanel panel = new JPanel();
		panel.add(bInicioSesion);
		panelCentro.add(panel);
		
		
		//Funcionalidad de los botones
		bInicioSesion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				char[] contraseniaChar = txtContrasenia.getPassword();
		        String contrasenia = new String(contraseniaChar);
		        String correo = txtCorreo.getText();
				if(autenticarUsuario(correo, contrasenia)) {
					JOptionPane.showMessageDialog(null, "Bienvenido");
					log.log(Level.INFO, "Inicio de sesión exitoso - Email: " + correo);
	                dispose();
				}else {
	                JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos");
					log.log(Level.WARNING, "Alerta - Inicio de sesión fallido para Email: " + correo);
	            }
			}
		});
		
		bRegistro.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				VRegistro v = new VRegistro();
				v.setVisible(true);
			}
		});		
	}
	
	public boolean autenticarUsuario(String correo, String contrasenia) {
		 if (VRegistro.usuariosRegistrados.containsKey(correo)) {
	        Usuario usuario = VRegistro.usuariosRegistrados.get(correo);
	        String hashAlmacenado = usuario.getContrasena();

	        if (BCrypt.checkpw(contrasenia, hashAlmacenado)) {
	            return true; // La contraseña es correcta
	        } else {
	            return false; // La contraseña es incorrecta
	        }
		 } else {
			 return false;// El correo no está registrado, la autenticación falla
		 }
    }
	
	private void aplicarEstiloCampo(JTextComponent textField, String texto) {
        textField.setForeground(new Color(169, 169, 169));
        textField.setPreferredSize(new Dimension(350, 30));
        textField.setText(texto);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(texto)) {
                    textField.setText("");
                    if(textField instanceof JPasswordField) {
                    	 ((JPasswordField) textField).setEchoChar('\u2022');
                    }
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(texto);
                    if(textField instanceof JPasswordField) {
                    	((JPasswordField) textField).setEchoChar((char) 0); 
                    }
                    textField.setForeground(new Color(169, 169, 169));
                }
            }
        });
        textField.setBorder(new RoundBorder(new Color(51, 255, 233), 20));
    }
	 private static ImageIcon ajustarIcon(ImageIcon icon) {
	        int maxWidth = 20; // Tamaño máximo de ancho
	        int maxHeight = 20; // Tamaño máximo de alto
	        int newWidth, newHeight;
	        Image img = icon.getImage();
	        if (icon.getIconWidth() > icon.getIconHeight()) {
	            newWidth = maxWidth;
	            newHeight = (maxWidth * icon.getIconHeight()) / icon.getIconWidth();
	        } else {
	            newHeight = maxHeight;
	            newWidth = (maxHeight * icon.getIconWidth()) / icon.getIconHeight();
	        }
	        // Redimensiona la imagen
	        Image newImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	        icon = new ImageIcon(newImg);
	        return icon;
	    }
	    
	    private static class RoundBorder extends AbstractBorder {
	        private final Color borderColor;
	        private final int roundRadius;

	        public RoundBorder(Color borderColor, int roundRadius) {
	            this.borderColor = borderColor;
	            this.roundRadius = roundRadius;
	        }

	        @Override
	        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setColor(borderColor);
	            g2.drawRoundRect(x, y, width - 1, height - 1, roundRadius, roundRadius);
	            g2.dispose();
	        }
	    }
	    private static class CustomPasswordField extends JPasswordField {
	        private JButton button;

	        public CustomPasswordField() {
	            super();
	            JButton button = new JButton();
	            setLayout(new BorderLayout());
	            add(button, BorderLayout.EAST);
	            button.setPreferredSize(new Dimension(30, 10));
	            try (FileInputStream fis = new FileInputStream("src/practica1/eye_closed_icon.png")){
		            ImageIcon imagenOjo = new ImageIcon("src/practica1/eye_closed_icon.png");
					button.setIcon((ajustarIcon(imagenOjo)));
	            }catch(IOException e) {
	                log.log(Level.SEVERE, "Error al cargar el icono ojo cerrado: " + e.getMessage());
	            }
	            
	    		button.setBackground(Color.WHITE);
	    		
	            button.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    char echoChar = getEchoChar();
	                    if (echoChar == 0) {
	                        setEchoChar('\u2022'); // Ocultar contraseña (punto negro)
	                        try (FileInputStream fis = new FileInputStream("src/practica1/eye_closed_icon.png")){
	        		            ImageIcon imagenOjo = new ImageIcon("src/practica1/eye_closed_icon.png");
	        					button.setIcon((ajustarIcon(imagenOjo)));
	        	            }catch(IOException e1) {
	        	                log.log(Level.SEVERE, "Error al cargar el icono ojo cerrado: " + e1.getMessage());
	        	            }

	                    } else {
	                    	try (FileInputStream fis = new FileInputStream("src/practica1/eye_opened_icon.png")){
	        		            ImageIcon imagenOjo = new ImageIcon("src/practica1/eye_opened_icon.png");
	        					button.setIcon((ajustarIcon(imagenOjo)));
	        	            }catch(IOException e1) {
	        	                log.log(Level.SEVERE, "Error al cargar el icono ojo abierto: " + e1.getMessage());
	        	            }

	                        setEchoChar((char) 0); // Mostrar contraseña
	                    }
	                }
	            });
	        }

	        public JButton getButton() {
	            return button;
	        }
	    }
	
	public static void main(String[] args) {
		try {
			log = Logger.getLogger("logger");
		}catch(Exception e) {
			e.printStackTrace();
		}
		log.log(Level.INFO, "Inicio de aplicación" + (new Date()) );
		
		Ventana v = new Ventana();
		v.setVisible(true);
	}
}
