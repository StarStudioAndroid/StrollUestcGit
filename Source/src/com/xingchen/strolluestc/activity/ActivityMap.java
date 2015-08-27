package com.xingchen.strolluestc.activity;

import xingchen.mianliao.strolluestc.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.xingchen.strolluestc.EnumOrganizationName;
import com.xingchen.strolluestc.popup.PopupWindowOrgan;

public class ActivityMap extends Activity implements OnClickListener
{
	private static final String TAG = "StrollUestc";
	SubsamplingScaleImageView imageView;
	PopupWindowOrgan mPopupLayout;
	TextView mTextView;
	boolean showAble = false;
	boolean calScale = true;

	private float bitScale;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		imageView = (SubsamplingScaleImageView) findViewById(R.id.imageview);
		imageView
				.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
		imageView.setImage(ImageSource.resource(R.drawable.map));
//		imageView.setDebug(true);

		// 在这里计算minScale得到的是NaN
		// bitScale=(imageView.getMaxScale()-imageView.getMinScale())/5;

		findViewById(R.id.zoomIn).setOnClickListener(this);
		findViewById(R.id.zoomOut).setOnClickListener(this);
		findViewById(R.id.info).setOnClickListener(this);

		mPopupLayout = (PopupWindowOrgan) LayoutInflater.from(this).inflate(
				R.layout.popup_organ, null);
		mTextView = (TextView) mPopupLayout.findViewById(R.id.nameTv);
		mPopupLayout.findViewById(R.id.detailButton).setOnClickListener(
				new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Toast.makeText(ActivityMap.this, "button is pressed",
								Toast.LENGTH_SHORT).show();
					}
				});

		initialiseImage();
	}

	private void initialiseImage()
	{
		final GestureDetector gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener()
				{
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e)
					{
						// Log.d(TAG, "gestureDetector onSingleTapConfirmed");
						if (imageView.isReady())
						{
							PointF sCoord = imageView.viewToSourceCoord(
									e.getX(), e.getY());
							EnumOrganizationName organizationName = getOrganizationName(
									(int) sCoord.x, (int) sCoord.y);
							if (organizationName != null)
							{
								switch (organizationName)
								{
								case LIBRARY:
									showPopupLayout((int) e.getX(),
											(int) e.getY(), "图书馆");
									break;
								case STUDENTS_UNION:
									showPopupLayout((int) e.getX(),
											(int) e.getY(), "学生活动中心");
									break;
								case SPORTS_CENTRE:
									showPopupLayout((int) e.getX(),
											(int) e.getY(), "体育运动中心");
									break;
								default:
									break;
								}
							}
						} else
						{
							Toast.makeText(getApplicationContext(),
									"Single tap: Image not ready",
									Toast.LENGTH_SHORT).show();
						}
						return true;
					}
				});

		imageView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{

				return gestureDetector.onTouchEvent(motionEvent);
			}
		});
	}

	private void showPopupLayout(int x, int y, String name)
	{
		int statusBarHeight = getStatusBarHeight();
		Log.d(TAG, "showPopupLayout");
		mTextView.setText(name);
		mPopupLayout.setAnchorYUp(y + statusBarHeight);
		mPopupLayout.setAnchorYDown(y + statusBarHeight);
		mPopupLayout.setAnchorX(x);
		mPopupLayout.show();
	}

	public int getStatusBarHeight()
	{
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0)
		{
			result = getResources().getDimensionPixelSize(resourceId);
		}
		// System.out.println("StatusBarHeight:" + result);
		return result;
	}

	private EnumOrganizationName getOrganizationName(int x, int y)
	{
		if (x >= 528 && x <= 694 && y >= 1110 && y <= 1260)
		{
			return EnumOrganizationName.LIBRARY;
		} else if (x >= 975 && x <= 1228 && y >= 1452 && y <= 1606)
		{
			return EnumOrganizationName.SPORTS_CENTRE;
		} else if (x >= 342 && x <= 410 && y >= 628 && y <= 773)
		{
			return EnumOrganizationName.STUDENTS_UNION;
		} else
		{
			return null;
		}
	}

	@Override
	public void onClick(View v)
	{
		if (calScale)
		{
			bitScale = (imageView.getMaxScale() - imageView.getMinScale()) / 5;
			calScale = false;
			Log.d("StrollUestc",
					"" + imageView.getMaxScale() + ","
							+ imageView.getMinScale() + "," + bitScale);
		}
		switch (v.getId())
		{
		case R.id.zoomIn:
			if (imageView.getScale() + bitScale <= imageView.getMaxScale())
			{
				imageView.setScaleAndCenter(imageView.getScale() + bitScale,
						imageView.getCenter());
			}
			// Toast.makeText(MainActivity.this, "zoomIn is pressed",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.zoomOut:
			if (imageView.getScale() - bitScale >= imageView.getMinScale())
			{
				imageView.setScaleAndCenter(imageView.getScale() - bitScale,
						imageView.getCenter());
			}
			// Toast.makeText(MainActivity.this, "zoomOut is pressed",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.info:
			// Toast.makeText(MainActivity.this, "info is pressed",
			// Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, ActivityMore.class));
			break;
		default:
			break;
		}
	}
}
