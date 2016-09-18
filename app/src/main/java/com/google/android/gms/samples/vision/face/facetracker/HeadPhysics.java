package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.PointF;
import android.os.SystemClock;

import com.google.android.gms.vision.face.Face;

/**
 * Created by frieda on 16-09-17.
 */
public class HeadPhysics {

    long now = SystemClock.elapsedRealtime();
//    const

    Boolean isStarted = false;

    PointF target;
    PointF curr;

    PointF velocity = new PointF(0, 0);
    PointF accel = new PointF(0, 0);

    PointF updatePhysics(Face face) {
        // NOTE: Coordinates in this class are of the preview, not those of the view
        // FaceGraphic calls translate(X|Y) and scale(X|Y) to transform to the view
        float x = (face.getPosition().x + face.getWidth() / 2);
        float y = (face.getPosition().y + face.getHeight() / 2);

        float xOffset = (face.getWidth() / 2.0f);
        float yOffset = (face.getHeight() / 2.0f);

        // Aim to place the label above the head
        target = new PointF(x, y - (face.getHeight() * 1.0f / 4.0f));

//        velocity = velocity + accel;
//        velocity = (target - curr)

//        return curr;
        return target;
    }

    PointF getNorth() {
        //        return curr;
        return target;
    }

}
