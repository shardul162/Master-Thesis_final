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
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Surface extends JPanel implements ActionListener  {

	Eye_Tracker getCoords;
    private final int DELAY = 50;
    private Timer timer;
    private Image circle; // the normal (red) dot image
    private Image circle_blue; // the color changed (blue) dot image
    boolean ended = false;
    BufferedReader br;
    int x = 0, y = 0;
    public StringBuffer dataBuffer = new StringBuffer("");
    boolean Tstart = false;
    boolean done = false;

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
    Float[] tempo = new Float[2];
    ArrayList<Float[]> al = new ArrayList<Float[]>();
    int index = 0;
    
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
    	getCoords = new Eye_Tracker();
    	getCoords.run();
    	br = new BufferedReader(new FileReader(fn));
    
    	date = new Date();
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ms");
		formattedDate = sdf.format(date);
    	
		firstTime = true;
		tk = this.getToolkit();
		screenOffset = new ScreenOffset(tk);
		
    	initTimer();
        loadImage();
        
        repainter = new Repainter();
        (new Thread(repainter)).start();
    }

    //HELPS ACHIEVING 60FPS PRINTING ON SCREEN
    private class Repainter implements Runnable {
		@Override
		public void run() {
			long nextTime = System.currentTimeMillis();
			while (true) {
				nextTime += 50/3;
				while(System.currentTimeMillis() < nextTime);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						repaint();
					}
				});
			}
		}
    }
    
    private Repainter repainter;
    
    // Timer initialization
    private void initTimer() {
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    // Load the dot image; subject-to-change: load a different
    // image for the color change 
    private void loadImage() {

    	circle = new ImageIcon("circle_im.png").getImage();
    	circle_blue = new ImageIcon("circle_im_blue.png").getImage();
    }
    
    public Timer getTimer() {
        
        return timer;
    }
    
    public void StoreCoords() throws  IOException{
    	
    	
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
			
				al.add(floatArray);
				}
		}
			
    	
    }
    
    
    // the drawing function
    public boolean doDrawing(Graphics g) throws IOException {

		if(firstTime){
			// get the pane size once in the beginning.
			xOffset = java.lang.Math.abs(this.getRootPane().getSize().getWidth() - tk.getScreenSize().getWidth());
			yOffset = java.lang.Math.abs(this.getRootPane().getSize().getHeight() - tk.getScreenSize().getHeight());

	        firstTime = false;
		}
		
		if(index == al.size()){
			
			timer.stop();
			ended = true;

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
 
		// Do the drawing
	    Graphics2D g2d = (Graphics2D) g;
	    
	    
		tempo = al.get(index);
		 x =(int) Math.round(tempo[0]);
    	 y =(int) Math.round(tempo[1]);
	    g2d.drawImage(circle, x+630,y+190, null);
	    index++;
	   
	    //SAVING INTO FILE
	    dataBuffer.append(x + "," + y + "," + (getCoords.gaze_x_coordinate - xOffset )+ "," + (getCoords.gaze_y_coordinate - yOffset) + "\n");
	    
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
	
        }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
    public  StringBuffer getBuffer() {
        return dataBuffer;
    }


}