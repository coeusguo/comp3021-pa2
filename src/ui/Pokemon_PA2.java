package ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import origin.Destination;
import origin.Empty;
import origin.Game;
import origin.Location;
import origin.Map;
import origin.Player;
import origin.Pokemon;
import origin.Station;
import origin.Wall;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Pokemon_PA2 extends Application{
	private int score = 0;
	private int caught = 0;
	private int numBalls = 0;
	private Text currentScore = new Text("Current score:" + score);
	private Text pokemonCaught = new Text("# of Pokemons caught:" + caught);
	private Text numballs = new Text("# of Pokeballs owened:" + numBalls);
	private Text message = new Text(""); 
	private Group gameScreen;
	private Game game;
	private Player player;
	private int maxWidth;
	private int maxHeight;
	public static final int stepSize = 40;
	private static final String front = new File("icons/front.png").toURI().toString();
	private static final String back = new File("icons/back.png").toURI().toString();
	private static final String left = new File("icons/left.png").toURI().toString();
	private static final String right = new File("icons/right.png").toURI().toString();
	private static final String tree = new File("icons/tree.png").toURI().toString();
	private ImageView avatar;
	private Image avatarImage;
	private Button pause;
	private Button resume;
	boolean goUp, goDown, goRight, goLeft;
	double currentPosx;
	double currentPosy;
	private ArrayList<Thread> threads;
	protected boolean stop = false;
	//indicate whether the game has finished
	private boolean finish = false;
	//indicate whether the game is paused
	private boolean gamePause = false;
	@Override
	public void start(Stage primaryStage) throws Exception {
		game = new Game("sampleIn.txt");
		player = game.getPlayer();
		Map map = game.getMap();
		ArrayList<Integer> xy = game.getMap().getMapSize();
		maxWidth = xy.get(0) * stepSize;
		maxHeight = xy.get(1) * stepSize;
		
		//set avatar to start location
		currentPosx = player.getColumn()*stepSize;
		currentPosy = player.getRow()*stepSize;
		
		avatarImage = new Image(front);
		avatar = new ImageView(avatarImage);
		//set the avatar size
		avatar.setFitHeight(stepSize);
		avatar.setFitWidth(stepSize);
		avatar.setPreserveRatio(true);
		avatar.relocate(currentPosx, currentPosy);
		

		gameScreen = new Group();
		gameScreen.getChildren().add(avatar);
		
		threads = new ArrayList<>();
		//add trees
		ArrayList<Wall> trees = map.getWall();
		for(Wall wall :trees){
			ImageView treeImage = new ImageView(new Image(tree));
			treeImage.setFitHeight(stepSize);
			treeImage.setFitWidth(stepSize);
			treeImage.setPreserveRatio(true);
			int x = wall.getColumn()*stepSize;
			int y = wall.getRow()*stepSize;
			treeImage.relocate(x, y);
			gameScreen.getChildren().add(treeImage);
		}
		
		//add pokeballs
		ArrayList<Station> stations = map.getStaion();
		for(Station s:stations){
			ImageView sView = s.getImageView();
			sView.setFitHeight(stepSize);
			sView.setFitWidth(stepSize);
			sView.setPreserveRatio(true);
			int x = s.getColumn()*stepSize;
			int y = s.getRow()*stepSize;
			sView.relocate(x, y);
			gameScreen.getChildren().add(sView);
		}
		
		//add pokemon
		ArrayList<Pokemon> pokemons = map.getPokemons();
		for(Pokemon p:pokemons){
			ImageView pView = p.getImageView();
			pView.setFitHeight(stepSize);
			pView.setFitWidth(stepSize);
			pView.setPreserveRatio(true);
			int x = p.getColumn()*stepSize;
			int y = p.getRow()*stepSize;
			pView.relocate(x, y);
			gameScreen.getChildren().add(pView);
			Moveable r = new Moveable(map,p.getRow(),p.getColumn());
			Thread t = new Thread(r);
			p.setTask(r);
			p.setThread(t);
			t.setDaemon(true);
			threads.add(t);
		}
		
		
		//add exit
		String url = new File("icons/exit.png").toURI().toString();
		Image exit = new Image(url);
		ImageView eView = new ImageView(exit);
		eView.setFitHeight(stepSize);
		eView.setFitWidth(stepSize);
		eView.setPreserveRatio(true);
		int x = map.getExit().getColumn()*stepSize;
		int y = map.getExit().getRow()*stepSize;
		eView.relocate(x, y);
		gameScreen.getChildren().add(eView);		
		HBox hbox = new HBox(10);
		VBox vbox = new VBox(10);
		
		HBox buttonField = new HBox();
		resume = new Button("Resume");
		resume.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gamePause = false;
			}
		});
		pause = new Button("Pause");
		pause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gamePause = true;
			}
		});
		buttonField.getChildren().addAll(resume,pause);
		vbox.getChildren().addAll(currentScore,pokemonCaught,numballs,message,buttonField);
		hbox.getChildren().addAll(gameScreen,vbox);
		Scene scene = new Scene(hbox,maxWidth+200,maxHeight);
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				int currRow = (int)(avatar.getLayoutY()/stepSize);
				int currColumn = (int)(avatar.getLayoutX()/stepSize);
				if((!finish) && (!gamePause)){
					switch (event.getCode()) {
					case UP:
					
						goUp = true;
						if(currRow != 0 && map.getMapElement(currRow-1, currColumn) instanceof Wall){
							goUp = false;
						}
						avatar.setImage(new Image(back));
					
						break;
					case DOWN:
				
						goDown = true;
						if(currRow != maxHeight/stepSize - 1 && map.getMapElement(currRow + 1, currColumn) instanceof Wall){
							goDown = false;
						}
						avatar.setImage(new Image(front));
						break;
					case LEFT:
				
						goLeft = true;
						if(currColumn != 0 && map.getMapElement(currRow, currColumn-1) instanceof Wall){
							goLeft = false;
						}
						avatar.setImage(new Image(left));
						break;
					case RIGHT:
				
						goRight = true;
						if(currColumn != maxWidth/stepSize - 1 && map.getMapElement(currRow, currColumn + 1) instanceof Wall){
							goRight = false;
						}
						avatar.setImage(new Image(right));
						break;
					default:
						break;
					}
				}
			}
		});
		
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					goUp = false;
					break;
				case DOWN:
					goDown = false;
					break;
				case LEFT:
					goLeft = false;
					break;
				case RIGHT:
					goRight = false;
					break;
				default:
					break;
				}
				stop = false;
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (stop)
					return;

				int dx = 0, dy = 0;

				if (goUp) {
					

					dy -= (stepSize);
				} else if (goDown) {
					

					dy += (stepSize);
				} else if (goRight) {
					

					dx += (stepSize);
				} else if (goLeft) {
					

					dx -= (stepSize);
				} else {
					// no key was pressed return
					return;
				}
				moveAvatarBy(dx, dy);
			}
		};
		// start the timer
		timer.start();
		
		for(Thread t: threads)
			t.start();
		
	}
	private void moveAvatarBy(int dx,int dy){
		final double cx = avatar.getBoundsInLocal().getWidth() / 2;
		final double cy = avatar.getBoundsInLocal().getHeight() / 2;
		double x = cx + avatar.getLayoutX() + dx;
		double y = cy + avatar.getLayoutY() + dy;
		moveAvatar(x, y);
	}

	private void moveAvatar(double x, double y) {
		final double cx = avatar.getBoundsInLocal().getWidth() / 2;
		final double cy = avatar.getBoundsInLocal().getHeight() / 2;
		int row,column;
		if (x - cx >= 0 && x + cx <= maxWidth && y - cy >= 0 && y + cy <= maxHeight) {
            // relocate ImageView avatar
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					avatar.relocate(x - cx, y - cy);
				}
				
			});
			score -- ;
			Platform.runLater(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					currentScore.setText("Current score:" + score);
					message.setText("");
				}
				
			});

			//update position
			currentPosx = x - cx;
			currentPosy = y - cy;
			stop = true;
			row = (int)(currentPosy/stepSize);
			column = (int)(currentPosx/stepSize);
			Location element = game.getMap().getMapElement(row,column);
			
			if(element instanceof Empty){
				element.reset(player.getRow(), player.getColumn());
				player.reset(row, column);
				game.getMap().setMapElement(element);
				game.getMap().setMapElement(player);
			}
			
			if(element instanceof Pokemon){
				Pokemon p  = (Pokemon)element;
				if(player.getPokemonCaught().containsValue(p)){
					Empty e = new Empty(p.getRow(),p.getColumn());
					game.getMap().setMapElement(e);
					game.getMap().getRoad().add(e);
					
					p.getImageView().setVisible(false);
				}else if(!p.isOnFlee()){
					catchPokemon(p);
					p.getThread().interrupt();
					Platform.runLater(new Runnable(){
						@Override
						public void run() {
							p.getImageView().setVisible(false);
						}
						
					});
					threads.remove(p.getThread());
				}
			}
			if(element instanceof Station){
				Station s = (Station)element;
				getPokeball(s);
			}
			if(element instanceof Destination){
				for(Thread t:threads){
					t.interrupt();
				}
				finish = true;
				gamePause = true;
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						message.setText("End Game!");
						message.setFill(Color.GREEN);
						pause.setDisable(true);
						resume.setDisable(true);
					}
					
				});	
			}
			
		}
		

	}
	
	void catchPokemon(Pokemon p){
		int balls = player.getNumPokeBalls();
		if(balls >= p.getNumRequiredBalls()){
			//stop the corresponding thread
			Moveable task = (Moveable)p.getTask();
			
			//player got the pokemon
			task.got();
			p.getImageView().setVisible(false);
			
			player.setNumPokeBalls(balls - p.getNumRequiredBalls());
			player.getPokemonCaught().put(p.getLocation(), p);
			caught ++;
			score += 5;
			numBalls -= p.getNumRequiredBalls();
			if(player.getMaxCombatPower() < p.getCombatPower()){
				score += p.getCombatPower() - player.getMaxCombatPower();
				player.setMaxCombatPower(p.getCombatPower());
			}
			if(!player.getTypeCaught().contains(p.getType())){
				player.getTypeCaught().add(p.getType());
				score += 10;
			}
			Platform.runLater(new Runnable() {
				@Override 
				public void run() {
					// update the labels
					numballs.setText("# of Pokeballs owened:" + numBalls);
					pokemonCaught.setText("# of Pokemons caught:" + caught);
					currentScore.setText("Current score:" + score);
					message.setText("Pokemon Caught!");
					message.setFill(Color.GREEN);
					p.getImageView().setVisible(false);
				}
			});

			Empty e = new Empty(p.getRow(),p.getColumn());
			game.getMap().setMapElement(e);
			game.getMap().addRoad(e);
			
			
		}else{
			Platform.runLater(new Runnable() {
				@Override 
				public void run() {
					// update the labels
					p.getImageView().setVisible(false);
					if(p.isOnFlee())
						message.setText("Not Enough Pokeballs!");
					message.setFill(Color.RED);
				}
			});
			p.setFlee(true);
		}
	}
	
	private void getPokeball(Station s){
		if(s.isActive()){
			int balls = s.getNumBalls();
			player.setNumPokeBalls(player.getNumPokeBalls() + balls);
			s.setActive(false);
			Moveable m = new Moveable(game.getMap(),s.getRow(),s.getColumn());
			s.setTask(m);
			m.start();
			threads.add(m.t);
			numBalls += balls;
			score += balls;
			Platform.runLater(new Runnable() {
				@Override 
				public void run() {
					// update the labels
					currentScore.setText("Current score:" + score);
					numballs.setText("# of Pokeballs owened:" + numBalls);
				}
			});
			
		}
	}
	
	
	class Moveable implements Runnable{
		public Thread t;
		private Map map;
		private Location element;
		private boolean isGot = false;
		private Random random = new Random();
		public Moveable(Map map,int row,int column){
			this.map = map;
			this.element = map.getMapElement(row, column);
		}
		
		public void got(){
			this.isGot = true;
		}
		
		public void run(){
			if(element instanceof Pokemon){
				Pokemon p = (Pokemon)element;
				while(!isGot){
					//get elements current location
					int currRow = element.getRow();
					int currColumn = element.getColumn();
					
					if(p.getImageView().isVisible()){
						//get the sleep time in milliseconds
						int	wait = (random.nextInt(2) + 1) * 1000;
			
						try {
							Thread.sleep(wait);
							setNewLocation(currRow,currColumn);
						} catch (InterruptedException e) {

						}
				
					}else{
						int	wait = (random.nextInt(2) + 3) * 1000;
						try {
							setNewLocation(currRow,currColumn);
							Thread.sleep(wait);
							p.setFlee(false);
							if(!isGot)
								p.getImageView().setVisible(true);
						} catch (InterruptedException e) {
						}
					}
				}
			}else{
				Station s = (Station)element;
				s.getImageView().setVisible(false);
				int currRow = s.getRow();
				int currColumn = s.getColumn();
				int wait = (random.nextInt(6) + 5) * 1000;
				try {
					Thread.sleep(wait);
					setNewLocation(currRow,currColumn);

				} catch (InterruptedException e) {
				}
				while(gamePause){
					try {
						Thread.sleep(wait);
					} catch (InterruptedException e) {
					}
				}
				if(!gamePause)
					s.getImageView().setVisible(true);
				s.setActive(true);
				threads.remove(this.t);
			}
			
			
		}
		
		public void start(){
			if(t == null){
				t = new Thread(this);
				t.setDaemon(true);
				t.start();
			}
		}
		
		private Lock lock = new ReentrantLock();
		private Condition condition = lock.newCondition();
		private int mutex = 0;
		
		//critical session
		private void setNewLocation(int currRow,int currColumn) throws InterruptedException{
			lock.lock();
			try{
				while(mutex != 0){
					try{
						condition.await();
					}catch(Exception e){}
				}
			}finally{
				mutex ++ ;
				if(element instanceof Pokemon){
					int row;
					int column;
			
					//use 0 and 1 to represent up(00)/down(01)/right(10)/right(11)
					int hDirection;
					int vDirection;
					do{
						row = currRow;
						column = currColumn;
						hDirection = random.nextInt(2);
						vDirection = random.nextInt(2);
						if(vDirection == 0 && hDirection == 0 && row > 0){
							row --;
						}
						if(vDirection == 0 && hDirection == 1 && row < map.getMapSize().get(1) - 1){
							row ++;
						}				
						if(hDirection == 0 && vDirection == 1 && column > 0){
							column --;
						}
						if(hDirection == 1 && vDirection == 1 && column < map.getMapSize().get(0) - 1){
							column ++;
						}
				
					}while(!(map.getMapElement(row, column) instanceof Empty || map.getMapElement(row, column) instanceof Player));
					
					//get a random empty place 
					int randomRow,randomColumn;
					ArrayList<Empty> road = map.getRoad();
					int index = random.nextInt(road.size());
					randomRow = road.get(index).getRow();
					randomColumn = road.get(index).getColumn();
					
					Pokemon p = (Pokemon)element;
					ImageView iv = p.getImageView();
					Location e = map.getMapElement(row, column);
					if(e instanceof Player){
						if(!gamePause)
							catchPokemon(p);
						Thread.sleep(500);
					}

					if(p.getImageView().isVisible()){
						if(!gamePause){
							element.reset(row,column);
							iv.relocate(column * Pokemon_PA2.stepSize, row * Pokemon_PA2.stepSize);
						}
					}else{
						if(!gamePause){
							element.reset(randomRow, randomColumn);
							e = map.getMapElement(randomRow, randomColumn);
							iv.relocate(randomColumn * Pokemon_PA2.stepSize, randomRow * Pokemon_PA2.stepSize);
						}
					}
					if(!(e instanceof Player) && !gamePause)
						e.reset(currRow, currColumn);
					
					if(!isGot)
						map.setMapElement(element);
					if(!gamePause)
						map.setMapElement(e);
			
				}else{
					Station s = (Station)element;
					int randomRow,randomColumn;
					ArrayList<Empty> road = map.getRoad();
					int index = random.nextInt(road.size());
					randomRow = road.get(index).getRow();
					randomColumn = road.get(index).getColumn();
					element.reset(randomRow, randomColumn);
					Empty e = (Empty)map.getMapElement(randomRow, randomColumn);
					e.reset(currRow, currColumn);
					if(!isGot)
						map.setMapElement(element);
					map.setMapElement(e);
					s.getImageView().relocate(randomColumn * Pokemon_PA2.stepSize, randomRow * Pokemon_PA2.stepSize);
				}
			mutex --;
			condition.signal();
			lock.unlock();
			}
			
		}
	}
}




