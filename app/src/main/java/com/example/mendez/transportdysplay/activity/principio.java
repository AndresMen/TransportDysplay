package com.example.mendez.transportdysplay.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.mendez.transportdysplay.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class principio extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principio);
        setAnimation();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final Intent in=new Intent(getBaseContext(),Inicio.class);
        final Bundle bundle=this.getIntent().getExtras();
        final Intent cc=new Intent(getBaseContext(),Crear_cuenta.class);
        int SPLASH_TIME_OUT = 5000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (user!=null){
                    if (bundle!=null){
                        in.putExtra("bun",bundle);
                    }
                    startActivity(in);
                    finish();
                }else{
                    startActivity(cc);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setAnimation() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(findViewById(R.id.textView), "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(2000);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(findViewById(R.id.textView), "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(2000);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(findViewById(R.id.textView), "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(2000);
        animatorSet.start();
        //findViewById(R.id.imagelogo2).setAlpha(1.0F);
        //  Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade);
        //  findViewById(R.id.imagelogo2).startAnimation(anim);

    }
}
