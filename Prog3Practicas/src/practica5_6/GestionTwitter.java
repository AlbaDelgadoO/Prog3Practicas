package practica5_6;

import java.io.*;
import java.util.*;

public class GestionTwitter {
	
	private static HashMap<String, UsuarioTwitter> mapaUsuariosID = new HashMap<String, UsuarioTwitter>();
	private static TreeMap<String, UsuarioTwitter> mapaUsuariosNick = new TreeMap<String, UsuarioTwitter>();
	private static TreeSet<UsuarioTwitter> treeUsuarios = new TreeSet<UsuarioTwitter>();
	
	static String usuariosEnMapa;
	static String usuariosEnTree;
	static String numUsuariosConAmigos;

	
	public static void agregarUsuarioID(String id, UsuarioTwitter usuario) {
		if(!mapaUsuariosID.containsKey(id)) {
			mapaUsuariosID.put(id, usuario);
		}
    }
	
	public static void agregarUsuarioNick(String nick, UsuarioTwitter usuario) {
		if(!mapaUsuariosNick.containsKey(nick)) {
			mapaUsuariosNick.put(nick, usuario);
		}
    }
	
	
	public static void crearTree() {
		int uConAmigosEnSistema = 0;
		//System.out.println("Usuarios con amigos en sistema: ");
		for(UsuarioTwitter usu: mapaUsuariosNick.values()) {
			int cont = 0;
			ArrayList<String> amigosEnSistema = new ArrayList<String>();
			if(usu.getFriends() != null) {	
				for(String id: usu.getFriends()) {
					if(mapaUsuariosID.containsKey(id)){
						cont++;
						amigosEnSistema.add(id);
					}
				}
				usu.setAmigosEnSistema(amigosEnSistema);
			}
			if(cont >= 10) {
				uConAmigosEnSistema++;
	            usu.setNAmigosEnSistema(cont);
				treeUsuarios.add(usu);
				usuariosEnMapa += "Usuario " + usu.getScreenName() + " tiene " + (usu.getFriendsCount() - usu.getNAmigosEnSistema()) + " amigos fuera de nuestro sistema y " + usu.getNAmigosEnSistema() + " dentro. \n";
			    //System.out.println(usuariosEnMapa);			
			}

		}
		
		numUsuariosConAmigos = "Hay " + uConAmigosEnSistema + " usuarios con algunos amigos dentro de nuestro sistema.";
		//System.out.println(numUsuariosConAmigos);
        //System.out.println("Usuarios más sociables");
		for (UsuarioTwitter u : treeUsuarios) {
            usuariosEnTree += u.getScreenName() + " - " + u.getNAmigosEnSistema() + " amigos \n";
			//System.out.println(usuariosEnTree);
			
        }
	}
	
	
	
	public static TreeSet<UsuarioTwitter> getTreeSet(){
		return treeUsuarios;
	}
	
	public static TreeMap<String, UsuarioTwitter> getMapaNicks(){
		return mapaUsuariosNick;
	}
	
	public static String getTextoSalida() {
		String salida = "";
		
		salida += "Usuarios con amigos en sistema: \n";
		salida += usuariosEnMapa + "\n";
		salida += numUsuariosConAmigos + "\n";
		salida += "Usuarios más sociables \n";
		salida += usuariosEnTree;
		return salida;
	}
	
	/***	
	public static void main(String[] args) {
			try {
				// TODO Configurar el path y ruta del fichero a cargar
				String fileName = "src/practica5_6/data.csv";
				CSV.processCSV( new File( fileName ) );
			} catch (Exception e) {
				e.printStackTrace();
			}
			crearTree();
		}
	***/
}
