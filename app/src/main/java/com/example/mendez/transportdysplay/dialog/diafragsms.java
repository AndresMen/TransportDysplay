package com.example.mendez.transportdysplay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mendez.transportdysplay.R;
import com.example.mendez.transportdysplay.activity.Inicio;

public class diafragsms extends DialogFragment{
    Context mcontext;

    TextView btnvr,btncr;
    TextView sms;
    public diafragsms(){
        setRetainInstance(true);
    }
    public AlertDialog createDia() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialasms2, null);
        btnvr=(TextView) v.findViewById(R.id.btnversms);

        btncr=(TextView)v.findViewById(R.id.btncersms);

        sms=(TextView)v.findViewById(R.id.tvme);
       sms.setText( getArguments().getString("nsm"));


        builder.setView(v);/*
        Window window = getDialog().getWindow();

        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.TOP|Gravity.LEFT);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 300;
        params.y = 100;
        window.setAttributes(params);*/
        btnvr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsm=new Intent(getContext(),Inicio.class);
                newsm.putExtra("me","me");
                startActivity(newsm);
            }
        });
        btncr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Fragment f =((FragmentActivity)mcontext).getSupportFragmentManager().findFragmentById(R.id.conten);
                if (f instanceof message){ // do something with f ((CustomFragmentClass) f).doSomething();
                    // Toast.makeText(getBaseContext(),"fragment visible",Toast.LENGTH_SHORT).show();
                    Intent newsm=new Intent(getContext(),Inicio.class);
                    newsm.putExtra("map","2");
                    startActivity(newsm);
                }else {*/
                dismiss();
              //  }
            }
        });
//setDialogPosition();
        return builder.create();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setDialogPosition();
        return createDia();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setDialogPosition();
    }

    private void setDialogPosition() {
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP | Gravity.START);

        WindowManager.LayoutParams params = window.getAttributes();
        params.y = dpToPx(60);

        window.setAttributes(params);
    }
    private int dpToPx(int dp) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
