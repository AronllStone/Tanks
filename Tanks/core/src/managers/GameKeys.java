package managers;

public class GameKeys {

	private static final int NUM_KEYS= 11;

	private static boolean[] keys = new boolean[NUM_KEYS];
	private static boolean[] pkeys = new boolean[NUM_KEYS];

	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int DOWN = 3;
	public static final int SPACE = 4;
	public static final int TOCH_UP = 5;
	public static final int TOCH_DOWN = 6;
	public static final int TOCH_LEFT= 7;
	public static final int TOCH_RIGHT= 8;
	public static final int TOCH_FIRE= 9;
	public static final int GET_POS= 10;



	public static void setKey(int k, boolean b){
		keys[k] = b;
	}

	public static void update(){
		for(int i = 0; i < NUM_KEYS; i++){
			pkeys[i] = keys[i];
		}
	}

	public static boolean isDown(int k){
		return keys[k];
	}

	public static boolean isPressed(int k){
		return keys[k] && !pkeys[k];
	}
}
