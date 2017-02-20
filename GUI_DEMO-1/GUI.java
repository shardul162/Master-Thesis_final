

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Random;
import java.awt.Font;
import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.data.Point2D;

//import TheEyeTrackingMain.Eye_Tracker;
//import TheEyeTrackingMain.Eye_Tracker.GazeListner;

/*
 *  Main GUI class
 */

/*
 * Eye_Tracking_Data_Thread
 * Thread for logging eye tracking data.
 * */




public class GUI {
	String fileName; // the filename of the stimulus
	String userName; // the selected username
	String logFilePath;
	/*
	 * Eye tracking related Variables
	 * */
	private Eye_Tracker eTracker;
	boolean startrecording = false;
	int eventCount = 0;
	
	private class Eye_Tracker{

		
		double gaze_x_coordinate;
		double gaze_y_coordinate;
		String timeStamp = "";
		double sampleCount = 0;
		Point2D coordinates;
		GazeManager gazeManager = null;
		GazeListner gazeListner = null;
		boolean success_server_connect = false;
		
		private class GazeListner implements com.theeyetribe.client.IGazeListener{
			@Override
			public void onGazeUpdate(GazeData gazeData){
				
				System.out.println("Update Gaze Coordinate: " + gazeData.smoothedCoordinates.x + ", " + gazeData.smoothedCoordinates.y + ":  " +  gazeData.timeStampString);

					gaze_x_coordinate = gazeData.smoothedCoordinates.x;
					gaze_y_coordinate = gazeData.smoothedCoordinates.y;
					timeStamp =  gazeData.timeStampString;
					
			
			}
		}

		public Point2D getCoordinates(){
			 coordinates.x = gaze_x_coordinate/sampleCount;
			 coordinates.y = gaze_y_coordinate/sampleCount;
			return coordinates;
		}
		
		public Eye_Tracker(){
			gaze_x_coordinate = 0;
			gaze_y_coordinate = 0;
			coordinates = new Point2D();

			//Connecting to the Eye Tribe Server
			gazeManager = GazeManager.getInstance();
			success_server_connect =  gazeManager.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
			
		
			if(success_server_connect){		//Register Gaze Listener. 
				gazeListner = new GazeListner();
				gazeManager.addGazeListener(gazeListner);
				//gm.addGazeListener(gazeListener);
			
			}
			else{
				System.out.println("Connection to Eye Tribe Server failed...");
			}
			
			//Adding a shut down hook to de-initialize Gaze Manager once the application is closed.
			Runtime.getRuntime().addShutdownHook(
					new Thread(){
						@Override
						public void run(){
							System.out.println("De initialing Gazemanager..");
							gazeManager.removeGazeListener(gazeListner);
							gazeManager.deactivate();
							
						}
			});
			
		}
	}
	
	/* 
	 * The stimulus drawing class;
	 * Place the dot image at the coordinates
	 * located in the selected .csv file
	 */
	class Surface extends JPanel implements ActionListener {

	    private final int DELAY = 50;
	    private Timer timer;
	    private Image circle; // the normal (red) dot image
	    private Image circle_blue; // the color changed (blue) dot image
	    boolean ended = false;
	    BufferedReader br;
	    int x = 0, y = 0;
	    public StringBuffer dataBuffer = new StringBuffer("");

	    SimpleDateFormat sdf; 
	    String formattedDate;
	    Date date;
	    // The random number used for the color change
	    int counter = 1;
	    boolean colorChanged = false;
	    Random r = new Random();
	    int Low = 400;
	    int High = 1000;
	    int randomTime = r.nextInt(High-Low) + Low;
	    public ScreenOffset screenOffset;
		Toolkit tk;
		boolean firstTime;
		double xOffset, yOffset;
	    
	    
	    private class ScreenOffset{
		    public double widthOffset;
		    public double heightOffset;
		    public Dimension actualScreenDimension;

		    public ScreenOffset(java.awt.Toolkit tk){
		    	actualScreenDimension = tk.getScreenSize();
		    }
		    
		    public void setOffset(double paneWidth, double paneHeight){
		    	this.widthOffset = java.lang.Math.abs(actualScreenDimension.getWidth() - paneWidth) ;
		    	this.heightOffset = java.lang.Math.abs(actualScreenDimension.getHeight() - paneHeight);
		    }
	    }
	    
	    public Surface(String fn) throws FileNotFoundException {
			eTracker = new Eye_Tracker();
	    	br = new BufferedReader(new FileReader(fn));
	    
	    	date = new Date();
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ms");
			formattedDate = sdf.format(date);
	    	
			firstTime = true;
			tk = this.getToolkit();
			screenOffset = new ScreenOffset(tk);
			
			
	    	initTimer();
	        loadImage();
	    }

	    
	    
	    // Timer initialization
	    private void initTimer() {
	        timer = new Timer(DELAY, this);
	        timer.start();
	    }
	    
	    // Load the dot image; subject-to-change: load a different
	    // image for the color change (add params)
	    private void loadImage() {

	    	circle = new ImageIcon("circle_im.png").getImage();
	    	circle_blue = new ImageIcon("circle_im_blue.png").getImage();
	    }
	    
	    public Timer getTimer() {
	        
	        return timer;
	    }
	    
	    
	    // the drawing function
	    private boolean doDrawing(Graphics g) throws IOException {

	    	// Read the .csv values
		    String splitBy = ","; // the .csv values are separated by a comma
			String line;   
			line = br.readLine();
			
			if(firstTime){
				// get the pane size once in the beginning.
				xOffset = java.lang.Math.abs(this.getRootPane().getSize().getWidth() - tk.getScreenSize().getWidth());
				yOffset = java.lang.Math.abs(this.getRootPane().getSize().getHeight() - tk.getScreenSize().getHeight());
				
//				screenOffset.setOffset(this.getRootPane().getSize().getWidth(), this.getRootPane().getSize().getHeight());
/*
				System.out.println("Pane: " + this.getRootPane().getSize().getWidth() + ", " + this.getRootPane().getSize().getHeight());
		        System.out.println("Difference: " + xOffset + ", " + yOffset);
		        System.out.println("Actual: " + tk.getScreenSize().getWidth() + ", " + tk.getScreenSize().getHeight());
*/				
		        firstTime = false;
			}
			if(line == null)
			{
				timer.stop();
				ended = true;
/*
				Toolkit tk = this.getToolkit();
				    Dimension dim = tk.getScreenSize();
*/
				    int messageX = (int) screenOffset.actualScreenDimension.getWidth() / 2;
				    int messageY = (int) screenOffset.actualScreenDimension.getHeight() /2;
			    int fontSize = 20;

			    g.setFont(new Font("Calibri", Font.PLAIN, fontSize));
			     
			    g.setColor(Color.black);
			    
			    g.drawString("Experiment ended", messageX, messageY);
			    g.drawString("Select File -> Done to save the results", messageX, messageY+fontSize);
			    g.drawString("or File -> Restart to repeat (discard the results)", messageX, messageY+fontSize*2);

				return false;
				
			}
			counter++;
			if(counter == randomTime)
				circle = new ImageIcon("circle_im_blue.png").getImage();
			
			String[] b = line.split(splitBy); // get the coordinates
	        
			// Do the drawing
		    Graphics2D g2d = (Graphics2D) g;
		    x = (int)Float.parseFloat(b[0]);
		    y = (int)Float.parseFloat(b[1]);
		    g2d.drawImage(circle, x,y, null);
		   
		    
		    dataBuffer.append(x + "," + y + "," + (eTracker.gaze_x_coordinate - xOffset )+ "," + (eTracker.gaze_y_coordinate - yOffset) + "\n");
		    System.out.println("Update UI: " + x + ", " + y);
		    return true;
	    }

	    @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        try {
				doDrawing(g);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    
	        repaint();
			System.out.println("Update UI :" + Calendar.getInstance().getTime().getSeconds());

	    }
	    public  StringBuffer getBuffer() {
	        return dataBuffer;
	    }
	}
	

	// The welcome (1st) screen
	public void Welcome()
	{

		JFrame F = new JFrame("Welcome");
		JLabel l;
		l = new JLabel();
		l.setText("    Name :");
		JTextField txt = new JTextField(20);
		JButton submitBut = new JButton(); // Submit button (for signing up a new user)
		JButton loginBut = new JButton(); // Button for loging in as an existing user (translated to a folder)
		submitBut.setText("Submit");
		loginBut.setText("Log in");
		// User list
        String[] userDirs = getCurrentUsers(); // Get the current user folders
        JComboBox userList = new JComboBox(userDirs); // the dropdown menu for the user selection
        userList.setEditable(false);	// the list cannot be edited
        
        // Save the selected user; know where to save the log file after the experiment has been carried out
        userName = (String)userList.getSelectedItem();
        userList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				userName = (String)userList.getSelectedItem();	
			}
        });
		if (userName == "") // if there are no users on the list, the login button is disabled
			loginBut.setEnabled(false);

		F.add(l);
		F.add(txt);
		F.add(submitBut);
		F.add(userList);
		F.add(loginBut);

		submitBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String newUser = ".\\Logs\\";
				newUser += txt.getText();	
				new File(newUser).mkdirs();
				F.dispose();
				Welcome();
							}
						});

		loginBut.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent e){
											F.dispose();
											stimulusSelect();
									}
							});
		F.setLayout(new FlowLayout());
		F.setSize(500,200);
		F.setVisible(true);
		F.setLocationRelativeTo(null);
		F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}
	
	
	// The stimulus select (2nd) screen
	public void stimulusSelect()
	{
		JFrame stimulusFr = new JFrame("stimulusSelect");
		JButton startB = new JButton();
		JButton refreshB = new JButton();
		startB.setText("START");
		refreshB.setText("Refresh list");
		
		String[] stimuliFiles;
		stimuliFiles = getCSVFiles();
		
		// Use the filenames array as options to the dropdown menu
        JComboBox stimulusList = new JComboBox(stimuliFiles); // the dropdown menu for the user selection
        stimulusList.setEditable(false);	// the list cannot be edited
        fileName = (String)stimulusList.getSelectedItem();
        stimulusList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileName = (String)stimulusList.getSelectedItem();
				
			}
        });
        
		JLabel labl;
		labl = new JLabel();
		labl.setText("Stimulus file:");
		stimulusFr.add(labl);
		stimulusFr.add(stimulusList);
		stimulusFr.add(startB); // Button for starting the tracking with the selected stimulus from the dropdown menu
		stimulusFr.add(refreshB); // Reload button to scan for new files since the last screen load
		
		// The action listener of the start button
		startB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				stimulusFr.dispose(); // Dispose the current (sign up screen)
					 EventQueue.invokeLater(new Runnable() {
				            @Override
				            public void run() {
				            	try {
									Stimulus();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				            }
				        });
			}
	});
		
		// The action listener for the refresh button; simply reloads the current screen
		refreshB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				stimulusFr.dispose();
					 EventQueue.invokeLater(new Runnable() {
				            @Override
				            public void run() {

				            	stimulusFr.dispose();
								stimulusSelect();
				            }
				        });
			}
	});	
		
		stimulusFr.setLayout(new FlowLayout());
		stimulusFr.setSize(500,200);
		stimulusFr.setVisible(true);
		stimulusFr.setLocationRelativeTo(null);
		stimulusFr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// The log off (4th, after the visual stimulus) screen
	public void LogOff()
	{

		JFrame LogOff = new JFrame("Log Off");
		
		JButton RetestBut = new JButton();
		RetestBut.setText("Home");
		
		JButton finishBut = new JButton();
		finishBut.setText("Finish");
		

		LogOff.add(RetestBut);
		LogOff.add(finishBut);
		finishBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					LogOff.dispose();
			}
	});
		
		RetestBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					LogOff.dispose();
					Welcome();
			}
	});
		LogOff.setLayout(new FlowLayout());
		LogOff.setSize(500,200);
		LogOff.setVisible(true);
		LogOff.setLocationRelativeTo(null);
		LogOff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public void Stimulus() throws FileNotFoundException
	{
		final Surface surface = new Surface(fileName);
		FileLog resultSave = new FileLog();
    	JFrame Stimulus = new JFrame("Stimulus");
    	JMenuBar menuBar = new JMenuBar();
    	
    	
        // Create the menu bar
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        // Add the "Done" option
        JMenuItem DoneMenuItem = new JMenuItem("Done (save results to a new file)", KeyEvent.VK_D);
        fileMenu.add(DoneMenuItem);
        DoneMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					Stimulus.dispose();
					String fileNamePath = resultSave.constructFileName(userName,"test");
					resultSave.SaveToFile(fileNamePath,surface.getBuffer());
					LogOff();
			}
	});
        
        
        // Add the "Restart" option
        JMenuItem RestartMenuItem = new JMenuItem("Restart", KeyEvent.VK_R);
        fileMenu.add(RestartMenuItem);
        RestartMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					Stimulus.dispose();
					try {
						Stimulus();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
	});
        
        Stimulus.setJMenuBar(menuBar);

        
        Stimulus.add(surface);
        Stimulus.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Timer timer = surface.getTimer();
                timer.stop();
            }
        });

        Stimulus.setTitle("Smooth pursuit stimulus");
        Stimulus.setExtendedState(JFrame.MAXIMIZED_BOTH); // start in full-screen
        Stimulus.setLocationRelativeTo(null);
        Stimulus.setVisible(true);
        Stimulus.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Subtracting the actual size of pane from actual size of screen
        
	}


	
	
	public String[] getCSVFiles()
	{
		// Get the .csv filenames to a string list
				File dir = new File(".");
				List<String> csvlist = Arrays.asList(dir.list(
				   new FilenameFilter() {
				      @Override public boolean accept(File dir, String name) {
				         return name.endsWith(".csv");
				      }
				   }
				));
				// convert the string list of filenames to an array
				return  csvlist.toArray(new String[0]);
	}
	
	
	public String[] getCurrentUsers()
	{
		String dirs = ""; 
        File logFolders = new File(".\\Logs");
        File[] filesList = logFolders.listFiles();
        for(File f : filesList){
            if(f.isDirectory())
            	dirs += f.getName() + ",";
            else
            	dirs = null;
        }
        return dirs.split(",");
	}
	
	class FileLog{
		public String constructFileName(String currentUser, String type) {
			return logFilePath = ".\\Logs\\" + currentUser +"\\" + currentUser + "_" + type + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss'.csv'").format(new Date());
		}
		
		
		public void SaveToFile(String filePath, StringBuffer resultsBuffer)
		{
		    String loggedData = resultsBuffer.toString();
			File logFile = new File(filePath);
	        try {
	            //create a new file if it doesn't exist already
	        	logFile.createNewFile();
	        } catch (IOException ex) {
	            ex.printStackTrace();
			}
			FileWriter fw = null;
			try {
				fw = new FileWriter(logFile.getAbsoluteFile());
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			BufferedWriter bw = new BufferedWriter(fw);
			try {
				bw.write(loggedData);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	public static void main(String args[]) throws FileNotFoundException
	{
		GUI myGUI = new GUI();
		myGUI.Welcome();
	}
}
