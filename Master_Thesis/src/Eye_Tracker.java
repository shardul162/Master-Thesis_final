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

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.data.Point2D;

public class Eye_Tracker implements Runnable {

	double gaze_x_coordinate;
	double gaze_y_coordinate;
	String timeStamp = "";
	Point2D coordinates;
	GazeManager gazeManager = null;
	GazeListner gazeListner = null;
	boolean success_server_connect = false;
	

	private class GazeListner implements com.theeyetribe.client.IGazeListener{
		@Override
		public void onGazeUpdate(GazeData gazeData){
			
			//START RECEIVING VALUES FROM THE EYE-TRACKER
			
				gaze_x_coordinate = gazeData.smoothedCoordinates.x;
				gaze_y_coordinate = gazeData.smoothedCoordinates.y;
				timeStamp =  gazeData.timeStampString;
				System.out.println("Update Gaze Coordinate: " + gazeData.smoothedCoordinates.x + ", " + gazeData.smoothedCoordinates.y + ":  " +  gazeData.timeStampString);		
				
		}
	}
	
	public void run(){
		
		try{
			
			//Connecting to the Eye Tribe Server
			gazeManager = GazeManager.getInstance();
			success_server_connect =  gazeManager.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
			
		
			if(success_server_connect){		//Register Gaze Listener. 
				gazeListner = new GazeListner();
				gazeManager.addGazeListener(gazeListner);

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
					}});

			}catch(Exception e){
			
		}
	}


}
