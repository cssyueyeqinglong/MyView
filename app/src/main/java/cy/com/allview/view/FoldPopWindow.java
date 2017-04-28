package cy.com.allview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import cy.com.allview.R;
import cy.com.allview.bean.FoldBean;
import cy.com.allview.utils.ImageViewLoader;

import static cy.com.allview.R.id.tv_name;

/**
 * Created by Administrator
 * on 2017/4/10.
 * des:
 */

public class FoldPopWindow extends PopupWindow {
    private int mWidth;
    private int mHeight;
    private View mContenView;
    private ListView mListView;
    private List<FoldBean> mDatas;
    private Context mContext;

    public FoldPopWindow(Context context, List<FoldBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        cacluteWidthAndHeight(context);
        mContenView = LayoutInflater.from(context).inflate(R.layout.pop_window_main, null);
        setContentView(mContenView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                lightsOnOrOff(1.0f);
            }
        });
        initViews(context);
    }

    private void initViews(Context context) {
        mListView = (ListView) mContenView.findViewById(R.id.list_view);
        mListView.setAdapter(new FoldAdapter(context, mDatas));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if(mListener!=null){
                    mListener.onClick(position);
                }
            }
        });
        lightsOnOrOff(0.3f);
    }

    private OnWindowItemClickLisenter mListener;

    public interface OnWindowItemClickLisenter{
        void onClick(int position);
    }

    public void setOnItemClickListener(OnWindowItemClickLisenter listener) {
        mListener=listener;
    }

    private void cacluteWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = (int) (metrics.heightPixels * 0.7f);
    }

    private class FoldAdapter extends ArrayAdapter<FoldBean> {
        private LayoutInflater mInflater;

        public FoldAdapter(Context context, List<FoldBean> objects) {
            super(context, 0, objects);
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_popup_window, parent, false);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.tvName = (TextView) convertView.findViewById(tv_name);
                holder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv.setImageResource(R.mipmap.ic_launcher);
            FoldBean item = getItem(position);
            ImageViewLoader.getInstance().loadImage(item.getFirstNamePath(), holder.iv);
            holder.tvNum.setText(item.getName());
            holder.tvName.setText(item.getCount() + "");
            return convertView;
        }

        private class ViewHolder {
            ImageView iv;
            TextView tvName;
            TextView tvNum;
        }
    }

    public void lightsOnOrOff(float alpha){
        WindowManager.LayoutParams attributes = ((Activity) mContext).getWindow().getAttributes();
        attributes.alpha=alpha;
        ((Activity) mContext).getWindow().setAttributes(attributes);
    }


}
