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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.data.Point2D;

//import GUI.Eye_Tracker;
//import GUI.Surface.ScreenOffset;
//import GUI.Eye_Tracker.GazeListner;

public class Eye_Tracker implements Runnable {


	public Eye_Tracker eTracker;
	double gaze_x_coordinate;
	double gaze_y_coordinate;
	String timeStamp = "";
	double sampleCount = 0;
	Point2D coordinates;
	GazeManager gazeManager = null;
	GazeListner gazeListner = null;
	boolean success_server_connect = false;
	
	//Surface surfaceObject = new Surface(timeStamp);
	
	
	private class GazeListner implements com.theeyetribe.client.IGazeListener{
		@Override
		public void onGazeUpdate(GazeData gazeData){
			
			System.out.println("Update Gaze Coordinate: " + gazeData.smoothedCoordinates.x + ", " + gazeData.smoothedCoordinates.y + ":  " +  gazeData.timeStampString);

				gaze_x_coordinate = gazeData.smoothedCoordinates.x;
				gaze_y_coordinate = gazeData.smoothedCoordinates.y;
				timeStamp =  gazeData.timeStampString;
				
		
		}
	}
	
	//public class Surface extends JPanel implements ActionListener{}
	

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
	public void run(){
		
		try{
			//eTracker = new Eye_Tracker();
			//Surface surfaceObject = new Surface();
			
			
		}catch(Exception e){
			
		}
		
	}


}
