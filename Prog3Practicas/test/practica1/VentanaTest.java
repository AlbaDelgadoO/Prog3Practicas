package practica1;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

public class VentanaTest {

	@Test
    public void testAutenticacionExitosa() {
        Ventana ventana = new Ventana();
        Usuario usuario = new Usuario("NombrePrueba", "correo_prueba@example.com", BCrypt.hashpw("Prueba*123", BCrypt.gensalt()));
        VRegistro.usuariosRegistrados.put("correo_prueba@example.com", usuario);
        assertTrue(ventana.autenticarUsuario("correo_prueba@example.com", "Prueba*123"));
    }

    @Test
    public void testAutenticacionFallida() {
        Ventana ventana = new Ventana();
        assertFalse(ventana.autenticarUsuario("correo_inexistente@example.com", "contrasena_incorrecta"));
    }

}
