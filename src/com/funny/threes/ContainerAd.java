package com.funny.threes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
	
	private static final int MSG_STEP_OVER = 101;
	private static final int MSG_ROTATE = 102;
	
	Number[][] mNumbers;
	Context mContext;
	Paint mPaint;
	RectF mBgRect1, mBgRect2;
	RectF[][] mBgCell;
	RectF[][] mNumberCell;
	float mRowGap, mColumnGap;
	float mMoveStartX, mMoveStartY;
	float mMoveLastX, mMoveLastY;
	float mMoveDeltaX, mMoveDeltaY;
	int mMoveDirection = -1;
	boolean mIsShowAnimation = false;
	  
	public ContainerAd(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context; 
		init();
	}
	
	/**
	 * Initiate the paint and number array
	 */
	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
		mNumbers = new Number[ROW_COUNT][COLUMN_COUNT];
	}
	
	/**
	 * After gotten the width and height of Container, 
	 * setup the views in Container.
	 */
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
	}
	
	/**
	 * Generate a number in (row, column) with value.
	 * @param row
	 * @param column
	 * @param value
	 * @return
	 */
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
	
	/**
	 * Generate the Number's RectF.
	 * @param row
	 * @param column
	 * @param bgCellWidth
	 * @param bgCellHeight
	 * @return
	 */
	private RectF generateNumberCellRect(int row, int column, float bgCellWidth, float bgCellHeight) {
		float x = mBgCell[row][column].left - mColumnGap/4;
		float y = mBgCell[row][column].top;
		RectF numberCell = new RectF(x, y, mBgCell[row][column].right, y + bgCellHeight);
		return numberCell;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mIsShowAnimation) {
			float ex = event.getX();
			float ey = event.getY();
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mMoveDirection = -1;
				mMoveStartX = ex;
				mMoveStartY = ey;
				mMoveLastX = ex;
				mMoveLastY = ey;
				mMoveDeltaX = 0f;
				mMoveDeltaY = 0f;
				break;
			case MotionEvent.ACTION_MOVE:
				if(mMoveDirection == -1) {
					mMoveDirection = getMoveDirection(ex, ey, mMoveStartX, mMoveStartY);	
				}
				
				move(mMoveLastX, mMoveLastY, ex, ey, mMoveDirection);
				mMoveLastX = ex;
				mMoveLastY = ey;
				break;
			case MotionEvent.ACTION_UP:
				testAnimation();
				mMoveLastX = -1f;
				mMoveLastY = -1f;
				mMoveDeltaX = 0f;
				mMoveDeltaY = 0f;
				
				break;
			}
		}
		
		return true;
	}
	
	private void move(float sx, float sy, float ex, float ey, int direction) {
		float deltaX = ex - sx;
		float deltaY = ey - sy;
		boolean moved = false;
		switch(direction) {
		case MOVE_DIRECTION_UP:
			for(int i=1; i<ROW_COUNT; i++) {
				for(int j=0; j<COLUMN_COUNT; j++) {
					if(mNumbers[i][j] != null && canMove(i, j, direction)) {
						if(mNumbers[i][j].rectF.top + deltaY >= mBgCell[i-1][j].top && 
								mNumbers[i][j].rectF.top + deltaY <= mBgCell[i][j].top) {
							mNumbers[i][j].rectF.top += deltaY;
							mNumbers[i][j].rectF.bottom = mNumbers[i][j].rectF.top + 
									mBgCell[i][j].bottom - mBgCell[i][j].top;
							moved = true;
						}
					}
				}
			}
			if(moved) {mMoveDeltaY += deltaY;}
			break;
		case MOVE_DIRECTION_RIGHT:
			for(int i=0; i<ROW_COUNT; i++) {
				for(int j=0; j<COLUMN_COUNT-1; j++) {
					if(mNumbers[i][j] != null && canMove(i, j, direction)) {
						if(mNumbers[i][j].rectF.right + deltaX < mBgCell[i][j+1].right &&
								mNumbers[i][j].rectF.right + deltaX > mBgCell[i][j].right) {
							mNumbers[i][j].rectF.right += deltaX;
							mNumbers[i][j].rectF.left = mNumbers[i][j].rectF.right - 
									mBgCell[i][j].right + mBgCell[i][j].left;
							moved = true;
						}
					}
				}
			}
			if(moved) {mMoveDeltaX += deltaX;}
			break;
		case MOVE_DIRECTION_DOWN:
			for(int i=0; i<ROW_COUNT-1; i++) {
				for(int j=0; j<COLUMN_COUNT; j++) {
					if(mNumbers[i][j] != null && canMove(i, j, direction)) {
						if(mNumbers[i][j].rectF.bottom + deltaY <= mBgCell[i+1][j].bottom && 
								mNumbers[i][j].rectF.bottom + deltaY >= mBgCell[i][j].bottom) {
							mNumbers[i][j].rectF.top += deltaY;
							mNumbers[i][j].rectF.bottom = mNumbers[i][j].rectF.top + 
									mBgCell[i][j].bottom - mBgCell[i][j].top;
							moved = true;
						}
					}
				}
			}
			if(moved) {mMoveDeltaY += deltaY;}
			break;
		case MOVE_DIRECTION_LEFT:
			for(int i=0; i<ROW_COUNT; i++) {
				for(int j=1; j<COLUMN_COUNT; j++) {
					if(mNumbers[i][j] != null && canMove(i, j, direction)) {
						if(mNumbers[i][j].rectF.left + deltaX > mBgCell[i][j-1].left &&
								mNumbers[i][j].rectF.left + deltaX < mBgCell[i][j].left) {
							mNumbers[i][j].rectF.right += deltaX;
							mNumbers[i][j].rectF.left  = mNumbers[i][j].rectF.right - 
									mBgCell[i][j].right + mBgCell[i][j].left;
							moved = true;
						}
					}
				} 
			}
			if(moved) {mMoveDeltaX += deltaX;}
			break;
		}
		invalidate();
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what == MSG_STEP_OVER) {
				stepOver();
			} else if(msg.what == MSG_ROTATE) {
				rotateAnimation();
			}
		}
	};
	
	/**
	 * Do the numbers' add and move
	 */
	private void stepOver() {
		switch(mMoveDirection) {
		case MOVE_DIRECTION_UP:
			for(int i=1; i<ROW_COUNT; i++) {
				for(int j=0; j<COLUMN_COUNT; j++) {
					if(mNumbers[i][j] != null && canMove(i, j, mMoveDirection)) {
						if(mNumbers[i-1][j] != null) {
							mNumbers[i][j].number += mNumbers[i-1][j].number;
						}
						mNumbers[i-1][j] = mNumbers[i][j];
						mNumbers[i][j] = null;
					}
				}
			}
			break;
		case MOVE_DIRECTION_RIGHT:
			for(int j=COLUMN_COUNT-2; j>=0; j--) {
				for(int i=0; i<ROW_COUNT; i++) {
					if(mNumbers[i][j] != null && canMove(i, j, mMoveDirection)) {
						if(mNumbers[i][j+1] != null) {
							mNumbers[i][j].number += mNumbers[i][j+1].number;
						}
						mNumbers[i][j+1] = mNumbers[i][j];
						mNumbers[i][j] = null;
					}
				}
			}
			break;
		case MOVE_DIRECTION_DOWN:
			for(int i=ROW_COUNT-2; i>=0; i--) {
				for(int j=0; j<COLUMN_COUNT; j++) {
					if(mNumbers[i][j] != null && canMove(i, j, mMoveDirection)) {
						if(mNumbers[i+1][j] != null) {
							mNumbers[i][j].number += mNumbers[i+1][j].number;
						}
						mNumbers[i+1][j] = mNumbers[i][j];
						mNumbers[i][j] = null;
					}
				}
			}
			break;
		case MOVE_DIRECTION_LEFT:
			for(int j=1; j<COLUMN_COUNT; j++) {
				for(int i=0; i<ROW_COUNT; i++) {
					if(mNumbers[i][j] != null && canMove(i, j, mMoveDirection)) {
						if(mNumbers[i][j-1] != null) {
							mNumbers[i][j].number += mNumbers[i][j-1].number;
						}
						mNumbers[i][j-1] = mNumbers[i][j];
						mNumbers[i][j] = null;
					}
				}
			}
			break;
		}
		invalidate();
		mMoveDirection = -1;
		mIsShowAnimation = false;
	}
	
	private int getMoveDirection(float ex, float ey, float sx, float sy) {
		int direction = -1;
		float dx = ex - sx;
		float dy = ey - sy;
		if(Math.abs(dx) > Math.abs(dy*1.2f)) {
			if(dx > 0) {
				direction = MOVE_DIRECTION_RIGHT;
			} else {
				direction = MOVE_DIRECTION_LEFT;
			}
		} else if(Math.abs(dy) > Math.abs(dx*1.2f)) {
			if(dy > 0) {
				direction = MOVE_DIRECTION_DOWN;
			} else {
				direction = MOVE_DIRECTION_UP;
			}
		}
		Log.i("Threes", "move direction is " + direction + ", dx " + dx + ", dy " + dy);
		return direction;
	}
	
	private void testAnimation() {
		switch(mMoveDirection) {
		case MOVE_DIRECTION_UP:
			float deltaY = (mBgCell[0][0].top - mBgCell[0][0].bottom - mRowGap) - 
					mMoveDeltaY;
			if(Math.abs(deltaY) < (mBgCell[0][0].bottom - mBgCell[0][0].top)) {
				translateAnimation(0, deltaY, false);	
			} else {
				translateAnimation(0, -mMoveDeltaY, true);
			}
			break;
		case MOVE_DIRECTION_RIGHT:
			float deltaX = (mBgCell[0][0].right - mBgCell[0][0].left + mColumnGap) - 
					mMoveDeltaX;
			if(Math.abs(deltaX) < (mBgCell[0][0].right - mBgCell[0][0].left)) {
				translateAnimation(deltaX, 0, false);
			} else {
				translateAnimation(-mMoveDeltaX, 0, true);
			}
			break;
		case MOVE_DIRECTION_DOWN:
		   	float deltaY2 = (mBgCell[0][0].bottom - mBgCell[0][0].top + mRowGap) - 
					mMoveDeltaY;
		   	if(Math.abs(deltaY2) < (mBgCell[0][0].bottom - mBgCell[0][0].top)) {
		   		translateAnimation(0, deltaY2, false);
		   	} else {
		   		translateAnimation(0, -mMoveDeltaY, true);
			}
			break;
		case MOVE_DIRECTION_LEFT:
			float deltaX2 = (mBgCell[0][0].left - mBgCell[0][0].right - mColumnGap) - 
					mMoveDeltaX;
			if(Math.abs(deltaX2) < (mBgCell[0][0].right - mBgCell[0][0].left)) {
				translateAnimation(deltaX2, 0, false);
			} else {
				translateAnimation(-mMoveDeltaX, 0, true);
			}
			break;
		}
	}
	
	/**
	 * show the translate animation.
	 * @param deltaTotalX
	 * @param deltaTotalY
	 * @param back: move direction or back to original place(cancel move).
	 */
	private void translateAnimation(float deltaTotalX, float deltaTotalY, final boolean back) {
		mIsShowAnimation = true;
		
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
							if(mNumbers[i][j] != null && canMove(i, j, mMoveDirection)) {
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
				
				if(!back) {
					Message msg = new Message();
					msg.what = MSG_ROTATE;
					mHandler.sendMessage(msg);
				} else {
					mIsShowAnimation = false;
				}
				
				super.run();
			}
		};
		translateThread.start();
	}
	
	private void rotateAnimation() {
		Thread rotateThread = new Thread() {
			
			@Override
			public void run() {
				//rotate, Note: rotateCount must be even number
				int rotateCount = ROTATE_ANIMATION_DURATION/ROTATE_ANIMATION_INTERVAL;
				int rotateTime = 0;
				float deltaRotateX = (mBgCell[0][0].right - mBgCell[0][0].left + mColumnGap/4)/rotateCount*2;
				while(rotateTime < rotateCount) {
					for(int i=0; i<ROW_COUNT; i++) {
						for(int j=0; j<COLUMN_COUNT; j++) {
							if(mNumbers[i][j] != null && canEat(i, j, mMoveDirection)) {
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
				
				Message msg = new Message();
				msg.what = MSG_STEP_OVER;
				mHandler.sendMessage(msg);
				super.run();
			}
		};
		rotateThread.start();
	}
	
	private boolean canEat(int row, int column, int direction) {
		boolean canEat = false;
		if(mNumbers[row][column] == null) {
			return false;
		}
		switch(direction) {
		case MOVE_DIRECTION_UP:
			if(row == 0) {
				canEat = false;
			} else if(mNumbers[row-1][column] != null && 
					canMove(row, column, direction) &&
					!canMove(row-1, column, direction)) {
				canEat = true;
				mNumbers[row-1][column].hide();
			}
			break;
		case MOVE_DIRECTION_RIGHT:
			if(column == COLUMN_COUNT - 1) {
				canEat = false;
			} else if(mNumbers[row][column+1] != null && 
					canMove(row, column, direction) &&
					!canMove(row, column+1, direction)) {
				canEat = true;
				mNumbers[row][column+1].hide();
			}
			break;
		case MOVE_DIRECTION_DOWN:
			if(row == ROW_COUNT - 1) {
				canEat = false;
			} else if(mNumbers[row+1][column] != null && 
					canMove(row, column, direction) &&
					!canMove(row+1, column, direction)) {
				canEat = true;
				mNumbers[row+1][column].hide();
			}
			break;
		case MOVE_DIRECTION_LEFT:
			if(column == 0) {
				canEat = false;
			} else if(mNumbers[row][column-1] != null && 
					canMove(row, column, direction) &&
					!canMove(row, column-1, direction)) {
				canEat = true;
				mNumbers[row][column-1].hide();
			}
			break;
		}
		
		return canEat;
	}
	
	private boolean canMove(int row, int column, int direction) {
		boolean canMove = false;
		if(mNumbers[row][column] == null) {
			return false;
		}
		
		switch(direction) {
		case MOVE_DIRECTION_UP:
			if(row == 0) {
				canMove = false;
			} else if(mNumbers[row-1][column] == null || 
					mNumbers[row-1][column].number == mNumbers[row][column].number ||
					mNumbers[row][column].number + mNumbers[row-1][column].number == 3){
				canMove = true;
			} else if(canMove(row-1, column, direction)) {
				canMove = true;
			}
			break;
		case MOVE_DIRECTION_RIGHT:
			if(column == COLUMN_COUNT - 1) {
				canMove = false;
			} else if(mNumbers[row][column+1] == null || 
					mNumbers[row][column+1].number == mNumbers[row][column].number ||
					mNumbers[row][column].number + mNumbers[row][column+1].number == 3){
				canMove = true;
			} else if(canMove(row, column+1, direction)) {
				canMove = true;
			}
			break;
		case MOVE_DIRECTION_DOWN:
			if(row == ROW_COUNT - 1) {
				canMove = false;
			} else if(mNumbers[row+1][column] == null || 
					mNumbers[row+1][column].number == mNumbers[row][column].number ||
					mNumbers[row][column].number + mNumbers[row+1][column].number == 3){
				canMove = true;
			} else if(canMove(row+1, column, direction)) {
				canMove = true;
			}
			break;
		case MOVE_DIRECTION_LEFT:
			if(column == 0) {
				canMove = false;
			} else if(mNumbers[row][column-1] == null || 
					mNumbers[row][column-1].number == mNumbers[row][column].number ||
					mNumbers[row][column].number + mNumbers[row][column-1].number == 3){
				canMove = true;
			} else if(canMove(row, column-1, direction)) {
				canMove = true;
			}
			break;
		}
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
					int colorBottom = mNumbers[i][j].getColorBottom();
					mPaint.setColor(colorBottom);
					canvas.drawRoundRect(mNumbers[i][j].rectF, 10, 10, mPaint);
					canvas.save();
					int colorTop = mNumbers[i][j].getColorTop();
					mPaint.setColor(colorTop);
					canvas.translate(0, -mRowGap/2);
					canvas.drawRoundRect(mNumbers[i][j].rectF, 10, 10, mPaint);
					canvas.restore();
				}
			}
		}
	}
	
	class Number {
		RectF rectF;
		int row, column;
		int number;
		int colorTop, colorBottom;
		boolean hide = false;
		
		public void hide() {
			colorTop = 0x00ffffff;
			colorBottom = 0x00ffffff;
			hide = true;
		}
		
		public int getColorTop() {
			if(!hide) {
				if(number == 1) {
					colorTop = COLOR_BLUE_TOP;
				} else if(number == 2) {
					colorTop = COLOR_RED_TOP;
				} else {
					colorTop = COLOR_WHITE;
				}
			}
			
			return colorTop;
		}
		
		public int getColorBottom() {
			if(!hide) {
				if(number == 1) {
					colorBottom = COLOR_BLUE_BOTTOM;
				} else if(number == 2) {
					colorBottom = COLOR_RED_BOTTOM;
				} else {
					colorBottom = COLOR_YELLOW;
				}
			}
			return colorBottom;
		}
	}

}
