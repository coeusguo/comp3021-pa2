package origin;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 Used to represent station/
 
The same reason as Pokemon class that to be defined under Location class
 */
public class Station extends Location{
	private int numBalls;
	private Image image;
	private ImageView iv;
	private boolean active = true;
	private Runnable task;
	public Station(int row,int column,int numBalls){
		super(row,column);
		this.numBalls = numBalls;
		String url = new File("icons/ball_ani.gif").toURI().toString();
		image = new Image(url);
		iv = new ImageView(image);
	}
	
	public int getNumBalls(){
		return this.numBalls;
	}
	
	public Location getLocation(){
		Location currentLocation = new Location(getRow(),getColumn());
		return currentLocation;
	}
	
	public ImageView getImageView(){
		return this.iv;
	}
	
	public void setActive(boolean option){
		this.active = option;
	}
	
	public boolean isActive(){
		return this.active;
	}
	
	public void setTask(Runnable r){
		this.task = r;
	}
	
	public Runnable getTask(){
		return this.task;
	}
}
