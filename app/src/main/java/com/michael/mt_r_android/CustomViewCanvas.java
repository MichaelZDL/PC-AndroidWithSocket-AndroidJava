package com.michael.mt_r_android;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

class CustomViewCanvas extends View {
    private int onDrawFlag;
    public static final int DRAW_BASE_ROBOT = 1;
    public static final int DRAW_DOT362 = 2;
    private float xCoordinate;
    private float yCoordinate;
    private int[] bufferFromSocket=new int[362];
    Paint paint;
    float width;
    float rangeRad;
    float rangeCenterX;
    float rangeCenterY;
    RectF rect;

    public CustomViewCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(); // set a pen
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if((onDrawFlag == DRAW_BASE_ROBOT)||(onDrawFlag == DRAW_DOT362)){

            paint.setColor(0xFF000000);//canvas black
            canvas.drawRect(0, 0, width, width, paint);

            paint.setColor(0xFF80B547);//range circle green
            canvas.drawArc(rect, 180, 180, true, paint);

            paint.setColor(0xFF6F6F6F);//robot body gray 0xFFB0B0B0
            float kuan=10;
            float chang=30;
            canvas.drawRect(rangeCenterX - width * (chang / 400), rangeCenterY - width * (kuan / 400),
                    rangeCenterX + width * (chang / 400), rangeCenterY, paint);
        }

        if(onDrawFlag == DRAW_DOT362){
            float dot_x,dot_y;
            paint.setColor(0xFFF1F1F1);//dot white
            float rad = 2;
            for (int i=0; i < 181; i++){
                if((bufferFromSocket[2*i]/10)>=200)
                    dot_x=200+200;
                else if((bufferFromSocket[2*i]/10)<=(-200))
                    dot_x=(-200)+200;
                else
                    dot_x=(bufferFromSocket[2*i]/10)+200;

                if((bufferFromSocket[2*i+1]/10)>=400)
                    dot_y=400-400;
                else
                    dot_y=400-(bufferFromSocket[2*i+1]/10);
                canvas.drawCircle(width * (dot_x / 400), width * (dot_y / 400), width * (rad / 400), paint);
            }
        }

    }

    protected void drawInit(){
        width = getWidth();
        //for range circle
        rangeRad = 55;
        rangeCenterX=0+200;
        rangeCenterY=400-0;
        rangeRad =width * (rangeRad / 400);
        rangeCenterX=width * (rangeCenterX / 400);
        rangeCenterY=width * (rangeCenterY / 400);
        rect = new RectF(rangeCenterX - rangeRad, rangeCenterY - rangeRad,
                rangeCenterX + rangeRad, rangeCenterY + rangeRad);
    }

    protected  void drawLaserMap(int[] buf){
        onDrawFlag = DRAW_DOT362;
        drawInit();
        bufferFromSocket = buf;
        invalidate();
    }
}
