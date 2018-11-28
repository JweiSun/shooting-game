package com.CES.example.game;

import javax.microedition.lcdui.game.Layer;

import com.CES.example.game.Button.OnClickListener;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 对话框类。这个对话框可以有一个按键也可以有两个按键。
 * 
 * @author Hong
 *
 */
public class Dialog extends Layer {
	
	private Paint paint;
	private RectF rect;
	private Rect rectTxt;
	private int step = 0;
	
	private String title;
	private String text;
	
	private int buttonCount;
	private Button button1;
	private Button button2;

	/**
	 * 构造函数。
	 * 
	 * @param title 标题
	 * @param text 文本内容
	 * @param bnIds 按键ID
	 * @param bnText 按键文本内容
	 * @param bnListener 按键监听器。
	 */
	public Dialog(String title, String text, 
			int[] bnIds, String[] bnText, OnClickListener[] bnListener) {
		super(GameHelper.screenWidth * 3 / 4, 200);
		
		if(title == null)
			throw new NullPointerException();
		
		this.title = title;
		this.text = text;
		buttonCount = bnIds.length;
		if(buttonCount ==0 || buttonCount > 2 ||
				bnText.length < buttonCount || bnListener.length < buttonCount)
			throw new IllegalArgumentException();
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		rect = new RectF();
		rectTxt = new Rect();
		
		button1 = new Button(bnIds[0], bnText[0], 100, 40);
		button1.setOnClickListener(bnListener[0]);
		if(buttonCount == 2) {
			button2 = new Button(bnIds[1], bnText[1], 100, 40);
			button2.setOnClickListener(bnListener[1]);
		}
		
		setPosition(GameHelper.screenWidth / 2 - getWidth() / 2, 
				GameHelper.screenHeight / 2 - getHeight() / 2);
		if(buttonCount == 1) {
			button1.setCenterPosition(GameHelper.screenWidth / 2, 
					getY() + getHeight() - 25);
		}
		else {
			button1.setCenterPosition(getX() + getWidth() / 4, 
					getY() + getHeight() - 25);
			button2.setCenterPosition(getX() + (getWidth() * 3) / 4, 
					getY() + getHeight() - 25);
		}
	}

	/**
	 * 绘制。
	 */
	@Override
	public void paint(Canvas canvas) {
		if(isVisible())
			step = (step < 2)? step + 1 : 2;
		else
			step = (step > 0)? step - 1 : 0;
		
		if(step <= 0)
			return;

		int alpha = 0;
		int line1 = 0, line2 = 0;
		int w = getWidth();
		int h = getHeight();
		if(step == 1) {
			rect.set(getX() + w / 4, getY() + 20, 
					getX() + (w * 3) / 4, getY() + h - 20);
			line1 = (int) (rect.top + rect.height() / 3);
			line2 = (int) (rect.top + (rect.height() * 2) / 3);
			alpha = 80;
		}
		else if(step == 2) {
			rect.set(getX(), getY(), getX() + w, getY() + h);
			line1 = (int) (rect.top + 60);
			line2 = (int) (rect.top + rect.height() - 50);
			alpha = 255;
		}
		
		// 绘制边框
		paint.setColor(Color.argb(alpha, 200, 200, 200));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRoundRect(rect, 5, 5, paint);
		paint.setColor(Color.argb(alpha, 0, 0, 0));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		canvas.drawRoundRect(rect, 5, 5, paint);
		canvas.drawLine(rect.left, line1, rect.right, line1, paint);
		canvas.drawLine(rect.left, line2, rect.right, line2, paint);
		
		// 绘制按键
		button1.paint(canvas);
		if(buttonCount == 2)
			button2.paint(canvas);
		
		if(step == 2) {
			// 绘制文字。
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(2.0f);
			paint.setTextSize(28.0f);
			canvas.drawText(title, getX() + 10, getY() + 40, paint);
			paint.getTextBounds(text, 0, text.length(), rectTxt);
			canvas.drawText(text, rect.centerX() - rectTxt.width() / 2, 
					rect.centerY(), paint);
		}
	}

	/**
	 * 设置可见情况。同时会去设置按键的可见情况。
	 */
	@Override
	public void setVisible(boolean visible) {
		button1.setVisible(visible);
		if(buttonCount == 2)
			button2.setVisible(visible);
		super.setVisible(visible);
	}

	/**
	 * 设置文本内容。
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * 触摸事件。会将这个事件交由按键处理。
	 * 
	 * @param act 触摸动作类型
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public void onTouch(int act, int x, int y) {
		button1.onTouch(act, x, y);
		if(buttonCount == 2)
			button2.onTouch(act, x, y);
	}

}
