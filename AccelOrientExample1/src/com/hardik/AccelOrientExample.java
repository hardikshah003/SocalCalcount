package com.hardik;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AccelOrientExample extends Activity implements SensorEventListener {
 
 // Accelerometer X, Y, and Z values
 private TextView accelXValue;
 private TextView accelYValue;
 private TextView accelZValue;
 
 // Orientation X, Y, and Z values
 private TextView orientXValue;
 private TextView orientYValue;
 private TextView orientZValue;
 private Double x[] = new Double[100];
 private Double y[] = new Double[100];
 private Double z[] = new Double[100];
 
 //This is the addition of x,y,z of the accelerometer
 //reading every 30 streams
//We will keep on adding the values till 30seconds

 //Addition of the x & y co-ordinates will be stored here
 double aax = 0.0,aay = 0.0,aaz = 0.0;
 //Total of 

 double aatot = 0.0;
 
 double ax2 = 0.0,ay2 = 0.0,az2 = 0.0;
 double aatot2 = 0.0;
 double integrationofmagnitude = 0.0;
 double integrationoftot2 = 0.0;
 double iav = 0;
 
 FileOutputStream streamofdata;
 FileOutputStream calcafter30sec;
 
 Button b;

 int start = 0;
 private SensorManager sensorManager = null;
 
 	Timer timer;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a reference to a SensorManager
        
        
        try
        {
        	
        /*File file = new File("hardik.csv");
        streamofdata = new BufferedWriter(new FileWriter(file));
        streamofdata.append("a,b,c");
        streamofdata.close();
        File file1 = new File("calcafter30sec.csv");
        calcafter30sec = new BufferedWriter(new FileWriter(file1));
        calcafter30sec.append("1,2,3");
        calcafter30sec.close();
        */
        	File outputFile = new File("/mnt/sdcard/socalcalcount/streamofdata.csv");
        	File of1 = new File("/mnt/sdcard/socalcalcount/calcafter30sec.csv");
        	// now attach the OutputStream to the file object, instead of a String representation
        	streamofdata = new FileOutputStream(outputFile,true);
        	//String buffer = "ax,ay,az\n";
        	//streamofdata.write(buffer.getBytes());
        	//String buffer1 = "IAAx,IAAy,IAAz,IAAtot,IAAx2,IAAy2,IAAz2,IAAtot2,IAV,IAV2\n";
        	calcafter30sec = new FileOutputStream(of1,true);
        	//calcafter30sec.write(buffer1.getBytes());
        }
        catch(Exception e)
        {
        	Log.e(e.getMessage(), e.getMessage());
        }
        
        
			
        
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.main);
       
        // Capture accelerometer related view elements
        accelXValue = (TextView) findViewById(R.id.accel_x_value);
        accelYValue = (TextView) findViewById(R.id.accel_y_value);
        accelZValue = (TextView) findViewById(R.id.accel_z_value);
        
        
        // Capture orientation related view elements
        orientXValue = (TextView) findViewById(R.id.orient_x_value);
        orientYValue = (TextView) findViewById(R.id.orient_y_value);
        orientZValue = (TextView) findViewById(R.id.orient_z_value);
       
        // Initialize accelerometer related view elements
          accelXValue.setText("0.00");
          accelYValue.setText("0.00");
          accelZValue.setText("0.00");
       
        // Initialize orientation related view elements
          orientXValue.setText("0.00");
          orientYValue.setText("0.00");
          orientZValue.setText("0.00"); 
          
          b = (Button) findViewById(R.id.button1);
          
          b.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(start%2 == 0)
				{
					
		          timer = new Timer();
		          timer.schedule(new RemindTask(), 0, //initial delay
		              1 * 30000); //subsequent rate
		          b.setText("Stop");
		          start++;
				}
				else
				{
					b.setText("Start");
					try
					{
						
						streamofdata.close();
						calcafter30sec.close();
						System.exit(0);
					}
					catch(Exception e)
					{
						Log.e(e.getMessage(), e.getMessage());
					}
					start++;
				}
			}
          });
    
         
    }
   int i = 0;
 
    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent) {
     synchronized (this) {
      if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
       accelXValue.setText(Float.toString(sensorEvent.values[0]));
       accelYValue.setText(Float.toString(sensorEvent.values[1]));
       accelZValue.setText(Float.toString(sensorEvent.values[2]));
       double v1 = sensorEvent.values[0];
       double v2 = sensorEvent.values[1];
       double v3 = sensorEvent.values[2];
       
       
       try
       {
    	   	Calendar c = new GregorianCalendar();
    	   	
	   		String s = c.getTimeInMillis()+","+v1+","+v2+","+v3+"\n";
	   		streamofdata.write(s.getBytes());
	   		
		   //We have added the stuff here
		   aax += Math.abs(v1);
		   aay += Math.abs(v2);
		   aaz += Math.abs(v3);
		   //For IAV
		   iav += Math.sqrt(Math.pow(aax, 2) + Math.pow(aay, 2) + Math.pow(aay, 2));
		   
       }
       catch(Exception e)
       {
       	Log.e(e.getMessage(), e.getMessage());
       }
      }
      
      if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
       orientXValue.setText(Float.toString(sensorEvent.values[0]));
       orientYValue.setText(Float.toString(sensorEvent.values[1]));
       orientZValue.setText(Float.toString(sensorEvent.values[2]));       
      }
     }
    }
   
    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1) {
  // TODO Auto-generated method stub
  
    }
    
    class RemindTask extends TimerTask {
        int numWarningBeeps = 3;

        public void run() {
        	double iatot = aax+aay+aaz;
        	double iaax2 = Math.pow(aax,2);
        	double iaay2 = Math.pow(aay,2);
        	double iaaz2 = Math.pow(aaz, 2);
        	double iaa2tot = iaax2 + iaay2 + iaaz2;
        	double iav2 = Math.pow(iav, 2);
        	Calendar c = new GregorianCalendar();
        	
        	String s = c.getTimeInMillis()+","+aax+","+aay+","+aaz+","+iatot+","+iaax2+","+iaay2+","+iaaz2+","+iaa2tot+","+iav+","+iav2+"\n";
        	
        	try
        	{
        		calcafter30sec.write(s.getBytes());
        	}
        	catch(Exception e)
        	{
        		Log.e("Exception", e.getMessage());
        	}
        	aax = aay = aaz = 0.0;
        	iav = 0;
        	
    	}
        
    	
      }
   
    @Override
    protected void onResume() {
     super.onResume();
     // Register this class as a listener for the accelerometer sensor
     sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
     // ...and the orientation sensor
     sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    }
   
    @Override
    protected void onStop() {
     // Unregister the listener
    	sensorManager.unregisterListener(this);
     	super.onStop();
    } 
 
}