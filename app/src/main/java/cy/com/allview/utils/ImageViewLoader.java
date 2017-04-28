package cy.com.allview.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Sampler;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator
 * on 2017/4/10.
 * des:自定义图片加载工具类
 */

public class ImageViewLoader {

    //图片的缓存对象
    private LruCache<String, Bitmap> mLruCache;
    //线程池
    private ExecutorService mThreadPool;
    //线程池的线程个数
    private static final int DEFAULT_THREAD_COUNT = 1;
    //队列的调度方式
    private TYPE mType = TYPE.LIFO;
    //任务队列
    private LinkedList<Runnable> mTaskQuene;
    //后台轮训线程
    private Thread mPoolThread;
    //后台轮训线程的handler;
    private Handler mPoolThreadHander;
    //ui线程的Handler
    private Handler mUIHandler;
    //信号量,因为有后台线程和添加子线程获取图片两个线程，存在并行问题，有可能导致mPoolThreadHander没被初始化就被调用，所以启用信号量
    //这里的信号量是为了初始化后台handler
    private Semaphore mSamplerPoolThreadHander=new Semaphore(0);
    //这个信号量是为了添加任务的时候先不管后台线程执行，直到后台线程执行一个完毕了，然后在重新调起从任务队列中取出一个，避免添加一个
    //任务到任务队列中，又直接被后台轮训线程从队列中取出来任务执行。
    private Semaphore mSamplerThreadPool;
    private ImageViewLoader(int threadCount, TYPE type) {
        init(threadCount, type);
    }

    private void init(int threadCount, TYPE type) {
        //后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        //线程池取出一个任务进行
                        mThreadPool.execute(getTask());
                        try {
                            //这里初始化的信号量有3个，所以当已经执行了3个，就会阻塞执行第4个任务
                            mSamplerThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSamplerPoolThreadHander.release();//当handler初始化完成之后，取消阻塞
                Looper.loop();
            }
        };
        mPoolThread.start();//开启后台轮询线程
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mLruCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQuene = new LinkedList<Runnable>();
        mType = type;

        mSamplerThreadPool=new Semaphore(threadCount);
    }

    //从任务队列取出任务
    private Runnable getTask() {
        if (mType == TYPE.FIFO) {
            return mTaskQuene.removeFirst();
        } else if (mType == TYPE.LIFO) {
            return mTaskQuene.removeLast();
        }
        return null;
    }

    private enum TYPE {
        FIFO, LIFO
    }

    private static ImageViewLoader mInstance;

    public static ImageViewLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageViewLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageViewLoader(DEFAULT_THREAD_COUNT, TYPE.LIFO);
                }
            }
        }
        return mInstance;
    }
    public static ImageViewLoader getInstance(int threadCount,TYPE type) {
        if (mInstance == null) {
            synchronized (ImageViewLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageViewLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    Bitmap bitmap = holder.bitmap;
                    String path = holder.path;
                    ImageView imageView = holder.imageView;
                    if (imageView.getTag().toString().equals(path)) {//
                        imageView.setImageBitmap(bitmap);
                    }
                }
            };
        }
        Bitmap bitmap = getBitmapFromCache(path);
        if (bitmap != null) {
            refreshBitmap(path, imageView, bitmap);
        } else {
            addTask(new Runnable() {//添加一个任务到后台线程池中
                @Override
                public void run() {
                    //加载图片
                    //获取图片需要显示的大小
                    ImageSize imageSize=getImageSise(imageView);
                    //压缩图片
                    Bitmap bm=decodeBitmapFromPath(path,imageSize.width,imageSize.height);
                    //图片加入到缓存中
                    addBitmap2LruCache(bm,path);
                    //刷新图片
                    refreshBitmap(path, imageView, bm);

                    mSamplerThreadPool.release();//当执行完一个任务后，就释放一个信号量，然后让后台轮训线程接着从任务队列中取任务执行
                }
            });
        }
    }

    private void refreshBitmap(String path, ImageView imageView, Bitmap bitmap) {
        Message msg = Message.obtain();
        ImageBeanHolder holder = new ImageBeanHolder();
        holder.bitmap = bitmap;
        holder.imageView = imageView;
        holder.path = path;
        msg.obj = holder;
        mUIHandler.sendMessage(msg);
    }

    private void addBitmap2LruCache(Bitmap bitmap, String path) {
        if(getBitmapFromCache(path)==null){
            if(bitmap!=null){
                mLruCache.put(path,bitmap);
            }
        }
    }

    //根据图片需要显示的大小压缩图片
    private Bitmap decodeBitmapFromPath(String path, int width, int height) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;//只获取图片的宽高，并不将图片加载到内存中
        BitmapFactory.decodeFile(path,options);
        int sampleSize = caculteSampleSize(options, width, height);
        options.inJustDecodeBounds=false;
        options.inSampleSize=sampleSize;
        Bitmap bitmap=BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    //根据图片宽高以及给定的宽高获取sampleSize
    private int caculteSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width=options.outWidth;
        int height=options.outHeight;
        int sampleSize=0;
        if(height>reqHeight||width>reqWidth){
            int widthRadio=Math.round(width*1.0f/reqWidth);
            int heightRadio=Math.round(height*1.0f/reqHeight);
            sampleSize=Math.min(widthRadio,heightRadio);
        }
        return sampleSize;
    }

    protected ImageSize getImageSise(ImageView imageView) {
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ImageSize imageSize = new ImageSize();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width;//如果控件还没显示出来，就先赋值布局中给到的宽度
        }
        if (width <= 0) {
//            width = imageView.getMaxWidth();
            width = getImageFieldValue(imageView,"mMaxWidth");
        }
        if (width <= 0) {
            width=displayMetrics.widthPixels;
        }
        imageSize.width=width;

        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;//如果控件还没显示出来，就先赋值布局中给到的宽度
        }
        if (height <= 0) {
//            height = imageView.getMaxHeight();
            height = getImageFieldValue(imageView,"mMaxHeight");
        }
        if (height <= 0) {
            height=displayMetrics.heightPixels;
        }
        imageSize.height=height;

        return imageSize;
    }

    //通过反射获取对象的某个属性值
    private static int getImageFieldValue(Object obj,String filedName){
        int value=0;
        try {
            Field field=ImageView.class.getDeclaredField(filedName);
            field.setAccessible(true);
            int anInt = field.getInt(obj);
            if(anInt>0&&anInt<Integer.MAX_VALUE){
                value=anInt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQuene.add(runnable);
        try {
            if(mPoolThreadHander==null){
                mSamplerPoolThreadHander.acquire();//为0的时候阻塞,同步防止并发问题。两个线程中操作同一个变量，需要同步，避免空指针，多线程阻塞的时候又要避免死锁
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromCache(String path) {
        return mLruCache.get(path);
    }

    private class ImageBeanHolder {
        Bitmap bitmap;
        String path;
        ImageView imageView;
    }

    private class ImageSize {
        int width;
        int height;
    }
}
