package jp.co.hws.pd.multipletableplus.conf;

public class GameType {
	public final static int TARGET_DISPLAY_WIDTH = 720;
	public final static int TARGET_DISPLAY_HEIGHT = 1280;
	
	public static final int[][] USE_NUM = {
			{1,2,3,4,5,6,7,8,9}, //easy
			{1,2,3,4,5,6,7,8,9}, //normal
			{1,2,3,4,5,6,7,8,9} //hard
	};
	
	public static final int[] LIMIT_TIME = {
			60,
			30,
			5,
	};
	
	public static final int[] QUESTION_SIZE = {
			10,
			20,
			50
	};
}
