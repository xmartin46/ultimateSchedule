package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import domain.controllers.CtrlDomain;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.AbstractListModel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

public class MainView extends JFrame {

	private CtrlPresentation ctrlPresentation;
	
	private JPanel contentPanel;
	private JTable table;
	
	final private JFileChooser fc = new JFileChooser();
	
	public MainView(CtrlPresentation ctrlPresentation) {
		this.ctrlPresentation = ctrlPresentation;
		
		//TODO: Remove
		CtrlDomain ctrlDomain = CtrlDomain.getInstance();
		try {
			ctrlDomain.importEnvironment("Q1+Q2.json");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//
		
	    setTitle("Ultimate Schedule");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 600);
		setLocationRelativeTo(null);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPanel);
		
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btn_genSchedule = new JButton("Generate Schedule");
		panel.add(btn_genSchedule);
		
		btn_genSchedule.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainView.this, "wtf");
			}
		});
		
		JButton btnLoadSchedule = new JButton("Load Schedule");
		panel.add(btnLoadSchedule);
		
		btnLoadSchedule.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File path = new File("data/schedules");
				File selected = loadLocalFile(path);
				if (!selected.equals(path)) {	//user selected a file
					System.out.println(selected.getName());
					try {
						ctrlPresentation.importSchedule(selected.getName());
						redrawScheduleMatrix();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton btnLoadEnv = new JButton("Load Environment");
		panel.add(btnLoadEnv);
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPanel.add(tabbedPane, BorderLayout.WEST);
		tabbedPane.setPreferredSize(new Dimension(300, 0));
		
		JTree treeSubjects = new JTree();
		JScrollPane scollPnlSubjects = new JScrollPane(treeSubjects, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.addTab("Subjects", null, scollPnlSubjects, null);
		
		JTree treeRooms = new JTree();
		JScrollPane scollPnlRooms = new JScrollPane(treeRooms, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.addTab("Rooms", null, scollPnlRooms, null);
		
		
		
		DefaultTreeModel model =(DefaultTreeModel) treeRooms.getModel();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		model.setRoot(root);
		for(String room : ctrlPresentation.getAllRooms()) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(room);
			root.add(child);
		}
		treeRooms.expandPath(new TreePath(root.getPath()));
		treeRooms.setRootVisible(false);
		
		
		//table = new JTable();
		initializeTable();
		JScrollPane scrollPaneTable = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		table.setRowHeight(40);
		table.setRowSelectionAllowed(false);
		
		ListModel lm = new AbstractListModel() {
		      String headers[] = {"08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00"};

		      @Override
			public int getSize() {
		        return headers.length;
		      }

		      @Override
			public Object getElementAt(int index) {
		        return headers[index];
		      }
		    };
		JList rowHeader = new JList(lm);
	    rowHeader.setFixedCellWidth(100);

	    rowHeader.setFixedCellHeight(table.getRowHeight());
	    rowHeader.setCellRenderer(new RowHeaderRenderer(table));
	    
		scrollPaneTable.setRowHeaderView(rowHeader);
		contentPanel.add(scrollPaneTable, BorderLayout.CENTER);
		
		btnLoadEnv.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainView.this, "wtf duuuude", "really?", JOptionPane.WARNING_MESSAGE);
			}
		});
	}
	
	private void initializeTable() {
		table = new JTable(new ScheduleTableModel(ctrlPresentation.getScheduleMatrix()));
		JTableHeader header = table.getTableHeader();
		header.setBorder(new LineBorder(Color.decode("#006699"),2));
	}
	
	private class RowHeaderRenderer extends JLabel implements ListCellRenderer {

		  RowHeaderRenderer(JTable table) {
		    JTableHeader header = table.getTableHeader();
		    setOpaque(true);
		    setBorder(header.getBorder());
		    setHorizontalAlignment(CENTER);
		    setForeground(header.getForeground());
		    setBackground(header.getBackground());
		    setFont(header.getFont());
		  }

		  @Override
		public Component getListCellRendererComponent(JList list, Object value,
		      int index, boolean isSelected, boolean cellHasFocus) {
		    setText((value == null) ? "" : value.toString());
		    return this;
		  }
	}
	
	private class ScheduleTableModel extends AbstractTableModel {
		String[] columnNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
		Object[][] data;
		public ScheduleTableModel(Object[][] data) {
			this.data = data;
		}

	    @Override
		public int getColumnCount() {
	        return columnNames.length;
	    }

	    @Override
		public int getRowCount() {
	        return data.length;
	    }

	    @Override
		public String getColumnName(int col) {
	        return columnNames[col];
	    }

	    @Override
		public Object getValueAt(int row, int col) {
	        return data[row][col];
	    }

	    @Override
		public Class getColumnClass(int c) {
	        //return getValueAt(0, c).getClass();
	    	return String.class;
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * editable.
	     */
	    @Override
		public boolean isCellEditable(int row, int col) {
	        return false;
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * data can change.
	     */
	    @Override
		public void setValueAt(Object value, int row, int col) {
	        data[row][col] = value;
	        fireTableCellUpdated(row, col);
	    }
	    
	    public void changeData(Object[][] data) {
	        this.data = data;
	        fireTableDataChanged();
	    }
	}
	
	private File loadLocalFile(File file) {
		fc.setCurrentDirectory(file);
		int returnVal = fc.showOpenDialog(contentPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
		return file;
	}
	
	private void redrawScheduleMatrix() {
		String data[][] = ctrlPresentation.getScheduleMatrix();
		((ScheduleTableModel) table.getModel()).changeData(data);
	}

}