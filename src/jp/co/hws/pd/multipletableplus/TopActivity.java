package jp.co.hws.pd.multipletableplus;

import jp.co.cayto.appc.sdk.android.AppC;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * メインゲーム処理
 * @author Kouki Higashikawa
 *
 */
public class TopActivity extends Activity {
	public final static int GAME_DIFF_EASY = 0; 
	public final static int GAME_DIFF_NORMAL = 1; 
	public final static int GAME_DIFF_HARD = 2;

	private AppC mAppC;

	int gameDifficulty = 1;

	String[] gameDiffStr = {"EASY","NORMAL","HARD"};

	// 音系
	SoundPool soundPool;
	private int soundSelect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);		

		// 効果音の読み込み
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundSelect = soundPool.load(this, R.raw.selectsound, 1);

		mAppC = new AppC(this);			
	}

	public void changeDifficulty(View view){
		soundPool.play(soundSelect, 2F, 2F, 0, 0, 1.0F);
		gameDifficulty = (gameDifficulty+1)%gameDiffStr.length;
		((TextView)view).setText(gameDiffStr[gameDifficulty]);
	}	

	public void gameStart(View view){
		soundPool.play(soundSelect, 2F, 2F, 0, 0, 1.0F);
		Intent intent = new Intent(this, MainActivity.class);		
		intent.putExtra("gameDiff", gameDifficulty);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {	
		super.onResume();
		mAppC.initCutin();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		if(keyCode == KeyEvent.KEYCODE_BACK){
			mAppC.callCutinFinish(AppC.CUTIN_BASIC);
		}
		//return super.onKeyDown(keyCode, event);
		return true;
	}
}
