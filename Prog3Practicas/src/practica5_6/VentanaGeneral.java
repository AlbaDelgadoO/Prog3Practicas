package practica5_6;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import practica6.Municipio;
import practica6.VentanaDatos.MiJTree;

public class VentanaGeneral extends JFrame{
	
    private JTextArea taSalida;
    private JFileChooser chooser;
    
    private JTable tabla;
    private TableModel modeloTabla;
    
    private JTextField tfEtiqueta;
    private static TreeSet<UsuarioTwitter> treeSet;
    private JButton bTags;
    private static TreeMap<String, UsuarioTwitter> mapaNicks;
    
    private MiJTree treeAmigos;	
	private DefaultTreeModel modeloTree;
	private DefaultMutableTreeNode raiz;
	
	public VentanaGeneral() {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 1000, 600 );
		setLocationRelativeTo( null );
		setLayout(new BorderLayout());
		setTitle("Práctica 5/6");

		taSalida = new JTextArea();
		taSalida.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(taSalida);      
        this.add(scrollPane, BorderLayout.WEST);
      
        JPanel pCentro = new JPanel();
        pCentro.setLayout(new BorderLayout());
        JPanel pSup = new JPanel();
        pSup.setLayout(new BorderLayout());
        
        tfEtiqueta = new JTextField();
        tfEtiqueta.setSize(60, 10);
        pSup.add(tfEtiqueta, BorderLayout.CENTER);
        
        bTags = new JButton("Aplicar filtro");
        pSup.add(bTags, BorderLayout.EAST);        
        pCentro.add(pSup, BorderLayout.NORTH);
        
        treeAmigos = new MiJTree();
        pCentro.add(treeAmigos, BorderLayout.CENTER);
        treeAmigos.setVisible(false);
        treeAmigos.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
		        TreePath tp = e.getPath();
		        if(tp != null) {
					DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) treeAmigos.getLastSelectedPathComponent();
					if(nodoSel != null && nodoSel.isLeaf()){
						String nomUsuario = (String) nodoSel.getUserObject();
						UsuarioTwitter u = buscarUsuarioPorNombre(nomUsuario);
						if(u != null) {
							ArrayList<String> amigos = u.getAmigosEnSistema();
							for(String id: amigos) {
								UsuarioTwitter amigo = buscarUsuarioPorId(id);
								if(amigo != null) {
									String nomAmigo = amigo.getScreenName();
									crearNodo(nodoSel, nomAmigo);
								}
							}
						}
					}
		        }
			}
		});
        
        this.add(pCentro, BorderLayout.CENTER);
        
        tabla = new JTable();
        JScrollPane sp = new JScrollPane(tabla);
        this.add(sp, BorderLayout.EAST);
        
        crearTabla();
        
        abrirArchivo();
		GestionTwitter.crearTree();
        taSalida.setText(GestionTwitter.getTextoSalida());
        
		setVisible(true);

        bTags.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tfEtiqueta.getText() != null) {
					pintarFilas();
				}
			}
		});
	}

	
	
	
	private void abrirArchivo() {
	  try{
		  chooser = new JFileChooser();
		  FileFilter filtro = new FileNameExtensionFilter("Archivos csv (.csv)", "csv");
		  chooser.setFileFilter(filtro);
		  chooser.showOpenDialog(this);
		  File file = chooser.getSelectedFile();
		  if(file != null) {
			  VProgressBar v = new VProgressBar();
			  CSV.processCSV(file);
			  v.setVisible(false);
		  }else {
			  System.out.println("Selección de archivo cancelada");
		  }
	  }catch(Exception e){
			e.printStackTrace();
	    }
	}
	
	private void crearTabla() {
		treeSet = GestionTwitter.getTreeSet();
		mapaNicks = GestionTwitter.getMapaNicks();
		modeloTabla = new MiTableModel(treeSet);
		tabla.setModel(modeloTabla);
		tabla.repaint();	
		
		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UsuarioTwitter usuario = null;
				int filaSel = tabla.rowAtPoint(e.getPoint());
				if(filaSel >= 0) {
					String id = (String) tabla.getValueAt(filaSel, 0);
					usuario = buscarUsuarioPorId(id);
				}
				if(usuario != null) {
					treeAmigos.setVisible(true);
					crearTree(usuario);
				}
			}
		});
	}
	
	private void crearTree(UsuarioTwitter usuario) {
		raiz = new DefaultMutableTreeNode(usuario.getScreenName());
		modeloTree = new DefaultTreeModel(raiz);
		treeAmigos.setModel(modeloTree);
		treeAmigos.setEditable(false);
		ArrayList<String> amigos = usuario.getAmigosEnSistema(); 
		for(String id: amigos) {
			UsuarioTwitter amigo = buscarUsuarioPorId(id);
			if(amigo != null) {
				String nomAmigo = amigo.getScreenName();
				crearNodo(raiz, nomAmigo);
			}
		}
	}
	
	private void crearNodo(DefaultMutableTreeNode padre, String nombreNodo) {
	    DefaultMutableTreeNode nuevoNodo = new DefaultMutableTreeNode(nombreNodo);
	    modeloTree.insertNodeInto(nuevoNodo, padre, padre.getChildCount());
	    TreePath pathNuevoNodo = new TreePath(modeloTree.getPathToRoot(nuevoNodo));
	    treeAmigos.scrollPathToVisible(pathNuevoNodo);
	  
	}
	
	private void pintarFilas() {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
	        @Override
	        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	            if (column == 0) {
	            	String id = (String) modeloTabla.getValueAt(row, column);
	            	UsuarioTwitter usu = buscarUsuarioPorId(id);
	                if (usu.getTags().contains(tfEtiqueta.getText())) {
	                    c.setBackground(Color.GREEN); 
	                }else {
	                	c.setBackground(tabla.getBackground());
	                }
	            }
	            return c;
	        }
	    };
	    tabla.setDefaultRenderer(Object.class, renderer);
	    tabla.repaint();
	}
	
	
	private UsuarioTwitter buscarUsuarioPorId(String id) {
	    for (UsuarioTwitter usu : treeSet) {
	        if (usu.getId().equals(id)) {
	            return usu;
	        }
	    }
		return null;
	}
	
	private UsuarioTwitter buscarUsuarioPorNombre(String nombre) {
	    if(mapaNicks.containsKey(nombre)) {
	    	return mapaNicks.get(nombre);
	    }else {
		    return null;

	    }
	}

	
	
	public static class MiJTree extends JTree {
		public void expandir( TreePath path, boolean estado ) {
			setExpandedState( path, estado );
		}
	}
	
	
	private class MiTableModel implements TableModel{
		
		private final Class<?>[] CLASES_COLS = { String.class, String.class, Long.class, Long.class, String.class, Long.class };
		private TreeSet<UsuarioTwitter> tree;
		
		private MiTableModel(TreeSet<UsuarioTwitter> tree) {
			this.tree = tree;
		}
		
		@Override
		public int getRowCount() {
			return tree.size();
		}

		@Override
		public int getColumnCount() {
			return 6;
		}
		
		private static final String[] cabeceras = {"ID", "Name", "followers", "friends", "lang", "lastSeen"};
		@Override
		public String getColumnName(int columnIndex) {
			return cabeceras[columnIndex];
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return CLASES_COLS[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			int i = 0;
		    for (UsuarioTwitter u : tree) {
		        if (i == rowIndex) {
		            switch (columnIndex) {
		                case 0:
		                    return u.getId();
		                case 1:
		                    return u.getScreenName();
		                case 2:
		                    return u.getFollowersCount();
		                case 3:
		                    return u.getFriendsCount();
		                case 4:
		                    return u.getLang();
		                case 5:
		                    return u.getLastSeen();
		                default:
		                    return null;
		            }
		        }
		        i++;
		    }
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	public static void main(String[] args) {
		
		VentanaGeneral v = new VentanaGeneral();
		
		try {
			// TODO Configurar el path y ruta del fichero a cargar
			//String fileName = "src/practica5_6/prueba.csv";
			//CSV.processCSV( new File( fileName ) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
