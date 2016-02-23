import java.io.*;
import java.util.*;

//Sinletone with LazyLoadind
class GameController implements Serializable {

	private AbstractUser[] players;
	private Logger logger;
	private int __serializing_check = 50;
	private boolean is_game_started;


	private boolean move; // true - comp, false - user
	private boolean is_game_over;

	public GameController(AbstractUser user, AbstractUser ai) {
		players = new AbstractUser[2];

		this.players[0] = user;
		this.players[1] = ai;
		is_game_over = false;

		move = false;

		logger = Logger.getInstance();

		checkForSaving();
		
	}

	public void checkForSaving() {
		File f = new File("save.out");
		String answer;
		Scanner sc = new Scanner(System.in);
		if (f.exists()) {
			System.out.println("1> Continue");
			System.out.println("2> Start new");
			answer = sc.nextLine();
			switch(answer) {
				case "1":
					this.is_game_started = true;
					try{
						loadGame();
						f.delete();
					} catch(IOException e) {
						e.printStackTrace();
					}
				break;
			}
		}
			
	}

	//P1 Make shoot -> send answer -> P2 apply answer
	public void play() {
		try{
			logger.file("log.txt");
		} catch(IOException e) {
			e.printStackTrace();
		}

		if (!is_game_started) {
			for (AbstractUser u : players) {
				u.placeShips();
			}
			is_game_started = true;
		}

		System.out.println("Welcome! Let's play!)");
		AnswerType answer;
		Point p;
		String game_over;
		while(!is_game_over) {
			//((AI)players[1]).showFields();
			p = players[getIntValue(move)].makeShoot();
			answer = players[changeValue(getIntValue(move))].takeShoot(p);
			try {
				logger.playerShot(getIntValue(move), p);
				logger.playerAnswer(changeValue(getIntValue(move)), answer);
			} catch(IOException e) {
				e.printStackTrace();
			}
			players[getIntValue(move)].applyEnemyAnswer(answer);

			if (answer == AnswerType.MISS) { // if last move was MISS type - change the move prior
				move = !move;
			}

			if (__serializing_check-- == 0) {
				try {
					saveGame();
				} catch(IOException e) {
					e.printStackTrace();
		}
			}

			is_game_over = (players[0].isGameOver() || players[1].isGameOver());
		}
		if (move) {
			game_over = ":)";
		}
		else {
			game_over = ":(";
		}
		System.out.println(game_over);

		try {
			logger.endOfGame(game_over);
			logger.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		try {
			saveGame();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public AbstractUser[] getPlayers() {
		return players;
	}

	public void getPlayers(AbstractUser[] players) {
		this.players = players;
	}

	// BEGIN PRIVATE
	
	private void saveGame() throws IOException {
		try {
			FileOutputStream fos = new FileOutputStream("save.out");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(players);
			oos.flush();
			oos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void loadGame() throws IOException {
		try {
			FileInputStream fis = new FileInputStream("save.out");
			ObjectInputStream oin = new ObjectInputStream(fis);
			AbstractUser[] players = (AbstractUser[])(oin.readObject());
			this.players = players;
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	private int getIntValue(boolean b) {
		return (b) ? 1 : 0;
	}

	private int changeValue(int i) {
		return i==1 ? 0 : 1;
	}

	//END PRIVATE

}