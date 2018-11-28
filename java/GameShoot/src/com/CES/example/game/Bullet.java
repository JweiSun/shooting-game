package com.CES.example.game;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;

/**
 * 子弹类，继承于Sprite类。这个子弹是由玩家飞机发射出来的。有两种类型。
 * 
 * @author Hong
 *
 */
public class Bullet extends Sprite {
	
	public final static int TYPE1 = 0;
	public final static int TYPE2 = 1;
	
	private static int[][] sequence = new int[][]{{0}, {1}};
	
	private static List<Bullet> tmp;

	/**
	 * 构造函数。
	 * 
	 * @param image 子弹图片。
	 * @param frameWidth 帧宽
	 * @param frameHeight 帧高
	 * @param type 子弹类型
	 */
	private Bullet(Bitmap image, int frameWidth, int frameHeight, int type) {
		super(image, frameWidth, frameHeight);
		setFrameSequence(sequence[type]);
	}
	
	/**
	 * 创建函数。
	 * 
	 * @param type 子弹类型。
	 * @return
	 */
	public static Bullet createBullet(int type) {
		if(type != TYPE1 && type != TYPE2)
			throw new IllegalArgumentException("Unkown type.");
		
		Bitmap image = GameHelper.getBitmap("bullet.png");
		int frameWidth = image.getWidth() / 2;
		int frameHeight = image.getHeight();
		Bullet bullet = new Bullet(image, frameWidth, frameHeight, type);
		return bullet;
	}
	
	/**
	 * 清除子弹。将不可见的子弹从子弹列表里清除出去。
	 *  
	 * @param bullets 子弹列表
	 */
	public static void clearBullets(List<Bullet> bullets) {
		if(tmp == null)
			tmp = new LinkedList<Bullet>();
		for(Bullet b : bullets) {
			if(!b.isVisible())
				tmp.add(b);
		}
		bullets.removeAll(tmp);
		tmp.clear();
	}
	
	/**
	 * 子弹移动。一旦移出了屏幕就设为不可见。
	 */
	public void move() {
		if(!isVisible())
			return;
		
		move(0, -getHeight());
		if(getY() < -getHeight())
			setVisible(false);
	}

}
