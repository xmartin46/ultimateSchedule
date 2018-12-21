package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.json.simple.parser.ParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import presentation.ScheduleTable.LectureClickedEventListener;

public class MainView extends JFrame{

	private CtrlPresentation ctrlPresentation;
	
	private JPanel contentPanel;
	final private JFileChooser fc = new JFileChooser();
	private JCheckBoxTree treeGroups = new JCheckBoxTree();
	private JCheckBoxTree treeRooms = new JCheckBoxTree();
	private JButton btnLoadEnvironment, btnLoadSchedule, btnGenSchedule, btnSaveSchedule, btnConfigRestrictions;
	private JLabel envText;
	private ScheduleTable table;
	private boolean environmentLoaded, scheduleLoaded;
	private String environmentName;
	
	public MainView(CtrlPresentation ctrlPresentation){
		this.ctrlPresentation = ctrlPresentation;
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileNameExtensionFilter("JSON Files", "json", "JSON"));
		
		environmentLoaded = false;
		scheduleLoaded = false;

	    setTitle("Ultimate Schedule");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 600);
		setLocationRelativeTo(null);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.setOpaque(true);
		//contentPanel.setBackground(Color.decode("#616161"));
		setContentPane(contentPanel);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		topPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		topPanel.setOpaque(true);
		contentPanel.add(topPanel, BorderLayout.NORTH);
		
		JPanel topLeftPanel = new JPanel();
		topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.X_AXIS));
		topLeftPanel.setBorder((BorderFactory.createCompoundBorder(new LineBorder(Color.decode("#eeeeee"), 5), new EmptyBorder(5, 10, 5, 5))));
		topLeftPanel.setPreferredSize(new Dimension(410-5+8, 0));
		topLeftPanel.setOpaque(true);
		topLeftPanel.setBackground(Color.decode("#dddddd"));
		topPanel.add(topLeftPanel, BorderLayout.WEST);
		
		btnLoadEnvironment = new JButton("Load Environment");
		btnLoadEnvironment.setFocusPainted(false);
		topLeftPanel.add(btnLoadEnvironment);
		
		envText = new JLabel("No Environment Loaded");
		envText.setFont(envText.getFont().deriveFont(Font.BOLD, 12f));
		envText.setBorder(new EmptyBorder(0, 10, 0, 0));
		envText.setBackground(Color.red);
		envText.setPreferredSize(new Dimension(410-5+8, 0));
		topLeftPanel.add(envText);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.decode("#dddddd"));
		buttonsPanel.setBorder((BorderFactory.createCompoundBorder(new LineBorder(Color.decode("#eeeeee"), 5), new EmptyBorder(5, 5, 5, 5))));
		
		topPanel.add(buttonsPanel, BorderLayout.CENTER);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnLoadEnvironment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File path = new File("data/environments");
				fc.setDialogTitle("Load Environment");
				File selected = loadLocalFile(path);
				if (!selected.equals(path)) {	//user selected a file
					try {
						ctrlPresentation.importEnvironment(selected.getAbsolutePath());
						environmentName = selected.getName();
						environmentLoaded();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		btnLoadSchedule = new JButton("Load Schedule");
		buttonsPanel.add(btnLoadSchedule);
		btnLoadSchedule.setEnabled(environmentLoaded);
		btnLoadSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File path = new File("data/schedules");
				fc.setDialogTitle("Load Schedule");
				File selected = loadLocalFile(path);
				if (!selected.equals(path)) {	//user selected a file
					try {
						ctrlPresentation.importSchedule(selected.getAbsolutePath());
						reloadSchedule();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		btnGenSchedule = new JButton("Generate Schedule");
		buttonsPanel.add(btnGenSchedule);
		btnGenSchedule.setEnabled(environmentLoaded);
		btnGenSchedule.addActionListener(new ActionListener() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				ctrlPresentation.generateSchedule();
			}
		});
		
		btnSaveSchedule = new JButton("Save Schedule");
		buttonsPanel.add(btnSaveSchedule);
		btnSaveSchedule.setEnabled(environmentLoaded);
		btnSaveSchedule.addActionListener(new ActionListener() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				//JLabel tfFilename = new JLabel("Filename");
				//JTextField tfFilename = new JTextField();
				//JOptionPane.showInputDialog(parentComponent, message, title, messageType)
				//String m = (String) JOptionPane.showInputDialog(MainView.this, "Enter filename:", "Save Schedule", JOptionPane.PLAIN_MESSAGE, null, null, "schedule.json");
				//JOptionPane.showMessageDialog(MainView.this, "Mehhh.. Doesn't work yet", null, JOptionPane.WARNING_MESSAGE);
				try {
					saveLocalFile();
				} catch (IOException | NullPointerException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnConfigRestrictions = new JButton("Config. Restrictions");
		buttonsPanel.add(btnConfigRestrictions);
		btnConfigRestrictions.setEnabled(environmentLoaded);
		btnConfigRestrictions.addActionListener(new ActionListener() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				ctrlPresentation.switchToRestrictionsView();
				/**SDHRestrictionView rView = new SDHRestrictionView("", 0, 0);
				rView.makeVisible();**/
			}
		});
		
		table = new ScheduleTable(ctrlPresentation.getScheduleMatrix());
		contentPanel.add(table, BorderLayout.CENTER);
		table.setBorder(new EmptyBorder(20, 8, 0, 0));
		table.addLectureClickedEventListener(new LectureClickedEventListener() {
			
			@Override
			public void lectureClicked(MouseEvent e, String[] value, int duration, int row, int col) {
				if(SwingUtilities.isRightMouseButton(e)) {
					final JPopupMenu popupMenu = new JPopupMenu();
			        JMenuItem removeLec = new JMenuItem("Remove Lecture");
			        JMenuItem moveLec = new JMenuItem("Move Lecture");
			        JMenuItem groupInfo = new JMenuItem("Group Info");
			        JMenuItem roomInfo = new JMenuItem("Room Info");
			        moveLec.addActionListener(new ActionListener() {
			            @Override
			            public void actionPerformed(ActionEvent e) {
			            	MoveLectureView mlView = new MoveLectureView(value[0], duration, col, row, value[1]);
			            	mlView.makeVisible();
			            }
			        });
			        
			        removeLec.addActionListener(new ActionListener() {
			            @Override
			            public void actionPerformed(ActionEvent e) {
			            	ctrlPresentation.removeLecture(duration, value[1], col, row);
			            	reloadSchedule();
			            }
			        });
			        
			        groupInfo.addActionListener(new ActionListener() {
			            @Override
			            public void actionPerformed(ActionEvent e) {
	   		            	ctrlPresentation.switchToGroupInfoView(value[0]);
			            }
			        });
			        
			        roomInfo.addActionListener(new ActionListener() {
			            @Override
			            public void actionPerformed(ActionEvent e) {
	   		            	ctrlPresentation.switchToRoomInfoView(value[1]);
			            }
			        });
			        
			        popupMenu.add(moveLec);
			        popupMenu.add(removeLec);
			        popupMenu.addSeparator();
			        popupMenu.add(groupInfo);
			        popupMenu.add(roomInfo);
                	popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
				
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPanel.add(tabbedPane, BorderLayout.WEST);
		tabbedPane.setPreferredSize(new Dimension(300, 0));
		
		JScrollPane scollPnlGroups = new JScrollPane(treeGroups, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.addTab("Groups", null, scollPnlGroups, null);
		treeGroups.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
            	table.stopEditing();
            	DefaultMutableTreeNode  tn = ((DefaultMutableTreeNode ) event.getPath().getLastPathComponent());
            	String groupName = (String) tn.getUserObject();
            	if(event.getChecked()) {
            		table.filterGroupIn(groupName);
            	}else{
            		table.filterGroupOut(groupName);
            	}
            }           
        });
		treeGroups.setModel(new DefaultTreeModel(null));
		treeGroups.addMouseListener(new MouseAdapter() {
		     public void mousePressed(MouseEvent e) {
		    	 if(SwingUtilities.isRightMouseButton(e)) {
                	int selRow = treeGroups.getRowForLocation(e.getX(), e.getY());
	   		        TreePath selPath = treeGroups.getPathForLocation(e.getX(), e.getY());
	   		        treeGroups.clearSelection();
	   		        treeGroups.setSelectionPath(selPath);
	   		        if(selRow != -1)  {
	   		        	final JPopupMenu popupMenu = new JPopupMenu();
	   		        	if(((DefaultMutableTreeNode) selPath.getLastPathComponent()).isRoot()){
	   		        		popupMenu.add("Add New Subject").addActionListener(new ActionListener() {
			   		            @Override
			   		            public void actionPerformed(ActionEvent e) {
			   		            	ctrlPresentation.switchToNewSubjectView();
			   		            }
			   		        });
						}else {
		   		        	String code = (String) ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
		   		        	JMenuItem info = new JMenuItem("View More Info");
		   		        	popupMenu.add(info);
		   		        	popupMenu.addSeparator();
		   		        	if(((DefaultMutableTreeNode) selPath.getLastPathComponent()).isLeaf()){ //is group
		   		        		info.addActionListener(new ActionListener() {
				   		            @Override
				   		            public void actionPerformed(ActionEvent e) {
				   		            	ctrlPresentation.switchToGroupInfoView(code);
				   		            }
				   		        });
		   		        		JMenuItem removeGroup = new JMenuItem("Remove Group");
		   		        		popupMenu.add(removeGroup);
		   		        	}else{ //is subject
		   		        		info.addActionListener(new ActionListener() {
				   		            @Override
				   		            public void actionPerformed(ActionEvent e) {
				   		            	ctrlPresentation.switchToSubjectInfoView(code);
				   		            }
				   		        });
		   		        		popupMenu.add("Add New Group").addActionListener(new ActionListener() {
				   		            @Override
				   		            public void actionPerformed(ActionEvent e) {
				   		            	ctrlPresentation.switchToNewGroupView(code);
				   		            }
				   		        });
		   		        		popupMenu.add("Remove Subject").addActionListener(new ActionListener() {
				   		            @Override
				   		            public void actionPerformed(ActionEvent e) {
				   		            	//ctrlPresentation.switchToRoomInfoView((String) ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject());
				   		            }
				   		        });
		   		        	}
						}
	   		        	popupMenu.show(e.getComponent(), e.getX(), e.getY());
	   		        }
                }
		         
		     }
		 });
		
		JScrollPane scollPnlRooms = new JScrollPane(treeRooms, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.addTab("Rooms", null, scollPnlRooms, null);
		treeRooms.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
            public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
            	table.stopEditing();
            	DefaultMutableTreeNode  tn = ((DefaultMutableTreeNode ) event.getPath().getLastPathComponent());
            	String roomName = (String) tn.getUserObject();
            	if(event.getChecked()) {
            		table.filterRoomIn(roomName);
            	}else{
            		table.filterRoomOut(roomName);
            	}   
            }
        });
		treeRooms.setModel(new DefaultTreeModel(null));
		treeRooms.addMouseListener(new MouseAdapter() {
		     public void mousePressed(MouseEvent e) {
		    	 if(SwingUtilities.isRightMouseButton(e)) {
                 	int selRow = treeRooms.getRowForLocation(e.getX(), e.getY());
	   		        TreePath selPath = treeRooms.getPathForLocation(e.getX(), e.getY());
	   		        treeRooms.clearSelection();
	   		        treeRooms.setSelectionPath(selPath);
	   		        if(selRow != -1) {
	   		        	final JPopupMenu popupMenu = new JPopupMenu();
	   		        	if(((DefaultMutableTreeNode) selPath.getLastPathComponent()).isRoot()){
		   		        	popupMenu.add("Add New Room").addActionListener(new ActionListener() {
			   		            @Override
			   		            public void actionPerformed(ActionEvent e) {
			   		            	ctrlPresentation.switchToNewRoomView();
			   		            }
			   		        });
						}else {
							String code = (String) ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
			   		        popupMenu.add("View More Info").addActionListener(new ActionListener() {
			   		            @Override
			   		            public void actionPerformed(ActionEvent e) {
			   		            	ctrlPresentation.switchToRoomInfoView(code);
			   		            }
			   		        });
			   		        popupMenu.addSeparator();
			   		        popupMenu.add("Delete Room").addActionListener(new ActionListener() {
			   		            @Override
			   		            public void actionPerformed(ActionEvent e) {
			   		            	int result = JOptionPane.showConfirmDialog(MainView.this, "Are you sure you want to delete '" + code + "'?\nThis action cannot be undone.", "Delete Room", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			   		            	if(result==JOptionPane.OK_OPTION) {
			   		            		ctrlPresentation.removeRoom(code);
			   		            	}
			   		            	
			   		            }
			   		        });
						}
	   		        	popupMenu.show(e.getComponent(), e.getX(), e.getY());
	   		        }
                 }
		         
		     }
		 });
	}
	
	private File loadLocalFile(File file) {
		fc.setCurrentDirectory(file);
		int returnVal = fc.showOpenDialog(MainView.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
		return file;
	}
	
	/**private boolean saveLocalFile() throws IOException {
		/**fc.setCurrentDirectory(file);
		int returnVal = fc.showOpenDialog(contentPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
		return file;
		int returnVal = fc.showSaveDialog(MainView.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would save the file.
            //log.append("Saving: " + file.getName() + "." + newline);
            return ctrlPresentation.exportSchedule(file.getAbsolutePath());
        } else {
            //log.append("Save command cancelled by user." + newline);
        }
        return false;
        //log.setCaretPosition(log.getDocument().getLength());
	}
	 * @throws IOException 
	 * @throws ParseException **/

	public boolean saveLocalFile() throws NullPointerException, IOException, ParseException {
	    boolean acceptable = false;
	    String filepath = "";
	    do {
	        File f = new File("data/schedules");
	        fc.setCurrentDirectory(f);
	        if (fc.showSaveDialog(MainView.this) == JFileChooser.APPROVE_OPTION) {
	        	filepath = fc.getSelectedFile().getAbsolutePath();
	            f = fc.getSelectedFile();
	            //System.out.println(theFile);
	            if (f.exists()) {
	            	JOptionPane.showMessageDialog(MainView.this, "Choose another filename.", "File already exists", JOptionPane.PLAIN_MESSAGE);
	            } else {
	                acceptable = true;
	            }
	        } else {
	            acceptable = true;
	        }
	    } while (!acceptable);

	    return ctrlPresentation.exportSchedule(filepath);

	}
	
	/**
	 * @param name
	 */
	private void environmentLoaded() {
		environmentLoaded = true;
		//scheduleLoaded = false;
		
		table.clearFilterLists();
		DefaultMutableTreeNode groupsRoot = new DefaultMutableTreeNode("All Subjects");
		DefaultTreeModel treeGroupsModel = new DefaultTreeModel(groupsRoot);
		for(String subject : ctrlPresentation.getSubjectNames()) {
			DefaultMutableTreeNode s = new DefaultMutableTreeNode(subject);
			for(String group : ctrlPresentation.getGroupsNamesFromSuject(subject)) {
				s.add(new DefaultMutableTreeNode(group));
			}
			groupsRoot.add(s);
		}
		treeGroups.setModel(treeGroupsModel);
		treeGroups.expandPath(new TreePath(groupsRoot.getPath()));
		treeGroups.checkSubTree(new TreePath(groupsRoot.getPath()), true);
		
		DefaultMutableTreeNode roomsRoot = new DefaultMutableTreeNode("All Rooms");
		DefaultTreeModel treeRoomsModel = new DefaultTreeModel(roomsRoot);
		for(String room : ctrlPresentation.getRoomNames())
			roomsRoot.add(new DefaultMutableTreeNode(room));
		treeRooms.setModel(treeRoomsModel);
		treeRooms.expandPath(new TreePath(roomsRoot.getPath()));
		treeRooms.checkSubTree(new TreePath(roomsRoot.getPath()), true);
		
		envText.setText(environmentName);
		btnLoadSchedule.setEnabled(environmentLoaded);
		btnGenSchedule.setEnabled(environmentLoaded);
		btnSaveSchedule.setEnabled(scheduleLoaded);
		btnConfigRestrictions.setEnabled(environmentLoaded);
	}
	
	public void subjectAdded(String name) {
		DefaultTreeModel model = (DefaultTreeModel) treeGroups.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(name);
	    root.add(child);
	    treeGroups.setModelUpdated(model, child);
	    ((DefaultTreeModel) treeGroups.getModel()).reload();
	    treeGroups.checkSubTree(new TreePath(child.getPath()), true);
	}
	
	/**
	 * 
	 */
	public void reloadSchedule() {
		scheduleLoaded = true;
		ArrayList<String[]>[][] data = ctrlPresentation.getScheduleMatrix();
		table.changeData(data);
		
		btnSaveSchedule.setEnabled(scheduleLoaded);
	}

}
