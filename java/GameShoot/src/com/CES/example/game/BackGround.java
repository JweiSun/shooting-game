package com.CES.example.game;

import javax.microedition.lcdui.game.TiledLayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * 背景类。这个是游戏的背景。这个背景是无限循环滚动的。
 * 这个类里有一个TiledLayer，并且是由这个TiledLayer构建出整张背景图片。
 * 
 * @author Hong
 *
 */
public class BackGround {
	
	private TiledLayer layer;
	private int speed;
	
	private int tileColumns, tileRows;
	private int layerColumns, layerRows;

	/**
	 * 构造函数。
	 * 
	 * @param speed 滚动速度
	 */
	public BackGround(int speed) {
		int tileWidth = 40;
		int tileHeight = 50;
		Bitmap image = GameHelper.getBitmap("background.png");

		tileColumns = image.getWidth() / tileWidth;
		tileRows = image.getHeight() / tileHeight;
		layerColumns = GameHelper.screenWidth / tileWidth + 1;
		layerRows = GameHelper.screenHeight / tileHeight + 1;

		// 初始化TiledLayer
		layer = new TiledLayer(layerColumns, layerRows, 
				image, tileWidth, tileHeight);
		for(int i = 0; i < layerRows; i++) {
			for(int j = 0; j < layerColumns; j++) {
				int tile = (i % tileRows) * tileColumns + ((j % tileColumns) + 1);
				layer.setCell(j, i, tile);
			}
		}

		this.speed = speed;
	}
	
	/**
	 * 背景滚动。这个滚动实际上是修改TiledLayer的位置与每个砖块的内容来实现的。
	 */
	public void scroll() {
		int y = layer.getY() + speed;
		int cellH = layer.getCellHeight();
		int dr = (y + cellH) / cellH;
		
		y -= dr * cellH;
		layer.setPosition(layer.getX(), y);

		int tileCount = tileColumns * tileRows;
		for(int i = 0; i < layerRows; i++) {
			for(int j = 0; j < layerColumns; j++) {
				int currenTile = layer.getCell(j, i);
				currenTile -= dr * tileColumns;
				if(currenTile <= 0)
					currenTile += tileCount;
				layer.setCell(j, i, currenTile);
			}
		}
	}
	
	/**
	 * 绘制。实际就是绘制TiledLayer。
	 * 
	 * @param canvas 画布
	 */
	public void paint(Canvas canvas) {
		layer.paint(canvas);
	}

	/**
	 * 获取滚动速度
	 * 
	 * @return 滚动速度
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * 设置滚动速度。
	 * 
	 * @param speed 滚动速度
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
