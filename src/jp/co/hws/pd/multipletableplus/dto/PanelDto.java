package jp.co.hws.pd.multipletableplus.dto;

/**
 * 答えパネル
 * @author Kouki Higashikawa
 *
 */
public class PanelDto {		
	private int num; // 数字
	private int x; // 絶対座標X
	private int y; // 絶対座標Y
	
	/**
	 * コンストラクタ
	 * @param num
	 * @param x
	 * @param y
	 */
	public PanelDto(int num, int x, int y){
		this.num = num;
		this.x = x;
		this.y = y;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
