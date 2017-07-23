package mapper;

import java.util.List;

public class Tile {
	float x;
	float y;
	int g;
	int h;
	int score;
	Tile parent;
	public Tile(float x, float y){
		this.x = x; 
		this.y = y;
	}
	public void setParent(Tile t){
		parent = t;
	}
	public Tile getParent(){
		return this.parent;
	}
}
