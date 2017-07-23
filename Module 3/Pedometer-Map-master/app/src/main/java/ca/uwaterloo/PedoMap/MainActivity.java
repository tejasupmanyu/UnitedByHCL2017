package ca.uwaterloo.PedoMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.uwaterloo.sensortoy.LineGraphView;
import ca.waterloo.PedoMap.R;
import mapper.InterceptPoint;
import mapper.LineSegment;
import mapper.MapListener;
import mapper.MapLoader;
import mapper.Mapper;
import mapper.PedometerMap;


public class MainActivity extends Activity{
	static LineGraphView graph; 
	static LineGraphView graphFiltered;
	public static Mapper mv; 
	static PedometerMap map;
	static MapListener Pl = new MapListener();
	static String name;
	static float xdisp;
	static float ydisp;
	static float xStep, yStep;
	static float xDest, yDest;
	static boolean[][] boolMap;
	static boolean[][] wasHere;
	public static ArrayList<PointF> anchors = new ArrayList<PointF>();
	static LineSegment compassNeedle = new LineSegment(new PointF(0,0), new PointF(100,100));
	public static List<InterceptPoint> intercept = new ArrayList<InterceptPoint>();
	public static List<PointF> solution = new ArrayList<PointF>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	
		name = "/storage/emulated/0/Android/data/com.android.chrome/files";
		File myfile = new File(name);
		
		mv = new Mapper(getApplicationContext(), 1000, 730, 40, 30);
	    MapLoader load = new MapLoader();
	    map = new PedometerMap();
		try{
		map = load.loadMap(myfile, "labroom.svg");}catch(Exception e){}
		mv.setMap(map);
		
		registerForContextMenu(mv);
		
		anchors.add(new PointF((float)2.90, (float)18.49));
		anchors.add(new PointF((float)5.6, (float)18.5));
		anchors.add(new PointF((float)11.9, (float)18.51));
		anchors.add(new PointF((float)20, (float)18.505));
		anchors.add(new PointF((float)21, (float)19.5));
		anchors.add(new PointF((float)20, (float)6.2));		
		 
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public  void  onCreateContextMenu(ContextMenu  menu , View v, ContextMenuInfo  menuInfo) {
		super.onCreateContextMenu(menu , v, menuInfo);
		mv.onCreateContextMenu(menu , v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item) ||  mv.onContextItemSelected(item);
	}	

	static class AccelerometerSensorEventListener implements SensorEventListener {
		static TextView stepView, displacement, positionStatus;	//output textviews
		static int steps, stepsF;
		public static MotionOnAxis x,y,z;
		public static float [] rotation = new float[9];
		public static float [] inclination = new float[9];
		public static float [] gravity = new float[3];
		public static float [] magnetic = new float[3];
		public static float [] orientation = new float[3];
		public static float [] filter = new float[3];
		
		public AccelerometerSensorEventListener (TextView stpView, TextView displace, TextView positionStatusA) {
			stepView = stpView;
			displacement = displace;
			positionStatus = positionStatusA;
			steps = 0;
			stepsF = 0;
			x = new MotionOnAxis();
			y = new MotionOnAxis();
			z = new MotionOnAxis();
		}
		
		public void onAccuracyChanged(Sensor s, int i) {}
		
		//Where the magic happens
		public  void onSensorChanged(SensorEvent se) {
				
				if (!x.didChangeAlot(se, 0) && !y.didChangeAlot(se, 1) && z.didChangeAlot(se, 2)){
					displacementCal();
					steps++;
					stepView.setText("Steps: " + steps);
				}
				
				if (distanceBetween(mv.startPoint, mv.destPoint) < 1){
					positionStatus.setText("You have arrived!");     
				} else if (mv.startPoint != null && mv.destPoint != null){
					solution.clear();
					positionStatus.setText("");     
					recursivePath(mv.startPoint);
				}
				
				//graph accel data
				graph.addPoint(se.values);
				
				
				float n = 2500;		//low bypass offset
				
				//low bypass filter 
				filter[0] += (se.values[0] - filter[0])/n;
				filter[1] += (se.values[1] - filter[1])/n;
				filter[2] += (se.values[2] - filter[2])/n;
				
				graphFiltered.addPoint(filter);		//graph bypass data
	
				drawCompass();
			}
		
		//find displacement in X and Y axis
		public void displacementCal() {
			float angle;
			float factor = 0.5f;
			float azm = AccelSensorEventListener.azm;
			
			if ((azm < 1.5707963 ) && (azm > 0.001 )) {
				angle = (float) (azm + Math.PI) ;
				xdisp += Math.sin(angle);
				ydisp += Math.cos(angle);
				//mv.startPoint = new PointF ((float)(mv.startPoint.x + factor*Math.sin(angle)), (float)(mv.startPoint.y + factor*Math.cos(angle)));

			}
			if ((azm > 1.5707963 ) && (azm < 3.141592)) {
				angle = (float)Math.PI - azm;
				xdisp += Math.sin(angle);
				ydisp -= Math.cos(angle);
				//mv.startPoint = new PointF ((float)(mv.startPoint.x + factor*Math.sin(angle)), (float)(mv.startPoint.y - factor*Math.cos(angle)));
			}
			if ((azm < -0.001 ) && (azm > -1.5707963)) { 
				angle = Math.abs(azm);
				xdisp -= Math.sin(angle);
				ydisp += Math.cos(angle);
				//mv.startPoint = new PointF ((float)(mv.startPoint.x - factor*Math.sin(angle)), (float)(mv.startPoint.y + factor*Math.cos(angle)));

			}
			if ((azm < -1.5707963) && (azm > -3.141592)) {
				angle = (float)Math.PI - Math.abs(azm);
				xdisp -= Math.sin(angle);
				ydisp -= Math.cos(angle);
				//mv.startPoint = new PointF ((float)(mv.startPoint.x + factor*Math.cos(azm - pi/3 )), (float)(mv.startPoint.y + factor*Math.sin(azm - pi/3)));
			}
			mv.startPoint = new PointF ((float)(mv.startPoint.x - factor*Math.cos(azm)), (float)(mv.startPoint.y - factor*Math.sin(azm)));	
			displacement.setText("X displacement: " + xdisp + "\n" + "Y displacement: " + ydisp + "\n");
		}
		
		public void recursivePath(PointF startPoint){
			PointF currentPoint = startPoint;
			PointF nextPoint;
			ArrayList<PointF> path = new ArrayList<PointF>();
			ArrayList<PointF> availablePoints = (ArrayList<PointF>)anchors.clone();
			path.add(startPoint);
			while (!availablePoints.isEmpty()){
				currentPoint = path.get(path.size() - 1);
				float min = 999999;
				nextPoint = null;
				for (PointF anchor : anchors){
					if (mv.calculateIntersections(currentPoint, mv.destPoint).isEmpty()){
						path.add(mv.destPoint);
						solution = path;
						mv.setUserPath(solution);
						return;
					}
					intercept = mv.calculateIntersections(currentPoint, anchor);
					if (intercept.isEmpty() & !path.contains(anchor)){
						float currentMin = distanceBetween(anchor, mv.destPoint);
						if (currentMin < min){
							nextPoint = anchor;
							min = currentMin;
							availablePoints.remove(anchor);
						}
					}
				}
				path.add(nextPoint);	
			}
			
			path.add(currentPoint);
			solution = path;
			mv.setUserPath(solution);
		}
		
		public void drawCompass() {
			PointF CompassEnd = new PointF();
			PointF CompassStart = new PointF();
			
			CompassStart = mv.getUserPoint();
			CompassStart = mv.getStartPoint(); 
			
			float yFinal = (float)(CompassStart.y +  (Math.cos(AccelSensorEventListener.azm) * -3 ));
			float xFinal = (float)(CompassStart.x +  (Math.sin(AccelSensorEventListener.azm) * -3 ));
			
			CompassEnd.set(xFinal, yFinal);
			List<PointF> CompassBearing = new ArrayList<PointF>();
			CompassBearing.add(CompassStart);
			CompassBearing.add(	CompassEnd);
			
			compassNeedle = new LineSegment(CompassStart, CompassEnd);
			
			mv.setCompass(compassNeedle);
			mv.setCompNeedle(CompassBearing);
		}
		
		//calculate distanceBetween user location and final location
		public float distanceBetween (PointF p1, PointF p2){
			if (p1 == null || p2 == null){
				return 0f;
			}
			float x1 = p1.x;
			float x2 = p2.x;
			float y1 = p1.y;
			float y2 = p2.y;
			return (float)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)));
		}
		
		//reset max values, using clear button
		public static void reset() {
			steps = 0;
			stepsF = 0;
			xdisp = 0;
			ydisp = 0;
			stepView.setText("Steps: " +steps);
			xdisp = 0;
			ydisp = 0;
		}
	}
	
	public void clear (View view) {
		//"clear" button calls this function, resets all event listeners and graph 
		LightSensorEventListener.reset();
		AccelerometerSensorEventListener.reset();
        graph.purge();
        graphFiltered.purge();
	}
	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			//declare all Textviews 
			TextView view  = new TextView(rootView.getContext());
			TextView stepView = new TextView(rootView.getContext());
			TextView lightSen = new TextView(rootView.getContext());
			TextView displacement = new TextView(rootView.getContext());
			TextView heading = new TextView(rootView.getContext());
			TextView magX = new TextView(rootView.getContext());
			TextView magY = new TextView(rootView.getContext());
			TextView magZ = new TextView(rootView.getContext());
			TextView positionStatus  =  new TextView(rootView.getContext());
			
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);	//set linearlayout to layout
		    
			//add textviews, graphs, and map to layout
			layout.addView(mv);
			mv.setVisibility(View.VISIBLE);
		
			layout.addView(positionStatus);
			layout.addView(stepView);
			layout.addView(displacement);
			layout.addView(lightSen);
			layout.addView(magX);
			layout.addView(magY);
			layout.addView(magZ);
			layout.addView(heading);
			layout.addView(view);
			
			//construct graph and add to view
			graph = new LineGraphView(rootView.getContext(),
					100,
					Arrays.asList("x", "y", "z"));
			layout.addView(graph);
			graph.setVisibility(View.VISIBLE); 
			
			graphFiltered = new LineGraphView(rootView.getContext(),
					100,
					Arrays.asList("x", "y", "z"));
			layout.addView(graphFiltered);
			graphFiltered.setVisibility(View.VISIBLE); 

			//declare sensor manager 
			SensorManager sensorManager = (SensorManager)
					rootView.getContext().getSystemService(SENSOR_SERVICE);
			
			//Declare all sensors 
			Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			Sensor accel2Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			
			//declare all sensor event listeners 
			SensorEventListener light = new LightSensorEventListener(lightSen);
			SensorEventListener accel = new AccelerometerSensorEventListener(stepView, displacement, positionStatus);
			SensorEventListener accel2 = new AccelSensorEventListener(view);
			SensorEventListener mag = new MagneticSensorEventListener(magX, magY, magZ);
			
			//register all sensor event listeners 
			sensorManager.registerListener(light, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(accel, accelSensor, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(accel2, accel2Sensor, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(mag, magSensor, SensorManager.SENSOR_DELAY_GAME);	
			
			return rootView;
		}
	}
}
