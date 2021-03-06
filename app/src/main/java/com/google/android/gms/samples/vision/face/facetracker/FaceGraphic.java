/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.annotation.TargetApi;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.google.android.Util;
import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;
import java.util.HashMap;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
//            Color.argb(255, 10, 46, 114),
//            Color.argb(255, 75, 60, 147),
//            Color.argb(255, 42, 157, 143),
//            Color.argb(255, 27, 196, 176),
            Color.argb(255, 5, 10, 25),
            Color.argb(255, 247, 245, 251)
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private JSONObject mMetaHash;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    private Context mContext;
    private HeadPhysics physics = new HeadPhysics();

    private static HashMap<String, JSONObject> faces;

    FaceGraphic(GraphicOverlay overlay, JSONObject metaHash, Context context) {
        super(overlay);
        faces = Util.faces;

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

//        Typeface plain = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
        mFacePositionPaint = new Paint();
//        mFacePositionPaint.setTypeface(plain);
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        mMetaHash = metaHash;
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    @TargetApi(21)
    private void drawSkewed(Canvas c, String s, int x, int y, int width, int offset) {
        Path path = new Path();
//        path.arcTo(x, y, x+width, y+20, 0, -180, true);
        path.moveTo(x,y);
        path.lineTo(x+width,y);
        c.drawTextOnPath(s, path, 0, 0, mIdPaint);
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;

        PointF north;
        if (face != null) {
            north = physics.updatePhysics(face);
        } else if (physics.isStarted) {
            north = physics.getNorth();
        } else {
            return;
        }

        float topX = translateX(north.x + face.getWidth() / 2);
        float topY = translateY(north.y);

        String[] info = {
                "Jacob Barnett",
                "Perimeter Institute",
                "Physics",
                "<Quote goes in here potentially>"
        };

        // Set String[] info
        if (faces.containsKey(mFaceId)) {
            while(faces.get(mFaceId).has("newId")) {
                try {
                    mFaceId = Integer.parseInt(faces.get(mFaceId).getString("newId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            try {
                String[] tempinfo = (String[])faces.get(mFaceId).get("info");
                if(tempinfo.length > 0) info = tempinfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int maxWidth = 460, height = 40, offset = 5, numSteps = info.length;

        for (int i = 0 ; i < numSteps ; i++) {
            drawSkewed(canvas,
                    info[i],
                    (int)(topX),
                    (int)topY+(i+1)*height,
                    maxWidth,
                    offset-(2*offset)*i/numSteps);
        }
//        canvas.drawTextOnPath("Jacob Barnett", topX, topY, mIdPaint);
//        canvas.drawText("Jacob Barnett", topX, topY, mIdPaint);
//        canvas.drawText("Jacob Barnett", topX, topY, mIdPaint);
//        canvas.drawText("Jacob Barnett", topX, topY, mIdPaint);
//        canvas.drawText("Jacob Barnett", topX, topY, mIdPaint);

        if (face == null) {
            return;
        }


        //==============================================================================================
        // FACE
        //==============================================================================================

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);

        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;

        // Draws a circle at the position of the detected face, with the face's track id below.

//        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
//        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
//        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET * 2, y - ID_Y_OFFSET * 2, mIdPaint);

        // Draws a bounding box around the face.
//        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }

    public void tidy() {
        JSONObject storage = new JSONObject();
        try {
            storage.put("deleted", true);
            storage.put("position", mFace.getPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Util.faces.put(""+mFaceId,
                storage
        );
    }
}
