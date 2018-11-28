package com.CES.example.game;

import java.util.List;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;

/**
 * 玩家类，继承于Sprite；
 * 
 * @author Hong
 *
 */
public class Player extends Sprite {
	
	private boolean isAlive;
	private int doubleGunTime;
	private int bombCount;
	private int fireCount;
	
	private int[] flySequence;	// 飞行帧
	private int[] bombSequence;	// 爆炸帧

	/**
	 * 构造函数。
	 * 
	 * @param image 玩家飞机图片
	 * @param frameWidth 帧宽
	 * @param frameHeight 帧高
	 */
	private Player(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight);
		this.isAlive = true;
		this.flySequence = new int[]{0, 1};
		this.bombSequence = new int[]{2, 3, 4, 4};
		this.setFrameSequence(flySequence);
		this.defineReferencePixel(frameWidth / 2, frameHeight / 2);
		this.defineCollisionRectangle(20, 20, 70, 80);
	}
	
	/**
	 * 创建玩家
	 * 
	 * @return 玩家Player实例
	 */
	public static Player createPlayer() {
		Bitmap image = GameHelper.getBitmap("player.png");
		int frameCount = 5;
		int frameWidth = image.getWidth() / frameCount;
		int frameHeight = image.getHeight();
		Player player = new Player(image, frameWidth, frameHeight);
		return player;
	}
	
	/**
	 * 移动。会去检测屏的边框。防止超出屏的范围。
	 */
	@Override
	public void move(int dx, int dy) {
		if(!isAlive || !isVisible())
			return;
		
		int x = getX();
		int y = getY();
		int w = getWidth();
		int h = getHeight();
		
		if(x + dx < 0)
			dx = -x;
		if(x + dx + w > GameHelper.screenWidth)
			dx = GameHelper.screenWidth - x - w;
		if(y + dy < 0)
			dy = -y;
		if(y + dy + w > GameHelper.screenHeight)
			dy = GameHelper.screenHeight - y - h;
		
		super.move(dx, dy);
	}

	/**
	 * 下一帧。如果不是存活状态下，当显示到爆炸帧序最后一帧时设为不可见。
	 */
	@Override
	public void nextFrame() {
		super.nextFrame();
		if(!isAlive || isVisible()) {
			int index = getFrame();
			if(index == bombSequence.length - 1)
				setVisible(false);
		}
	}

	/**
	 * 移动到屏幕某一位置。会检测屏的边框。
	 * 
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public void moveTo(int x, int y) {
		if(!isAlive || !isVisible())
			return;
		
		int w = getWidth();
		int h = getHeight();
		if(x - w / 2 < 0)
			x = w / 2;
		if(x + w / 2 > GameHelper.screenWidth)
			x = GameHelper.screenWidth - w / 2;
		if(y - h / 2 < 0)
			y = h / 2;
		if(y + h / 2 > GameHelper.screenHeight)
			y = GameHelper.screenHeight - h / 2;
		setRefPixelPosition(x, y);
	}
	
	/**
	 * 被撞击。被撞击后会死亡。将当前帧序换成爆炸帧序。
	 */
	public void knocked() {
		if(!isAlive || !isVisible())
			return;
		isAlive = false;
		setFrameSequence(bombSequence);
		GameHelper.playSound(R.raw.game_over);
	}
	
	/**
	 * 发射子弹。如果还有双枪时间就发射两颗子弹，否则发射一颗子弹。
	 * 
	 * @param bullets 子弹列表。
	 */
	public void fire(List<Bullet> bullets) {
		if(!isAlive || !isVisible())
			return;
		
		if(doubleGunTime > 0) {
			Bullet b = Bullet.createBullet(Bullet.TYPE2);
			b.setPosition(getX() + getWidth() / 2 - b.getWidth() / 2 - 15,
					getY() - (fireCount % 6) * 25 - b.getHeight());
			b.setVisible(true);
			bullets.add(b);
			
			b = Bullet.createBullet(Bullet.TYPE2);
			b.setPosition(getX() + getWidth() / 2 - b.getWidth() / 2 + 15,
					getY() - (fireCount % 6) * 25 - b.getHeight());
			b.setVisible(true);
			bullets.add(b);
			doubleGunTime--;
		}
		else {
			Bullet b = Bullet.createBullet(Bullet.TYPE1);
			b.setPosition(getX() + getWidth() / 2 - b.getWidth() / 2,
					getY() - (fireCount % 6) * 25 - b.getHeight());
			b.setVisible(true);
			bullets.add(b);
		}
		fireCount++;
		GameHelper.playSound(R.raw.fire);
	}

	/**
	 * 复活。并将当前帧序设为飞行帧序
	 */
	public void relive() {
		this.isAlive = true;
		this.doubleGunTime = 0;
		this.bombCount = 0;
		this.fireCount = 0;
		setFrameSequence(flySequence);
	}

	/**
	 * 是否存活。
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * 捡到双枪装备。
	 */
	public void doubleGun() {
		doubleGunTime = 200;
		GameHelper.playSound(R.raw.get_double_gun);
	}
	
	/**
	 * 捡到炸弹。
	 */
	public void addBomb() {
		bombCount++;
		GameHelper.playSound(R.raw.get_bomb);
	}
	
	/**
	 * 使用炸弹。
	 * 
	 * @return 有炸弹用返回true，否则返回false。
	 */
	public boolean useBomb() {
		if(bombCount > 0) {
			bombCount--;
			GameHelper.playSound(R.raw.use_bomb);
			return true;
		}
		else
			return false;
	}

	/**
	 * 获取炸弹数量。
	 * 
	 * @return
	 */
	public int getBombCount() {
		return bombCount;
	}

}
