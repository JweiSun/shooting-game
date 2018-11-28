package com.CES.example.game;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 游戏视图类。继承于SurfaceView。
 * <P/>
 * 由于游戏过程中我们有时候会需要需要到很多的内容.如果用传统的控件我们就需要花很大一部分
 * 资源来管理这些控件,这样会使游戏不流畅.所以建议游戏里的所有内容都是用画出来的.
 * 在这里我们建议使用SurfaceView作为游戏图片的载体,而不是View
 * <P/>
 * SurfaceView和View最本质的区别在于，surfaceView是在一个新起的单独线程中可以重新绘制
 * 画面而View必须在UI的主线程中更新画面。那么在UI的主线程中更新画面 可能会引发问题，
 * 比如你更新画面的时间过长，那么你的主UI线程会被你正在画的函数阻塞。那么将无法响应按键，
 * 触屏等消息。 当使用surfaceView 由于是在新的线程中更新画面所以不会阻塞你的UI主线程。
 * 但这也带来了另外一个问题，就是事件同步。比如你触屏了一下，你需要surfaceView中thread
 * 处理，一般就需要有一个event queue的设计来保存touch event，这会稍稍复杂一点，因为涉
 * 及到线程同步。对于Surface相关的，Android底层还提供了GPU加速功能，所以一般实时性很强
 * 的应用中主要使用SurfaceView而不是直接从View构建.
 * 同时OpenGL中的GLSurfaceView也是从该类实现。
 * <P/>
 * SurfaceView是视图(View)的继承类，这个视图里内嵌了一个专门用于绘制的Surface。
 * 你可以控制这个Surface的格式和尺寸。Surfaceview控制这个Surface的绘制位置。
 * surface是纵深排序(Z-ordered)的，这表明它总在自己所在窗口的后面。
 * surfaceview提供了一个可见区域，只有在这个可见区域内 的surface部分内容才可见，
 * 可见区域外的部分不可见。surface的排版显示受到视图层级关系的影响，
 * 它的兄弟视图结点会在顶端显示。这意味者 surface的内容会被它的兄弟视图遮挡，
 * 这一特性可以用来放置遮盖物(overlays)(例如，文本和按钮等控件)。
 * 注意，如果surface上面 有透明控件，那么它的每次变化都会引起框架重新计算它和顶层控
 * 件的透明效果，这会影响性能。你可以通过SurfaceHolder接口访问这个surface.getHolder()
 * 方法可以得到这个接口。
 * surfaceview变得可见时，surface被创建；surfaceview隐藏前，surface被销毁。
 * 这样能节省资源。如果你要查看 surface被创建和销毁的时机，
 * 可以重载surfaceCreated(SurfaceHolder)和 surfaceDestroyed(SurfaceHolder)。
 * surfaceview的核心在于提供了两个线程：UI线程和渲染线程。这里应注意：
 * 	1> 所有SurfaceView和SurfaceHolder.Callback的方法都应该在UI线程里调用，
 * 一般来说就是应用程序主线程。渲染线程所要访问的各种变量应该作同步处理。
 * 	2> 由于surface可能被销毁，它只在SurfaceHolder.Callback.surfaceCreated()和
 *  SurfaceHolder.Callback.surfaceDestroyed()之间有效，所以要确保渲染线程访问的是
 *  合法有效的surface。<P/>
 * 
 * @author Hong
 *
 */
@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements SurfaceHolder.Callback, 
		Runnable, Button.OnClickListener {
	
	// 游戏状态
	private final static int STATE_IDLE		= 0;
	private final static int STATE_PLAY		= 1;
	private final static int STATE_PAUSE	= 2;
	private final static int STATE_OVER		= 3;
	
	// 按键ID
	private final static int BN_START		= 1;
	private final static int BN_BACK		= 2;
	private final static int BN_RESTART		= 3;
	private final static int BN_PAUSE		= 4;
	private final static int BN_RESUME		= 5;
	private final static int BN_BOMB		= 6;
	private final static int BN_FINISH		= 7;
	private final static int BN_SURE		= 8;
	private final static int BN_CANCEL		= 9;
	
	// 帧刷新时间
	private final static int FRAME_TIME		= 50;
	
	private BackGround backGround;
	private Player player;
	private Equipment equip;
	private List<Enemy> enemys;
	private List<Bullet> bullets;
	private int score;
	
	private Button bnStart;
	private Button bnBack;
	private Button bnRestart;
	private Button bnResume;
	private Button bnPause;
	private Button bnBomb;
	private Dialog dlgFinish;
	private Dialog dlgRestart;
	private Dialog dlgOvre;
	
	private SurfaceHolder holder;
	private Handler handler;
	private int state = STATE_IDLE;
	private boolean isRunning = false;
	
	private Paint paint;
	private int touchX = -1;
	private int touchY = -1;
	private int frameCount = 0;

	/**
	 * 构造函数。在这里会进行游戏的初始化。
	 * 
	 * @param context 应用上下文。
	 * @param handler 应用的Handler。
	 * @param screenWidth 屏宽
	 * @param screenHeight 屏高
	 */
	public GameView(Context context, Handler handler, 
			int screenWidth, int screenHeight) {
		super(context);
		this.handler = handler;
		GameHelper.init(context, screenWidth, screenHeight);
		
		// 初始化各元素
		backGround = new BackGround(5);
		player = Player.createPlayer();
		enemys = new LinkedList<Enemy>();
		bullets = new LinkedList<Bullet>(); 
		
		// 初始化按键
		bnStart = new Button(BN_START, "开始游戏", screenWidth / 2, 40);
		bnBack = new Button(BN_BACK, "退出游戏", screenWidth / 2, 40);
		bnRestart = new Button(BN_RESTART, "重新开始", screenWidth / 2, 40);
		bnResume = new Button(BN_RESUME, "返回游戏", screenWidth / 2, 40);
		bnPause = new Button(BN_PAUSE, GameHelper.getBitmap("pause.png"));
		bnBomb = new Button(BN_BOMB, GameHelper.getBitmap("bomb.png"));
		
		// 初始化对话框
		dlgFinish = new Dialog("注意", "是否退出游戏", 
				new int[]{BN_FINISH, BN_CANCEL}, 
				new String[]{"退出", "取消"}, 
				new Button.OnClickListener[]{this, this});
		dlgRestart = new Dialog("注意", "是否重新开始游戏", 
				new int[]{BN_SURE, BN_CANCEL}, 
				new String[]{"确定", "取消"}, 
				new Button.OnClickListener[]{this, this});
		dlgOvre = new Dialog("最终得分", "", 
				new int[]{BN_CANCEL}, 
				new String[]{"继续"}, 
				new Button.OnClickListener[]{this});
		
		// 设置按键监听器
		bnStart.setOnClickListener(this);
		bnBack.setOnClickListener(this);
		bnRestart.setOnClickListener(this);
		bnResume.setOnClickListener(this);
		bnPause.setOnClickListener(this);
		bnBomb.setOnClickListener(this);
		
		paint = new Paint();
		paint.setTextSize(32.0f);
		
		getHolder().addCallback(this);
		gameIdle();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	/**
	 * 创建surface，在这里启动绘图线程。
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder = holder;
		(new Thread(this)).start();
	}

	/**
	 * 销毁surface，在这里关闭绘图线程。
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	/**
	 * 绘图线程主体
	 */
	@Override
	public void run() {
		System.out.println("Game start.");
		isRunning = true;
		
		try {
			while(isRunning) {
				long t1 = System.currentTimeMillis();
				Canvas canvas = holder.lockCanvas();
				
				try {
					// 绘制游戏内容。
					onPaint(canvas);	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				holder.unlockCanvasAndPost(canvas);
				long t2 = System.currentTimeMillis();
				if(t2 - t1 < FRAME_TIME) {
					try {
						Thread.sleep(FRAME_TIME - (t2 - t1));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				frameCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		isRunning = false;
		System.out.println("Game finish.");
	}

	/**
	 * 键盘事件响应处。这里只响应返回按键。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(state == STATE_IDLE)
				onClick(BN_BACK);
			else if(state == STATE_PLAY)
				onClick(BN_PAUSE);
			else if(state == STATE_PAUSE) {
				if(dlgRestart.isVisible())
					onClick(BN_CANCEL);
				else
					onClick(BN_BACK);
			}
			else if(state == STATE_OVER)
				onClick(BN_CANCEL);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 触摸事件响应处。不同的游戏状态会分配给不同的元素响应。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int index = event.getActionIndex();
		int id = event.getPointerId(index);
		if(id != 0)
			return super.onTouchEvent(event);
		
		int act = event.getActionMasked();
		int x = (int) event.getX(index);
		int y = (int) event.getY(index);
		
		if(state == STATE_IDLE) {
			bnStart.onTouch(act, x, y);
			bnBack.onTouch(act, x, y);
			dlgFinish.onTouch(act, x, y);
		}
		else if(state == STATE_PLAY) {
			bnPause.onTouch(act, x, y);
			bnBomb.onTouch(act, x, y);
			
			// 玩家飞机的触摸响应内容。
			if(act == MotionEvent.ACTION_DOWN || 
					act == MotionEvent.ACTION_POINTER_DOWN) {
				touchX = x;
				touchY = y;
			}
			else if(act == MotionEvent.ACTION_MOVE) {
				int dx = x - touchX;
				int dy = y - touchY;
				touchX = x;
				touchY = y;
				player.move(dx, dy);
			}
			else {
				touchX = -1;
				touchY = -1;
			}
		}
		else if(state == STATE_PAUSE) {
			bnBack.onTouch(act, x, y);
			bnRestart.onTouch(act, x, y);
			bnResume.onTouch(act, x, y);
			dlgFinish.onTouch(act, x, y);
			dlgRestart.onTouch(act, x, y);
		}
		else if(state == STATE_OVER) {
			dlgOvre.onTouch(act, x, y);
		}
		else {
			return super.onTouchEvent(event);
		}
		return true;
	}
	
	/**
	 * 按键点击事件响应处
	 */
	@Override
	public void onClick(int id) {
		switch(id) {
		case BN_START:	// 开始游戏按键
			gameStart();
			break;
		case BN_BACK:	// 退出游戏按键
			bnStart.setVisible(false);
			bnBack.setVisible(false);
			bnResume.setVisible(false);
			bnRestart.setVisible(false);
			dlgFinish.setVisible(true);
			break;
		case BN_RESTART:// 重新开始游戏按键
			bnResume.setVisible(false);
			bnRestart.setVisible(false);
			bnBack.setVisible(false);
			dlgRestart.setVisible(true);
			break;
		case BN_PAUSE:	// 暂停游戏按键
			gamePause();
			break;
		case BN_RESUME:	// 继续游戏按键
			gameResume();
			break;
		case BN_BOMB:	// 使用炸弹按键
			if(state == STATE_PLAY) {
				score += useBomb();
			}
			break;
		case BN_FINISH:	// 对话框退出按键
			handler.sendEmptyMessage(0);
			break;
		case BN_SURE:	// 对话框确定重新开始游戏按键
			gameStart();
			break;
		case BN_CANCEL:	// 对话框取消按键
			dlgFinish.setVisible(false);
			dlgRestart.setVisible(false);
			dlgOvre.setVisible(false);
			if(state == STATE_IDLE || state == STATE_OVER)
				gameIdle();
			if(state == STATE_PAUSE)
				gamePause();
			break;
		}
	}
	
	/**
	 * 暂停游戏
	 */
	public void onPause() {
		gamePause();
	}

	/**
	 * 绘制游戏内容。
	 * 
	 * @param canvas 画布
	 */
	private void onPaint(Canvas canvas) {
		backGround.paint(canvas);
		
		// 当暂停或游戏时我们都要绘制玩家，敌人，子弹等游戏的内容。
		if(state == STATE_PAUSE || state == STATE_PLAY) {
			// 绘制玩家
			player.paint(canvas);
			// 绘制敌人
			for(Enemy e : enemys)
				e.paint(canvas);
			// 绘制子弹
			for(Bullet b : bullets)
				b.paint(canvas);
			// 绘制装备
			if(equip != null)
				equip.paint(canvas);
			
			// 如果是游戏状态我们还要对各个元素进行移动和碰撞检测等。
			if(state == STATE_PLAY) {
				if(!player.isVisible())
					gameOver();
				
				bnPause.paint(canvas);
				
				// 碰撞检测
				score += GameHelper.collideDetect(bullets, enemys);
				GameHelper.collideDetect(player, equip);
				GameHelper.collideDetect(player, enemys);
				// 刷新敌人
				if(frameCount % 2 == 0)
					GameHelper.refreshEnemy(enemys, true);
				GameHelper.time += FRAME_TIME;
				
				// 换帧与行动
				backGround.scroll();
				if(frameCount % 2 == 0) player.nextFrame();
				for(Enemy e : enemys) {
					e.move();
					if(frameCount % 2 == 0) e.nextFrame();
				}
				if(equip != null && equip.isVisible())
					equip.move();

				// 玩家开火
				Bullet.clearBullets(bullets);
				if(frameCount % 2 == 0) player.fire(bullets);
				
				// 每30秒就创建一个装备
				if(GameHelper.time % (10000 * 3) == 0)
					equip = GameHelper.createEquipment();
			}
			
			// 绘制得分与炸弹数量
			canvas.drawText(Integer.toString(score), bnPause.getWidth() + 10, 
					40, paint);
			int bombCount = player.getBombCount();
			if(bombCount > 0) {
				bnBomb.setVisible(true);
				bnBomb.paint(canvas);
				canvas.drawText("X " + bombCount, bnBomb.getWidth() + 10, 
						bnBomb.getY() + 45, paint);
			}
			else
				bnBomb.setVisible(false);
		}

		// 绘制按键
		bnStart.paint(canvas);
		bnBack.paint(canvas);
		bnResume.paint(canvas);
		bnRestart.paint(canvas);
		bnBack.paint(canvas);
		
		// 绘制对话框
		dlgFinish.paint(canvas);
		dlgRestart.paint(canvas);
		dlgOvre.paint(canvas);
	}
	
	/**
	 *  游戏进行休闲状态
	 */
	private void gameIdle() {
		state = STATE_IDLE;
		
		player.setVisible(false);
		
		bnStart.setVisible(true);
		bnBack.setVisible(true);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(false);
		bnBomb.setVisible(false);
		
		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnStart.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 - 50);
		bnBack.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 + 50);
	}
	
	/**
	 *  开始游戏，进入游戏状态
	 */
	private void gameStart() {
		state = STATE_PLAY;
		score = 0;
		GameHelper.time = 0;
		
		player.relive();
		player.setRefPixelPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight - player.getHeight() / 2);
		player.setVisible(true);
		
		for(Enemy e : enemys)
			e.setVisible(false);
		GameHelper.refreshEnemy(enemys, false);
		bullets.clear();

		bnStart.setVisible(false);
		bnBack.setVisible(false);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(true);
		bnBomb.setVisible(false);
		
		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnPause.setPosition(0, 0);
		bnBomb.setPosition(0, GameHelper.screenHeight - bnBomb.getHeight());
	}
	
	/**
	 * 游戏进入暂停状态
	 */
	private void gamePause() {
		state = STATE_PAUSE;

		bnStart.setVisible(false);
		bnBack.setVisible(true);
		bnRestart.setVisible(true);
		bnResume.setVisible(true);
		bnPause.setVisible(false);

		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnResume.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 - 100);
		bnRestart.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2);
		bnBack.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 + 100);
	}
	
	/**
	 * 唤醒游戏，进入游戏状态。
	 */
	private void gameResume() {
		state = STATE_PLAY;

		bnStart.setVisible(false);
		bnBack.setVisible(false);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(true);

		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnPause.setPosition(0, 0);
		bnBomb.setPosition(0, GameHelper.screenHeight - bnBomb.getHeight());
	}
	
	/**
	 * 游戏结束。
	 */
	private void gameOver() {
		state = STATE_OVER;

		bnStart.setVisible(false);
		bnBack.setVisible(false);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(false);

		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(true);
		
		dlgOvre.setText(Integer.toString(score));
	}
	
	/**
	 * 使用炸弹。这里会计算所有敌人的分数。
	 * 
	 * @return 分数。
	 */
	private int useBomb() {
		int score = 0;
		if(player.isVisible() && player.isAlive() && player.useBomb()) {
			for(Enemy e : enemys) {
				if(e.isAlive() && e.isVisible()) {
					e.bombed();
					score += e.getScore();
				}
			}
		}
		return score;
	}

}
