package workandlife.com.workandlife;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import workandlife.com.workandlife.util.WorkTimer;

public class FloatingButtonService extends Service {
    private static final String TAG = FloatingButtonService.class.getSimpleName();

    // Floating button
    private View mView;
    private TextView mStatusText;
    private Button mStart;
    private Button mPause;
    private Button mStop;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    // Timer
    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;

    private static int mCurrentStatus = STOPPED;

    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    public  void setCurrentStatus(int status) {
        mCurrentStatus = status;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private float mTouchX, mTouchY;
    private int mViewX, mViewY;

    private boolean mIsMoving = false;

    private View.OnTouchListener mStartTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsMoving = false;
                    mTouchX = event.getRawX();
                    mTouchY = event.getRawY();
                    mViewX = mParams.x;
                    mViewY = mParams.y;
                    break;

                case MotionEvent.ACTION_UP:
                    if (!mIsMoving) {
                        if (getCurrentStatus() == STOPPED) {
                            setButtonVisibility(View.GONE, View.VISIBLE);
                            WorkTimer.start();
                        } else if (getCurrentStatus() == PAUSED) {
                            mStart.setVisibility(View.GONE);
                            mPause.setVisibility(View.VISIBLE);
                            WorkTimer.resume();
                        }
                        mStatusText.setText(R.string.working);
                        setCurrentStatus(RUNNING);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    mIsMoving = true;

                    int x = (int) (event.getRawX() - mTouchX);
                    int y = (int) (event.getRawY() - mTouchY);
                    final int num = 5;
                    if ((x > -num && x < num) && (y > -num && y < num)) {
                        mIsMoving = false;
                        break;
                    }
                    mParams.x = mViewX + x;
                    mParams.y = mViewY + y;

                    mWindowManager.updateViewLayout(mView, mParams);
                    break;
            }
            return true;
        }
    };

    private View.OnTouchListener mPauseTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsMoving = false;
                    mTouchX = event.getRawX();
                    mTouchY = event.getRawY();
                    mViewX = mParams.x;
                    mViewY = mParams.y;
                    break;

                case MotionEvent.ACTION_UP:
                    if (!mIsMoving) {
                        mStart.setVisibility(View.VISIBLE);
                        mPause.setVisibility(View.INVISIBLE);
                        mStatusText.setText(R.string.pausing);
                        WorkTimer.pause();
                        setCurrentStatus(PAUSED);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    mIsMoving = true;

                    int x = (int) (event.getRawX() - mTouchX);
                    int y = (int) (event.getRawY() - mTouchY);
                    final int num = 5;
                    if ((x > -num && x < num) && (y > -num && y < num)) {
                        mIsMoving = false;
                        break;
                    }
                    mParams.x = mViewX + x;
                    mParams.y = mViewY + y;

                    mWindowManager.updateViewLayout(mView, mParams);
                    break;
            }
            return true;
        }
    };

    private View.OnTouchListener mStopTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsMoving = false;
                    mTouchX = event.getRawX();
                    mTouchY = event.getRawY();
                    mViewX = mParams.x;
                    mViewY = mParams.y;
                    break;

                case MotionEvent.ACTION_UP:
                    if (!mIsMoving) {
                        setButtonVisibility(View.VISIBLE, View.GONE);
                        mStatusText.setText("Worked " + WorkTimer.stop());
                        setCurrentStatus(STOPPED);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    mIsMoving = true;

                    int x = (int) (event.getRawX() - mTouchX);
                    int y = (int) (event.getRawY() - mTouchY);
                    final int num = 5;
                    if ((x > -num && x < num) && (y > -num && y < num)) {
                        mIsMoving = false;
                        break;
                    }
                    mParams.x = mViewX + x;
                    mParams.y = mViewY + y;

                    mWindowManager.updateViewLayout(mView, mParams);
            }
            return true;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        LayoutInflater mInfalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView  = mInfalter.inflate(R.layout.floating_button_layout, null);
        mStatusText = mView.findViewById(R.id.status_text);
        mStart = mView.findViewById(R.id.floating_start);
        mPause = mView.findViewById(R.id.floating_pause);
        mStop = mView.findViewById(R.id.floating_stop);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mView, mParams);

        mStart.setOnTouchListener(mStartTouchListener);
        mPause.setOnTouchListener(mPauseTouchListener);
        mStop.setOnTouchListener(mStopTouchListener);

        // set reset alarm (default 03:00 KST)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        Log.d(TAG, "sy310, gettime in millis : " + calendar.get(Calendar.HOUR_OF_DAY));
        Log.d(TAG, "sy310, gettime in millis : " + calendar.getTimeInMillis());
        WorkTimer.setResetTime(calendar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
        Log.d(TAG, "onDestroy");
        if (mView != null) {
            mWindowManager.removeView(mView);
            mView = null;
        }
    }

    private void setButtonVisibility(int startVisibie, int stopVisible) {
        mStart.setVisibility(startVisibie);
        mPause.setVisibility(stopVisible);
        mStop.setVisibility(stopVisible);
    }
}
