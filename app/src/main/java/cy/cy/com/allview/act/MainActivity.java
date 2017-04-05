package cy.cy.com.allview.act;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cy.cy.com.allview.bean.MyUser;
import cy.cy.com.allview.R;
import cy.cy.com.allview.view.PwdView;

public class MainActivity extends AppCompatActivity implements PwdView.InputCompleteLisenter {

    private PwdView mPv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPv = (PwdView) findViewById(R.id.pwd);
        mPv.setInputCompleteLisenter(this);
        MyUser.mUserId = 2;
        Log.d("log", "MainActivity.useId====" + MyUser.mUserId);
        startActivity(new Intent(this, SecondActivity.class));

    }

    @Override
    public void inputComplete() {
        String content = mPv.getEditContent();
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteContent(boolean isDelete) {

    }
}
