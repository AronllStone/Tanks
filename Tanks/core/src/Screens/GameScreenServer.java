package Screens;

import actors.Bullet;
import actors.Player;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import main.Main;
import managers.GameKeys;
import managers.InputProcessor;
import managers.Level;
import managers.LevelManager;

import java.io.*;
import java.net.*;

public class GameScreenServer extends ApplicationAdapter implements Screen {

	public static int WIDTH;
	public static int HEIGHT;
	public static int GdxWidth;
	public static int GdxHeight;
	public static float kX;
	public static float kY;

	public static java.net.ServerSocket serverSocket;
	public static Socket client;
	public static int ClientCount = 0;

	public static DataInputStream in;
	public static DataOutputStream out;

	public static String P1 = "STOP";
	public static String fireP1 = "NULL";
	public static String P2 = "STOP";
	public static String fireP2 = "NULL";

	ShapeRenderer sr;


	public static OrthographicCamera camera;

	Texture spriteSheet;
	Texture arrowUp;
	Texture arrowDown;
	Texture arrowLeft;
	Texture arrowRight;

	SpriteBatch batch;

	public static Player player;
	public static Player player2;


	LevelManager lvlManager;
	BitmapFont font;

	public static int flag = 1;

	int shootTimer;
	int shootTimer2;
	Main main;

	public GameScreenServer() {

		GdxWidth = Gdx.graphics.getWidth();
		GdxHeight = Gdx.graphics.getHeight();

		try {
			serverSocket = new java.net.ServerSocket(1525);
		} catch (IOException e) {
			System.out.println("Cant create socket");
			e.printStackTrace();
		}

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
		sr = new ShapeRenderer();
		arrowUp = new Texture(Gdx.files.internal("ArrowUp.png"));
		arrowDown = new Texture(Gdx.files.internal("ArrowDown.png"));
		arrowLeft = new Texture(Gdx.files.internal("ArrowLeft.png"));
		arrowRight = new Texture(Gdx.files.internal("ArrowRight.png"));
		spriteSheet = new Texture(Gdx.files.internal("TanksSpriteSheet.png"));
		lvlManager = new LevelManager(spriteSheet, 2);
		player = new Player(spriteSheet, Level.PLAYER_START_POS, 8, 8, 8, 0, 2);
		player2 = new Player(spriteSheet, Level.PLAYER_START_POS2, 8, 8, 8, 0, 2);
		shootTimer = 0;
		shootTimer2 = 0;

		UDPlistner();


		try {
			if (ClientCount == 0) {
				client = serverSocket.accept();
				ClientCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			InputStream inputStream = client.getInputStream();
			OutputStream outputStream = client.getOutputStream();
			in = new DataInputStream(inputStream);
			out = new DataOutputStream(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Change Input Processor */
		Gdx.input.setInputProcessor(new InputProcessor());
	}

	public void render(float a) {
		//timeServer++;
		try {

			//out = new DataOutputStream(outputStream);
			//System.out.println("AHJGJKHGKJHG");
			String lin = in.readUTF();
			//System.out.println("}{}{}{}{}{}{}{}{}{}{}{");
			P2 = lin.substring(0, 4);
			//System.out.println(P2);
			fireP2 = lin.substring(4);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		//flag = 0;
//
//		/*if (queueTimeClient.size() != 0 & queueTimeClient.size() == moveP2queue.size()) {
//			queueBuff = queueTimeClient;
//			lol = Integer.valueOf(queueTimeClient.remove());
//			if (lol <= timeServer) {
//				P2 = moveP2queue.remove();
//				fireP2 = fireP2queue.remove();
//			}
//			if (lol > timeServer) {
//				queueTimeClient = queueBuff;
//				try {
//					Thread.sleep(2500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}*/
//		/*try {
//			InputStream inputStream = client2.getInputStream();
//			OutputStream outputStream = client2.getOutputStream();
//
//			in2 = new DataInputStream(inputStream);
//			out2 = new DataOutputStream(outputStream);
//
//			if (timeClient < timeServer) {
//				System.out.println("timeClient = " + timeClient);
//				System.out.println("timeServer = " + timeServer);
//				System.out.println(in2.readUTF());
//			}
//			if (timeClient == timeServer)
//				out2.writeUTF("1");
//
////            P2 = in.readUTF();
////            fireP2 = in.readUTF();
//
////            String line = in.readUTF();                                 //TODO: Принятие данных от клиента
////            System.out.println("Client say = " + line);
////
////            out.writeUTF("");                                           //TODO: Отправка данных клиенту
////            System.out.println("Text message send");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}*/


		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Gdx.app.log("GameScreen FPS", (1/a) + "");

		batch.setProjectionMatrix(camera.combined);

		/*****************************************
		 *  			  UPDATING               *
		 *****************************************/
		shootTimer++;
		shootTimer2++;

		if (GameKeys.isDown(GameKeys.LEFT)) {
			player.setVelocity(Player.LEFT);
			P1 = "LEFT";
		} else if (GameKeys.isDown(GameKeys.UP)) {
			player.setVelocity(Player.UP);
			P1 = "UPPP";
		} else if (GameKeys.isDown(GameKeys.RIGHT)) {
			player.setVelocity(Player.RIGHT);
			P1 = "RIGH";
		} else if (GameKeys.isDown(GameKeys.DOWN)) {
			player.setVelocity(Player.DOWN);
			P1 = "DOWN";
		} else {
			player.setVelocity(Player.STOPPED);
			P1 = "STOP";
		}

		if (lvlManager.getCurrentLevel().resolveCollisions(player.getCollisionRect())) {
			player.setVelocity(Player.STOPPED);
		}

		if (P2.equals("LEFT")) {
			player2.setVelocity(Player.LEFT);
		} else if (P2.equals("UPPP")) {
			player2.setVelocity(Player.UP);
		} else if (P2.equals("RIGH")) {
			player2.setVelocity(Player.RIGHT);
		} else if (P2.equals("DOWN")) {
			player2.setVelocity(Player.DOWN);
		} else if (P2.equals("STOP")) {
			player2.setVelocity(Player.STOPPED);
		}

		if (lvlManager.getCurrentLevel().resolveCollisions(player2.getCollisionRect())) {
			player2.setVelocity(Player.STOPPED);
		}

		for (int i = 0; i < lvlManager.getCurrentLevel().getEnemiesList().size(); i++) {
			for (int j = 0; j < lvlManager.getCurrentLevel().getEnemiesList().get(i).getBullets().size(); j++) {
				if (lvlManager.getCurrentLevel().getEnemiesList().get(i).getBullets().get(j).getCollisionRect().overlaps(player.getCollisionRect())) {
					lvlManager.getCurrentLevel().getEnemiesList().get(i).getBullets().get(j).setAlive(false);
					//TODO: Add logic to remove player
				}
			}
		}

		if (GameKeys.isDown(GameKeys.SPACE)) {
			if (shootTimer > 30) {
				player.shoot(Bullet.BULLET_PLAYER);
				fireP1 = "FIRE";
				shootTimer = 0;
			}
		} else
			fireP1 = "NULL";

		if (fireP2.equals("FIRE")) {
			if (shootTimer2 > 30) {
				player2.shoot(Bullet.BULLET_PLAYER);
				shootTimer2 = 0;
			}
		}

		player.update(Gdx.graphics.getDeltaTime());
		player2.update(Gdx.graphics.getDeltaTime());
		//System.out.println("Player " + player.isAlive());
		for (int i = 0; i < player.getBullets().size(); i++) {
			if (lvlManager.getCurrentLevel().resloveDestructible(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resloveUnDestructible(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resloveBase(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resloveEnemyCollisions(player.getBullets().get(i).getCollisionRect())) {
				player.getBullets().get(i).setAlive(false);
				continue;
			}
		}

		for (int i = 0; i < player2.getBullets().size(); i++) {
			if (lvlManager.getCurrentLevel().resloveDestructible(player2.getBullets().get(i).getCollisionRect())) {
				player2.getBullets().get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resloveUnDestructible(player2.getBullets().get(i).getCollisionRect())) {
				player2.getBullets().get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resloveBase(player2.getBullets().get(i).getCollisionRect())) {
				player2.getBullets().get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resloveEnemyCollisions(player2.getBullets().get(i).getCollisionRect())) {
				player2.getBullets().get(i).setAlive(false);
				continue;
			}
		}

		lvlManager.update(Gdx.graphics.getDeltaTime());

		GameKeys.update();




       /* *//*TODO ОТПРАВКА ДАННЫХ НА КЛИЕНТ*//*
		{


            try {
                out.writeUTF(P1);
                out.writeUTF(fireP1);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        *//*TODO ОТПРАВКА ДАННЫХ НА КЛИЕНТ*/

		try {

			System.out.println("POPOP");
			String line = P1 + fireP1;
			out.writeUTF(line);

			System.out.println("					OPOPOPOPOPOPO");
//					System.out.println(fireP2);
//
//					moveP2queue.add(in.readUTF());
//					fireP2queue.add(in.readUTF());
//					queueTimeClient.add(in.readUTF());

			//System.out.println("timeClient = " + timeClient);

			//out.writeUTF(P1);
			//out.writeUTF(fireP1);

		} catch (IOException e) {
			e.printStackTrace();
		}

//		P2buf = P2;
//		fireP2buf = fireP2;
//		P1buf = P1;
//		fireP1buf = fireP1;

		/*****************************************
		 *  			   DRAWING               *
		 *****************************************/
		lvlManager.drawLevelBack();

		batch.begin();
		player.draw(batch);
		player2.draw(batch);
//		batch.draw(arrowUp, ((float) Gdx.graphics.getWidth()*5/32)*kX, ((float) Gdx.graphics.getHeight() - (float)Gdx.graphics.getHeight()*3/4)* kY);
//		batch.draw(arrowDown, ((float) Gdx.graphics.getWidth()*5/32)*kX,( (float) Gdx.graphics.getHeight() - (float)Gdx.graphics.getHeight()*31/32)*kY);
//		batch.draw(arrowLeft, ((float) Gdx.graphics.getWidth()/16)*kX, ((float) Gdx.graphics.getHeight() - (float)Gdx.graphics.getHeight()*7/8)*kY);
//		batch.draw(arrowRight,((float) Gdx.graphics.getWidth()/48)*kX, ((float) Gdx.graphics.getHeight() - (float)Gdx.graphics.getHeight()*7/8)*kY);
		lvlManager.draw(batch);
		batch.end();

		sr.begin(ShapeType.Filled);
		player.drawDebug(sr);
		player2.drawDebug(sr);
		lvlManager.drawShapes(sr);
		sr.end();

		lvlManager.drawLevelFor();
		flag = 1;
	}

	public void UDPlistner() {
		try {
			DatagramSocket datagramSocket = new DatagramSocket(1520);
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			System.out.println("server gotov");
			datagramSocket.receive(receivePacket);
			System.out.println("prinal");
			if (receivePacket != null) {
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				sendData = "Ready".getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				datagramSocket.send(sendPacket);
				System.out.println("otpravil");
			}
			System.out.println("vyshel");
			datagramSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		batch.dispose();
		spriteSheet.dispose();
		sr.dispose();

		try {
			client.close();
			//client2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}