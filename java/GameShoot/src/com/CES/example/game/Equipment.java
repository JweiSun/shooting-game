package com.CES.example.game;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;

/**
 * 装备类，继承于Sprite。在两类型：一个是双枪，另一个是炸弹。
 * 
 * @author Hong
 *
 */
public class Equipment extends Sprite {
	
	public final static int TYPE_DOUBLE	= 0;
	public final static int TYPE_BOMB	= 1;
	
	private static int[][] sequence = new int[][]{{0}, {1}};
	private static int[] speed = new int[]{50, 50, 40, 25, -10,
		-40, -40, -35, 0, 40, 45, 50, 60};
	
	private int type;
	private int speedIndex;

	/**
	 * 构造函数。
	 * 
	 * @param image 装备图片。
	 * @param frameWidth 帧宽
	 * @param frameHeight 帧高
	 * @param type 类型
	 */
	private Equipment(Bitmap image, int frameWidth, int frameHeight, int type) {
		super(image, frameWidth, frameHeight);
		setFrameSequence(sequence[type]);
		defineCollisionRectangle(10, 10, 50, 90);
		this.type = type;
		this.speedIndex = 0;
	}
	
	/**
	 * 创建装备。
	 * 
	 * @param type 类型
	 * @return 一个装备Equipment实例。
	 */
	public static Equipment createEquipment(int type) {
		if(type != TYPE_DOUBLE && type != TYPE_BOMB)
			throw new IllegalArgumentException("Unkown type.");
		
		Bitmap image = GameHelper.getBitmap("equip.png");
		int frameWidth = image.getWidth() / 2;
		int frameHeight = image.getHeight();
		Equipment equip = new Equipment(image, frameWidth, frameHeight, type);
		
		return equip;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 移动。这个移动是有一个特定的轨迹。
	 */
	public void move() {
		move(0, speed[speedIndex]);
		speedIndex = (speedIndex == speed.length - 1)? speedIndex : speedIndex + 1;
		if(getY() > GameHelper.screenHeight)
			setVisible(false);
	}

}
