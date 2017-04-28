package cy.com.allview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cy.com.allview.utils.ImageViewLoader;

/**
 * Created by Administrator
 * on 2017/4/10.
 * des:
 */

public class ImageAdapter extends BaseAdapter {
    private static List<String> mSelectImg=new ArrayList<String>();
    private String path;
    private List<String> mDatas;
    private LayoutInflater mInflater;

    public ImageAdapter(String path, List<String> list, Context context) {
        mInflater = LayoutInflater.from(context);
        this.path = path;
        this.mDatas = list;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_img, parent, false);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.image_button);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //重置状态
        holder.imageView.setImageResource(R.mipmap.morentouxiang);
        holder.imageView.setColorFilter(null);
        holder.imageButton.setImageResource(R.mipmap.bg_verify);
        final String filePath=path+"/"+mDatas.get(position);
        ImageViewLoader.getInstance().loadImage(filePath, holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectImg.contains(filePath)){//已经被选中，就移除
                   mSelectImg.remove(filePath);
                    holder.imageView.setColorFilter(null);
                    holder.imageButton.setImageResource(R.mipmap.bg_verify);
                }else {
                    holder.imageView.setColorFilter(Color.parseColor("#33000000"));
                    holder.imageButton.setImageResource(R.mipmap.bg_verify_press);
                    mSelectImg.add(filePath);
                }
            }
        });
        if(mSelectImg.contains(filePath)){
            holder.imageView.setColorFilter(Color.parseColor("#33000000"));
            holder.imageButton.setImageResource(R.mipmap.bg_verify_press);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        ImageButton imageButton;
    }
}