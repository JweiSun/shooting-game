package com.CES.example.game;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;

/**
 * 敌人类，继承于Sprite。有三个类型。
 * @author Hong
 *
 */
public class Enemy extends Sprite {
	
	public final static int TYPE1 = 1;
	public final static int TYPE2 = 2;
	public final static int TYPE3 = 3;
	
	private int type;
	private int score;
	private int speed;
	private int live;
	private boolean isAlive;
	
	private int[] flySequence;	// 飞行帧序
	private int[] bombSequence;	// 爆炸帧序
	private int hitFrame;		// 击中帧

	/**
	 * 构造函数，不同的类型有不同的飞行帧序，爆炸帧序，击中帧，分数和碰撞范围。
	 * 
	 * @param image 敌人图片
	 * @param frameWidth 帧宽
	 * @param frameHeight 帧高
	 * @param type 类型
	 */
	private Enemy(Bitmap image, int frameWidth, int frameHeight, int type) {
		super(image, frameWidth, frameHeight);
		
		this.type = type;
		this.live = 0;
		this.isAlive = false;
		
		if(type == TYPE1) {
			flySequence = new int[]{0};
			bombSequence = new int[]{1, 2, 3, 4, 4};
			hitFrame = 0;
			score = 100;
			defineCollisionRectangle(5, 10, 45, 30);
		}
		else if(type == TYPE2){
			flySequence = new int[]{0, 1};
			bombSequence = new int[]{2, 3, 4, 5, 5};
			hitFrame = 1;
			score = 500;
			defineCollisionRectangle(15, 15, 50, 70);
		}
		else if(type == TYPE3) {
			flySequence = new int[]{0, 1, 2};
			bombSequence = new int[]{3, 4, 5, 6, 7, 8, 8};
			hitFrame = 2;
			score = 1000;
			defineCollisionRectangle(20, 20, 130, 220);
		}
		
		setFrameSequence(flySequence);
	}
	
	/**
	 * 创建敌人。不同的类型有不同的图片。
	 * 
	 * @param type 敌人类型
	 * @return 敌人Enemy类的实例
	 */
	public static Enemy createEnemy(int type) {
		Bitmap image = null;
		int frameWidth = 0;
		int frameHeight = 0;
		
		if(type == TYPE1) {
			image = GameHelper.getBitmap("enemy1.png");
			frameWidth = image.getWidth() / 5;
			frameHeight = image.getHeight();
		}
		else if(type == TYPE2) {
			image = GameHelper.getBitmap("enemy2.png");
			frameWidth = image.getWidth() / 6;
			frameHeight = image.getHeight();
		}
		else if(type == TYPE3) {
			image = GameHelper.getBitmap("enemy3.png");
			frameWidth = image.getWidth() / 9;
			frameHeight = image.getHeight();
		}
		else {
			throw new IllegalArgumentException("Unkown type enemy.");
		}
		
		Enemy enemy = new Enemy(image, frameWidth, frameHeight, type);
		
		return enemy;
	}
	
	/**
	 * 下一帧。如果是存活状态，就要跳过击中帧。
	 * 如果在爆炸状态，当爆炸帧显示完就要设置为不可见。
	 */
	@Override
	public void nextFrame() {
		super.nextFrame();
		if(isAlive && hitFrame != 0 && getFrame() == hitFrame)
			nextFrame();
		if(!isAlive || isVisible()) {
			int index = getFrame();
			if(index == bombSequence.length - 1)
				setVisible(false);
		}
	}

	/**
	 * 复活。不同类型有不同的生命值。
	 * 
	 * @param speed 复活后的速度。
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public void relive(int speed, int x, int y) {
		this.speed = speed;
		this.live = (type == TYPE1)? 1 : (type == TYPE2)? 15 : 40;
		this.isAlive = true;
		setPosition(x, y);
		setFrameSequence(flySequence);
		setVisible(true);
	}
	
	/**
	 * 移动。当移出屏幕就要设为不可见。
	 */
	public void move() {
		if(!isAlive || !isVisible())
			return;
		
		move(0, speed);
		if(getY() > GameHelper.screenHeight)
			setVisible(false);
	}
	
	/**
	 * 被击中。并将当前帧设为击中帧。
	 */
	public void hited() {
		if(!isAlive || !isVisible())
			return;
		
		setFrame(hitFrame);
		if(--live == 0) {
			isAlive = false;
			setFrameSequence(bombSequence);
			if(type == TYPE1)
				GameHelper.playSound(R.raw.enemy1_down);
			else if(type == TYPE2)
				GameHelper.playSound(R.raw.enemy2_down);
			else if(type == TYPE3)
				GameHelper.playSound(R.raw.enemy3_down);
		}
	}
	
	/**
	 * 爆炸。当玩家使用炸弹时敌人会爆炸。
	 */
	public void bombed() {
		if(!isAlive || !isVisible())
			return;
		
		live = 0;
		isAlive = false;
		setFrameSequence(bombSequence);
	}

	/**
	 * 是否存活。
	 * 
	 * @return 存活返回true，否则返回false。
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * 获取速度
	 * 
	 * @return 速度
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * 获取分数。
	 * 
	 * @return 分数
	 */
	public int getScore() {
		return score;
	}

	/**
	 * 获取类型。
	 * 
	 * @return 类型
	 */
	public int getType() {
		return type;
	}

}
