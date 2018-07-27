package workandlife.com.workandlife.util;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class DummyLayout extends FrameLayout {
    private static final String TAG = DummyLayout.class.getSimpleName();
    private MyKeyEventCallbackListener myKeyEventCallbackListener;

    public DummyLayout(Context context) {
        super(context);
    }

    public DummyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DummyLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DummyLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public interface MyKeyEventCallbackListener {
        void onKeyEvent(KeyEvent event);
    }

    public void setMyKeyEventCallbackListener(MyKeyEventCallbackListener callback) {
        this.myKeyEventCallbackListener = callback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_BACK:
                    if (myKeyEventCallbackListener != null)
                        myKeyEventCallbackListener.onKeyEvent(event);
                    break;
                default:
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
