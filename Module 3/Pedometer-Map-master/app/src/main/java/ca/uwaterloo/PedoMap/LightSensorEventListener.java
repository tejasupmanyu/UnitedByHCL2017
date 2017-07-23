package ca.uwaterloo.PedoMap;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

class LightSensorEventListener implements SensorEventListener {		
	TextView outputLight;	//output textview
	static double maxLight;		//store abs of light sensor 
	
	public LightSensorEventListener(TextView outputView){
		outputLight = outputView;	//outputLight to constructor 
	}

	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		if (se.sensor.getType() == Sensor.TYPE_LIGHT) {
			if (Math.abs(se.values[0]) > maxLight) {
				//check if abs is greater than max value, if yes set new max value 
				maxLight = Math.abs(se.values[0]);
			}
			
			//display sensor data and max sensor value (formated) 
			outputLight.setText("Light Sensor: " + se.values[0] + "\n" + "Max Light Sensor: " + maxLight + "\n");
		}
	}
	
	public static void reset() {
		//reset maxvalue with "clear" button
		maxLight = 0;
	}
}
