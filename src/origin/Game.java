package origin;
import java.io.File;

import java.util.Scanner;

import javafx.application.Application;
import ui.Pokemon_PA2;

//import java.util.Iterator;
//import java.io.PrintWriter;


public class Game {
	private File inputFile;
	//private File outputFile;
	private Map map = null;
	private Player player= null;
	

	
	
	public Game(String inputFile,String outputFile)throws Exception{
			this.inputFile = new File(inputFile);
			//this.outputFile = new File(outputFile);
			//initialize the map
			initialize();
	}
	
	public Game(String inputFile)throws Exception{
		this.inputFile = new File(inputFile);
		initialize();
	}
	
	private void initialize() throws Exception{
		Scanner input = new Scanner(inputFile);
		
		// Read the first of the input file
		String line = input.nextLine();
		int M = Integer.parseInt(line.split(" ")[0]);
		int N = Integer.parseInt(line.split(" ")[1]);
		
		//initialize the player
		this.player = new Player();
		// define the map
		this.map = new Map(M,N);
		
		// Read the following M lines of the Map
		for (int i = 0; i < M; i++) {
			line = input.nextLine();
			for(int j = 0;j < N; j++){
				switch(line.charAt(j)){
					case ' ':
						//empty,instantiate an Empty object
						Empty e = new Empty(i,j);
						this.map.setMapElement(e);
						this.map.addRoad(e);
						break;
					case '#':
						//wall,Wall object
						Wall wall = new Wall(i,j);
						this.map.setMapElement(wall);
						this.map.addWall(wall);
						break;
					case 'B':
						//start point of the map
						player.reset(i, j);
						//record the start point,which is convenient for further use
						this.map.setMapElement(player);
						break;
					case 'D':
						//destination
						Location exit = new Destination(i,j);
						this.map.setMapElement(exit);
						this.map.setExit(exit);
						break;
					case 'S':
					case 'P'://set pokemons and stations below
						break;
					default: 
						throw new Exception("Errow eccured in map initialization part");
				}
			}
			// Read the map line by line
		}
		
		
		
		while(input.hasNext()){
			String content[] = input.nextLine().split(",");
			for(int i = 0;i < content.length;i++){
				content[i].trim();
			}
			/*
			 * if length of the content of one line is larger than 3,the content of the line contains the information of pokemon
			 * otherwise,it contains the information of station
			 */
			if(content.length > 3){
				//e.g. content = { <2 , 10> , Spearow , fly , 120 , 2 }

				Pokemon pokemon = new Pokemon(content[2]/*name*/,content[3]/*type*/,Integer.parseInt(content[4].trim())/*combat power*/,Integer.parseInt(content[5].trim())/*required pokeballs*/,Integer.parseInt(content[0].substring(1))/*row, remove '<'*/,Integer.parseInt(content[1].substring(0, content[1].length()-1))/*column,remove '>'*/);
				
				this.map.addPokemon(pokemon);
			}
			//station`s content e.g. { <2 , 10> , 3 },whose length is not larger than 3
			else{
				Station station = new Station(Integer.parseInt(content[0].substring(1).trim())/*row, remove '<' */,Integer.parseInt(content[1].substring(0, content[1].length()-1).trim())/*column, remove '>'*/,Integer.parseInt(content[2].trim())/*number of pokeballs*/);
				this.map.addStation(station);
			}
			
			
		}
		//set pokemons in the map
		for(Location i:this.map.getPokemons())
			this.map.setMapElement(i);
			
		//set stations in the map
		for(Location i:this.map.getStaion())
			this.map.setMapElement(i);

		
		// to do
		// Find the number of stations and pokemons in the map 
		
		input.close();
	}
	
	//when a player reach the destination,output the result to the output file
	/*private void outputResult(Player player)throws Exception{
		try(PrintWriter output = new PrintWriter(outputFile)){
			//print max score
			output.println(Player.maxScore);
			//print NB:NP:NS:MCP
			output.println(player.getNumPokeBalls() + ":" + player.getPokemonCaught().size() + ":" + player.getTypeCaught().size() + ":" + player.getMaxCombatPower());
			
			//print the path
			Iterator<Location> ite =player.getPathVisited().listIterator();
			output.print(ite.next().toString());
			while(ite.hasNext()){
				output.print("->" + ite.next().toString());
			}
		}
	}*/
	
	//call findPath method
	/*public void findPath()throws Exception{
		findPath(player,1);
		//1 means start from recursion level 1
		//will explain "null" later
	}*/
	
	/*bugs here
	private void findPath(Player player,int level)throws Exception{
		int currentRow = player.getRow();
		int currentColumn = player.getColumn();
		
		//check whether the current location is out of the map
		if(this.map.isOutOfBound(currentRow, currentColumn))
			return;
		
		
		//add current location to the visited path of the player
		Location element = this.map.getMapElement(currentRow, currentColumn);
		player.getPathVisited().add(new Location(currentRow,currentColumn));
		*/
	
		/*
		 The recursion level don`t need to continue to deeper than 200 levels.According to the assignment description,
		 the empty cell is less than 30% of the map and the map is no larger than 10*30,which means the maximum number of 
		 empty cell is only 90,200 level is enough to go through the entire map twice. 
		 */
	/*
		if(level > 200)
			return;
		
		
		//if the current location is wall,the path is invalid
		if(element instanceof Wall){
			return;
		}	
		
		//add current location to the visited list
		player.getHasCome().add(new Location(currentRow,currentColumn));
		
		//check whether player reach destination
		if(element instanceof Destination){
			if(player.calculateScore()>Player.maxScore){
				Player.maxScore = player.calculateScore();
				//if current solution generate a higher score than current max score,output the new result
				outputResult(player);
			}
			return;
		}
		*/
		
		/*
		 Algorithm:
		 player don`t need to go back unless find a new station or catch a new pokemon
		 the 'refresh' variable is a indicator to indicate whether a new pokemon caught or new station visited
		 in current location
		 */
	/*
		if(element instanceof Station){
			Station station = (Station)element;
			//check whether current station is visited
			if(!player.hasVisited(station)){
				//update player`s pokeballs and visit list of station
				player.getStationVisited().put(station.getLocation(), station);
				player.setNumPokeBalls(player.getNumPokeBalls()+station.getNumBalls());
				//reset the visited list,which means player can go to the place that has visited before
				player.getHasCome().clear();
			}
		}
		
		if(element instanceof Pokemon){
			Pokemon pokemon = (Pokemon)element;
			//check whether current pokemon is caught
			if(!player.hasCaught(pokemon)){
				//check whether player has enough pokeballs to catch this pokemon
				if(player.getNumPokeBalls() >= pokemon.getNumRequiredBalls()){
					player.getPokemonCaught().put(pokemon.getLocation(), pokemon);
					player.setNumPokeBalls(player.getNumPokeBalls()-pokemon.getNumRequiredBalls());
					player.getTypeCaught().add(pokemon.getType());
					if(pokemon.getCombatPower() > player.getMaxCombatPower())
						player.setMaxCombatPower(pokemon.getCombatPower());
					//reset the visited list,which means player can go to the place that has visited before
					player.getHasCome().clear();
				}
			}
		}*/
		
		/*
		 The part below will call the findPath method recursively
		 Increase the recursion level by 1 
		 */
		
		/*
		level++;
				
		
		Location newLocationLeft = new Location(currentRow,currentColumn-1);
		//check whether the player has visited the left side
		if(!player.getHasCome().contains(newLocationLeft)){
			Player player1 = new Player(player);
			player1.setCurrentLocation(newLocationLeft);
			findPath(player1,level);
		}
		
		
		Location newLocationUp = new Location(currentRow-1,currentColumn);
		//check whether the player has visited the up side
		if(!player.getHasCome().contains(newLocationUp)){
			Player player2 = new Player(player);
			player2.setCurrentLocation(newLocationUp);
			findPath(player2,level);
		}
		
		
		Location newLocationRight = new Location(currentRow,currentColumn+1);
		//check whether the player has visited the right side
		if(!player.getHasCome().contains(newLocationRight)){
			Player player3 = new Player(player);
			player3.setCurrentLocation(newLocationRight);
			findPath(player3,level);
			
		}
		
		
		Location newLocationDown = new Location(currentRow+1,currentColumn);
		//check whether the player has visited the down side
		if(!player.getHasCome().contains(newLocationDown)){
			Player player4 = new Player(player);
			player4.setCurrentLocation(newLocationDown);
			findPath(player4,level);
		}
		
		//if refresh = true,the player has caught a new pokemon or visited a new station in current location
	}
	*/
	//if argument is not empty,then set input or output file to specified file
	public void setInputFile(String file)throws Exception{
		this.inputFile = new File(file);
	}
	/*
	public void setOutputFile(String file)throws Exception{
		this.outputFile = new File(file);
	}*/
	
	public Map getMap(){
		return this.map;
	}
	
	public Player getPlayer(){
		return this.player;
	}

	/*
	public static void main(String[] args) throws Exception{
		Game game = new Game("./test3.txt","./output3.txt");
		if (args.length > 0) 
			game.setInputFile(args[0]);
	
		if (args.length > 1) 
			game.setOutputFile(args[1]);
		
		game.findPath();
		
		System.out.println("done!");
		// TO DO 
		// Read the configures of the map and pokemons from the file inputFile
		// and output the results to the file outputFile
	}
	*/
	public static void main(String[] args) throws Exception{
		
		Application.launch(Pokemon_PA2.class, args);
	}
}
