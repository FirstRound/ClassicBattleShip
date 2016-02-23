import java.io.*;

class App {

	private static GameController controller;

	private static AbstractUser user = new AI();
	private static AbstractUser ai = new AI();

	public static void main(String[] args) {
		controller = new GameController(user, ai);
		controller.play();
	}
}