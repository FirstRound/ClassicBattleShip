import java.lang.*;
import java.util.*;
import java.io.*;

enum AnswerType implements Serializable { HIT, MISS, KILL }

public abstract class AbstractUser implements Serializable {

	protected Field my_field;
	protected Field enemy_field;
	protected ArrayList<Ship> ships;
	protected Point my_last_shoot;
	protected Point enemy_last_shoot;

	abstract public Point makeShoot();

	abstract public AnswerType takeShoot(Point p);

	abstract public boolean isGameOver();

	abstract public void placeShips();

	abstract public void applyEnemyAnswer(AnswerType answer);
}