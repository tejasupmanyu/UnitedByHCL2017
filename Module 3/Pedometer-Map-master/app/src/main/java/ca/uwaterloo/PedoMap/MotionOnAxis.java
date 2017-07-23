package ca.uwaterloo.PedoMap;

import android.hardware.SensorEvent;


public class MotionOnAxis{
	public float point1, point2;
	public int state, frequency;
	public double slope;
	public int pollCounter;
	public float filteredValues;
	
	public MotionOnAxis(){
		pollCounter = 0;
		slope = 0.0;
		state = 0;
		frequency = 4;
	}
	
	//A method which checks if the motion in the given axis is "a lot". For z-axis the motion must be parabolic.
	//For the other axis, it simply checks to see if the magnitude of the motion was high.
	public boolean didChangeAlot(SensorEvent se, int axis){
		int n;
		if (axis == 0){
			n = 50;
		}else{
			n = 70;
		}
		filteredValues += (se.values[axis] - filteredValues)/n; 
		
		//If the x and y axis moved a lot, return true
		if (axis != 2){
			pollCounter++;
			if (pollCounter % frequency == 1){
				point1 = filteredValues;
			}
			if (pollCounter % frequency == 0){
				point2 = filteredValues;
				slope = (point1 - point2)/4;
			}
			
			if (pollCounter > 30000000){
				pollCounter = 0;
			}
			if (Math.abs(slope) > 0.0125){
				return true;
			}else{
				return false;
			}
		}
		
		pollCounter++;
		if (pollCounter % frequency == frequency/2) {
			point1 = filteredValues;
			slope = -1;
		}
		if (pollCounter % frequency == 0) {
			point2 = filteredValues;
			slope = (point2 - point1)/(2); 
			pollCounter = 0;
			
			//Below algorithm checks for sinosodal motion in z-axis
			float decreasing = (float)  .008;
			float increasing = (float) -.008;
			if (slope > decreasing ||  state > 1) {
				state++;
				if (slope < increasing || state > 2 ) {
					state++;
					if (slope > decreasing || state > 3) {
						state++;
						
						if (slope < increasing || state > 4) {
							state = 0;
							return true;
						}
					}
				}
			}
		}
		return false;
		
		
	}
	
}