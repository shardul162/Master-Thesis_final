import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

//import GUI.Eye_Tracker;

//import GUI.Eye_Tracker;
//import GUI.Surface.ScreenOffset;

public class Surface extends JPanel implements ActionListener  {

	//private Eye_Tracker eTracker;
    private final int DELAY = 50;
    private Timer timer;
    private Image circle; // the normal (red) dot image
    private Image circle_blue; // the color changed (blue) dot image
    boolean ended = false;
    BufferedReader br;
    int x = 0, y = 0;
    public StringBuffer dataBuffer = new StringBuffer("");
    boolean Tstart = false;

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
	double currentTime;
    double PassedTime;
    
    
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
		//Eye_Tracker eTracker = new Eye_Tracker(); 
		//eTracker.start(true);
    	//Tstart = true;
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
    
    public void StoreCoords() throws  IOException{
    	
    	ArrayList<Float[]> al = new ArrayList<Float[]>();
    	
    	 // the .csv values are separated by a comma
    	String splitBy = ",";
		String line;
		
		while((line = br.readLine()) != null){	
			
			String[] b = line.split(splitBy);//reading coordinates
			Float[] floatArray = new Float[b.length];
			
			if(!line.isEmpty()) {
				for(int i = 0;i< b.length;i++){
					
					floatArray[i]= Float.parseFloat(b[i]);
							
				}
				
				System.out.println("The floatArray contains: "+floatArray[0]+" "+floatArray[1]);
			
				al.add(floatArray);
				}
		}
			
    	
    }
    
    
    // the drawing function
    public boolean doDrawing(Graphics g) throws IOException {
    	Eye_Tracker getCoords = new Eye_Tracker();
    	Eye_Tracker eTracker = new Eye_Tracker(); 
		eTracker.start(true);
    	// Read the .csv values
	    String splitBy = ","; // the .csv values are separated by a comma
		String line;   
		line = br.readLine();
		
		if(firstTime){
			// get the pane size once in the beginning.
			xOffset = java.lang.Math.abs(this.getRootPane().getSize().getWidth() - tk.getScreenSize().getWidth());
			yOffset = java.lang.Math.abs(this.getRootPane().getSize().getHeight() - tk.getScreenSize().getHeight());
			
//			screenOffset.setOffset(this.getRootPane().getSize().getWidth(), this.getRootPane().getSize().getHeight());
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
	   
	    
	    dataBuffer.append(x + "," + y + "," + (getCoords.gaze_x_coordinate - xOffset )+ "," + (getCoords.gaze_y_coordinate - yOffset) + "\n");

	    System.out.println("Update UI: " + x + ", " + y);
	    return true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        try {
			StoreCoords();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        currentTime = System.currentTimeMillis();
        try {
   			doDrawing(g);
   			
   				
   		} catch (IOException e) {
   			
   			e.printStackTrace();
   		}
        
        PassedTime = System.currentTimeMillis();
        System.out.println(PassedTime-currentTime);
        while(PassedTime-currentTime <= (50/3)){
        	
        	PassedTime = System.currentTimeMillis(); 
        }
        	
        	//System.out.println(PassedTime-currentTime);
        	
        	
        }

    @Override
    public void actionPerformed(ActionEvent e) {
    	
    	currentTime = System.currentTimeMillis();
    	
        repaint();
        PassedTime = System.currentTimeMillis();
        System.out.println(PassedTime-currentTime);
        while(PassedTime-currentTime <= (50/3)){
        	
        	PassedTime = System.currentTimeMillis(); 
        	
        	//System.out.println(PassedTime-currentTime);
        	
        	
        }
		//System.out.println("Update UI :" + Calendar.getInstance().getTime().getSeconds());

    }
    public  StringBuffer getBuffer() {
        return dataBuffer;
    }


}