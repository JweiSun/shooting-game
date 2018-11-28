package com.CES.example.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.annotation.SuppressLint;
import android.app.Activity;

/**
 * 在游戏设计里,Activity的主要功能是控制游戏流程.如启动游戏,暂停游戏,结束游戏等.
 * 
 * @author Hong
 *
 */
@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	
	private GameView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		// 获得屏幕宽和高
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		
		// 这个Handler是用来退出游戏的
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					finish();
					break;
				}
			}
		};
		
		// 创建游戏视图
		view = new GameView(this, handler, screenWidth, screenHeight);
		setContentView(view);
	}

	@Override
	protected void onDestroy() {
		// 停止所有音乐
		GameHelper.mediaPlayer.stop();
		GameHelper.mediaPlayer.release();
		GameHelper.soundPool.release();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 暂停音乐和游戏
		GameHelper.mediaPlayer.pause();
		view.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 开始音乐
		GameHelper.mediaPlayer.start();
	}

	/**
	 * 键盘事件响应处。在游戏的过程中要对返回键进行一些特殊的处理。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(view != null) {
				if(view.dispatchKeyEvent(event))
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
