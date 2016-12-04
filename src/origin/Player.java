package origin;
import java.util.*;

public class Player extends Location{
	//used to store the current max score of each game
	public static int maxScore = -100000000;
	
	//used to store the caught pokemons and visited stations
	//get element from hash map and hash set is O(1),can increase performance 
	private HashMap<Location,Pokemon> pokemonCaught = null;
	private HashMap<Location,Station> stationVisited = null;
	//store distinct types of pokemons caught
	private HashSet<String> caughtTypes = null;
	
	
	//record the current max combat power of all the pokemon caught
	private int maxCombatPowerCaught = 0;
	
	//store the path visited
	LinkedList<Location> pathVisited = null;
	
	private int numPokeBalls;
	
	//store the number of times that each cell in the map has been visited
	HashSet<Location> hasCome= null;
	
	public Player(){
		super(-1,-1);
		pokemonCaught = new HashMap<>();
		stationVisited = new HashMap<>();
		caughtTypes = new HashSet<>();
		pathVisited = new LinkedList<>();
		hasCome = new HashSet<>();;
	}
	
	public Player(Player player)throws Exception{
		this();
		super.reset(player.getRow(), player.getColumn());
		this.pokemonCaught.putAll(player.pokemonCaught);
		this.stationVisited.putAll(player.stationVisited);
		this.caughtTypes.addAll(player.caughtTypes);
		this.pathVisited.addAll(player.pathVisited);
		this.numPokeBalls = player.numPokeBalls;
		this.maxCombatPowerCaught = player.maxCombatPowerCaught;
		this.hasCome.addAll(player.hasCome);
		
	}
	
	//calculate the score of the player according to the formula
	public int calculateScore(){
		return numPokeBalls + 5*pokemonCaught.size()+10*caughtTypes.size() + maxCombatPowerCaught - pathVisited.size()+1;
	}
	
	public int getMaxCombatPower(){
		return this.maxCombatPowerCaught;
	}
	public void setMaxCombatPower(int a){
		this.maxCombatPowerCaught = a;
	}
	
	public HashSet<Location> getHasCome(){
		return this.hasCome;
	}

	
	public HashMap<Location,Pokemon> getPokemonCaught(){
		return this.pokemonCaught;
	}
	
	public HashMap<Location,Station> getStationVisited(){
		return this.stationVisited;
	}
	
	public HashSet<String> getTypeCaught(){
		return this.caughtTypes;
	}
	
	
	public LinkedList<Location> getPathVisited(){
		return pathVisited;
	}
	
	public int getNumPokeBalls(){
		return this.numPokeBalls;
	}
	
	public void setNumPokeBalls(int num){
		this.numPokeBalls = num;
	}
	
	//check whether a given pokemon is caught
	public boolean hasCaught(Pokemon pokemon){
		if(pokemonCaught.containsKey(pokemon.getLocation()))
			return true;
		return false;
	}
	//check whether a given supply station is visited
	public boolean hasVisited(Station station){
		if(stationVisited.containsKey(station.getLocation()))
			return true;
		return false;
	}
}
