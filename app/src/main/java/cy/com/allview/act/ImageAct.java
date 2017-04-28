package cy.com.allview.act;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cy.com.allview.ImageAdapter;
import cy.com.allview.R;
import cy.com.allview.bean.FoldBean;
import cy.com.allview.view.FoldPopWindow;

/**
 * Created by Administrator
 * on 2017/4/10.
 * des:
 */

public class ImageAct extends AppCompatActivity {
    private GridView mGridView;
    private RelativeLayout mRlLayout;
    private TextView mDirName;
    private TextView mDirCount;
    private ProgressDialog mProgressDialog;
    private List<FoldBean> mDirs;
    private int mCurrentCount;
    private File mCurrentFile;
    private List<String> mFiles;
    private ImageAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                mProgressDialog.dismiss();
                data2View();
            }
        }
    };

    private void data2View() {
        if (mCurrentFile == null) {
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mFiles = Arrays.asList(mCurrentFile.list());
        mAdapter = new ImageAdapter(mCurrentFile.getAbsolutePath(), mFiles, this);
        mGridView.setAdapter(mAdapter);
        mDirCount.setText(mCurrentCount + "");
        mDirName.setText(mCurrentFile.getName());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_img);
        findViews();
        initDatas();
    }

    private void initDatas() {
        mDirs = new ArrayList<FoldBean>();
        //扫描图片
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "请等待");
        new Thread() {
            @Override
            public void run() {
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = ImageAct.this.getContentResolver();
                Cursor cursor = resolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE
                                + "= ? or " + MediaStore.Images.Media.MIME_TYPE
                                + "= ? or " + MediaStore.Images.Media.MIME_TYPE
                                + "= ? ", new String[]{"image/jpeg", "image/jpg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> parentPaths = new HashSet<String>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();//拿到图片的父目录
                    if (parentFile == null) continue;
                    String dirPath = parentFile.getAbsolutePath();

                    if (parentPaths.contains(dirPath)) {
                        continue;
                    } else {
                        parentPaths.add(dirPath);
                        FoldBean bean = new FoldBean();
                        bean.setDir(dirPath);
                        bean.setFirstNamePath(path);
                        if (parentFile.list() == null) {
                            continue;
                        }
                        int picSize = parentFile.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                if (name.endsWith("jpeg") || name.endsWith("png") || name.endsWith("jpg")) {
                                    return true;
                                }
                                return false;
                            }
                        }).length;
                        bean.setCount(picSize);
                        mDirs.add(bean);
                        if (picSize > mCurrentCount) {
                            mCurrentCount = picSize;
                            mCurrentFile = parentFile;
                        }
                    }

                }
                cursor.close();
                mHandler.sendEmptyMessage(0x110);
            }
        }.start();

    }

    private void findViews() {
        mGridView = (GridView) findViewById(R.id.gv);
        mRlLayout = (RelativeLayout) findViewById(R.id.rl_bottom);
        mDirCount = (TextView) findViewById(R.id.tv_dir_num);
        mDirName = (TextView) findViewById(R.id.tv_dir_name);

        mRlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoldPopWindow popWindow = new FoldPopWindow(ImageAct.this, mDirs);
                popWindow.setAnimationStyle(R.style.pop_anim);
                popWindow.setOnItemClickListener(new FoldPopWindow.OnWindowItemClickLisenter() {
                    @Override
                    public void onClick(int position) {
                        Logger.d("itemclick");
                        FoldBean bean = mDirs.get(position);
                        mCurrentFile = new File(bean.getDir());
                        mCurrentCount = bean.getCount();
                        data2View();
                    }
                });
                popWindow.showAsDropDown(mRlLayout, 0, 0);
            }
        });
    }

}
