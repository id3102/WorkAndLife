package workandlife.com.workandlife;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import workandlife.com.workandlife.util.DummyLayout;

public class KeyListenerService extends Service {
    private static final String TAG = KeyListenerService.class.getSimpleName();

    private View mView;
    private WindowManager mWindowManager;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            //       | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            PixelFormat.TRANSLUCENT
    );

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        LayoutInflater mInfalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView  = mInfalter.inflate(R.layout.dummy_layout, null);

        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = (Build.VERSION.SDK_INT < 20 ? pm.isScreenOn() : pm.isInteractive());
        if (!isScreenOn) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mView, mParams);
        }

        DummyLayout dummyLayout = mView.findViewById(R.id.service_root_view);
        dummyLayout.setMyKeyEventCallbackListener((new DummyLayout.MyKeyEventCallbackListener() {
            @Override
            public void onKeyEvent(KeyEvent event) {
                int code = event.getKeyCode();
                Log.d(TAG, "onKeyEvent, keycode: " + code);
                switch (code) {
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        Toast.makeText(getBaseContext(), "pressed volume up", Toast.LENGTH_LONG).show();
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        break;
                }
            }
        }));

        // receive screen on/off event
        // remove window when screen is on (cannot handle system back key)
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(mScreenEvent, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mScreenEvent);
    }

    BroadcastReceiver mScreenEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.d(TAG, "Screen off, add view");
                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                mWindowManager.addView(mView, mParams);
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                Log.d(TAG, "Screen on, remove view");
                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                mWindowManager.removeViewImmediate(mView);
            }
        }
    };
}
