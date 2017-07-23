package mapper;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

public class MapListener implements IMapperListener{
	
	
	PointF position = new PointF(0,0);
	PointF originalPosition = new PointF(0,0);
	boolean setClear= false;
	
	

	public PointF curLoc(){
		return position;
	}
	public PointF startLock(){
		return originalPosition;
		
	}
	public boolean getSetClear(){
		return setClear;
	}
	public void setSetClear(boolean v){
		setClear = v;
	}
	
	@Override
	public void locationChanged(Mapper source, PointF loc) {
	
		source.setUserPoint(loc);
		
		position.x = loc.x;
		position.y = loc.y;
		originalPosition = loc;
		setClear = true;
		
		
		
		
		
	}
	
	public void updatelocation(Mapper source, PointF loc){
		source.setUserPoint(loc);
	}
	
	
	@Override
	public void DestinationChanged(Mapper source, PointF dest) {
		// TODO Auto-generated method stub
		
    }


}

	

		
		
