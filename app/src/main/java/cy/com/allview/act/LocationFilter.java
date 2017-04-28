package cy.com.allview.act;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import cy.com.allview.R;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static cy.com.allview.R.id.tv_name;

/**
 * Created by Administrator on 2017/4/28.
 */

public class LocationFilter extends AppCompatActivity {

    private ExpandableStickyListHeadersListView mLv;
    private List<String> datas;
    WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationfilter);
        mLv = (ExpandableStickyListHeadersListView) findViewById(R.id.list);

        datas = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            char head = (char) ('a' + i);
            for (int j = 0; j <=i; j++) {
                datas.add(head + "数据来咯");
            }
        }
        mLv.setAnimExecutor(new AnimationExecutor());
        mLv.setAdapter(new MyAdapter(this));
        mLv.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if(mLv.isHeaderCollapsed(headerId)){
                    mLv.expand(headerId);
                }else {
                    mLv.collapse(headerId);
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeadViewHolder holder;
            if (convertView == null) {
                holder = new HeadViewHolder();
                convertView = inflater.inflate(R.layout.item_head, parent, false);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_head);
                convertView.setTag(holder);
            } else {
                holder = (HeadViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            String headerText = "" + datas.get(position).subSequence(0, 1).charAt(0);
            holder.tvName.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return datas.get(position).subSequence(0, 1).charAt(0);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_popup_window, parent, false);
                holder.tvName = (TextView) convertView.findViewById(tv_name);
                holder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(datas.get(position));
            holder.tvNum.setText("" + position);
            return convertView;
        }

        private class ViewHolder {
            ImageView iv;
            TextView tvName;
            TextView tvNum;
        }

        private class HeadViewHolder {
            TextView tvName;
        }
    }

    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if(ExpandableStickyListHeadersListView.ANIMATION_EXPAND==animType&&target.getVisibility()==View.VISIBLE){
                return;
            }
            if(ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE==animType&&target.getVisibility()!=View.VISIBLE){
                return;
            }
            if(mOriginalViewHeightPool.get(target)==null){
                mOriginalViewHeightPool.put(target,target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }
}
