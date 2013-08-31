package jp.co.hws.pd.multipletableplus.dto;

import jp.co.hws.pd.multipletableplus.conf.GameType;

/**
 * 99の問題DTO
 * @author Kouki Higashikawa
 *
 */
public class QuestionDto {
	private int[] useNum;  // 利用する数字
	private int limitTime; // 制限時間(s)
	private int questionSize; // 問題数	
	private int gameType; // 問題タイプ(0:数字の小さい順に、 1:ランダム)
	private int gameDiff;
	
	/**
	 * コンストラクタ
	 * @param useNum
	 * @param limitTime
	 * @param questionSize
	 */
	public QuestionDto(int gameDiff){		
		this.gameDiff = gameDiff;
		this.limitTime = GameType.LIMIT_TIME[gameDiff];
		this.useNum = GameType.USE_NUM[gameDiff];
		this.questionSize = GameType.QUESTION_SIZE[gameDiff];
	}
	
	public int[] getUseNum() {
		return useNum;
	}

	public void setUseNum(int[] useNum) {
		this.useNum = useNum;
	}

	public int getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(int limitTime) {
		this.limitTime = limitTime;
	}

	public int getQuestionSize() {
		return questionSize;
	}

	public void setQuestionSize(int questionSize) {
		this.questionSize = questionSize;
	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	public int getGameDiff() {
		return gameDiff;
	}

	public void setGameDiff(int gameDiff) {
		this.gameDiff = gameDiff;
	}
}
