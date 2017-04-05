package cy.cy.com.allview.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import cy.cy.com.allview.R;

/**
 * Created by Administrator
 * on 2017/3/30.
 * des:密码框的控件
 */

public class PwdView extends FrameLayout {

    private EditText mEt;
    private TextView mTv01, mTv02, mTv03, mTv04;
    private TextView[] mTvs;
    private StringBuffer mBuffer = new StringBuffer();
    private String etContent;
    private int count = 4;

    public PwdView(Context context) {
        super(context);
        init();
    }

    public PwdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PwdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_pwd, this);
        mEt = (EditText) rootView.findViewById(R.id.et);
        mTv01 = (TextView) rootView.findViewById(R.id.tv_01);
        mTv02 = (TextView) rootView.findViewById(R.id.tv_02);
        mTv03 = (TextView) rootView.findViewById(R.id.tv_03);
        mTv04 = (TextView) rootView.findViewById(R.id.tv_04);
        mTvs = new TextView[4];
        mTvs[0] = mTv01;
        mTvs[1] = mTv02;
        mTvs[2] = mTv03;
        mTvs[3] = mTv04;
        mEt.setCursorVisible(false);
        setLisenter();
    }

    public void clearContent() {
        mBuffer.delete(0, mBuffer.length());
        etContent = mBuffer.toString();
        for (int i = 0; i < 4; i++) {
            mTvs[i].setText("");
            mTvs[i].setBackgroundResource(R.mipmap.bg_verify);
        }
    }

    private void setLisenter() {

        mEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //重点，如果字符不为空的时候才可以进行造作
                if (!"".equals(s.toString())) {//文本内容大于3时就置空输入内容
                    if (mBuffer.length() > 3) {
                        mEt.setText("");
                        return;
                    } else {
                        //将文字添加到容器中
                        mBuffer.append(s);
                        mEt.setText("");//添加后置空文本框内容
                        count = mBuffer.length();
                        etContent = mBuffer.toString();
                        if (mBuffer.length() == 4) {
                            if (mInputCompleteLisenter != null) {
                                mInputCompleteLisenter.inputComplete();
                            }
                        }
                    }
                    for (int i = 0; i < mBuffer.length(); i++) {
                        mTvs[i].setText("" + etContent.charAt(i));
                        mTvs[i].setBackgroundResource(R.mipmap.bg_verify_press);
                    }
                }


            }
        });

        mEt.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (onKeyDel()) {
                        return true;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public boolean onKeyDel() {
        if (count == 0) {
            count = 4;
            return true;
        }
        if (mBuffer.length() > 0) {
            mBuffer.delete(count - 1, count);
            count--;
            etContent = mBuffer.toString();
            mTvs[mBuffer.length()].setText("");
            mTvs[mBuffer.length()].setBackgroundResource(R.mipmap.bg_verify);
            if (mInputCompleteLisenter != null) {
                mInputCompleteLisenter.deleteContent(true);
            }
        }
        return false;
    }

    private InputCompleteLisenter mInputCompleteLisenter;

    public void setInputCompleteLisenter(InputCompleteLisenter mInputCompleteLisenter) {
        this.mInputCompleteLisenter = mInputCompleteLisenter;
    }

    public interface InputCompleteLisenter {
        void inputComplete();

        void deleteContent(boolean isDelete);
    }

    public String getEditContent() {
        return etContent;
    }

}
