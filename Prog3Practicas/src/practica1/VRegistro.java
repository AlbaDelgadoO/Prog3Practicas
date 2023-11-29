package practica1;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import org.mindrot.jbcrypt.BCrypt;


public class VRegistro extends JFrame{
	
    protected static HashMap<String, Usuario> usuariosRegistrados = new HashMap<>();
	private JTextField txtNombre,txtCorreo;
	private static CustomPasswordField txtContrasenia, txtConfirmar;
	private JLabel lInst;
	
	public VRegistro() {

		   // Características de la ventana principal
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds(500, 150, 400, 400);
        this.setTitle("Registro");
        this.setLayout(new BorderLayout());

        // Creación de paneles y componentes
        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelNorte = new JPanel(new BorderLayout());
        JPanel panelCentro = new JPanel(new GridLayout(5, 1));
        add(panelSur, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);

        JPanel pTitulo = new JPanel();
        JLabel lTitulo = new JLabel("Rellene las Casillas:");
        Font fontTit = new Font("Futura", Font.BOLD, 14);
        lTitulo.setFont(fontTit);
        pTitulo.add(lTitulo);

        JPanel pBoton = new JPanel();
        JButton btnRegistro = new JButton("Registrarse");
        pBoton.add(btnRegistro);

        JPanel pNom = new JPanel();
        txtNombre = new JTextField();
        aplicarEstiloCampo(txtNombre, "Nombre");
        pNom.add(txtNombre);

        JPanel pCorreo = new JPanel();
        txtCorreo = new JTextField();
        aplicarEstiloCampo(txtCorreo, "Correo");
        pCorreo.add(txtCorreo);

        JPanel pContr = new JPanel();
        txtContrasenia = new CustomPasswordField();
        aplicarEstiloCampo(txtContrasenia, "Contraseña");
        txtContrasenia.setEchoChar((char) 0);
        pContr.add(txtContrasenia);

        JPanel pConf = new JPanel();
        txtConfirmar = new CustomPasswordField();
        aplicarEstiloCampo(txtConfirmar, "Confirmar contraseña");
        txtConfirmar.setEchoChar((char) 0);
        pConf.add(txtConfirmar);

        JPanel pInst = new JPanel(new BorderLayout());
        lInst = new JLabel();
        try (FileInputStream fis = new FileInputStream("src/practica1/info_icon.png")) {
            ImageIcon icon = new ImageIcon("src/practica1/info_icon.png");
            lInst.setIcon(ajustarIcon(icon));
        }catch(Exception e) {
            Ventana.log.log(Level.SEVERE, "Error al cargar el icono info: " + e.getMessage());        	
        }
        lInst.setToolTipText("<html>La contraseña debe contener al menos una mayúscula, una minúscula,<br> un número, un carácter especial y tener al menos 6 caracteres.</html>");
        pInst.add(lInst);
        pConf.add(pInst);

        // Añadimos los elementos previamente creados a los paneles
        panelNorte.add(pTitulo);
        panelCentro.add(pNom);
        panelCentro.add(pCorreo);
        panelCentro.add(pContr);
        panelCentro.add(pConf);
        panelSur.add(pBoton);

        // Funcionalidad de los botones
        btnRegistro.addActionListener((e) -> {
            Pattern patronContrasenia = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$");
            char[] contrasenia = txtContrasenia.getPassword();
            char[] confirmada = txtConfirmar.getPassword();
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            if (nombre.equals("Nombre") || correo.equals("Correo") || contrasenia.length == 0 || confirmada.length == 0) {
                JOptionPane.showMessageDialog(null, "Para registrarse, debe introducir datos en todas las casillas.");
                return;
            }
            char[] confirmarContrasenia = txtConfirmar.getPassword();
            if (!Arrays.equals(contrasenia, confirmarContrasenia)) {
                JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String cont = new String(contrasenia);
            Matcher matcher = patronContrasenia.matcher(cont);
            if (!matcher.matches()) {
                StringBuilder mensajeError = new StringBuilder("La contraseña no cumple con los requisitos:\n");
                if (!matcher.matches()) {
                    if (!contraseniaCumpleRequisito("[A-Z]", cont)) {
                        mensajeError.append("- Debe contener al menos una letra mayúscula.\n");
                    }
                    if (!contraseniaCumpleRequisito("[a-z]", cont)) {
                        mensajeError.append("- Debe contener al menos una letra minúscula.\n");
                    }
                    if (!contraseniaCumpleRequisito("\\d", cont)) {
                        mensajeError.append("- Debe contener al menos un dígito.\n");
                    }
                    if (!contraseniaCumpleRequisito("[@$!%*?&]", cont)) {
                        mensajeError.append("- Debe contener al menos un carácter especial (@$!%*?&).\n");
                    }
                    mensajeError.append("- Debe tener al menos 6 caracteres.\n");

                    JOptionPane.showMessageDialog(null, mensajeError.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
            	String hashContrasenia = BCrypt.hashpw(cont, BCrypt.gensalt());
                Usuario u = new Usuario(nombre, correo, hashContrasenia);
                if(!usuariosRegistrados.containsKey(correo)) {
                	registrarUsuario(u);
                    JOptionPane.showMessageDialog(null, "Te has registrado correctamente");
                    Ventana.log.log(Level.INFO, "Nuevo usuario registrado - Nombre: " + nombre + " Email: " + correo + "Contraseña: " + hashContrasenia);
                }else {
                    JOptionPane.showMessageDialog(null, "El correo ya está en uso. Por favor, elija otro.");
                }
                dispose();
                Ventana v = new Ventana();
                v.setVisible(true);
            }
        });
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

    private boolean contraseniaCumpleRequisito(String regex, String contrasenia) {
        return Pattern.compile(regex).matcher(contrasenia).find();
    }

    protected void registrarUsuario(Usuario u) {
    	String correo = u.getCorreo();
        usuariosRegistrados.put(correo, u);
        
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
            	Ventana.log.log(Level.SEVERE, "Error al cargar el icono ojo cerrado: " + e.getMessage());
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
        	            	Ventana.log.log(Level.SEVERE, "Error al cargar el icono ojo cerrado: " + e1.getMessage());
        	            }

                    } else {
                    	try (FileInputStream fis = new FileInputStream("src/practica1/eye_opened_icon.png")){
        		            ImageIcon imagenOjo = new ImageIcon("src/practica1/eye_opened_icon.png");
        					button.setIcon((ajustarIcon(imagenOjo)));
        	            }catch(IOException e1) {
        	                Ventana.log.log(Level.SEVERE, "Error al cargar el icono ojo abierto: " + e1.getMessage());
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
}


