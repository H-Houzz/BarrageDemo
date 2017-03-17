package com.cnlive.barragedemo.danmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;

import com.cnlive.barragedemo.model.Danmu;
import com.cnlive.barragedemo.utils.DpOrSp2PxUtil;
import com.cnlive.barragedemo.view.CenteredImageSpan;
import com.cnlive.barragedemo.view.CircleDrawable;

import java.util.HashMap;
import java.util.List;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * @Author Mr.hou
 * @time 2017/3/16
 * @Desc
 */

public class DanmuControl {
    private BaseDanmaku danmaku;
    private static final String TAG = "DanmuControl";

    //弹幕显示的时间(如果是list的话，会 * i)，记得加上mDanmakuView.getCurrentTime()
    private static final long ADD_DANMU_TIME = 2000;

    private static final int PINK_COLOR = 0xffff5a93;//粉红 楼主
    private static final int ORANGE_COLOR = 0xffff815a;//橙色 我
    private static final int BLACK_COLOR = 0xb2000000;//黑色 普通

    private int BITMAP_WIDTH = 30;//头像的大小
    private int BITMAP_HEIGHT = 30;
    private float DANMU_TEXT_SIZE = 14f;//弹幕字体的大小

    //这两个用来控制两行弹幕之间的间距
    private int DANMU_PADDING = 8;
    private int DANMU_PADDING_INNER = 7;
    private int DANMU_RADIUS = 11;//圆角半径

    private final int mGoodUserId = 1;
    private final int mMyUserId = 2;

    private Context mContext;
    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;


    public DanmuControl(Context context) {
        this.mContext = context;
        setSize(context);
        initDanmuConfig();


    }


    /**
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
     */
    private void setSize(Context context) {

        BITMAP_WIDTH = DpOrSp2PxUtil.dp2pxConvertInt(context, BITMAP_HEIGHT);
        BITMAP_HEIGHT = DpOrSp2PxUtil.dp2pxConvertInt(context, BITMAP_HEIGHT);
        DANMU_PADDING = DpOrSp2PxUtil.dp2pxConvertInt(context, DANMU_PADDING);
        DANMU_PADDING_INNER = DpOrSp2PxUtil.dp2pxConvertInt(context, DANMU_PADDING_INNER);
        DANMU_RADIUS = DpOrSp2PxUtil.dp2pxConvertInt(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = DpOrSp2PxUtil.sp2px(context, DANMU_TEXT_SIZE);
    }

    /**
     * 初始化配置
     */
    private void initDanmuConfig() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示2行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)//越大速度越慢
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
    }




    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            if (!danmaku.isGuest && danmaku.userId == mGoodUserId && mGoodUserId != 0) {
                paint.setColor(PINK_COLOR);//粉红 楼主
            } else if (!danmaku.isGuest && danmaku.userId == mMyUserId
                    && danmaku.userId != 0) {
                paint.setColor(ORANGE_COLOR);//橙色 我
            } else {
                paint.setColor(BLACK_COLOR);//黑色 普通
            }
            if (danmaku.isGuest) {//如果是赞 就不要设置背景
                paint.setColor(BLACK_COLOR);
            }
            canvas.drawRoundRect(new RectF(left + DANMU_PADDING_INNER, top + DANMU_PADDING_INNER
                            , left + danmaku.paintWidth - DANMU_PADDING_INNER + 6,
                            top + danmaku.paintHeight - DANMU_PADDING_INNER + 6),//+6 主要是底部被截得太厉害了，+6是增加padding的效果
                    DANMU_RADIUS, DANMU_RADIUS, paint);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 禁用描边绘制
        }
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void setDanmakuView(IDanmakuView danmakuView) {
        this.mDanmakuView = danmakuView;
        initDanmuView();
    }

    private void initDanmuView() {
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });
        }

        mDanmakuView.prepare(new BaseDanmakuParser() {

            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        mDanmakuView.enableDanmakuDrawingCache(true);
    }

    public void pause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    public void hide() {
        if (mDanmakuView != null) {
            mDanmakuView.hide();
        }
    }

    public void show() {
        if (mDanmakuView != null) {
            mDanmakuView.show();
        }
    }

    public void resume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    public void destroy() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public void addDanmuList(final List<Danmu> danmuLists) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < danmuLists.size(); i++) {
                    addDanmu(danmuLists.get(i), i);
                }
            }
        }).start();
    }

    public void addDanmu(Danmu danmu, int i) {
        if (danmu.getPosition() == 1) {
            danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_TOP);
        } else if (danmu.getPosition() == 2) {
            danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        } else if (danmu.getPosition() == 3) {
            danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_FIX_BOTTOM);

        } else {
            danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        }
        danmaku.userId = danmu.userId;
        danmaku.isGuest = danmu.type.equals("Like");//isGuest此处用来判断是赞还是评论
        SpannableStringBuilder spannable;
        Bitmap bitmap;
        CircleDrawable circleDrawable;
        if (danmu.getTextSize() == 1) {
            danmaku.textSize = 20;
            bitmap = getDefaultBitmap(danmu.avatarUrl, 20, 20);
            circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
            circleDrawable.setBounds(0, 0, DpOrSp2PxUtil.dp2pxConvertInt(mContext, 20), DpOrSp2PxUtil.dp2pxConvertInt(mContext, 20));

        } else if (danmu.getTextSize() == 2) {
            danmaku.textSize = 30;
            bitmap = getDefaultBitmap(danmu.avatarUrl, 30, 30);
            circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
            circleDrawable.setBounds(0, 0, DpOrSp2PxUtil.dp2pxConvertInt(mContext, 30), DpOrSp2PxUtil.dp2pxConvertInt(mContext, 30));
        } else if (danmu.getTextSize() == 3) {
            danmaku.textSize = 40;
            bitmap = getDefaultBitmap(danmu.avatarUrl, 40, 40);
            circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
            circleDrawable.setBounds(0, 0, DpOrSp2PxUtil.dp2pxConvertInt(mContext, 40), DpOrSp2PxUtil.dp2pxConvertInt(mContext, 40));
        } else {
            danmaku.textSize = 20;
            bitmap = getDefaultBitmap(danmu.avatarUrl, 20, 20);
            circleDrawable = new CircleDrawable(mContext, bitmap, danmaku.isGuest);
            circleDrawable.setBounds(0, 0, DpOrSp2PxUtil.dp2pxConvertInt(mContext, 20), DpOrSp2PxUtil.dp2pxConvertInt(mContext, 20));
        }


        spannable = createSpannable(circleDrawable, danmu.content);
        danmaku.text = spannable;
        danmaku.padding = DANMU_PADDING;
        danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致行数的限制失效
        danmaku.isLive = false;
        danmaku.time = mDanmakuView.getCurrentTime() + (i * ADD_DANMU_TIME);
        danmaku.textSize = DANMU_TEXT_SIZE/* * (mDanmakuContext.getDisplayer().getDensity() - 0.6f)*/;

        if (danmu.getTextColor() == 1) {
            danmaku.textColor = Color.GRAY;
        } else if (danmu.getTextColor() == 2) {
            danmaku.textColor = Color.GREEN;
        } else if (danmu.getTextColor() == 3) {
            danmaku.textColor = Color.RED;
        } else if (danmu.getTextColor() == 4) {
            danmaku.textColor = Color.YELLOW;
        } else {
            danmaku.textColor = Color.BLACK;
        }

        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        mDanmakuView.addDanmaku(danmaku);
    }

    private Bitmap getDefaultBitmap(int drawableId, int mheight, int mwidth) {
        Bitmap mDefauleBitmap = null;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(((float) DpOrSp2PxUtil.dp2pxConvertInt(mContext, mwidth)) / width, ((float) DpOrSp2PxUtil.dp2pxConvertInt(mContext, mheight)) / height);
            mDefauleBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        }
        return mDefauleBitmap;
    }

    private SpannableStringBuilder createSpannable(Drawable drawable, String content) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        CenteredImageSpan span = new CenteredImageSpan(drawable);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(content)) {
            spannableStringBuilder.append(" ");
            spannableStringBuilder.append(content.trim());
        }
        return spannableStringBuilder;
    }
}
