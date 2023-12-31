package practica3_4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BaseDeDatos {
	
	private static Connection connection;
	private static Statement statement;

	
	public static void inicializarBaseDeDatos() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:CartasBD.db");
			statement = connection.createStatement();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void crearTablas() {
		try {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Carta (id INTEGER, palo STRING, valor STRING)");
			insertarCartas();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS Filtro (codigo INTEGER PRIMARY KEY, descripcion STRING)");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS CartaMano (id_mano INTEGER, num_mano INTEGER, palo STRING, valor STRING, FOREIGN KEY (id_mano) REFERENCES ManoFiltro(id_mano))");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS ManoFiltro (codigo_filtro INTEGER, id_mano INTEGER PRIMARY KEY, FOREIGN KEY (codigo_filtro) REFERENCES Filtro(codigo))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private static void insertarCartas() {
		int id = 1;
		if(!cartaEnBD(id)) {
	        for (Carta.Palo palo : Carta.Palo.values()) {
	            for (Carta.Valor valor : Carta.Valor.values()) {
	                insertarCarta(id, palo.name(), valor.name());
		            id++;
	            }
	        }
		}
    }
	public static void insertarCarta(int id, String palo, String valor) {
        String insertQuery = "INSERT INTO Carta (id, palo, valor) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, id);
            insertStatement.setString(2, palo);
            insertStatement.setString(3, valor);
            insertStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static boolean existeFiltro(int cod) {
	    String query = "SELECT codigo FROM Filtro WHERE codigo = ?";
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, cod);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        return resultSet.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public static void nuevoFiltro(int cod, String desc) {
		if(existeFiltro(cod)) {
			String updateQuery = "UPDATE Filtro SET descripcion = ? WHERE codigo = ?";
		    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
		        updateStatement.setString(1, desc);
		        updateStatement.setInt(2, cod);
		        updateStatement.executeUpdate();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}else {
			String insertQuery = "INSERT INTO Filtro (codigo, descripcion) VALUES (?, ?)";
			try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
				insertStatement.setInt(1, cod);
	            insertStatement.setString(2, desc);
	            insertStatement.executeUpdate();
			}catch (Exception e) {
	            e.printStackTrace();
			}
		}
	}
	
	
	public static void aniadirCartasDeMano(int idMano, int numMano, ArrayList<Carta> mano, int codFiltro) {
		if(!existenCartasMano(idMano)){
			for(Carta carta: mano) {
				String insertQuery = "INSERT INTO CartaMano (id_mano, num_mano, palo, valor) VALUES (?, ?, ?, ?)";
				try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
					insertStatement.setInt(1, idMano);
		            insertStatement.setInt(2, numMano);
		            insertStatement.setString(3, carta.getPalo());
		            insertStatement.setString(4, carta.getValor());
		            insertStatement.executeUpdate();
				}catch (Exception e) {
		            e.printStackTrace();
				} 
			}
		}
	}


	public static void manoFiltro(int contFiltro, int idMano) {
		if(!existeMano(contFiltro, idMano)) {
			String insertQuery = "INSERT INTO ManoFiltro (codigo_filtro, id_mano) VALUES (?, ?)";
			try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){
	            insertStatement.setInt(1, contFiltro);
				insertStatement.setInt(2, idMano);
	            insertStatement.executeUpdate();
			}catch (Exception e) {
	            e.printStackTrace();
			} 
		}
	}
		
	
	private static boolean existeMano(int cod, int idMano) {
		String query = "SELECT id_mano FROM ManoFiltro WHERE codigo_filtro = ? AND id_mano = ?";
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, cod);
	        preparedStatement.setInt(2, idMano);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        return resultSet.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	private static boolean existenCartasMano(int idMano) {
		String query = "SELECT id_mano FROM CartaMano WHERE id_mano = ?";
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, idMano);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        return resultSet.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	private static boolean cartaEnBD(int id) {
		String query = "SELECT id FROM Carta WHERE id = ?";
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setInt(1, id);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        return resultSet.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public static void cerrarConexion() {
	    try {
	        if (statement != null) {
	            statement.close();
	        }
	        if (connection != null) {
	            connection.close();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}
