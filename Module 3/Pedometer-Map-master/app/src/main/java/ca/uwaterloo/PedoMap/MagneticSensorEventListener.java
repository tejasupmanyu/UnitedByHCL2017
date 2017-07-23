package ca.uwaterloo.PedoMap;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import ca.uwaterloo.PedoMap.MainActivity.AccelerometerSensorEventListener;

class MagneticSensorEventListener implements SensorEventListener {
	TextView magX, magY, magZ;		//output textviews
	static double maxMagX, maxMagY, maxMagZ;		//store abs of magnetic sensor for each axis
	public static float [] magnetic = new float[3];
	
	public MagneticSensorEventListener(TextView xAxis, TextView yAxis, TextView zAxis) {
		magX = xAxis;		//link output textviews with constructors
		magY = yAxis;
		magZ = zAxis;
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {
		if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			
			magnetic = se.values;
			
			AccelerometerSensorEventListener.magnetic = se.values;
			//display magnetic sensor data and max sensor values(formated)
			magX.setText("Magnetic X-Axis: " + se.values[0] ); //+ "Max X: " + maxMagX);
			magY.setText("Magnetic Y-Axis: " + se.values[1] );//+ "Max Y: " + maxMagY);
		    magZ.setText("Magnetic Z-Axis: " + se.values[2] ); //+ "Max Z: " + maxMagZ + "\n");
		}
	}
	
}
