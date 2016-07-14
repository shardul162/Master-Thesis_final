import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.Timer;
//import GUI.FileLog;
//import GUI.Surface;

public class GUI implements Runnable{
	
	String fileName; // the filename of the stimulus
	String userName; // the selected username
	String logFilePath;

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
		Surface surface = new Surface(fileName);
		
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
	public void run(){
		try{
			
			Welcome();
			
		}catch(Exception e){
			System.out.println("error in GUI");
		}
	}
	
}
