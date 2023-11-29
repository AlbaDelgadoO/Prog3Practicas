package practica1;

public class Usuario {
    public String nombre;
	public String correo;
	public String contrasena;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public Usuario(String nombre, String correo, String contrasena) {
		super();
		this.nombre = nombre;
		this.correo = correo;
		this.contrasena = contrasena;
	}
	
}
