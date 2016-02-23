import java.io.*;

//Sinletone with LazyLoadind
class Logger implements Serializable {
	private BufferedWriter out;

	private String state;

	private static Logger logger;

	private Logger() {}

	public static synchronized Logger getInstance() {
		if(logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	public void file(String str) throws IOException {
		try {
			out = new BufferedWriter(new FileWriter(str));
		} catch(IOException e) {
			throw e;
		}
	}

	public void close() throws IOException {
		try {
			out.close();
		} catch(IOException e) {
			throw e;
		}
	}

	public void playerShot(int player_id, Point p) throws IOException {
		state = "Player " + player_id + ": " + ((char)((char)p.x+'A')) + "" + p.y + "\n";
		out.write(state);
	}

	public void playerAnswer(int player_id, AnswerType answer) throws IOException{
		state = "Player " + player_id + ": ";
		switch(answer) {
			case MISS:
				state += "M";
				break;
			case HIT:
				state += "W";
				break;
			case KILL:
				state += "K";
				break;
		} 
		state += "\n";
		out.write(state);
	}

	public void endOfGame(String str) throws IOException{
		state += str;
		out.write(state);
	}

}