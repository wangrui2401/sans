package com.funny.threes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Container extends View {
	
	private static final int ROW_COUNT = 4;
	private static final int COLUMN_COUNT = 4;
	
	public static final int MOVE_OVER = -1;
	public static final int MOVE_UP = 0;
	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_DOWN = 2;
	public static final int MOVE_LEFT = 3;
	
	private static final int GAP = 10;
	
	private float moveStartX = -1.0f, moveStartY = -1.0f;
	private float moveLastX = -1.0f, moveLastY = -1.0f;
	private int moveDirection = MOVE_OVER;
	
//	int[][] numbers = new int[ROW_COUNT][COLUMN_COUNT];
	Number[][] numbers = new Number[ROW_COUNT][COLUMN_COUNT];

	public Container(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private void testInit() {
		int cw = getWidth();
		int ch = getHeight();
		int nw = (cw - GAP *(COLUMN_COUNT + 1))/COLUMN_COUNT;
		int nh = (ch - GAP *(ROW_COUNT + 1))/ROW_COUNT;
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				int x = (nw + GAP)*j;
				int y = (nh + GAP)*i;
				numbers[i][j] = new Number(x, y, nw, nh);
				numbers[i][j].setNumber(i*j%3);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float ex = event.getX();
		float ey = event.getY();
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			moveStartX = ex;
			moveStartY = ey;
			moveLastX = ex;
			moveLastY = ey;
			moveDirection = MOVE_OVER;
			break;
		case MotionEvent.ACTION_MOVE:
			if(moveDirection == MOVE_OVER) {
				if(Math.abs((ex - moveStartX)) > Math.abs((ey - moveStartY))){
					if(ex > moveStartX) {
						moveDirection = MOVE_RIGHT;
					} else {
						moveDirection = MOVE_LEFT;
					}
				} else {
					if(ey > moveStartY) {
						moveDirection = MOVE_DOWN;
					} else {
						moveDirection = MOVE_UP;
					}
				}
			}
			float deltaX = ex - moveLastX;
			float deltaY = ey - moveLastY;
			move(moveDirection, deltaX, deltaY);
			moveLastX = ex;
			moveLastY = ey;
			break;
		case MotionEvent.ACTION_UP:
			stepOperation(moveDirection);
			moveOver();
			break;
		}
		invalidate();
		return true;
	}

	public void move(int direction, float deltaX, float deltaY) {
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				if(numbers[i][j] != null && canMove(direction, i, j)) {
					numbers[i][j].move(direction, (int)deltaX, (int)deltaY);
				}
			}
		}
	}
	
	private void moveOver() {
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
				if(numbers[i][j] != null) {
					numbers[i][j].moveOver();
				}
			}
		}
	}
	
	private void stepOperation(int direction) {
		switch(direction) {
		case MOVE_UP:
			for(int i = 1; i < ROW_COUNT; i++) {
				for(int j = 0; j < COLUMN_COUNT; j++) {
					if(numbers[i][j] != null && canMove(direction, i, j)) {
						if(numbers[i - 1][j] != null) {
							numbers[i][j].eatNumber(numbers[i - 1][j]);
						}
						numbers[i - 1][j] = numbers[i][j];
						numbers[i][j] = null;
					}
				}
			}
			break;
		case MOVE_RIGHT:
			for(int j = COLUMN_COUNT - 2; j >= 0; j--) {
				for(int i = 0; i < ROW_COUNT; i++) {
					if(numbers[i][j] != null && canMove(direction, i, j)) {
						if(numbers[i][j + 1] != null) {
							numbers[i][j].eatNumber(numbers[i][j + 1]);
						}
						numbers[i][j + 1] = numbers[i][j];
						numbers[i][j] = null;
					}
				}
			}
			break;
		case MOVE_DOWN:
			for(int i = ROW_COUNT - 2; i >= 0; i--) {
				for(int j = 0; j < COLUMN_COUNT; j++) {
					if(numbers[i][j] != null && canMove(direction, i, j)) {
						if(numbers[i + 1][j] != null) {
							numbers[i][j].eatNumber(numbers[i + 1][j]);
						}
						numbers[i + 1][j] = numbers[i][j];
						numbers[i][j] = null;
					}
				}
			}
			break;
		case MOVE_LEFT:
			for(int j = 1; j < COLUMN_COUNT; j++) {
				for(int i = 0; i < ROW_COUNT; i++) {
					if(numbers[i][j] != null && canMove(direction, i, j)) {
						if(numbers[i][j - 1] != null) {
							numbers[i][j].eatNumber(numbers[i][j - 1]);
						}
						numbers[i][j - 1] = numbers[i][j];
						numbers[i][j] = null;
					}
				}
			}
			break;
		}
	}
	
	private boolean canMove(int direction, int row, int column) {
		boolean canMove = false;
		
		switch(direction) {
		case MOVE_UP:
			if(row - 1 >= 0) {
				if(numbers[row - 1][column] == null || 
						numbers[row - 1][column].number == 0) {
					canMove = true;
				} else {
					while(row - 1 >= 0) {
						if(canMove(direction, row - 1, column)) {
							canMove = true;
							break;
						} else {
							row = row - 1;
						}
					}	
				}
			}
			break;
		case MOVE_RIGHT:
			if(column + 1 < COLUMN_COUNT) {
				if(numbers[row][column + 1] == null || 
						numbers[row][column + 1].number == 0) {
					canMove = true;	
				} else {
					while(column + 1 < COLUMN_COUNT) {
						if(canMove(direction, row, column + 1)) {
							canMove = true;
							break;
						} else {
							column = column + 1;
						}
					}	
				}
			} 
			break;
		case MOVE_DOWN:
			if(row + 1 < ROW_COUNT) {
				if(numbers[row + 1][column] == null || 
						numbers[row + 1][column].number == 0) {
					canMove = true;
				} else {
					while(row + 1 < ROW_COUNT) {
						if(canMove(direction, row + 1, column)) {
							canMove = true;
							break;
						} else {
							row = row + 1;
						}
					}
				}
			}
			break;
		case MOVE_LEFT:
			if(column - 1 >= 0) {
				if(numbers[row][column - 1] == null || 
						numbers[row][column - 1].number == 0) {
					canMove = true;	
				} else {
					while(column - 1 >= 0) {
						if(canMove(direction, row, column - 1)) {
							canMove = true;
							break;
						} else {
							column = column - 1;
						}
					}
				}
			}
			break;
		}
		
		return canMove;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		testInit();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(int i=0; i<ROW_COUNT; i++) {
			for(int j=0; j<COLUMN_COUNT; j++) {
//				drawNumber(canvas, i, j);
				if(numbers[i][j] != null) {
					numbers[i][j].draw(canvas);	
				}
			}
		}
	}
	
	class Number {
		int x, y, width, height;
		int totalMoveX, totalMoveY;
		int number;
		int color;
		int bgId;
		
		public Number(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
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
			case MOVE_LEFT:
			case MOVE_RIGHT:
				totalMoveX += deltaX;
				if(Math.abs(totalMoveX) < width) {
					x = x + deltaX;	
				}
				break;
			case MOVE_UP:
			case MOVE_DOWN:
				totalMoveY += deltaY;
				if(Math.abs(totalMoveY) < height) {
					y = y + deltaY;	
				}
				break;
			}
		}
		
		public void moveOver() {
			
			totalMoveX = 0;
			totalMoveY = 0;
		}
		
		public void draw(Canvas canvas){
			Paint p = new Paint();
			p.setColor(color);
			Rect rect = new Rect(x, y, x+width, y+height);
			canvas.drawRect(rect, p);
			p.setColor(Color.WHITE);
			p.setTextSize(20);
			canvas.drawText("" + number, x + width/2, y + height/2, p);
		}
	}
	
}
