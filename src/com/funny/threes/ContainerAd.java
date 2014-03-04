package com.funny.threes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ContainerAd extends View {
	
	private static final int ROW_COUNT = 4;
	private static final int COLUMN_COUNT = 4;
	Number[][] mNumbers = new Number[ROW_COUNT][COLUMN_COUNT];
	Context mContext;
	Paint mPaint;
	RectF mBgRect1, mBgRect2;
	RectF[][] mBgCell;
	RectF[][] mNumberCell;
	float mRowGap, mColumnGap;

	public ContainerAd(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context; 
		init();
	}
	
	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
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
		mNumberCell[0][0] = generateNumberCellRect(0, 0, bgCellWidth, bgCellHeight);
		mNumberCell[0][1] = generateNumberCellRect(0, 1, bgCellWidth, bgCellHeight);
		mNumberCell[1][1] = generateNumberCellRect(1, 1, bgCellWidth, bgCellHeight);
		mNumberCell[2][1] = generateNumberCellRect(2, 1, bgCellWidth, bgCellHeight);
	}
	
	private RectF generateNumberCellRect(int row, int column, float bgCellWidth, float bgCellHeight) {
		float x = mBgCell[row][column].left - mColumnGap/4;
		float y = mBgCell[row][column].top - mRowGap/2;
		RectF numberCell = new RectF(x, y, mBgCell[row][column].right, y + bgCellHeight);
		return numberCell;
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
		mPaint.setColor(0xffe5e5e5);
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
				if(mNumberCell[i][j] != null) {
					int color = (int)(Math.random()*Integer.MAX_VALUE) & 0xFFFFFFFF;
					mPaint.setColor(color);
					canvas.drawRoundRect(mNumberCell[i][j], 10, 10, mPaint);
					canvas.save();
					int color2 = (int)(Math.random()*Integer.MAX_VALUE) & 0xFFFFFFFF;
					mPaint.setColor(color2);
					canvas.translate(0, mRowGap/2);
					canvas.drawRoundRect(mNumberCell[i][j], 10, 10, mPaint);
					canvas.restore();
				}
			}
		}
	}
	
	class Number {
		
		int row, column;
		int number;
	}

}
