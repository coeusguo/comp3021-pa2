package origin;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ui.PokemonList;

/*
 This class contains all information of Pokemon.
 
 Defined as a subclass of Location class,together with other elements,such as station,wall,emptycell and destination,
 will be stored as the supertype 'Location' in the map
 
 */

public class Pokemon extends Location{
	private String name;
	private String type;
	private int combatPower;
	private int numRequiredBalls;//number of pokeballs required to catch this pokemon
	private Image image;
	private ImageView iv;
	private Runnable r;
	private Thread t;
	private boolean caught = false;
	//indicate whether pokemon escape because player don`t have enough pokeballs
	private boolean onFlee = false;
	
	public Pokemon(String name,String type,int combatPower,int numRequiredBalls,int row,int column){
		super(row,column);
		this.name = name.trim();
		this.type = type;
		this.combatPower = combatPower;
		this.numRequiredBalls = numRequiredBalls;
		int id = PokemonList.getIdOfFromName(name.trim());
		String url = new File("icons/" + id + ".png").toURI().toString();
		image = new Image(url);
		iv = new ImageView(image);
	}
	
	//return the location of the pokemon
	public Location getLocation(){
		Location currentLocation = new Location(getRow(),getColumn());
		return currentLocation;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.type;
	}
	
	public int getNumRequiredBalls(){
		return this.numRequiredBalls;
	}
	
	public int getCombatPower(){
		return this.combatPower;
	}

	public ImageView getImageView(){
		return this.iv;
	}
	
	public void setTask(Runnable r){
		this.r = r;
	}
	
	public Runnable getTask(){
		return this.r;
	}
	
	public void setThread(Thread t){
		this.t = t;
	}
	
	public Thread getThread(){
		return this.t;
	}
	
	public void caught(){
		caught = true;
	}
	public boolean isCaught(){
		return caught;
	}

	public boolean isOnFlee(){
		return onFlee;
	}
	
	public void setFlee(boolean value){
		onFlee = value;
	}
}
