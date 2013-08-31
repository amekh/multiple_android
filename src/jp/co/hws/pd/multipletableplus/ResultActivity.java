package jp.co.hws.pd.multipletableplus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * メインゲーム処理
 * @author Kouki Higashikawa
 *
 */
public class ResultActivity extends Activity {

	private int gameDiff;

	// 音系
	SoundPool soundPool;
	private int soundSelect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		TextView trueNumlabel = (TextView)findViewById(R.id.trueNumlabel);
		TextView trueNum = (TextView)findViewById(R.id.trueNum);		

		trueNumlabel.setTypeface(Typeface.createFromAsset(getAssets(), 
				"print_bold_tt.ttf"));
		trueNum.setTypeface(Typeface.createFromAsset(getAssets(), 
				"print_bold_tt.ttf"));

		Intent intent = getIntent();
		int score = intent.getIntExtra("score", 0);
		int questionSize = intent.getIntExtra("questionSize", 0);
		gameDiff = intent.getIntExtra("gameDiff", 1);

		trueNum.setText(score + "/" + questionSize);

		Button again = (Button)findViewById(R.id.again);
		Button back = (Button)findViewById(R.id.back);

		again.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				again(v);
			}			
		});
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				toTop(v);
			}			
		});
	}

	public void again(View v){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("gameDiff", gameDiff);
		startActivity(intent);
	}

	public void toTop(View v){		
		Intent intent = new Intent(this, TopActivity.class);
		startActivity(intent);
	}	

}
