package actors;

import Screens.GameScreen;
import Screens.GameScreenClient;
import Screens.GameScreenServer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import managers.LevelManager;

import java.util.ArrayList;

abstract class GameObject {

	public static final int IDLE = 1;
	public static final int MOVING = 2;

	public static final int LEFT = 180;
	public static final int RIGHT = 0;
	public static final int DOWN = 270;
	public static final int UP = 90;
	public static final int STOPPED = -1;

	public static final int MOVE_SPEED = 1;

	protected TextureRegion[] frames;
	protected Animation movingAnimation;
	protected TextureRegion currentFrame;

	protected float stateTime;

	protected int currentState = IDLE;
	protected int currentFacing = UP;

	protected Vector2 position;
	protected Vector2 velocity;
	protected Vector2 origin;
	protected Vector2 kof;
	protected float rotation;
	public double previousX = 0.0;
	public double previousY = 0.0;
	protected ArrayList<Bullet> bullets;
	public LevelManager lvlManager;

	public int previousFacing = 0;

	GameScreen gameScreen;
	public int screengam = 0;

	protected GameObject(Texture spriteSheet, Vector2 position, int spriteSheetRows,
						 int spriteSheetCols, int numFrames, int animationStartRow, int screengam) {

		//System.out.println("kx = " + GameScreen.kX + " kY = " + GameScreen.kY);
		this.position = position;
		this.screengam = screengam;
		origin = new Vector2(position.x + 16, position.y + 16);
		rotation = 90f;
		bullets = new ArrayList<Bullet>();
		stateTime = 0f;
		spriteSheet = new Texture(Gdx.files.internal("TanksSpriteSheet.png"));
/*		if (screengam == 1)
			lvlManager = GameScreen.lvlManager;
		else if (screengam == 2)
			lvlManager = GameScreenServer.lvlManager;
		else if (screengam == 3)
			lvlManager = GameScreenClient.lvlManager;*/
		lvlManager = (screengam == 1) ? GameScreen.lvlManager : ((screengam == 2) ? GameScreenServer.lvlManager : ((screengam == 3) ? GameScreenClient.lvlManager : null));

		//lvlManager = new LevelManager(spriteSheet,screengam);

		/* Load Texture Regions into Animation */
		TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() /
				spriteSheetRows, spriteSheet.getHeight() / spriteSheetCols);
		frames = new TextureRegion[numFrames];
		for (int i = 0; i < numFrames; i++) {
			frames[i] = tmp[animationStartRow][i];
		}
		movingAnimation = new Animation(0.075f, frames);
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	/*
	 * setVelocity() -- sets the movement speed and state of the player based on keyboard input(r) from the user.
	 */
	public void setVelocity(int r) {
		switch (r) {
			case LEFT:
				currentFacing = LEFT;
				rotation = 180f;
				currentState = MOVING;
				velocity = new Vector2(-MOVE_SPEED, 0);
				break;
			case RIGHT:
				currentFacing = RIGHT;
				rotation = 0;
				currentState = MOVING;
				velocity = new Vector2(MOVE_SPEED, 0);
				break;
			case UP:
				currentFacing = UP;
				rotation = 90f;
				currentState = MOVING;
				velocity = new Vector2(0, MOVE_SPEED);
				break;
			case DOWN:
				currentFacing = DOWN;
				rotation = 270f;
				currentState = MOVING;
				velocity = new Vector2(0, -MOVE_SPEED);
				break;
			case -1:
				currentState = IDLE;
				velocity = Vector2.Zero;
				break;
		}
	}

	public void setPosition(Vector2 vector) {
		position = vector;
	}

	public Vector2 getPosition() {
		return position;
	}

	/*
	 * shoot() -- determines position and velocity(direction/speed) of a newly generated bullet
	 * based on the position and facing of the player.
	 */
	public void shoot(int bulletType) {
		Vector2 bulletVelocity;
		Vector2 bulletPosition;

		switch (currentFacing) {
			case LEFT:
				bulletVelocity = new Vector2(-Bullet.BULLET_SPEED, 0);
				bulletPosition = new Vector2(origin.x - 16, origin.y);
				break;
			case RIGHT:
				bulletVelocity = new Vector2(Bullet.BULLET_SPEED, 0);
				bulletPosition = new Vector2(origin.x + 16, origin.y);
				break;
			case DOWN:
				bulletVelocity = new Vector2(0, -Bullet.BULLET_SPEED);
				bulletPosition = new Vector2(origin.x, origin.y - 16);
				break;
			case UP:
				bulletVelocity = new Vector2(0, Bullet.BULLET_SPEED);
				bulletPosition = new Vector2(origin.x, origin.y + 16);
				break;
			default:
				bulletVelocity = Vector2.Zero;
				bulletPosition = Vector2.Zero;
		}
		bullets.add(new Bullet(bulletPosition, bulletVelocity, screengam));
	}

	/*
	 * getCollisionRect() -- returns the collision rectangles for the player.
	 */
	public Rectangle getCollisionRect() {
		if (currentState == IDLE) {
			return new Rectangle(position.x + 6, position.y + 6, 32 - 12, 32 - 12);
		} else {
			switch (currentFacing) {
				case RIGHT:
					return new Rectangle(position.x + 6 + 1, position.y + 6, 32 - 12, 32 - 12);
				case LEFT:
					return new Rectangle(position.x + 6 - 1, position.y + 6, 32 - 12, 32 - 12);
				case UP:
					return new Rectangle(position.x + 6, position.y + 6 + 1, 32 - 12, 32 - 12);
				case DOWN:
					return new Rectangle(position.x + 6, position.y + 6 - 1, 32 - 12, 32 - 12);
				default:
					return null;
			}
		}
	}

	protected void update(float dt) {
		previousX = position.x;
		previousY = position.y;
		previousFacing = currentFacing;

		origin = new Vector2(position.x + 16, position.y + 16);

		if (screengam == 1)
			if (getCollisionRect().x < 0 || getCollisionRect().y < 0 ||
					getCollisionRect().x + getCollisionRect().width > GameScreen.WIDTH ||
					getCollisionRect().y + getCollisionRect().height > GameScreen.HEIGHT) {
				setVelocity(STOPPED);
			} else if (screengam == 2)
				if (getCollisionRect().x < 0 || getCollisionRect().y < 0 ||
						getCollisionRect().x + getCollisionRect().width > GameScreenServer.WIDTH ||
						getCollisionRect().y + getCollisionRect().height > GameScreenServer.HEIGHT) {
					setVelocity(STOPPED);
				} else if (screengam == 3)
					if (getCollisionRect().x < 0 || getCollisionRect().y < 0 ||
							getCollisionRect().x + getCollisionRect().width > GameScreenClient.WIDTH ||
							getCollisionRect().y + getCollisionRect().height > GameScreenClient.HEIGHT) {
						setVelocity(STOPPED);
					}


		if (currentState == MOVING) {
			position.add(velocity);
			stateTime += dt;
		}

		// Update Bullet Array
		for (int i = 0; i < bullets.size(); i++) {

			if (lvlManager.getCurrentLevel().resolveDestructible(bullets.get(i).getCollisionRect())) {
				bullets.get(i).setAlive(false);
				continue;
			}
			if (lvlManager.getCurrentLevel().resolveBase(bullets.get(i).getCollisionRect())) {
				bullets.get(i).setAlive(false);
				continue;
			}

			if (lvlManager.getCurrentLevel().resolvePlayerCollisions(bullets.get(i).getCollisionRect())) {
				bullets.get(i).setAlive(false);
				continue;
			}

			if (lvlManager.getCurrentLevel().resolveUnDestructible(bullets.get(i).getCollisionRect())) {
				bullets.get(i).setAlive(false);
				bullets.remove(i);
				i--;
				continue;
			}
			if (!bullets.get(i).getAlive()) {
				bullets.remove(i);
			} else {
				bullets.get(i).update(dt);
			}
		}

		currentFrame = movingAnimation.getKeyFrame(stateTime, true);
	}

	protected void draw(SpriteBatch batch) {
		batch.draw(currentFrame, position.x, position.y, origin.x - position.x, origin.y - position.y,
				32, 32, 1, 1, rotation);
	}

	protected void drawDebug(ShapeRenderer sr) {
		// Draw Bullets
		for (Bullet bullet : bullets) {
			if (bullet.getAlive()) {
				bullet.draw(sr);
			}
		}
	}
}
