package com.CES.example.game;

import javax.microedition.lcdui.game.Layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * 按键类，继承于Layer类。按键有两种类型。类型1是由画笔画出来的，
 * 类型2是一张图片。
 * 
 * @author Hong
 *
 */
public class Button extends Layer {
	
	private int id;
	private Bitmap src;
	private String text;
	private int step = 0;
	
	private Paint paint;
	private RectF rect;
	private Rect rectSrc;
	private int centerX;
	private int centerY;
	private int type;
	
	private OnClickListener listener;

	/**
	 * 按键类型1的构造函数。
	 * 
	 * @param id 按键ID，这个ID是用于点击事件响应的。必须是惟一的。
	 * @param text 按键文本内容
	 * @param width 按键宽
	 * @param height 按键高
	 */
	public Button(int id, String text, int width, int height) {
		super(width, height);
		this.id = id;
		this.text = text;
		this.type = 1;

		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setDither(true);
		this.paint.setStrokeJoin(Paint.Join.ROUND);
		this.paint.setStrokeCap(Paint.Cap.ROUND);
		this.paint.setTextSize(25.0f);
		
		rect = new RectF();
		rectSrc = new Rect();
		centerX = (getX() + width) / 2;
		centerY = (getY() + height) / 2;
	}
	
	/**
	 * 按键类型2的构造函数。
	 * 
	 * @param id 按键ID，这个ID是用于点击事件响应的。必须是惟一的。
	 * @param src 按键图片。
	 */
	public Button(int id, Bitmap src) {
		super(src.getWidth(), src.getHeight());
		this.id = id;
		this.src = src;
		this.type = 2;
		
		rectSrc = new Rect();
		centerX = (getX() + src.getWidth()) / 2;
		centerY = (getY() + src.getHeight()) / 2;
	}

	/**
	 * 绘制。
	 */
	@Override
	public void paint(Canvas canvas) {
		if(type == 1)
			paintType1(canvas);
		else
			paintType2(canvas);
	}
	
	/**
	 * 绘制按键类型1。这里会有一个弹出与关闭时的动态效果。
	 * 
	 * @param canvas 画布
	 */
	private void paintType1(Canvas canvas) {
		if(isVisible())
			step = (step < 3)? step + 1 : 3;
		else
			step = (step > 0)? step - 1 : 0;
		
		if(step <= 0)
			return;

		int alpha = 0;
		int w = getWidth();
		int h = getHeight();
		if(step == 1) {
			rect.set(centerX - w / 6, centerY - h / 6, 
					centerX + w / 6, centerY + h / 6);
			alpha = 20;
		}
		else if(step == 2) {
			rect.set(centerX - w / 3, centerY - h / 3, 
					centerX + w / 3, centerY + h / 3);
			alpha = 80;
		}
		else if(step == 3) {
			rect.set(centerX - w / 2, centerY - h / 2, 
					centerX + w / 2, centerY + h / 2);
			alpha = 255;
		}
		
		paint.setColor(Color.argb(alpha, 220, 220, 220));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, paint);
		paint.setColor(Color.argb(alpha, 0, 0, 0));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, paint);
		if(step == 3) {
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(1.0f);
			paint.getTextBounds(text, 0, text.length(), rectSrc);
			canvas.drawText(text, centerX - rectSrc.width() / 2, 
					centerY + rectSrc.height() / 2 - 2, paint);
		}
	}
	
	/**
	 * 绘制按键类型2。
	 * 
	 * @param canvas 画布
	 */
	private void paintType2(Canvas canvas) {
		if(!isVisible())
			return;
		
		rectSrc.left = getX();
		rectSrc.top = getY();
		rectSrc.right = rectSrc.left + getWidth();
		rectSrc.bottom = rectSrc.top + getHeight();
		
		canvas.drawBitmap(src, null, rectSrc, null);
	}
	
	/**
	 * 触摸事件。用于是否点击了按键。
	 * 
	 * @param act 触摸动作类型
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public void onTouch(int act, int x, int y) {
		if(!isVisible())
			return;
		
		if(act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_POINTER_UP) {
			int left = getX();
			int right = left + getWidth();
			int top = getY();
			int bottom = top + getHeight();
			if(listener != null && 
					x >= left && x < right && y >= top && y < bottom) {
				GameHelper.playSound(R.raw.button);
				listener.onClick(id);
			}
		}
	}

	/**
	 * 设置按键的文本内容。
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * 设置按键的中心位置
	 * 
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public void setCenterPosition(int x, int y) {
		int dx = x - centerX;
		int dy = y - centerY;
		move(dx, dy);
		centerX = x;
		centerY = y;
	}
	
	/**
	 * 设置点击监听器
	 * 
	 * @param l 监听器
	 */
	public void setOnClickListener(OnClickListener l) {
		this.listener = l;
	}

	/**
	 * 点击监听器接口。
	 * 
	 * @author Hong
	 *
	 */
	public interface OnClickListener {
		void onClick(int id);
	}

}
