package com.example.mendez.transportdysplay.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mendez.transportdysplay.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class infomarker implements GoogleMap.InfoWindowAdapter {

    Context context;
    public  infomarker( Context ctx){
        context=ctx;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v =((Activity)context).getLayoutInflater().inflate(R.layout.info_marker, null);
        TextView tvdi = (TextView) v.findViewById(R.id.direc);
        TextView tvdi2 = (TextView) v.findViewById(R.id.direc2);
        TextView tvcon = (TextView) v.findViewById(R.id.con);
        TextView dis=(TextView)v.findViewById(R.id.dis);
        LinearLayout llda=(LinearLayout)v.findViewById(R.id.llda);
        TextView has=(TextView)v.findViewById(R.id.has);
        TextView des=(TextView)v.findViewById(R.id.des);
        if (marker.getSnippet()!=null){

            String []da=marker.getSnippet().split(",");
            String []x=da[0].split(" ");
            String []di=marker.getTitle().split("-");
            tvdi.setText(di[0]);
            tvdi2.setText(di[1]);

            tvcon.setText(x[0]);
            dis.setText(da[1]);
        }
        else{
            llda.setVisibility(View.GONE);
            has.setVisibility(View.GONE);
            des.setVisibility(View.GONE);
            tvdi2.setVisibility(View.GONE);
            tvdi.setText(marker.getTitle());
        }
        return v;
    }
}
