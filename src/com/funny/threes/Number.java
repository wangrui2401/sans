package com.funny.threes;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;



public class Number extends View {

	int x, y, width, height, originX, originY;
	int totalMoveX, totalMoveY;
	int number;
	int color;
	int bgId;
	Context context;
	private static final int TRANSLATE_ANIMATION_DURATION = 500;
	private static final int TRANSLATE_ANIMATION_INTERVAL = 50;
	private static final int ROTATE_ANIMATION_DURATION = 500;
	private static final int ROTATE_ANIMATION_INTERVAL = 50;
	
	public Number(Context context) {
		super(context);
		this.context = context;
	}
	
	public Number(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void initNumber(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.originX = x;
		this.originY = y;
		this.width = width;
		this.height = height;
		this.color = (int)(Math.random()*Integer.MAX_VALUE) & 0xFFFFFFFF;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public void eatNumber(Number number) {
		
	}
	
	public void move(int direction, int deltaX, int deltaY) {
		switch(direction) {
		case Container.MOVE_LEFT:
			if(totalMoveX + deltaX > 0) {
				x = originX;
				totalMoveX = 0;
			} else if(totalMoveX + deltaX > -width) {
				x = x + deltaX;
				totalMoveX += deltaX;
			}
			break;
		case Container.MOVE_RIGHT:
			if(totalMoveX + deltaX < 0) {
				x = originX;
				totalMoveX = 0;
			} else if(totalMoveX + deltaX < width) {
				x = x + deltaX;
				totalMoveX += deltaX;
			}
			break;
		case Container.MOVE_UP:
			if(totalMoveY + deltaY > 0) {
				y = originY;
				totalMoveY = 0;
			} else if(totalMoveY + deltaY > -height) {
				y = y + deltaY;
				totalMoveY += deltaY;
			}
			break;
		case Container.MOVE_DOWN:
			if(totalMoveY + deltaY < 0) {
				y = originY;
				totalMoveY = 0;
			} else if(totalMoveY + deltaY < height) {
				y = y + deltaY;
				totalMoveY += deltaY;
			} 
			break;
		}
	}
	
	public void stepOperation(int direction, boolean move) {
		int ex = x, ey = y;
		switch(direction) {
		case Container.MOVE_UP:
			if(move) {
				ey = originY - height;
//				originY = y;
			} else {
				ey = originY;
			}
			break;
		case Container.MOVE_RIGHT:
			if(move) {
				ex = originX + width;
//				originX = x;
			} else {
				ex = originX;
			}
			break;
		case Container.MOVE_DOWN:
			if(move) {
				ey = originY + height;
				originY = y;
			} else {
				ey = originY;
			}
			break;
		case Container.MOVE_LEFT:
			if(move) {
				ex = originX - width;
//				originX = x;
			} else {
				ex = originX;
			}
			break;
		}
		translateAnimation(x, ex, y, ey);
		
	}
	
	private void translateAnimation(int sx, final int ex, int sy, final int ey) {
		final int deltaX = (ex - sx)/(TRANSLATE_ANIMATION_DURATION/TRANSLATE_ANIMATION_INTERVAL);
		final int deltaY = (ey - sy)/(TRANSLATE_ANIMATION_DURATION/TRANSLATE_ANIMATION_INTERVAL);
		Thread translateThread = new Thread() {
			int count = TRANSLATE_ANIMATION_DURATION/TRANSLATE_ANIMATION_INTERVAL;
			
			@Override
			public void run() {
				while(count > 0) {
					try {
						Thread.sleep(TRANSLATE_ANIMATION_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.i("Threes", "translate start, x " + x + ", y " + y + ", deltax " + deltaX
							+ ", deltaY " + deltaY);
					x = x + deltaX;
					y = y + deltaY;
					Log.i("Threes", "translate end, x " + x + ", y " + y + ", deltax " + deltaX
							+ ", deltaY " + deltaY);
					
					postInvalidate();
					count--;
					
				}
				originX = x;
				originY = y;
				super.run();
			}
		};
		translateThread.start();
	}
	
	public void moveOver() {
		totalMoveX = 0;
		totalMoveY = 0;
	}
	
	public boolean validMove() {
		if(Math.abs(totalMoveX) > width/4 || Math.abs(totalMoveY) > height/4) {
			return true;
		} else {
			return false;
		}
	}
	
//		public void draw(Canvas canvas){
//			Paint p = new Paint();
//			p.setColor(color);
//			Rect rect = new Rect(x, y, x+width, y+height);
//			canvas.drawRect(rect, p);
//			p.setColor(Color.WHITE);
//			p.setTextSize(20);
//			canvas.drawText("" + number, x + width/2, y + height/2, p);
//		}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.i("Threes", "number ondraw, x " + x + ", y " + y);
		Paint p = new Paint();
		p.setColor(color);
		Rect rect = new Rect(x, y, x+width, y+height);
		canvas.drawRect(rect, p);
		p.setColor(Color.WHITE);
		p.setTextSize(20);
		canvas.drawText("" + number, x + width/2, y + height/2, p);
	}
		
}
