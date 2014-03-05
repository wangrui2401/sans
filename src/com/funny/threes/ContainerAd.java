package com.funny.threes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class ContainerAd extends View {
	
	private static final int ROW_COUNT = 4;
	private static final int COLUMN_COUNT = 4;
	private static final int MOVE_DIRECTION_UP = 0;
	private static final int MOVE_DIRECTION_RIGHT = 1;
	private static final int MOVE_DIRECTION_DOWN = 2;
	private static final int MOVE_DIRECTION_LEFT = 3;
	
	private static final int TRANSLATE_ANIMATION_DURATION = 250;
	private static final int TRANSLATE_ANIMATION_INTERVAL = 25;
	private static final int ROTATE_ANIMATION_DURATION = 300;
	private static final int ROTATE_ANIMATION_INTERVAL = 25;
	
	private static final int COLOR_BLUE_TOP = 0xff6bceff;
	private static final int COLOR_BLUE_BOTTOM = 0xff63aaf7;
	private static final int COLOR_RED_TOP = 0xffff6583;
	private static final int COLOR_RED_BOTTOM = 0xffce557b;
	private static final int COLOR_WHITE = 0xffffffff;
	private static final int COLOR_YELLOW = 0xffffce6b;
	
	Number[][] mNumbers;
	Context mContext;
	Paint mPaint;
	RectF mBgRect1, mBgRect2;
	RectF[][] mBgCell;
	RectF[][] mNumberCell;
	float mRowGap, mColumnGap;
	float mMoveStartX, mMoveStartY;
	int mMoveDirection;
	  
	public ContainerAd(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context; 
		init();
	}
	
	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
		mNumbers = new Number[ROW_COUNT][COLUMN_COUNT];
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		setupView();
	}
	
	/**
	 * Setup the background and cells' coordinate
	 */
	private void setupView() {
		int width = getWidth();
		int height = getHeight();
		mBgRect1 = new RectF((float)width*2f/27f, (float)height*2f/27f, 
				(float)getWidth() - (float)width*2f/27f, 
				(float)getHeight() - (float)height*2f/27f);
		mBgRect2 = new RectF((float)width*2f/27f, (float)height*2f/27f, 
				(float)getWidth() - (float)width*2f/27f, 
				(float)getHeight() - (float)height*2f/27f);
		
		mBgCell = new RectF[ROW_COUNT][COLUMN_COUNT];
		float bgRectWidth = mBgRect2.right - mBgRect2.left;
		float bgRectHeight = mBgRect2.bottom - mBgRect2.top;
		float bgCellWidth = bgRectWidth/5;
		float bgCellHeight = bgRectHeight/5;
		mRowGap = bgRectHeight/25;
		mColumnGap = bgRectWidth/25;
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				float x = mBgRect2.left + bgCellWidth*j + (j+1)*mColumnGap;
				float y = mBgRect2.top + bgCellHeight*i + (i+1)*mRowGap;
				mBgCell[i][j] = new RectF(x, y, x+bgCellWidth, y+bgCellHeight);
			}
		}
		
		mNumberCell = new RectF[ROW_COUNT][COLUMN_COUNT];
		testGenerateNumbers(bgCellWidth, bgCellHeight);
	}
	
	/**
	 * For test only
	 */
	private void testGenerateNumbers(float bgCellWidth, float bgCellHeight) {
		mNumbers[0][0] = generateNumber(0, 0, 1);
		mNumbers[0][1] = generateNumber(0, 1, 2);
		mNumbers[1][1] = generateNumber(1, 1, 3);
		mNumbers[2][1] = generateNumber(2, 1, 12);
//		mNumberCell[0][0] = generateNumberCellRect(0, 0, bgCellWidth, bgCellHeight);
//		mNumberCell[0][1] = generateNumberCellRect(0, 1, bgCellWidth, bgCellHeight);
//		mNumberCell[1][1] = generateNumberCellRect(1, 1, bgCellWidth, bgCellHeight);
//		mNumberCell[2][1] = generateNumberCellRect(2, 1, bgCellWidth, bgCellHeight);
	}
	
	private Number generateNumber(int row, int column, int value) {
		Number number = new Number();
		number.number = value;
		number.rectF = generateNumberCellRect(row, column, 
				mBgCell[row][column].right - mBgCell[row][column].left, 
				mBgCell[row][column].bottom - mBgCell[row][column].top);
		if(value == 1) {
			number.colorBottom = COLOR_BLUE_BOTTOM;
			number.colorTop = COLOR_BLUE_TOP;
		} else if(value == 2) {
			number.colorBottom = COLOR_RED_BOTTOM;
			number.colorTop = COLOR_RED_TOP;
		} else {
			number.colorBottom = COLOR_YELLOW;
			number.colorTop = COLOR_WHITE;
		}
		number.row = row;
		number.column = column;
		return number;
	}
	
	private RectF generateNumberCellRect(int row, int column, float bgCellWidth, float bgCellHeight) {
		float x = mBgCell[row][column].left - mColumnGap/4;
		float y = mBgCell[row][column].top;
		RectF numberCell = new RectF(x, y, mBgCell[row][column].right, y + bgCellHeight);
		return numberCell;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float ex = event.getX();
		float ey = event.getY();
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMoveStartX = ex;
			mMoveStartY = ey;
			break;
		case MotionEvent.ACTION_MOVE:
			mMoveDirection = getMoveDirection(ex, ey, mMoveStartX, mMoveStartY);
			break;
		case MotionEvent.ACTION_UP:
			testAnimation();
			break;
		}
		return true;
	}
	
	private int getMoveDirection(float ex, float ey, float sx, float sy) {
		int direction = -1;
		float dx = ex - sx;
		float dy = ey - sy;
		if(Math.abs(dx) > Math.abs(dy*1.5f)) {
			if(dx > 0) {
				direction = MOVE_DIRECTION_RIGHT;
			} else {
				direction = MOVE_DIRECTION_LEFT;
			}
		} else if(Math.abs(dy) > Math.abs(dx*1.5f)) {
			if(dy > 0) {
				direction = MOVE_DIRECTION_DOWN;
			} else {
				direction = MOVE_DIRECTION_UP;
			}
		}
		return direction;
	}
	
	private void testAnimation() {
		switch(mMoveDirection) {
		case MOVE_DIRECTION_UP:
			showAnimation(0, mBgCell[0][0].top - mBgCell[0][0].bottom - mRowGap);
			break;
		case MOVE_DIRECTION_RIGHT:
			showAnimation(mBgCell[0][0].right - mBgCell[0][0].left + mColumnGap, 0);
			break;
		case MOVE_DIRECTION_DOWN:
			showAnimation(0, mBgCell[0][0].bottom - mBgCell[0][0].top + mRowGap);
			break;
		case MOVE_DIRECTION_LEFT:
			showAnimation(mBgCell[0][0].left - mBgCell[0][0].right - mColumnGap, 0);
			break;
		}
	}
	
	private void showAnimation(float deltaTotalX, float deltaTotalY) {
		final float deltaX = deltaTotalX/(TRANSLATE_ANIMATION_DURATION/TRANSLATE_ANIMATION_INTERVAL);
		final float deltaY = deltaTotalY/(TRANSLATE_ANIMATION_DURATION/TRANSLATE_ANIMATION_INTERVAL);
		Thread translateThread = new Thread() {
			int translateCount = TRANSLATE_ANIMATION_DURATION/TRANSLATE_ANIMATION_INTERVAL;
			@Override
			public void run() {
				//translate animation
				while(translateCount > 0) {
					for(int i=0; i<ROW_COUNT; i++) {
						for(int j=0; j<COLUMN_COUNT; j++) {
							if(mNumbers[i][j] != null && canMove(i, j)) {
								mNumbers[i][j].rectF.left += deltaX;
								mNumbers[i][j].rectF.right += deltaX;
								mNumbers[i][j].rectF.top += deltaY;
								mNumbers[i][j].rectF.bottom += deltaY;
							}
						}
					}
					postInvalidate();
					translateCount--;
					try {
						Thread.sleep(TRANSLATE_ANIMATION_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//rotate, Note: rotateCount must be even number
				int rotateCount = ROTATE_ANIMATION_DURATION/ROTATE_ANIMATION_INTERVAL;
				int rotateTime = 0;
				float deltaRotateX = (mBgCell[0][0].right - mBgCell[0][0].left + mColumnGap/4)/rotateCount*2;
				while(rotateTime < rotateCount) {
					for(int i=0; i<ROW_COUNT; i++) {
						for(int j=0; j<COLUMN_COUNT; j++) {
							if(mNumbers[i][j] != null && canMove(i, j)) {
								if(rotateTime < rotateCount/2) {
									mNumbers[i][j].rectF.left += deltaRotateX/2;
									mNumbers[i][j].rectF.right -= deltaRotateX/2;	
								} else {
									mNumbers[i][j].rectF.left -= deltaRotateX/2;
									mNumbers[i][j].rectF.right += deltaRotateX/2;
								}
							}
						}
					}
					postInvalidate();
					rotateTime++;
					try {
						Thread.sleep(TRANSLATE_ANIMATION_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				super.run();
			}
		};
		translateThread.start();
	}
	
	private boolean canMove(int row, int column) {
		boolean canMove = true;
		return canMove;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawBg(canvas);
		drawNumbers(canvas);
	}
	
	private void drawBg(Canvas canvas) {
		//draw background
		int width = getWidth();
		
		canvas.save();
		mPaint.setColor(Color.GRAY);
		canvas.rotate(-2.0f, width, 0);
		float mx = (float)Math.tan(-2.0*Math.PI/360)*(mBgRect1.bottom - mBgRect1.top);
		float my = (float)Math.tan(-2.0*Math.PI/360)*(mBgRect1.right - mBgRect1.left);
		canvas.translate(mx, my);
		canvas.drawRoundRect(mBgRect1, 20, 20, mPaint);
		
		canvas.restore();
		mPaint.setColor(0xffe6e6e6);
		canvas.drawRoundRect(mBgRect2, 20, 20, mPaint);
		
		//draw background cell
		mPaint.setColor(0xffd5d5d5);
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				canvas.drawRoundRect(mBgCell[i][j], 10, 10, mPaint);
			}
		}
	}
	
	private void drawNumbers(Canvas canvas) {
		//draw numbers
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				if(mNumbers[i][j] != null) {
					int colorBottom = mNumbers[i][j].colorBottom;
					mPaint.setColor(colorBottom);
					canvas.drawRoundRect(mNumbers[i][j].rectF, 10, 10, mPaint);
					canvas.save();
					int colorTop = mNumbers[i][j].colorTop;
					mPaint.setColor(colorTop);
					canvas.translate(0, -mRowGap/2);
					canvas.drawRoundRect(mNumbers[i][j].rectF, 10, 10, mPaint);
					canvas.restore();
				}
//				if(mNumberCell[i][j] != null) {
//					int color = (int)(Math.random()*Integer.MAX_VALUE) & 0xFFFFFFFF;
//					mPaint.setColor(color);
//					canvas.drawRoundRect(mNumberCell[i][j], 10, 10, mPaint);
//					canvas.save();
//					int color2 = (int)(Math.random()*Integer.MAX_VALUE) & 0xFFFFFFFF;
//					mPaint.setColor(color2);
//					canvas.translate(0, mRowGap/2);
//					canvas.drawRoundRect(mNumberCell[i][j], 10, 10, mPaint);
//					canvas.restore();
//				}
			}
		}
	}
	
	class Number {
		RectF rectF;
		int row, column;
		int number;
		int colorTop, colorBottom;
	}

}
