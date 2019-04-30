package com.example.mendez.transportdysplay.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

@SuppressLint("AppCompatCustomView")
public class micheckbox extends CheckBox {


        public micheckbox(Context context)
        {
            super(context);
        }

        public micheckbox(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        public micheckbox(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
        }

        @Override
        public void setPressed(boolean pressed)
        {
            if (pressed && getParent() instanceof View && ((View) getParent()).isPressed())
            {
                return;
            }
            super.setPressed(pressed);
        }

}
