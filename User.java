import java.lang.*;
import java.util.*;
import java.io.*;

//Class for User
class User extends AbstractUser implements Serializable {

	private Scanner sc = new Scanner(System.in);
	private int wrecked_ships;

	public User() {
		this.my_field = new Field(10, 10);
		this.enemy_field = new Field(10, 10);
		this.wrecked_ships = 10;
	}

	//@Override
	public Point makeShoot() {
		Point attack = new Point();
		String regexp = "^([a-jA-J])(\\d{1,2})$";
		String split = "^([a-jA-J])";
		String shoot;
		do{
			shoot = sc.nextLine();
			if (shoot.matches(regexp)) {
				shoot = shoot.toUpperCase();
				attack.x = shoot.charAt(0) - 'A';
				attack.y = (int)(Integer.parseInt((String)shoot.split(split)[1]))-1;
			}
		} while(!shoot.matches(regexp));
		my_last_shoot = attack;
		return attack;
	}


	//@Override
	public AnswerType takeShoot(Point p) {
		enemy_last_shoot = p;
		String shoot = "";
		shoot += (char)((char)p.x+'A');
		shoot += "";
		shoot += p.y;
		System.out.println(shoot);
		String answer = "";
		answer = sc.nextLine();
		switch(answer) {
			case "K":
				wrecked_ships--;
				return AnswerType.KILL;
			case "M":
				return AnswerType.MISS;
			case "W":
				return AnswerType.HIT;
			default:
				return AnswerType.MISS;
		}
	}

	//@Override
	public void placeShips() { }

	public boolean isGameOver() {
		return wrecked_ships <= 0;
	}

	public void applyEnemyAnswer(AnswerType answer) {
		switch(answer) {
			case MISS:
			System.out.println("M");
			break;
			case HIT:
			System.out.println("W");
			break;
			case KILL:
			System.out.println("K");
			break;
		}
	}
}