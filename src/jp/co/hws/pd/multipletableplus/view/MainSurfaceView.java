package jp.co.hws.pd.multipletableplus.view;

import java.util.concurrent.TimeUnit;

import jp.co.hws.pd.multipletableplus.R;
import jp.co.hws.pd.multipletableplus.R.id;
import jp.co.hws.pd.multipletableplus.ResultActivity;
import jp.co.hws.pd.multipletableplus.conf.GameType;
import jp.co.hws.pd.multipletableplus.dto.PanelDto;
import jp.co.hws.pd.multipletableplus.dto.QuestionDto;
import jp.co.hws.pd.multipletableplus.model.QuestionModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * メインスレッド
 * 
 * @author Kouki Higashikawa
 * 
 */
public class MainSurfaceView extends SurfaceView implements
SurfaceHolder.Callback, Runnable, OnGestureListener, OnTouchListener {
	private final static String TAG = "MainSurfaceView";

	private final static int FPS = 25; // FPSの設定
	private final static int MAX_PANEL_SIZE = 6;
	// ドロップエリアの半径を求める(screenWidthを割る値)
	private final static int DROP_AREA_RADIUS_SCREEN_WIDTH_DIV = 4;
	private final static int DROP_AREA_STATUS_WAITING = 0;
	private final static int DROP_AREA_STATUS_ON_PANEL = 1;
	private final static int DROP_AREA_STATUS_IN_PANEL = 2;

	Context context;
	private GestureDetector mGestureDetector;

	private Thread thread;
	private SurfaceHolder holder;
	Handler uiHandler;

	private float screenWidth; // 画面横幅
	private float screenHeight; // 画面縦幅
	private float panelWidth;
	private float panelHeight;

	private PanelDto[] panels; // パネル配列
	private int touchIndex; // タッチしたパネルの添字

	Bitmap panelBg;

	// View
	private TextView $num1;
	private TextView $num2;
	ProgressBar $timeBar;

	private int gameDiff;
	private QuestionDto question; // 問題の設定
	private PanelDto panel; // パネルの設定
	private QuestionModel questionModel;
	private int[] DropPointXY; // ドロップ領域のXY
	private int[] DropPointWH; // ドロップ領域のWidth、Height
	private int DropAreaStatus; // ドロップエリアの状態を保存
	private String resultColor;
	private int questionTime;
	private long gameTime;
	private long progTime;
	private int currentPageNum; // 現在の問題番号

	private Canvas canvas;

	// 音系
	SoundPool soundPool;
	private int soundTake;
	private int soundPut;
	private int soundTrue;
	private int soundFalse;

	/**
	 * 初期化メソッド
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public MainSurfaceView(Context context, int gameDiff) {
		super(context);
		this.context = context;
		this.gameDiff = gameDiff;

		holder = getHolder();
		holder.addCallback(this);

		uiHandler = new Handler();

		// 効果音の読み込み
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundTake = soundPool.load(context, R.raw.catchbgm, 1);
		soundTrue = soundPool.load(context, R.raw.truesound, 1);
		soundFalse = soundPool.load(context, R.raw.falsesound, 1);

		// Viewを初期化
		$num1 = (TextView) ((Activity) context).findViewById(id.num1);
		$num2 = (TextView) ((Activity) context).findViewById(id.num2);

		panels = new PanelDto[MAX_PANEL_SIZE];
		touchIndex = -1;

		// 問題を設定 !!= インテントで値を受け渡す予定 =!!
		question = new QuestionDto(gameDiff);
		questionModel = new QuestionModel(question);

		// タイムバーの設定
		$timeBar = (ProgressBar) ((Activity) context)
				.findViewById(id.progressBar1);
		$timeBar.setMax(question.getLimitTime());
		questionTime = question.getLimitTime();
		
		Resources r = getResources();
		panelBg = Bitmap.createBitmap(BitmapFactory.decodeResource(r, R.drawable.panel_bg));
		
		// ジェスチャーを実装するための決まり処理
		this.setOnTouchListener(this);
		this.mGestureDetector = new GestureDetector(this);
		this.setClickable(true);
	}

	int num1, num2, answer;

	private int score;

	private float scaleSize;

	@Override
	public void run() {
		// メイン周期
		Log.d(TAG, "run()");
		while (thread != null) {
			try {
				canvas = holder.lockCanvas();
				// キャンバスのクリーン
				clearDisplay();
				dropAreaDraw();
				// clearDisplay(canvas);
				if (questionTime < question.getLimitTime()) {
					// 問題開始からの経過時間をprogTimeに記録
					putPanel();
				} else {
					if (currentPageNum > question.getQuestionSize()) {
						// ゲーム終了
						gameClear();						
					}else{

						DropAreaStatus = DROP_AREA_STATUS_WAITING;

						// 制限時間の初期化
						questionTime = 0;
						progTime = 0;

						currentPageNum++;

						touchIndex = -1;

						// 問題式を構築
						int num[] = questionModel.randomNum1Num2();
						num1 = num[0];
						num2 = num[1];
						answer = num1 * num2;
						uiHandler.post(new Runnable() {
							@Override
							public void run() {
								$num1.setText(num1 + "");
								$num2.setText(num2 + "");
							}
						});
						createPanel();
					}
				}

				// 経過時間を記録し、時間制限をカウントダウンする。
				progTime += System.currentTimeMillis() - gameTime;
				gameTime = System.currentTimeMillis();

				if (questionTime != question.getLimitTime()) {
					questionTime = (int) (TimeUnit.MILLISECONDS
							.toSeconds(progTime));
				}

				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						$timeBar.setProgress((int) TimeUnit.MILLISECONDS
								.toSeconds(progTime));
					}
				});

				Thread.sleep(1000 / FPS);
				holder.unlockCanvasAndPost(canvas);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 画面をクリアする
	 * 
	 * @param canvas
	 */
	private void clearDisplay() {
		// Canvasの背景色を白で塗る
		canvas.drawColor(Color.WHITE);
	}

	private void dropAreaDraw() {
		// 中点を探す
		float centerX = screenWidth / 2;
		float centerY = screenHeight / 2;

		Paint paint = new Paint();
		paint.setStrokeWidth(4);
		paint.setStyle(Paint.Style.STROKE);
		// 状態によって色を帰る
		switch (DropAreaStatus) {
		case DROP_AREA_STATUS_WAITING:
			paint.setColor(Color.parseColor("#69f799"));
			break;
		case DROP_AREA_STATUS_ON_PANEL:
			paint.setColor(Color.parseColor("#ffda4b"));
			break;
		case DROP_AREA_STATUS_IN_PANEL:
			paint.setColor(Color.parseColor(resultColor));
			break;
		}
		paint.setAntiAlias(true);

		if(screenHeight > screenWidth){
			canvas.drawCircle(centerX, centerY, screenWidth
					/ DROP_AREA_RADIUS_SCREEN_WIDTH_DIV, paint);
		}else{
			canvas.drawCircle(centerX, centerY, screenHeight
					/ DROP_AREA_RADIUS_SCREEN_WIDTH_DIV, paint);
		}
	}

	/**
	 * パネルの生成
	 */
	private void createPanel() {						
		// パネルを生成する
		int[] randAnswers = questionModel.randAnswers(answer);
		for (int i = 0; i < MAX_PANEL_SIZE; i++) {
			int rand = (int) Math.floor(Math.random() * randAnswers.length);

			// 配列に値が格納されていない場合は、再度乱数を生成
			if (randAnswers[rand] == 0) {
				i--;
				continue;
			}

			if (i != MAX_PANEL_SIZE - 1 && randAnswers[rand] == answer) {
				i--;
				continue;
			} else if (i == MAX_PANEL_SIZE - 1) {
				randAnswers[rand] = answer;
			}

			panels[i] = new PanelDto(randAnswers[rand],
					(int) (Math.random() * screenWidth),
					(int) (Math.random() * screenHeight));

			// 配列から削除
			randAnswers[rand] = 0;

			// 画面からはみ出し対策
			if (panels[i].getX() + panelWidth > screenWidth) {
				panels[i]
						.setX(panels[i].getX()
								+ (int) (screenWidth - (panels[i].getX() + panelWidth)));
			}
			if (panels[i].getY() + panelHeight > screenHeight) {
				panels[i]
						.setY(panels[i].getY()
								+ (int) (screenHeight - (panels[i].getY() + panelHeight)));
			}

			// 初期位置がドロップエリア内に入る対策
			if (isDropArea(panels[i].getX(), panels[i].getY())) {
				// panels[i].setX((int)
				// (panels[i].getX()+screenWidth/DROP_AREA_RADIUS_SCREEN_WIDTH_DIV+1));
				i--;
				continue;
			}
		}
	}

	/**
	 * パネルを配置する
	 */
	private void putPanel() {
		// パネルを描く
		Paint paint = new Paint();		
		paint.setColor(Color.argb(255, 0, 255, 255));
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(4);
		// フォント用
		Paint paintf = new Paint();
		paintf.setAntiAlias(true);
		paintf.setTextSize(panelWidth/3);
		paintf.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"print_bold_tt.ttf"));		
		
		for (PanelDto panel : panels) {
			float fontWidth = paintf.measureText(panel.getNum() + "");
			float fontPositionX = (panel.getX() + panelWidth / 2)
					- fontWidth / 2;
			float fontPositionY = (panel.getY() + panelHeight / 2)
					+ paintf.getTextSize() / 4;
			canvas.drawBitmap(panelBg, panel.getX(), panel.getY(), null);
			canvas.drawText(panel.getNum() + "", fontPositionX, fontPositionY,
					paintf);
		}
	}

	/**
	 * その座標がドロップエリア内の場合、trueを返す
	 * 
	 * @return
	 */
	private boolean isDropArea(int x, int y) {
		// 中点を探す
		float centerX = screenWidth / 2;
		float centerY = screenHeight / 2;

		float panelCX = x + this.panelWidth / 2;
		float panelCY = y + this.panelHeight / 2;
		
		double distance = Math.abs(Math.sqrt(
				((centerX-panelCX)*(centerX-panelCX)) +
				((centerY-panelCY)*(centerY-panelCY))
				));
		/*if (((centerX - x) * (centerX - x)) + ((centerY - y) * (centerY - y)) < (screenWidth / DROP_AREA_RADIUS_SCREEN_WIDTH_DIV)
				* (screenWidth / DROP_AREA_RADIUS_SCREEN_WIDTH_DIV)) {				
			return true;
		}*/
		Log.d(TAG,distance+"*+*"+this.panelWidth/2);
		if(distance < this.panelWidth/2){
			return true;
		}
		return false;
	}

	private void gameClear() {
		// result画面に遷移
		// scoreをインテントで渡す
		thread = null;
		Intent intent = new Intent(context, ResultActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("score", score);
		intent.putExtra("questionSize", question.getQuestionSize());
		intent.putExtra("gameDiff", question.getGameDiff());
		context.startActivity(intent);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int width,
			int height) {
		this.screenWidth = width;
		this.screenHeight = height;				
		
		if(height > width){
			this.panelWidth = height/6;
			this.panelHeight = height/6;
		}else{
			this.panelWidth = width/6;
			this.panelHeight = width/6;
		}
		
		// リソースの読み込み
		Resources r = getResources();
		Bitmap panelBgSrc = Bitmap.createBitmap(BitmapFactory.decodeResource(r, R.drawable.panel_bg));
		
		this.scaleSize = ((float)this.panelHeight)/((float)panelBgSrc.getHeight());
		
		// マトリクス作成
		Matrix matrix = new Matrix();
		matrix.postScale(this.scaleSize, this.scaleSize);		
		panelBg = Bitmap.createBitmap(panelBgSrc, 0, 0, panelBgSrc.getWidth(), panelBgSrc.getHeight(), matrix, true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated()");

		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// 終了処理
		thread = null;
	}

	@Override
	public boolean onTouch(View view, MotionEvent e) {
		// マウスが離れた時		
		if (touchIndex == -1)
			return this.mGestureDetector.onTouchEvent(e);

		if (MotionEvent.ACTION_UP == e.getAction()) {
			if (isDropArea(panels[touchIndex].getX(), panels[touchIndex].getY())) {
				DropAreaStatus = DROP_AREA_STATUS_IN_PANEL;

				if (isTrueAnswer(panels[touchIndex].getNum())) {
					resultColor = "#ff4a4a";
					score++;
					soundPool.play(soundTrue, 2F, 2F, 0, 0, 1.0F);
				} else {
					soundPool.play(soundFalse, 0.5F, 0.5F, 0, 0, 1.0F);
					resultColor = "#6464ff";
				}

				// 問題終了
				questionTime = question.getLimitTime();
			}
			touchIndex = -1;
		}
		return this.mGestureDetector.onTouchEvent(e);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if(panels == null) return false;

		float x = e.getX();
		float y = e.getY();

		for (int i = panels.length - 1; i >= 0; i--) {
			if (panels[i].getX() <= x
					&& panels[i].getX() + panelWidth >= x
					&& panels[i].getY() <= y
					&& panels[i].getY() + panelHeight >= y) {
				// パネルが存在する座標をクリックした
				soundPool.play(soundTake, 2F, 2F, 0, 0, 1.0F);
				touchIndex = i;
				break;
			}
		}

		return false;
	}

	int currentX, currentY;

	@Override
	public boolean onScroll(MotionEvent e, MotionEvent e1, float distanceX,
			float distanceY) {
		// タッチしていない時
		if (touchIndex == -1)
			return false;

		int updateX = (int) (panels[touchIndex].getX() - distanceX);
		int updateY = (int) (panels[touchIndex].getY() - distanceY);

		panels[touchIndex].setX(updateX);
		panels[touchIndex].setY(updateY);

		// ドロップエリアの状態を監視、変化させる
		if (isDropArea(updateX, updateY)) {
			DropAreaStatus = DROP_AREA_STATUS_ON_PANEL;
		} else {
			DropAreaStatus = DROP_AREA_STATUS_WAITING;
		}

		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private boolean isTrueAnswer(int answer) {
		return this.answer == answer;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}
}
