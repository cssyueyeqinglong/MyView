package cy.com.allview.act;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cy.com.allview.R;

/**
 * Created by Administrator on 2017/4/28.
 */

public class LocationFilter extends AppCompatActivity {

    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationfilter);
        mTv = (TextView) findViewById(R.id.tv_01);

    }
}
