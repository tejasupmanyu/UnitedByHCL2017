package ca.uwaterloo.PedoMap;

import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

class AccelSensorEventListener implements SensorEventListener {
	public static float [] rotation = new float[9];
	public static float [] inclination = new float[9];
	public static float [] gravity = new float[3];
	public static float [] orientation = new float[3];
	private static float [] points = new float[5];
	float SmoothFactorCompass = (float) 0.5;
	float SmoothThresholdCompass = (float) 30.0;
	float oldCompass = (float) 0.0;
	private int counter;
	private boolean full;
	public static float azm;
	public static float compassAzm;
	TextView heading;
	
	
	public AccelSensorEventListener (TextView view) {
		heading = view;
		full = false;
		counter = 0;
		azm = 0;
	}
	
	
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	//Where the magic happens
	public  void onSensorChanged(SensorEvent se) {
		if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
			
			gravity = se.values;
			SensorManager.getRotationMatrix(rotation, inclination, gravity, MagneticSensorEventListener.magnetic);
			SensorManager.getOrientation(rotation, orientation);
			
			heading.setText("Azmouth: " + orientation[0] + "\n"
							+ "Pitch: " + orientation[1] + "\n"
							+ "Yaw: "   + orientation[2] + "\n");  
			
			if (counter > points.length-1){
				full = true;
				counter = 0;
			}
				
			float newCompass = (float) Math.toDegrees(orientation[0]);
			
		
			if (orientation[0] < 0) {
				newCompass = (float) Math.toDegrees(Math.abs(orientation[0]));
			}
			else {
				newCompass = (float) ((float) 180 - Math.toDegrees(orientation[0]));
			}
			
			
			if (Math.abs(newCompass - oldCompass) < 180) {
			    if (Math.abs(newCompass - oldCompass) > SmoothThresholdCompass) {
			        oldCompass = newCompass;
			    }
			    else {
			        oldCompass = oldCompass + SmoothFactorCompass * (newCompass - oldCompass);
			    }
			}
			else {
			    if (360.0 - Math.abs(newCompass - oldCompass) > SmoothThresholdCompass) {
			        oldCompass = newCompass;
			    }
			    else {
			        if (oldCompass > newCompass) {
			            oldCompass = (oldCompass + SmoothFactorCompass * ((360 + newCompass - oldCompass) % 360) + 360) % 360;
			        } 
			        else {
			            oldCompass = (oldCompass - SmoothFactorCompass * ((360 - newCompass + oldCompass) % 360) + 360) % 360;
			        }
			    }
			}
			
			azm = (float) Math.toRadians(oldCompass);
			
			}
		}
	
	private float avg (float[] points){
		float total = 0;
		for (float point : points){
			total+= point;
		}
		return total/points.length;
	}
}