package com.example.mendez.transportdysplay.util;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.util.Property;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AnimationMarker {
   public static void animateMarkerToICS(LatLng finalPosition, final LatlngInterpolator latLngInterpolator, Circle circle) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
  /*      Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);

        animator.setDuration(1000);
        animator.start();
*/
        Property<Circle,LatLng> pro=Property.of(Circle.class,LatLng.class,"center");
        ObjectAnimator animatorcircle=ObjectAnimator.ofObject(circle,pro,typeEvaluator,finalPosition);
        animatorcircle.setDuration(1000);
        animatorcircle.start();
    }
   public static void animateMarkerCar(final Marker marker, final LatLng finalPosition, final LatlngInterpolator latLngInterpolator, final String desfin) {
        final float startRotation = marker.getRotation();

        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {

                marker.setRotation(computeRotation(fraction,startRotation, Float.parseFloat(desfin)));
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);

        animator.setDuration(1000);
        animator.start();
    }
    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // girar el inicio a 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }
}
