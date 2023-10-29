package practica6;

import java.awt.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.plugins.tiff.GeoTIFFTagSet;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import java.util.List;

public class VentanaDatos extends JFrame{
	private DataSetMunicipios datosMunis;   
	
	private JTable tablaDatos;
	private TableModel modeloDatosTabla;
	
	private MiJTree treeDatos;	
	private DefaultTreeModel modeloTree;
	private DefaultMutableTreeNode raiz;
	
	private JPanel pDer;
	
	private int criterioOrden;  //0 si esta en orden alfabético y 1 si esta ordenado por habitantes
	
	public VentanaDatos(JFrame ventOrigin) {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 1000, 400 );
		setLocationRelativeTo( null );
		setLayout(new BorderLayout());

		JLabel lSup = new JLabel("");
		this.add(lSup, BorderLayout.NORTH);
		treeDatos = new MiJTree();
		this.add(new JScrollPane(treeDatos), BorderLayout.WEST);
		tablaDatos = new JTable();
		this.add(new JScrollPane(tablaDatos),BorderLayout.CENTER);
		pDer = new JPanel() {
			@Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            dibujarGrafico((Graphics2D) g);
	            {
	                setOpaque(true);
	                setPreferredSize(new Dimension(300, 600));
	            }
	        }
		};
		this.add(pDer, BorderLayout.EAST);
		JPanel pBotones = new JPanel();
		JButton bInsercion = new JButton("Inserción");
		JButton bBorrar = new JButton("Borrar");
		JButton bOrden = new JButton("Orden");
		pBotones.add(bInsercion);
		pBotones.add(bBorrar);
		pBotones.add(bOrden);
		this.add(pBotones, BorderLayout.SOUTH);
	
		
		bInsercion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) treeDatos.getSelectionPath().getLastPathComponent();
	            if (nodoSel != null && nodoSel.isLeaf()) {
	            	String provinciaSel = (String) nodoSel.getUserObject();
	            	String autonomiaSel = (String) nodoSel.getParent().toString();	
					Municipio nuevo = new Municipio(datosMunis.getListaMunicipios().size()+1, " ", 50000, provinciaSel, autonomiaSel);
					datosMunis.anyadir(nuevo);
		            ((MiTableModel) modeloDatosTabla).anyadirFila(nuevo); 
					tablaDatos.repaint();
	            }
				
			}
		});
		
		bBorrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int filaSel = tablaDatos.getSelectedRow();
				if(filaSel >= 0) {
					String provinciaSel = (String) tablaDatos.getValueAt(filaSel, 4);
					int opcion = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres borrar este municipio?", "Confirmación de Borrado", JOptionPane.YES_NO_OPTION);
		            if (opcion == JOptionPane.YES_OPTION) {
		                ((MiTableModel) modeloDatosTabla).borrarFila(filaSel);
		                ((MiTableModel) modeloDatosTabla).setListaMunicipios(datosMunis.getMunicipiosEnProvincia(provinciaSel));
		                tablaDatos.setModel(modeloDatosTabla);
		                tablaDatos.repaint();
		            }
		        } else {
		            JOptionPane.showMessageDialog(null, "Selecciona un municipio para borrar.", "Error", JOptionPane.ERROR_MESSAGE);
		        }
			}
		});
		
		bOrden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String provincia = treeDatos.getSelectionPath().getLastPathComponent().toString();
				//System.out.println(provincia);
				List<Municipio> municipiosEnProvincia = datosMunis.getMunicipiosEnProvincia(provincia);

				if(criterioOrden == 1) {
					municipiosEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
					criterioOrden = 0;

				}else {
					municipiosEnProvincia.sort(Comparator.comparingInt(Municipio::getHabitantes).reversed());
					criterioOrden = 1;
				}	
				((MiTableModel) tablaDatos.getModel()).setListaMunicipios(municipiosEnProvincia);
				tablaDatos.repaint();

			}
		});
	}
	
	//PANEL
	
	private void dibujarGrafico(Graphics2D grafico) {
        int anchoPanelDer = pDer.getWidth();
        int altoPanelDer = pDer.getHeight();

        if(treeDatos.getSelectionPath() != null) {
            DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) treeDatos.getSelectionPath().getLastPathComponent();
            String provinciaSel = "";
            if (nodoSel != null && nodoSel.isLeaf()) {
            	provinciaSel = (String) nodoSel.getUserObject();
            }
	        //System.out.println("provincia seleccionada" + provinciaSel);
	        List<Municipio> municipiosEnProvincia = datosMunis.getMunicipiosEnProvincia(provinciaSel);
	        //System.out.println("municipios de esta:" + municipiosEnProvincia);
	        int poblacionProvincia = 0;
	        int poblacionTotal = 0;
	
	        for (Municipio municipio : municipiosEnProvincia) {
	            poblacionProvincia += municipio.getHabitantes();
	        }
	
	        for (Municipio municipio : datosMunis.getListaMunicipios()) {
	            poblacionTotal += municipio.getHabitantes();
	        }
	
	        int anchoBarraProvincia = (anchoPanelDer / 2) - 20;
	        int alturaMax = altoPanelDer - 40;
	        double porcentajeProvincia = (double) poblacionProvincia / poblacionTotal;
	        int alturaBarraProvincia = (int) (porcentajeProvincia * alturaMax);
	        int xBarraProvincia = 10;
	        int yBarraProvincia = altoPanelDer - alturaBarraProvincia;
	
	        grafico.setColor(Color.GREEN);
	        grafico.fillRect(xBarraProvincia, yBarraProvincia, anchoBarraProvincia, alturaBarraProvincia);
	
	        grafico.setColor(Color.BLACK);
	        int ySeparador = yBarraProvincia;
	        for (Municipio municipio : municipiosEnProvincia) {
	            int alturaSeparador = (int) ((double) municipio.getHabitantes() / poblacionProvincia * alturaBarraProvincia);
	            grafico.drawLine(xBarraProvincia, ySeparador, xBarraProvincia + anchoBarraProvincia, ySeparador);
	            grafico.drawLine(xBarraProvincia, ySeparador + alturaSeparador, xBarraProvincia + anchoBarraProvincia, ySeparador + alturaSeparador);
	            ySeparador += alturaSeparador;
	        }
	
	        int anchoBarraEstado = (anchoPanelDer / 2) - 20;
	        int xBarraEstado = xBarraProvincia + anchoBarraProvincia + 10;
	        int alturaBarraEstado = alturaMax;
	        int yBarraEstado = altoPanelDer - alturaBarraEstado;
	
	        grafico.setColor(Color.BLUE);
	        grafico.fillRect(xBarraEstado, yBarraEstado, anchoBarraEstado, alturaBarraEstado);
	
	        grafico.setColor(Color.BLACK);    
        }
    }
	
	//TREE
	
	public void setDatosTree(DataSetMunicipios datosMunis) {
		this.datosMunis = datosMunis;

		raiz = new DefaultMutableTreeNode("Municipios");
		modeloTree = new DefaultTreeModel(raiz);
		treeDatos.setModel(modeloTree);
		treeDatos.setEditable(false);
		crearNodos(datosMunis.getListaMunicipios(), raiz);
		rendererProvincia(treeDatos, datosMunis);
		
		treeDatos.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				if(tp != null) {
					DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) tp.getLastPathComponent();
					//System.out.println("Nodo seleccionado: " + nodoSel.getUserObject().getClass().getName() + " " + nodoSel.getUserObject());
					if(nodoSel != null && nodoSel.isLeaf()) {
						String provinciaSel = (String) nodoSel.getUserObject();
						CargarDatosTabla(provinciaSel);
						rendererProvincia(treeDatos, datosMunis);
						pDer.repaint();
					}
				}
			}
		});
	}
	
	private void rendererProvincia(JTree treeDatos, DataSetMunicipios datosMunis) {
	    treeDatos.setCellRenderer(new DefaultTreeCellRenderer() {
	        @Override
	        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	                                                      boolean leaf, int row, boolean hasFocus) {
	            Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

	            if (value instanceof DefaultMutableTreeNode) {
	                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	                if (datosMunis.getListaProvincias().contains(node.getUserObject())) {
	            
	                    String provincia = (String) node.getUserObject();
	                    
	                    int habitantes = 0;
	                    for(int i=0; i<datosMunis.getMunicipiosEnProvincia(provincia).size(); i++) {
	                    	habitantes += datosMunis.getMunicipiosEnProvincia(provincia).get(i).getHabitantes();
	                    }
	                    JProgressBar progressBar = new JProgressBar();
	                    progressBar.setMaximum(5000000);
	                    progressBar.setValue(habitantes);

	                    JPanel panel = new JPanel(new BorderLayout());
	                    panel.add(new JLabel(provincia), BorderLayout.WEST);
	                    panel.add(progressBar, BorderLayout.EAST);

	                    return panel;
	                }
	            }
	            return c;
	        }
	    });
	}
	
	private DefaultMutableTreeNode crearNodo( Object o, DefaultMutableTreeNode nodoPadre, int pos ) {
		DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(o);
		modeloTree.insertNodeInto(nodo, nodoPadre, pos);
		treeDatos.expandir(new TreePath(nodo.getPath()), true);
		return nodo;
	}
	
	private void crearNodos(List<Municipio> municipios, DefaultMutableTreeNode nodoPadre) {
	    ArrayList<String> autonomias = new ArrayList<String>();
	    for(Municipio m: municipios) {
	    	String autonomia = m.getAutonomia();
	    	if(!autonomias.contains(autonomia)) {
	    		DefaultMutableTreeNode nodoAutonomia = crearNodo(autonomia, nodoPadre, autonomias.size());
	    		autonomias.add(autonomia);
	    		
	    		ArrayList<String> provincias = new ArrayList<String>();
	    		for(Municipio mun: municipios) {
	    			if (mun.getAutonomia().equals(autonomia)) {
	                    String provincia = mun.getProvincia();
	                    if (!provincias.contains(provincia)) {
	                        crearNodo(provincia, nodoAutonomia, provincias.size());
	                        provincias.add(provincia);
	                    }
	                }
	    		}
	    	}
	    }
	}	
	
	public static class MiJTree extends JTree {
		public void expandir( TreePath path, boolean estado ) {
			setExpandedState( path, estado );
		}
	}
	
	
	
	//TABLA
	
	private void CargarDatosTabla(String provinciaSel) {
		List<Municipio> municipiosEnProvincia = datosMunis.getMunicipiosEnProvincia(provinciaSel);
		municipiosEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
		modeloDatosTabla = new MiTableModel(municipiosEnProvincia);
		tablaDatos.setModel(modeloDatosTabla);

		tablaDatos.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			private JProgressBar pbPoblacion = new JProgressBar(50000, 5000000);
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if(column == 3) {
					int poblacion = (Integer) value;
					double porcentaje = (double) (poblacion - 50000) / (5000000 - 50000);

					int red = (int) (255 * porcentaje);
					int green = (int) (255 * (1 - porcentaje));

					pbPoblacion.setValue(poblacion);
					pbPoblacion.setForeground(new Color(red, green, 0));
					
					return pbPoblacion;
				}else {
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			}
		});
		
		tablaDatos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean municipioIsSel;
				int filaEnTabla = tablaDatos.rowAtPoint(e.getPoint());
				int colEnTabla = tablaDatos.columnAtPoint(e.getPoint());
				int codSel = (int) tablaDatos.getValueAt(filaEnTabla, 0);
				if(colEnTabla == 1 && filaEnTabla >= 0) {
					Municipio municipioSel = null;
					for(int i=0; i<datosMunis.getListaMunicipios().size(); i++) {
						if(datosMunis.getListaMunicipios().get(i).getCodigo() == codSel) {
							municipioSel = datosMunis.getListaMunicipios().get(i);
						}
					}
					municipioIsSel = true;
					colorearCelda(municipioSel, municipioIsSel, municipiosEnProvincia);
				}else {
					municipioIsSel = false;
					colorearCelda(null, municipioIsSel, null);
				}
				
			}
		});
	}
	

	private void colorearCelda(Municipio municipioSel, boolean municipioIsSel, List<Municipio> municipiosEnProvincia) {
	    tablaDatos.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
	        @Override
	        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	            
	            if (municipioIsSel && column == 1) {
	            	if(criterioOrden == 0) {
						municipiosEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
		            	Municipio municipioActual = municipiosEnProvincia.get(row);
		                if (municipioActual.getHabitantes() > municipioSel.getHabitantes()) {
		                    c.setBackground(Color.RED);
		                } else if (municipioActual.getHabitantes() < municipioSel.getHabitantes()) {
		                    c.setBackground(Color.GREEN);
		                } else {
		                    c.setBackground(Color.WHITE);
		                }
	            	}else {
	            		municipiosEnProvincia.sort(Comparator.comparingInt(Municipio::getHabitantes).reversed());
	            		Municipio municipioActual = municipiosEnProvincia.get(row);
		                if (municipioActual.getHabitantes() > municipioSel.getHabitantes()) {
		                    c.setBackground(Color.RED);
		                } else if (municipioActual.getHabitantes() < municipioSel.getHabitantes()) {
		                    c.setBackground(Color.GREEN);
		                } else {
		                    c.setBackground(Color.WHITE);
		                }
	            	}
                
	            } else {
	                c.setBackground(table.getBackground());
	            }
	            return c;
	        }
	    });
	    tablaDatos.repaint();
	}


	private class MiTableModel implements TableModel {

		private final Class<?>[] CLASES_COLS = { Integer.class, String.class, Integer.class, Integer.class, String.class, String.class };
		private List<Municipio> listaMunicipios;
		
		private MiTableModel(List<Municipio> municipios) {
			this.listaMunicipios = municipios;
		}
		
		public void setListaMunicipios(List<Municipio> municipiosEnProvincia) {
			this.listaMunicipios = municipiosEnProvincia;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return CLASES_COLS[columnIndex];
		}
		
		@Override
		public int getColumnCount() {
			return 6; 
		}
		
		@Override
		public int getRowCount() {
			return listaMunicipios.size();
		}
		
		private static final String[] cabeceras = {"Código", "Nombre", "Habitantes", "Población", "Provincia", "Autonomia"};
		@Override
		public String getColumnName(int columnIndex) {
			return cabeceras[columnIndex];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Municipio m = listaMunicipios.get(rowIndex);
			switch(columnIndex) {
			case 0:
				return m.getCodigo();
			case 1:
				return m.getNombre();
			case 2:
				return m.getHabitantes();
			case 3:
				return m.getHabitantes();
			case 4:
				return m.getProvincia();
			case 5:
				return m.getAutonomia();
			default:
				return null;
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 1 || columnIndex == 2) {
				return true;
			}
			return false;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
			Municipio m = listaMunicipios.get(rowIndex);
			switch(columnIndex) {
			case 0:
				m.setCodigo((Integer) aValue);
				break;
			case 1:
				m.setNombre((String) aValue);
				break;
			case 2:
				try {
					m.setHabitantes((Integer) aValue); 
					tablaDatos.repaint();
				}catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Numero de habitantes erróneo");
				}
				break;
			case 3:
				try {
					m.setHabitantes((Integer) aValue);
				}catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Numero de habitantes erróneo");
				}
				break;
			case 4:
				m.setProvincia((String) aValue);
				break;
			case 5:
				m.setAutonomia((String) aValue);
				break;
			}
		}
		

		ArrayList<TableModelListener> listaEsc = new ArrayList<>();
		@Override
		public void addTableModelListener(TableModelListener l) {
			listaEsc.add(l);
		}
		
		@Override
		public void removeTableModelListener(TableModelListener l) {
			listaEsc.remove(l);
		}
		
		//DefaultTableModel lo hace asi
		public void fireTableChanged(TableModelEvent e) {
			for(TableModelListener l: listaEsc) {
				l.tableChanged(e);
			}
		}
		
		public void borrarFila(int fila) {
			if (fila >= 0 && fila < listaMunicipios.size()) {
		        Municipio municipioBorrado = listaMunicipios.remove(fila);
		        datosMunis.quitar(municipioBorrado.getCodigo());
		        fireTableChanged(new TableModelEvent(modeloDatosTabla, fila, datosMunis.getListaMunicipios().size()));
		        tablaDatos.repaint();
			}
		}
		
		public void anyadirFila(Municipio municipio) {
	        listaMunicipios.add(municipio);
	        fireTableChanged(new TableModelEvent(this, listaMunicipios.size() - 1, listaMunicipios.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	        
		}
	}
}


