package Screens;

import actors.Bullet;
import actors.Player;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import main.Main;
import managers.GameKeys;
import managers.InputProcessor;
import managers.LevelManager;

import java.util.Random;

public class GameScreen extends ApplicationAdapter implements Screen {

	public static int WIDTH;
	public static int HEIGHT;
	public static int GdxWidth;
	public static int GdxHeight;
	public static float kX;
	public static float kY;


	public static OrthographicCamera camera;

	Texture spriteSheet;
	public static Texture touchpad_background;
	public static Texture touchpad_knob;
	public static Texture tx_boom1;
	public static Texture tx_boom2;
	public static Texture tx_boom3;

	float playerBoomX = 0;
	float playerBoomY = 0;

	public static int boomScaleX;
	public static int boomScaleY;

	public static Sound au_shoot;
	public static Sound au_boom;
	public static Sound au_tick;
	public static Sound au_move;
	public static Music au_startGame;
	public static Music au_GameOver;

	boolean moved = false;
	boolean movedBuf = false;

	public static int back_x;
	public static int knob_x;
	public static int back_y;
	public static int knob_y;


	SpriteBatch batch;
	ShapeRenderer sr;

	public static Player player;
	public static LevelManager lvlManager;
	public static Random random;
	public static int irand = 2;
	BitmapFont font;

	int Lives = 3;
	int livesTimer;
	String gameover;

	int shootTimer;
	int boomTimer;
	int boomTimer1;
	boolean enemyBoom = false;
	float enemyBoomX = 0;
	float enemyBoomY = 0;
	playerMove playerMove;
	Main main;

	public GameScreen(Main gameScreen) {

/*		Vector2 vect = new Vector2(1,1);
		System.out.println("VECT = " + vect);
		System.out.println("String = " + vect.toString());
		String asd = "(2.0,2.0)";
		vect.fromString(asd);
		System.out.println("String -> vect = " + vect);*/
		this.main = gameScreen;
		System.out.println("GameScreen is created");
		GdxWidth = Gdx.graphics.getWidth();
		GdxHeight = Gdx.graphics.getHeight();


		WIDTH = 640;
		HEIGHT = 360;
		kX = (float) WIDTH / (float) GdxWidth;
		kY = (float) HEIGHT / (float) GdxHeight;
		/* Set up the camera */
		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.update();

		/* Set up variables */
		batch = new SpriteBatch();

		//font = new BitmapFont(new FileHandle("test.fnt"), new FileHandle("test.png"), false);
		font = new BitmapFont();
		sr = new ShapeRenderer();

		spriteSheet = new Texture(Gdx.files.internal("TanksSpriteSheet.png"));
		touchpad_background = new Texture(Gdx.files.internal("touchpad_background.png"));
		touchpad_knob = new Texture(Gdx.files.internal("touchpad_knob.png"));

		tx_boom1 = new Texture(Gdx.files.internal("boom1.png"));
		tx_boom2 = new Texture(Gdx.files.internal("boom2.png"));
		tx_boom3 = new Texture(Gdx.files.internal("boom3.png"));

		boomScaleX = scaleHeight(Gdx.graphics.getHeight()) - tx_boom1.getHeight() / 2;
		boomScaleY = scaleHeight(Gdx.graphics.getWidth()) - tx_boom1.getHeight() / 2;

		au_shoot = Gdx.audio.newSound(Gdx.files.internal("Shoot.wav"));
		au_boom = Gdx.audio.newSound(Gdx.files.internal("Boom.wav"));
		au_move = Gdx.audio.newSound(Gdx.files.internal("Move.wav"));
		au_tick = Gdx.audio.newSound(Gdx.files.internal("Steel.wav"));
		au_startGame = Gdx.audio.newMusic(Gdx.files.internal("StartGame.mp3"));
		au_GameOver = Gdx.audio.newMusic(Gdx.files.internal("GameOver.mp3"));

		lvlManager = new LevelManager(spriteSheet, 1);
		player = new Player(spriteSheet, new Vector2(144, 4), 8, 8, 8, 3, 1);

		playerMove = new playerMove();
		playerMove.start();

		//player2 = new Player(spriteSheet, Level.PLAYER_START_POS2, 8, 8, 8, 0, 1);
		random = new Random();
		irand = (int) (Math.random() * 100);
		random.setSeed(irand);
		shootTimer = 0;
		//shootTimer2 = 0;


		/* Change Input Processor */
		Gdx.input.setInputProcessor(new InputProcessor());

		au_startGame.play();
	}

	public void render(float a) {

		/* Clear the screen */
		livesTimer++;
		if (!player.isAlive() & Lives > 1 & livesTimer > 100) {
			Lives--;
			au_move.stop();
			player = new Player(spriteSheet, new Vector2(144, 4), 8, 8, 8, 3, 1);
			playerMove = new playerMove();
			playerMove.start();

			livesTimer = 0;
		}

		if (lvlManager.getCurrentLevel().getEnemiesDown() == lvlManager.getCurrentLevel().getTotalEnemies()) {
			if (lvlManager.getAllLevels() == lvlManager.getCurrentLevelNumber() + 1) {
				gameover = "You Win!";
				this.dispose();
			} else {
				lvlManager.nextLevel();
				player.setPosition(new Vector2(144, 4));
			}
		}


		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);

		/*****************************************
		 *  			  UPDATING               *
		 *****************************************/
		shootTimer++;
		//shootTimer2++;

		if (player.isAlive()) {
			/*if (GameKeys.isDown(GameKeys.LEFT)) {
				player.setVelocity(Player.LEFT);
				moved = true;
			} else if (GameKeys.isDown(GameKeys.UP)) {
				player.setVelocity(Player.UP);
				moved = true;
			} else if (GameKeys.isDown(GameKeys.RIGHT)) {
				player.setVelocity(Player.RIGHT);
				moved = true;
			} else if (GameKeys.isDown(GameKeys.DOWN)) {
				player.setVelocity(Player.DOWN);
				moved = true;
			} else {
				player.setVelocity(Player.STOPPED);
				moved = false;
			}*/

			player.setVelocity(playerMove.threadMovement);
			moved = playerMove.threadMoved;


			if (moved != movedBuf) {
				if (moved) {
					au_move.play(0.6f);
					au_move.loop();
				} else {
					au_move.stop();
				}
				movedBuf = moved;
			}

//				System.out.println("POSSITION PLAYER = " + player.getPosition()); //TODO GET POSITION OF PLAYER
			if (lvlManager.getCurrentLevel().resolveCollisions(player.getCollisionRect()) || lvlManager.getCurrentLevel().resolvePlayerOnEnemyCollisions(player.getCollisionRect())) {
				player.setVelocity(Player.STOPPED);
			}

			for (int i = 0; i < lvlManager.getCurrentLevel().getEnemiesList().size(); i++) {
				for (int j = 0; j < lvlManager.getCurrentLevel().getEnemiesList().get(i).getBullets().size(); j++) {
					if (lvlManager.getCurrentLevel().getEnemiesList().get(i).getBullets().get(j).getCollisionRect().overlaps(player.getCollisionRect())) {
						lvlManager.getCurrentLevel().getEnemiesList().get(i).getBullets().get(j).setAlive(false);
						player.setAlive(false);
						livesTimer = 0;
						au_move.stop();
						au_boom.play();
						Gdx.input.vibrate(200);
					}
				}
			}

			if (Gdx.input.isKeyPressed(Input.Keys.U)) {
				Lives = 10;
			}
			if (GameKeys.isDown(GameKeys.SPACE)) {
				if (shootTimer > 30) {
					//if (player.getBullets().size() == 0) {
					au_shoot.play();
					player.shoot(Bullet.BULLET_PLAYER);
					//}
					shootTimer = 0;
				}
			}
		}


		//if (player.isAlive()) {
		player.update(Gdx.graphics.getDeltaTime());
		for (int i = 0; i < player.getBullets().size(); i++) {    //TODO обработка пуль для игрока
			if (lvlManager.getCurrentLevel().resolveDestructible(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				au_tick.play();
				continue;
			}
			if (lvlManager.getCurrentLevel().resolveUnDestructible(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				player.getBullets().remove(i).setAlive(false);
				au_tick.play();
				continue;
			}
			if (lvlManager.getCurrentLevel().resolveBase(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				au_tick.play();
				continue;
			}
			ArrayMap<Boolean, Vector2> lol = null;
			lol = lvlManager.getCurrentLevel().resolveEnemyCollisions(player.getBullets().get(i).getCollisionRect());
			if (lol != null) {
				player.getBullets().get(i).setAlive(false);
				enemyBoomX = lol.get(true).x;
				enemyBoomY = lol.get(true).y;
				enemyBoom = true;
				au_boom.play();
				continue;
			}
			//}
		}

		lvlManager.update(Gdx.graphics.getDeltaTime());

		GameKeys.update();


		/*****************************************
		 *  			   DRAWING               *
		 *****************************************/
		lvlManager.drawLevelBack();

		sr.begin(ShapeRenderer.ShapeType.Filled);

//		if (player.isAlive())
		player.drawDebug(sr);    //TODO отрисовка пуль игрока
		lvlManager.drawShapes(sr);
		sr.end();
		batch.begin();


		if (player.isAlive()) {
			player.draw(batch);
			playerBoomX = player.getPosition().x;
			playerBoomY = player.getPosition().y;
			boomTimer = 0;
		} else {
			if (boomTimer < 8)
				batch.draw(tx_boom1, playerBoomX, playerBoomY);
			else if (boomTimer < 16)
				batch.draw(tx_boom2, playerBoomX, playerBoomY);
			else if (boomTimer < 24)
				batch.draw(tx_boom3, playerBoomX, playerBoomY);
			boomTimer++;
		}

		if (enemyBoom) {
			if (boomTimer1 < 8)
				batch.draw(tx_boom1, enemyBoomX, enemyBoomY);
			else if (boomTimer1 < 16)
				batch.draw(tx_boom2, enemyBoomX, enemyBoomY);
			else if (boomTimer1 < 24)
				batch.draw(tx_boom3, enemyBoomX, enemyBoomY);
			else
				enemyBoom = false;
			boomTimer1++;
		} else {
			boomTimer1 = 0;
		}

		lvlManager.draw(batch);
		if (Gdx.input.getX() > Gdx.graphics.getWidth() / 2 & Gdx.input.getY() > Gdx.graphics.getHeight() / 2) {    //TODO рисование джойстика на экране
		} else if (Gdx.input.isTouched() & InputProcessor.onAr) {
			back_x = scaleWidth(InputProcessor.arrowX) - (touchpad_background.getWidth() / 2);
			back_y = scaleHeight(Gdx.graphics.getHeight() - InputProcessor.arrowY) - (touchpad_background.getHeight() / 2);
			knob_x = scaleWidth(Gdx.input.getX()) - (touchpad_knob.getWidth() / 2);
			knob_y = scaleHeight(Gdx.graphics.getHeight() - Gdx.input.getY()) - (touchpad_knob.getHeight() / 2);
			batch.draw(touchpad_background, back_x, back_y);
			if (knob_x < back_x)
				knob_x = back_x;
			if (knob_x > (back_x + touchpad_background.getWidth()))
				knob_x = (back_x + touchpad_background.getWidth()) - touchpad_knob.getWidth() / 2;
			if (knob_y < back_y)
				knob_y = back_y;
			if (knob_y > (back_y + touchpad_background.getHeight()))
				knob_y = (back_y + touchpad_background.getHeight()) - touchpad_knob.getHeight() / 2;
			batch.draw(touchpad_knob, knob_x, knob_y);
		}

		font.draw(batch, "Lives = " + Lives, 550, 20);
		font.draw(batch, "Enimes Left = " + (lvlManager.getCurrentLevel().getTotalEnemies() - lvlManager.getCurrentLevel().getEnemiesDown()), 10, 360);
		batch.end();


		lvlManager.drawLevelFor();

		if (!lvlManager.getCurrentLevel().baseIsAlive()) {
			for (int i = 0; i < lvlManager.getCurrentLevel().enemies.size(); i++)
				lvlManager.getCurrentLevel().enemies.get(i).setAlive(false);
			gameover = "You Lose";
			stopAllSounds();
			player.setAlive(false);
			au_GameOver.play();
			this.dispose();
		}

		if (!player.isAlive() & Lives == 1) {
			for (int i = 0; i < lvlManager.getCurrentLevel().enemies.size(); i++)
				lvlManager.getCurrentLevel().enemies.get(i).setAlive(false);
			gameover = "Game Over";
			stopAllSounds();
			au_GameOver.play();
			this.dispose();
		}
	}

	public class playerMove extends Thread {

		public boolean threadMoved;
		public int threadMovement;

		playerMove() {
		}

		void lol() {
			if (GameKeys.isDown(GameKeys.LEFT)) {
				threadMovement = Player.LEFT;
				threadMoved = true;
			} else if (GameKeys.isDown(GameKeys.UP)) {
				threadMovement = Player.UP;
				threadMoved = true;
			} else if (GameKeys.isDown(GameKeys.RIGHT)) {
				threadMovement = Player.RIGHT;
				threadMoved = true;
			} else if (GameKeys.isDown(GameKeys.DOWN)) {
				threadMovement = Player.DOWN;
				threadMoved = true;
			} else {
				threadMovement = Player.STOPPED;
				threadMoved = false;
			}
		}

		public void run() {
			while (true) {
				if (player.isAlive()) {
					lol();
//					System.out.println("HELLO PLAYER");
					try {
						sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("ПОКА !!!");
					break;
				}
			}
		}
	}

	void stopAllSounds() {
		au_move.stop();
		au_startGame.stop();
		au_tick.stop();
		au_boom.stop();
		au_shoot.stop();
	}

	/**
	 * Входит размер из Gdx.graphics.getWidth в процентах , а выходит скалированный разер относительно WIDTH
	 * <p>
	 *
	 * @author Jarviz
	 */
	public static int scaleWidth(int per) {
		return (WIDTH * per) / Gdx.graphics.getWidth();
	}

	/**
	 * Входит размер из Gdx.graphics.getHeight в процентах, а выходит скалированный разер относительно Height
	 * <p>
	 *
	 * @author Jarviz
	 */
	public static int scaleHeight(int per) {
		return (HEIGHT * per) / Gdx.graphics.getHeight();
	}

	@Override
	public void show() {

	}

	public void resize(int width, int height) {

	}

	public void pause() {

	}

	public void resume() {

	}

	public void render() {
		System.out.println("Render of AppListnder");
	}

	@Override
	public void hide() {

	}

	public void dispose() {
//		batch.dispose();
//		spriteSheet.dispose();
//		sr.dispose();
		main.setScreen(new Gameover(main, gameover));
	}
}