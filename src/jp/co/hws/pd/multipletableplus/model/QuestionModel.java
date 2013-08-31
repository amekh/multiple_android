package jp.co.hws.pd.multipletableplus.model;

import jp.co.hws.pd.multipletableplus.dto.QuestionDto;
import android.util.Log;

public class QuestionModel {
	
	QuestionDto question;
	
	public QuestionModel(QuestionDto question){
		this.question = question;		
	}
	
	public int[] randomNum1Num2(){
		int num1 = 0, num2 = 0;
		
		int rand = (int) Math.floor((Math.random()*question.getUseNum().length));
		num1 = question.getUseNum()[rand];
		
		rand = (int) Math.floor((Math.random()*question.getUseNum().length));
		num2 = question.getUseNum()[rand];
		
		return new int[]{num1, num2};
	}
	
	/**
	 * 使っている数字に対しての全てのかけ算の答え配列を返す
	 * @param answer
	 * @return
	 */
	public int[] randAnswers(int answer){
		int[] answers = new int[((int) Math.exp(question.getUseNum().length)-question.getUseNum().length)/2];
		Log.d("TAG",((int) Math.exp(question.getQuestionSize())-question.getQuestionSize())/2 + "");
		int rowCnt=0;
		for(int i=0; i<question.getUseNum().length; i++){
			for(int j=i; j<question.getUseNum().length; j++){
				answers[rowCnt] = question.getUseNum()[i]*question.getUseNum()[j];
				rowCnt++;
			}
		}
		
		return answers; 
	}
}
