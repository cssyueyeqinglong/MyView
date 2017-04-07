package cy.com.allview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cy.com.allview.R;

/**
 * Created by Administrator
 * on 2017/3/28.
 * des:仿华为天气view
 */

public class WeatherView extends View {
    private int width;//控件的宽
    private int height;//控件的高
    private int radious;//半径
    private Paint mArcPaint;
    private float startDegree = 60;//起始角度
    private float sweepDegree = -300;//旋转的角度
    private Paint mLinePaint;

    public WeatherView(Context context) {
        super(context);
        init();
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mArcPaint = new Paint();
        mArcPaint.setColor(Color.BLUE);
        mArcPaint.setStrokeWidth(2);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLUE);
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setTextAlign(Paint.Align.CENTER);
        mLinePaint.setTextSize(144);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //=======================================在这里初始化控件的大小，是所有自定义view的万能代码
        int baseWidth = 600;//
        int width = measureDimension(600, widthMeasureSpec);
        int height = measureDimension(600, heightMeasureSpec);
        int baseWid = Math.min(width, height);
        setMeasuredDimension(baseWidth, baseWid);//使控件是一个正方形
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int size;
        int mode = MeasureSpec.getMode(measureSpec);
        int measuserSize = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            size = measuserSize;
        } else {
            size = defaultSize;
            if (mode == MeasureSpec.AT_MOST) {
                size = Math.min(size, defaultSize);
            }
        }
        return size;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radious = (width - getPaddingLeft() - getPaddingRight()) / 2;
        canvas.translate(width / 2, height / 2);//将画布的中心移动到控件中心

//        drawArcView(canvas);
        drawLine(canvas);
        drawBitmap(canvas);
    }


    //绘制图片
    private void drawBitmap(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        canvas.drawBitmap(bitmap,-bitmap.getWidth()/2,radious-bitmap.getHeight()/2,null);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.FILL);
        canvas.drawText("20°",0,0+getTextPaintOffSet(mLinePaint),mLinePaint);
    }

    private int getTextPaintOffSet(Paint mLinePaint) {
        Paint.FontMetricsInt metricsInt = mLinePaint.getFontMetricsInt();
        return -metricsInt.descent+(metricsInt.bottom-metricsInt.top)/2;
    }

    //绘制小短线
    private void drawLine(Canvas canvas) {
        canvas.save();
        float itemDegree = 5;
        int count = 60;
        canvas.rotate(210);
        for (int i = 0; i <= count; i++) {
            if (i == 0 || i == count) {
                mLinePaint.setStrokeWidth(5);
                canvas.drawLine(0,-radious,0,-radious+40,mLinePaint);
            }else {
                mLinePaint.setStrokeWidth(3);
                canvas.drawLine(0,-radious,0,-radious+20,mLinePaint);
            }
            canvas.rotate(itemDegree);
        }

        canvas.restore();
    }


    //画圆
    private void drawArcView(Canvas canvas) {
        RectF rectF = new RectF(-radious, -radious, radious, radious);
        canvas.drawArc(rectF, startDegree, sweepDegree, false, mArcPaint);
    }
}
