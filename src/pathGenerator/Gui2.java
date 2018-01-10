package pathGenerator;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.modifiers.TankModifier;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.Utilities;

public class Gui2 {

	private JFrame frmMotionProfileGenerator;

	private JTextField txtTime;
	private JTextField txtVelocity;
	private JTextField txtAcceleration;
	private JTextField txtJerk;
	private JTextField txtWheelBase;
	private JTextField txtAngle;
	private JTextField txtXValue;
	private JTextField txtYValue;
	private JTextField txtFileName;

	private final int WINX = 1300;
	private final int WINY = 700;
	JButton btnAddPoint;

	private JTabbedPane tabbedPane;

	FalconLinePlot allianceGraph = new FalconLinePlot(new double[][] { { 0.0, 0.0 } });
	FalconLinePlot velocityGraph = new FalconLinePlot(new double[][] { { 0.0, 0.0 } });

	private JTextArea txtAreaWaypoints;
	int lineNum;
	int rowStart;

	private JFileChooser fileChooser;
	private File directory;
	private File pFile;

	// Path Waypoints
	// private Waypoint[] points;
	private List<Waypoint> points = new ArrayList<Waypoint>(); // can be variable length after creation

	double timeStep;
	double velocity;
	double acceleration;
	double jerk;
	double wheelBase;

	Trajectory left;
	Trajectory right;

	File lFile;
	File rFile;
	File preferenceFile;

	String fileName;

	/**
	 * Create the application.
	 */
	public Gui2() {
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMotionProfileGenerator = new JFrame();
		frmMotionProfileGenerator.setResizable(false);
		frmMotionProfileGenerator.setTitle("Motion Profile Generator");
		frmMotionProfileGenerator.setLocation(150, 100);
		frmMotionProfileGenerator.setSize(WINX, WINY);
		frmMotionProfileGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMotionProfileGenerator.getContentPane().setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(460, 22, 759, 621);
		frmMotionProfileGenerator.getContentPane().add(tabbedPane);

		velocityGraph.setSize(759, 621);
		velocityGraph.setLocation(1070, 0);

		JPanel trajecPanel = new JPanel();
		trajecPanel.setBounds(0, 22, 450, 617);
		frmMotionProfileGenerator.getContentPane().add(trajecPanel);
		trajecPanel.setLayout(null);

		JLabel lblTimeStep = new JLabel("Time Step");
		lblTimeStep.setBounds(142, 60, 80, 20);
		trajecPanel.add(lblTimeStep);

		JLabel lblVelocity = new JLabel("Velocity");
		lblVelocity.setBounds(142, 90, 80, 20);
		trajecPanel.add(lblVelocity);

		JLabel lblAcceleration = new JLabel("Acceleration");
		lblAcceleration.setBounds(142, 120, 80, 20);
		trajecPanel.add(lblAcceleration);

		JLabel lblJerk = new JLabel("Jerk");
		lblJerk.setBounds(142, 150, 80, 20);
		trajecPanel.add(lblJerk);

		txtTime = new JTextField();
		txtTime.setText("0.05");
		txtTime.setBounds(222, 60, 86, 20);
		trajecPanel.add(txtTime);
		txtTime.setColumns(10);

		txtVelocity = new JTextField();
		txtVelocity.setText("4");
		txtVelocity.setBounds(222, 90, 86, 20);
		trajecPanel.add(txtVelocity);
		txtVelocity.setColumns(10);

		txtAcceleration = new JTextField();
		txtAcceleration.setText("3");
		txtAcceleration.setBounds(222, 120, 86, 20);
		trajecPanel.add(txtAcceleration);
		txtAcceleration.setColumns(10);

		txtJerk = new JTextField();
		txtJerk.setText("60");
		txtJerk.setBounds(222, 150, 86, 20);
		trajecPanel.add(txtJerk);
		txtJerk.setColumns(10);

		JButton btnGeneratePath = new JButton("Generate Path");
		btnGeneratePath.setBounds(90, 566, 130, 24);
		trajecPanel.add(btnGeneratePath);

		btnGeneratePath.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnGeneratePathActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		btnAddPoint = new JButton("Add Point");
		btnAddPoint.setBounds(130, 329, 90, 20);
		trajecPanel.add(btnAddPoint);

		btnAddPoint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAddPointActionPerformed(evt);
			}
		});

		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(230, 328, 90, 20);
		trajecPanel.add(btnClear);

		btnClear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnClearActionPerformed(evt);
			}
		});

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(334, 522, 89, 24);
		trajecPanel.add(btnBrowse);

		btnBrowse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBrowseActionPerformed(evt);
			}
		});

		JButton btnSave = new JButton("Save");
		btnSave.setBounds(230, 566, 130, 24);
		trajecPanel.add(btnSave);

		btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnSaveActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JLabel lblMotionVariables = new JLabel("Motion Variables");
		lblMotionVariables.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblMotionVariables.setBounds(137, 11, 176, 40);
		trajecPanel.add(lblMotionVariables);

		JLabel lblWaypoints = new JLabel("Waypoints");
		lblWaypoints.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblWaypoints.setBounds(170, 230, 110, 40);
		trajecPanel.add(lblWaypoints);

		txtWheelBase = new JTextField();
		txtWheelBase.setText("1.464");
		txtWheelBase.setBounds(222, 180, 86, 20);
		trajecPanel.add(txtWheelBase);
		txtWheelBase.setColumns(10);

		JLabel lblWheelBase = new JLabel("Wheel Base");
		lblWheelBase.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblWheelBase.setBounds(142, 180, 80, 20);
		trajecPanel.add(lblWheelBase);

		txtAngle = new JTextField();
		txtAngle.setBounds(257, 298, 63, 20);
		trajecPanel.add(txtAngle);
		txtAngle.setColumns(10);

		txtAreaWaypoints = new JTextArea();
		txtAreaWaypoints.setEditable(false);
		txtAreaWaypoints.setFont(new Font("Monospaced", Font.PLAIN, 14));
		txtAreaWaypoints.setBounds(131, 363, 188, 176);
		txtAreaWaypoints.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mouseEvent(e);
			}
		});

		txtXValue = new JTextField();
		txtXValue.setBounds(130, 298, 63, 20);
		trajecPanel.add(txtXValue);
		txtXValue.setColumns(10);

		txtYValue = new JTextField();
		txtYValue.setBounds(193, 298, 64, 20);
		trajecPanel.add(txtYValue);
		txtYValue.setColumns(10);

		JLabel lblX = new JLabel("X");
		lblX.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblX.setBounds(160, 275, 10, 20);
		trajecPanel.add(lblX);

		JLabel lblY = new JLabel("Y");
		lblY.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblY.setBounds(220, 275, 10, 20);
		trajecPanel.add(lblY);

		JLabel lblAngle = new JLabel("Angle");
		lblAngle.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAngle.setBounds(270, 275, 34, 20);
		trajecPanel.add(lblAngle);

		JScrollPane scrollPane = new JScrollPane(txtAreaWaypoints, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(130, 376, 190, 118);
		trajecPanel.add(scrollPane);

		txtFileName = new JTextField();
		txtFileName.setBounds(117, 524, 216, 20);
		trajecPanel.add(txtFileName);
		txtFileName.setColumns(10);

		JLabel lblLeftFileName = new JLabel("File Name");
		lblLeftFileName.setHorizontalAlignment(SwingConstants.CENTER);
		lblLeftFileName.setBounds(27, 524, 90, 20);
		trajecPanel.add(lblLeftFileName);

		JTextArea txtAreaWaypointsTitle = new JTextArea();
		txtAreaWaypointsTitle.setBounds(130, 352, 190, 24);
		trajecPanel.add(txtAreaWaypointsTitle);
		txtAreaWaypointsTitle.setEditable(false);
		txtAreaWaypointsTitle.setFont(new Font("Monospaced", Font.PLAIN, 14));
		String format = "%1$4s %2$6s %3$9s";
		String line = String.format(format, "X", "Y", "Angle");
		txtAreaWaypointsTitle.append(line + "\n");

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, WINX, 21);
		frmMotionProfileGenerator.getContentPane().add(menuBar);
		menuBar.setBackground(UIManager.getColor("Menu.background"));

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNewProfile = new JMenuItem("New Profile");
		mnFile.add(mntmNewProfile);

		mntmNewProfile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnClearActionPerformed(evt);
			}
		});

		JMenuItem mntmSaveFile = new JMenuItem("Save Profile");
		mnFile.add(mntmSaveFile);

		mntmSaveFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnMenuSaveActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JMenuItem mntmLoadProfile = new JMenuItem("Load Profile");
		mnFile.add(mntmLoadProfile);

		mntmLoadProfile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					btnMenuLoadActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		mntmExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				System.exit(0);
			}
		});

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);

		mntmHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						URI uri = new URI("https://github.com/vannaka/Motion_Profile_Generator");
						desktop.browse(uri);
					} catch (IOException ex) {
						return;
					} catch (URISyntaxException ex) {
						return;
					}
				} else {
					return;
				}
			}
		});

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		mntmAbout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutPage();
			}
		});

		motionGraphAlliance();
		velocityGraph();
	};

	private void aboutPage() {
		JFrame about = new JFrame();
		about.setLocationByPlatform(true);
		about.setVisible(true);
		about.setTitle("About");
		about.setBounds(100, 100, 600, 400);
		about.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		about.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 584, 361);
		about.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblMotionProfileGenerator = new JLabel("Motion Profile Generator");
		lblMotionProfileGenerator.setFont(new Font("Arial", Font.PLAIN, 34));
		lblMotionProfileGenerator.setBounds(109, 29, 365, 64);
		panel.add(lblMotionProfileGenerator);

		JLabel lblVersion = new JLabel("Version 1.2.0");
		lblVersion.setFont(new Font("Arial", Font.PLAIN, 14));
		lblVersion.setBounds(82, 104, 85, 14);
		panel.add(lblVersion);

		JLabel lblThisProductIs = new JLabel("This product is licensed under the MIT license");
		lblThisProductIs.setFont(new Font("Arial", Font.PLAIN, 14));
		lblThisProductIs.setBounds(82, 128, 296, 14);
		panel.add(lblThisProductIs);

		JLabel lblDevelopers = new JLabel("Developers");
		lblDevelopers.setFont(new Font("Arial", Font.PLAIN, 14));
		lblDevelopers.setBounds(82, 152, 85, 14);
		panel.add(lblDevelopers);

		JLabel lblLukeMammen = new JLabel("Luke Mammen");
		lblLukeMammen.setFont(new Font("Arial", Font.PLAIN, 14));
		lblLukeMammen.setBounds(109, 176, 110, 14);
		panel.add(lblLukeMammen);

		JLabel lblBlakeMammen = new JLabel("Blake Mammen");
		lblBlakeMammen.setFont(new Font("Arial", Font.PLAIN, 14));
		lblBlakeMammen.setBounds(109, 200, 110, 14);
		panel.add(lblBlakeMammen);

		JLabel lblAcknowedgements = new JLabel("Acknowledgments");
		lblAcknowedgements.setFont(new Font("Arial", Font.PLAIN, 14));
		lblAcknowedgements.setBounds(82, 224, 150, 14);
		panel.add(lblAcknowedgements);

		JLabel lblJaci = new JLabel("Jaci for the path generation code");
		lblJaci.setFont(new Font("Arial", Font.PLAIN, 14));
		lblJaci.setBounds(109, 248, 250, 14);
		panel.add(lblJaci);

		JLabel lblJH = new JLabel("KHEngineering for the graph code");
		lblJH.setFont(new Font("Arial", Font.PLAIN, 14));
		lblJH.setBounds(109, 272, 250, 14);
		panel.add(lblJH);
	}

	// Graphics to help visualizations
	private void motionGraphAlliance() {
		tabbedPane.insertTab("Field View", null, allianceGraph, null, 0);
		// Create a blank grid for the field graph
		allianceGraph.yGridOn();
		allianceGraph.xGridOn();
		allianceGraph.setYLabel("Y (feet)");
		allianceGraph.setXLabel("X (feet)");
		allianceGraph.setTitle(
				"Top Down View of FRC Field - Blue Alliance (33 x 27ft) \n shows global position of robot path with left and right wheel trajectories");

		// force graph to show field dimensions of 30ft x 27 feet
		double fieldWidth = 27.0; // height for top-down view
		double fieldLength = 33.0; // length for top-down view
		allianceGraph.setXTic(0, fieldLength, 1);
		allianceGraph.setYTic(0, fieldWidth, 1);

		allianceGraph.addData(FieldStructures.AllianceSwitch, Color.black);
		allianceGraph.addData(FieldStructures.TopSwitchPlate, new Color(138, 43, 226));
		allianceGraph.addData(FieldStructures.BotSwitchPlate, new Color(138, 43, 226));

		allianceGraph.addData(FieldStructures.Scale, new Color(138, 43, 226));
		allianceGraph.addData(FieldStructures.BluePlat, Color.blue);
		allianceGraph.addData(FieldStructures.BluePlatRamp, Color.blue);
		allianceGraph.addData(FieldStructures.RedPlat, Color.red);
		allianceGraph.addData(FieldStructures.RedPlatRamp, Color.red);
		
		allianceGraph.addData(FieldStructures.XChangeZone, Color.blue);
		allianceGraph.addData(FieldStructures.CubeZone, Color.blue);
		// Auto Line
		double[][] baseLine = new double[][] { { 10.0, 0 }, { 10.0, fieldWidth } };
		allianceGraph.addData(baseLine, Color.black);

		// Mid Field
		double[][] midTick1 = new double[][] { { 27.0, 0 }, { 27.0, 6. } };
		allianceGraph.addData(midTick1, Color.black);

		double[][] midTick2 = new double[][] { { 27.0, fieldWidth }, { 27.0, 21. } };
		allianceGraph.addData(midTick2, Color.black);
		
		//Portal Walls
		double[][] botPortal = new double[][] {{0,2.474} , {2.917,0}};
		allianceGraph.addData(botPortal, Color.black);

		double[][] topPortal = new double[][] {{0,fieldWidth-2.474} , {2.917,fieldWidth}};
		allianceGraph.addData(topPortal, Color.black);

	}

	private void velocityGraph() {
		tabbedPane.insertTab("Velocity", null, velocityGraph, null, 1);
		velocityGraph.yGridOn();
		velocityGraph.xGridOn();
		velocityGraph.setYLabel("Velocity (ft/sec)");
		velocityGraph.setXLabel("time (seconds)");
		velocityGraph.setTitle("Velocity Profile for Left and Right Wheels \n Left = Cyan, Right = Magenta");
	}

	private void btnMenuLoadActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle("Choose a file to load.");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			pFile = fileChooser.getSelectedFile();

			String preference = pFile.getName();
			String extension = "";

			int i = preference.lastIndexOf('.');
			if (i > 0) {
				extension = preference.substring(i + 1);
			}

			if (extension.equals("bot")) {
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(pFile));

				String sTime = br.readLine();
				String sVelocity = br.readLine();
				String sAcceleration = br.readLine();
				String sJerk = br.readLine();
				String sWheel = br.readLine();

				txtTime.setText(sTime);
				txtVelocity.setText(sVelocity);
				txtAcceleration.setText(sAcceleration);
				txtJerk.setText(sJerk);
				txtWheelBase.setText(sWheel);

				points.clear();
				txtAreaWaypoints.setText(null);

				String st;
				while ((st = br.readLine()) != null) {

					String[] splitStr = st.trim().split("\\s*,\\s*");
					String xValueS = splitStr[0];
					String yValueS = splitStr[1];
					String aValueS = splitStr[2];

					double dX = Double.parseDouble(xValueS);
					double dY = Double.parseDouble(yValueS);
					double dA = Double.parseDouble(aValueS);

					String format = "%1$6.2f %2$6.2f %3$7.2f";
					String line = String.format(format, dX, dY, dA);

					txtAreaWaypoints.append(line + "\n");
					points.add(new Waypoint(dX, dY, Pathfinder.d2r(dA)));
				}
			} else {
				JOptionPane.showMessageDialog(null, "The file type is invalid! Make sure it is .bot",
						"Invalid file type", JOptionPane.INFORMATION_MESSAGE);
			}

		}

		else {
			return;
		}
	}

	private void btnGeneratePathActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
		timeStep = Double.parseDouble(txtTime.getText()); // default 0.05
		velocity = Double.parseDouble(txtVelocity.getText()); // default 4
		acceleration = Double.parseDouble(txtAcceleration.getText()); // default 3
		jerk = Double.parseDouble(txtJerk.getText()); // default 60
		wheelBase = Double.parseDouble(txtWheelBase.getText()); // default 1.464

		// clear graphs
		velocityGraph.clearGraph();
		velocityGraph.repaint();
		allianceGraph.clearGraph();
		allianceGraph.repaint();

		motionGraphAlliance();
		velocityGraph();

		if (timeStep > 0) {
			if (velocity > 0) {
				if (acceleration > 0) {
					if (jerk > 0) {
						if (wheelBase > 0) {
							// If waypoints exist
							if (points.size() > 1) {
								Waypoint tmp[] = new Waypoint[points.size()];
								points.toArray(tmp);
								try {
									trajectory(timeStep, velocity, acceleration, jerk, wheelBase, tmp);
								} catch (Exception e) {
									JOptionPane.showMessageDialog(null,
											"The trajectory provided was invalid! Invalid trajectory could not be generated",
											"Invalid Points.", JOptionPane.INFORMATION_MESSAGE);
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"We need at least two points to generate a profile.", "Insufficient Points.",
										JOptionPane.INFORMATION_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(null, "The Wheel Base value is invalid!", "Invalid Value",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "The Jerk value is invalid!", "Invalid Value",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "The Acceleration value is invalid!", "Invalid Value",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "The Velocity value is invalid!", "Invalid Value",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "The Time Step value is invalid!", "Invalid Value",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private void mouseEvent(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		if (e.getClickCount() != 2) {
			return;
		}

		int offset = txtAreaWaypoints.viewToModel(e.getPoint());

		try {
			rowStart = Utilities.getRowStart(txtAreaWaypoints, offset);
			int rowEnd = Utilities.getRowEnd(txtAreaWaypoints, offset);
			String selectedLine = txtAreaWaypoints.getText().substring(rowStart, rowEnd);

			btnAddPoint.setText("Update");

			String[] splitStr = selectedLine.trim().split("\\s+");
			String xValueS = splitStr[0];
			String yValueS = splitStr[1];
			String aValueS = splitStr[2];

			txtXValue.setText(xValueS);
			txtYValue.setText(yValueS);
			txtAngle.setText(aValueS);

			int off = txtAreaWaypoints.getCaretPosition();
			lineNum = txtAreaWaypoints.getLineOfOffset(off);

			Document document = txtAreaWaypoints.getDocument();

			int len = rowEnd - rowStart + 1;
			if (rowStart + len > document.getLength()) {
				len--;
			}
			document.remove(rowStart, len);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "The Row is empty!", "Row Empty", JOptionPane.INFORMATION_MESSAGE);
		}
		points.remove(lineNum);
	}

	private void btnAddPointActionPerformed(java.awt.event.ActionEvent evt) {
		double xValue = 0;
		double yValue = 0;
		double angle = 0;

		// get x value
		try {
			xValue = Double.parseDouble(txtXValue.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The X value is invalid!", "Invalid Value",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// get y value
		try {
			yValue = Double.parseDouble(txtYValue.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The Y value is invalid!", "Invalid Value",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// get angle value
		try {
			angle = Double.parseDouble(txtAngle.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The Angle value is invalid!", "Invalid Value",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String format = "%1$6.2f %2$6.2f %3$7.2f";
		String line = String.format(format, xValue, yValue, angle);

		if (btnAddPoint.getText() == "Add Point") {
			txtAreaWaypoints.append(line + "\n");
			// add new point to points list
			points.add(new Waypoint(xValue, yValue, Pathfinder.d2r(angle)));
		} else {
			txtAreaWaypoints.insert(line + "\n", rowStart);
			points.add(lineNum, new Waypoint(xValue, yValue, Pathfinder.d2r(angle)));
		}

		txtXValue.setText("");
		txtYValue.setText("");
		txtAngle.setText("");

		btnAddPoint.setText("Add Point");
	}

	private void btnMenuSaveActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
		if (txtFileName.getText().equals("") == false) {
			if (directory != null) {
				if (left != null) {
					lFile = new File(directory, fileName + "_left.csv");
					rFile = new File(directory, fileName + "_right.csv");

					if (lFile.exists() || rFile.exists()) {
						int n = JOptionPane.showConfirmDialog(null, "File already exist. Would you like to replace it?",
								"File Exists", JOptionPane.YES_NO_OPTION);

						switch (n) {
						case JOptionPane.YES_OPTION:
							break; // Continue with method

						case JOptionPane.NO_OPTION:
							return; // Stop Saving

						default:
							return;
						}
					}

					FileWriter lfw = new FileWriter(lFile);
					FileWriter rfw = new FileWriter(rFile);
					PrintWriter lpw = new PrintWriter(lfw);
					PrintWriter rpw = new PrintWriter(rfw);

					// Detailed CSV with dt, x, y, position, velocity, acceleration, jerk, and
					// heading
					File leftFile = new File(directory, fileName + "_left_detailed.csv");
					Pathfinder.writeToCSV(leftFile, left);

					File rightFile = new File(directory, fileName + "_right_detailed.csv");
					Pathfinder.writeToCSV(rightFile, right);

					// CSV with position and velocity. To be used with your robot.
					// save left path to CSV
					for (int i = 0; i < left.length(); i++) {
						Segment seg = left.get(i);
						lpw.printf("%f, %f, %d\n", seg.position, seg.velocity, (int) (seg.dt * 1000));
					}

					// save right path to CSV
					for (int i = 0; i < right.length(); i++) {
						Segment seg = right.get(i);
						rpw.printf("%f, %f, %d\n", seg.position, seg.velocity, (int) (seg.dt * 1000));
					}

					lpw.close();
					rpw.close();

					preferenceFile = new File(directory, fileName + "_Preferences.bot");
					FileWriter pfw = new FileWriter(preferenceFile);
					PrintWriter ppw = new PrintWriter(pfw);

					ppw.println(timeStep);
					ppw.println(velocity);
					ppw.println(acceleration);
					ppw.println(jerk);
					ppw.println(wheelBase);

					for (int i = 0; i < points.size(); i++) {
						ppw.printf("%4.2f, %4.2f, %4.2f", points.get(i).x, points.get(i).y,
								Pathfinder.r2d(points.get(i).angle));
						ppw.println();
					}

					ppw.close();
				} else {
					JOptionPane.showMessageDialog(null, "No Trajectory has been generated!", "Trajectory Not Generated",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"No file destination chosen! \nClick the Browse button to choose a directory!",
						"File Destination Empty", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

		} else {
			JOptionPane.showMessageDialog(null,
					"The File Name/directory field is empty! \nPlease enter a file name and click Browse for a destination!",
					"File Name Empty", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {
		if (txtFileName.getText().equals("") == false) {
			fileName = txtFileName.getText();

			fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setDialogTitle("Choose a Directory to Save Files In");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);

			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				// directory = fileChooser.getCurrentDirectory();
				directory = fileChooser.getSelectedFile();
			}

			else {
				return;
			}
		} else {
			JOptionPane.showMessageDialog(null, "The File Name field is empty! \nPlease enter a file name!",
					"File Name Empty", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		txtFileName.setText(directory + "\\" + fileName);
	}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
		if (txtFileName.getText().equals("") == false) {
			if (directory != null) {
				if (left != null) {
					lFile = new File(directory, fileName + "_left.csv");
					rFile = new File(directory, fileName + "_right.csv");

					if (lFile.exists() || rFile.exists()) {
						int n = JOptionPane.showConfirmDialog(null, "File already exist. Would you like to replace it?",
								"File Exists", JOptionPane.YES_NO_OPTION);

						switch (n) {
						case JOptionPane.YES_OPTION:
							break; // Continue with method

						case JOptionPane.NO_OPTION:
							return; // Stop Saving

						default:
							return;
						}
					}

					FileWriter lfw = new FileWriter(lFile);
					FileWriter rfw = new FileWriter(rFile);
					PrintWriter lpw = new PrintWriter(lfw);
					PrintWriter rpw = new PrintWriter(rfw);

					// Detailed CSV with dt, x, y, position, velocity, acceleration, jerk, and
					// heading
					File leftFile = new File(directory, fileName + "_left_detailed.csv");
					Pathfinder.writeToCSV(leftFile, left);

					File rightFile = new File(directory, fileName + "_right_detailed.csv");
					Pathfinder.writeToCSV(rightFile, right);

					// CSV with position and velocity. To be used with your robot.
					// save left path to CSV
					for (int i = 0; i < left.length(); i++) {
						Segment seg = left.get(i);
						lpw.printf("%f, %f, %d\n", seg.position, seg.velocity, (int) (seg.dt * 1000));
					}

					// save right path to CSV
					for (int i = 0; i < right.length(); i++) {
						Segment seg = right.get(i);
						rpw.printf("%f, %f, %d\n", seg.position, seg.velocity, (int) (seg.dt * 1000));
					}

					lpw.close();
					rpw.close();

					preferenceFile = new File(directory, fileName + "_Preferences.bot");
					FileWriter pfw = new FileWriter(preferenceFile);
					PrintWriter ppw = new PrintWriter(pfw);

					ppw.println(timeStep);
					ppw.println(velocity);
					ppw.println(acceleration);
					ppw.println(jerk);
					ppw.println(wheelBase);

					for (int i = 0; i < points.size(); i++) {
						ppw.printf("%4.2f, %4.2f, %4.2f", points.get(i).x, points.get(i).y,
								Pathfinder.r2d(points.get(i).angle));
						ppw.println();
					}

					ppw.close();
				} else {
					JOptionPane.showMessageDialog(null, "No Trajectory has been generated!", "Trajectory Not Generated",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"No file destination chosen! \nClick the Browse button to choose a directory!",
						"File Destination Empty", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

		} else {
			JOptionPane.showMessageDialog(null,
					"The File Name/directory field is empty! \nPlease enter a file name and click Browse for a destination!",
					"File Name Empty", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {
		// clear graphs
		allianceGraph.clearGraph();
		allianceGraph.repaint();
		velocityGraph.clearGraph();
		velocityGraph.repaint();

		motionGraphAlliance();
		velocityGraph();

		points.clear();

		txtAreaWaypoints.setText(null);
	}

	private void trajectory(double timeStep, double velocity, double acceleration, double jerk, double wheelBase,
			Waypoint[] points) throws IOException {

		// Configure the trajectory with the time step, velocity, acceleration, jerk
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,
				Trajectory.Config.SAMPLES_HIGH, timeStep, velocity, acceleration, jerk);

		// Generate the path
		Trajectory trajectory = Pathfinder.generate(points, config);

		// Tank drive modifier with the wheel base
		TankModifier modifier = new TankModifier(trajectory).modify(wheelBase);

		// Separate the trajectory into left and right
		left = modifier.getLeftTrajectory();
		right = modifier.getRightTrajectory();

		// Left and Right paths to display on the Field Graph
		double[][] leftPath = new double[left.length()][2];
		double[][] rightPath = new double[right.length()][2];

		for (int i = 0; i < left.length(); i++) {
			leftPath[i][0] = left.get(i).x;
			leftPath[i][1] = left.get(i).y;
			rightPath[i][0] = right.get(i).x;
			rightPath[i][1] = right.get(i).y;
		}
		allianceGraph.addData(leftPath, Color.magenta);
		allianceGraph.addData(rightPath, Color.magenta);
		allianceGraph.repaint();

		// Velocity to be used in the Velocity graph
		double[][] leftVelocity = new double[left.length()][2];
		double[][] rightVelocity = new double[right.length()][2];
		double[][] middleVelocity = new double[trajectory.length()][2];

		for (int i = 0; i < left.length(); i++) {
			leftVelocity[i][0] = left.segments[i].dt * i;
			leftVelocity[i][1] = left.segments[i].velocity;
			rightVelocity[i][0] = right.segments[i].dt * i;
			rightVelocity[i][1] = right.segments[i].velocity;
			middleVelocity[i][0] = trajectory.segments[i].dt * i;
			middleVelocity[i][1] = trajectory.segments[i].velocity;
		}

		// Velocity Graph
		velocityGraph.addData(leftVelocity, Color.magenta);
		velocityGraph.addData(rightVelocity, Color.cyan);
		velocityGraph.addData(middleVelocity, Color.blue);
		velocityGraph.repaint();

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui2 window = new Gui2();
					window.frmMotionProfileGenerator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static class FieldStructures {

		// lets add field markers to help visual
		public static double[][] AllianceSwitch = new double[][] { // 12*4 box
				{ 11.875, 7.275 }, // bottom left corner
				{ 16.125, 7.275 }, // bottom right corner
				{ 16.125, 19.725 }, // top right corner
				{ 11.875, 19.725 }, // top left corner
				{ 11.875, 7.275 } // bottom left corner
		};

		public static double[][] TopSwitchPlate = new double[][] { { 16, 19.5 }, // top right corner
				{ 12, 19.5 }, // top left corner
				{ 12, 16.5 }, // bottom left corner
				{ 16, 16.5 }, // bottom right corner
				{ 16, 19.5 } // top right corner
		};

		public static double[][] BotSwitchPlate = new double[][] { { 12, 7.5 }, // bottom left corner
				{ 16, 7.5 }, // bottom right corner
				{ 16, 10.5 }, // top right corner
				{ 12, 10.5 }, // top left corner
				{ 12, 7.5 } // bottom left corner
		};

		public static double[][] Scale = new double[][] { // 4*15
				{ 27.0, 9.0 }, { 25.0, 9.0 }, { 25.0, 6.0 }, { 29.0, 6.0 }, { 29.0, 9.0 }, { 27.0, 9.0 },
				{ 27.0, 18.0 }, { 25.0, 18.0 }, { 25.0, 21.0 }, { 29.0, 21.0 }, { 29.0, 18.0 }, { 27.0, 18.0 } };

		public static double[][] BluePlat = new double[][] { { 23.562, 17.833 }, { 23.562, 9.166 }, { 27.0, 9.166 },
				{ 27.0, 17.833 }, { 23.562, 17.833 } };

		
		public static double[][] RedPlat = new double[][] { { 30.438, 17.833 }, { 30.438, 9.166 }, { 27.0, 9.166 },
				{ 27.0, 17.833 }, { 30.438, 17.833 } };

		private static double rampWidth = 1.08333;
		public static double[][] BluePlatRamp = new double[][] { { 25.0, 17.833 + rampWidth },
						{ 23.562 - rampWidth, 17.833 + rampWidth }, { 23.562 - rampWidth, 9.166 - rampWidth },
						{ 25.0, 9.166 - rampWidth }, };

		public static double[][] RedPlatRamp = new double[][] { { 29.0, 17.833 + rampWidth },
				{ 30.438 + rampWidth, 17.833 + rampWidth }, { 30.438 + rampWidth, 9.166 - rampWidth },
				{ 29.0, 9.166 - rampWidth }, };
				
		public static double[][] CubeZone = new double[][] {
			{11.875,15.375},
			{8.375,15.375},
			{8.375,11.625},
			{11.875,11.625}
		};
		
		public static double[][] XChangeZone = new double[][] {
			{0,12.5},
			{3,12.5},
			{3,8.5},
			{0,8.5},
			{0,12.5}

		};
		
	}
}
