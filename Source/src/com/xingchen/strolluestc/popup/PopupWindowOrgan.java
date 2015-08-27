package com.xingchen.strolluestc.popup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopupWindowOrgan extends LinearLayout
{
	/**
	 * x of anchor triangle in the popup
	 */
	private float mTriangleX;
	/**
	 * border color
	 */
	private int mBorderColor = 0xff93c47d;
	/**
	 * border width
	 */
	private int mBorderWidth = 2;
	/**
	 * background color
	 */
	private int mBgColor = 0xffffffff;
	/**
	 * anchor height
	 */
	private float mAnchorHeight = 20;
	/**
	 * anchor width
	 */
	private float mAnchorWidth = 30;
	/**
	 * If content under anchor
	 */
	private boolean mShowDown = true;
	/**
	 * Below items for draw
	 */
	private ShapeDrawable mBorderDrawable;
	private Path mBorderPath;
	private ShapeDrawable mBgDrawable;
	private Path mBgPath;
	private int mWidth;
	private int mHeight;
	/**
	 * Keep a record of original padding.
	 */
	private int mPadding;
	/**
	 * anchor x, y in screen
	 */
	private int mAnchorYUp;
	private int mAnchorYDown;
	private int mAnchorX;

	/**
	 * screen height & width
	 */
	private int mScreenHeight;
	private int mScreenWidth;
	private float mDensity;
	private PopupWindow mPopupWindow;

	public PopupWindowOrgan(Context context)
	{
		this(context, null);
	}

	public PopupWindowOrgan(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public PopupWindowOrgan(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		mPadding = getPaddingBottom();
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		mScreenHeight = dm.heightPixels;
		mScreenWidth = dm.widthPixels;
		mDensity = dm.scaledDensity;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
//		Log.d("PopupLayout", "w=" + w + " h=" + h + " oldw=" + oldw + " oldh="
//				+ oldh);
		mWidth = w;
		mHeight = h;
		show();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		initPath(mWidth, mHeight);
		mBorderDrawable = new ShapeDrawable(new PathShape(mBorderPath, mWidth,
				mHeight));
		mBorderDrawable.getPaint().setColor(mBorderColor);
		mBgDrawable = new ShapeDrawable(new PathShape(mBgPath, mWidth, mHeight));
		int bgColor = mBgColor;
		mBgDrawable.getPaint().setColor(bgColor);

		int x = 0;
		int y = 0;
		mBorderDrawable.setBounds(x, y, x + mWidth, y + mHeight);
		mBorderDrawable.draw(canvas);
		mBgDrawable.setBounds(x, y, x + mWidth, y + mHeight);
		mBgDrawable.draw(canvas);
		super.onDraw(canvas);
	}

	@SuppressWarnings("deprecation")
	public void show()
	{
		if (mPopupWindow != null)
		{
			mPopupWindow.dismiss();
		}
		mPopupWindow = new PopupWindow(this, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		Point pos = getLayoutValue();
		mTriangleX = mAnchorX - pos.x;
		//这里的绘制可能是以包括状态栏的手机屏幕左上角（0，0）为基准的
		//到底因为path（）的结构还是因为这个函数showAtLocation（）？
		mPopupWindow.showAtLocation(this, Gravity.LEFT | Gravity.TOP, pos.x,
				pos.y);
	}

	/**
	 * Calculate the pop up window's right position.
	 */
	private Point getLayoutValue()
	{
		int x = mAnchorX - mWidth / 2;
		if (x < 10 * mDensity)
		{
			x = (int) (10 * mDensity);
		} else if (x + mWidth > mScreenWidth - 10 * mDensity)
		{
			x = (int) (mScreenWidth - mWidth - 10 * mDensity);
		}
		boolean showDown = mAnchorYDown + mHeight < mScreenHeight
				|| mAnchorYDown <= mScreenHeight / 2;
		setShowDown(showDown);
		int y = showDown ? mAnchorYDown : mAnchorYUp - mHeight;
		return new Point(x, y);
	}

	public void setShowDown(boolean showDown)
	{
		mShowDown = showDown;
		if (mShowDown)
		{
			setPadding(getPaddingLeft(), (int) mAnchorHeight + mPadding,
					getPaddingRight(), mPadding);
		} else
		{
			setPadding(getPaddingLeft(), mPadding, getPaddingRight(),
					(int) mAnchorHeight + mPadding);
		}
	}

	private void initPath(int width, int height)
	{
		mBorderPath = new Path();
		mBgPath = new Path();

		if (mShowDown)
		{
			/**
			 * 这里的绘制可能是以包括状态栏的手机屏幕左上角（0，0）为基准的
			 * |<---------------width--------->|
			 *                  2
			 *                  /\ (anchor) 
			 * 0/7-------------1  3------------4 
			 * |								|
			 * |								|  height 
			 * | 								| 
			 * 6-------------------------------5
			 */
			PointF[] borderPoints = new PointF[] {
					new PointF(0, mAnchorHeight),
					new PointF(mTriangleX - mAnchorWidth / 2, mAnchorHeight),
					new PointF(mTriangleX, 0),
					new PointF(mTriangleX + mAnchorWidth / 2, mAnchorHeight),
					new PointF(width, mAnchorHeight),
					new PointF(width, height), new PointF(0, height),
					new PointF(0, mAnchorHeight), };
			mBorderPath = createLIneToPath(borderPoints);

			PointF[] bgPoints = new PointF[] {
					new PointF(borderPoints[0].x + mBorderWidth,
							borderPoints[0].y + mBorderWidth),
					new PointF(borderPoints[1].x + mBorderWidth,
							borderPoints[1].y + mBorderWidth),
					new PointF(borderPoints[2].x, borderPoints[2].y
							+ mBorderWidth),
					new PointF(borderPoints[3].x - mBorderWidth,
							borderPoints[3].y + mBorderWidth),
					new PointF(borderPoints[4].x - mBorderWidth,
							borderPoints[4].y + mBorderWidth),
					new PointF(borderPoints[5].x - mBorderWidth,
							borderPoints[5].y - mBorderWidth),
					new PointF(borderPoints[6].x + mBorderWidth,
							borderPoints[6].y - mBorderWidth),
					new PointF(borderPoints[7].x + mBorderWidth,
							borderPoints[7].y + mBorderWidth), };
			mBgPath = createLIneToPath(bgPoints);
		} else
		{
			PointF[] borderPoints = new PointF[] {
					new PointF(0, 0),
					new PointF(width, 0),
					new PointF(width, height - mAnchorHeight),
					new PointF(mTriangleX + mAnchorWidth / 2, height
							- mAnchorHeight),
					new PointF(mTriangleX, height),
					new PointF(mTriangleX - mAnchorWidth / 2, height
							- mAnchorHeight),
					new PointF(0, height - mAnchorHeight), new PointF(0, 0), };
			mBorderPath = createLIneToPath(borderPoints);

			PointF[] bgPoints = new PointF[] {
					new PointF(borderPoints[0].x + mBorderWidth,
							borderPoints[0].y + mBorderWidth),
					new PointF(borderPoints[1].x - mBorderWidth,
							borderPoints[1].y + mBorderWidth),
					new PointF(borderPoints[2].x - mBorderWidth,
							borderPoints[2].y - mBorderWidth),
					new PointF(borderPoints[3].x - mBorderWidth,
							borderPoints[3].y - mBorderWidth),
					new PointF(borderPoints[4].x, borderPoints[4].y
							- mBorderWidth),
					new PointF(borderPoints[5].x + mBorderWidth,
							borderPoints[5].y - mBorderWidth),
					new PointF(borderPoints[6].x + mBorderWidth,
							borderPoints[6].y - mBorderWidth),
					new PointF(borderPoints[7].x + mBorderWidth,
							borderPoints[7].y + mBorderWidth), };
			mBgPath = createLIneToPath(bgPoints);
		}
	}

	private Path createLIneToPath(PointF[] points)
	{
		Path path = new Path();
		if (points != null && points.length > 1)
		{
			path.moveTo(points[0].x, points[0].y);
			for (int i = 1; i < points.length; i++)
			{
				path.lineTo(points[i].x, points[i].y);
			}
		}
		path.close();
		return path;
	}

	public int getAnchorYUp()
	{
		return mAnchorYUp;
	}

	public void setAnchorYUp(int mAnchorYUp)
	{
		this.mAnchorYUp = mAnchorYUp;
	}

	public int getAnchorYDown()
	{
		return mAnchorYDown;
	}

	public void setAnchorYDown(int mAnchorYDown)
	{
		this.mAnchorYDown = mAnchorYDown;
	}

	public int getAnchorX()
	{
		return mAnchorX;
	}

	public void setAnchorX(int anchorX)
	{
		this.mAnchorX = anchorX;
	}

}
