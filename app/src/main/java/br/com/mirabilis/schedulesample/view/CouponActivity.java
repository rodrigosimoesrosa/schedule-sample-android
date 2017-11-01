package br.com.mirabilis.schedulesample.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import br.com.mirabilis.schedulesample.R;

/**
 * Created by ticketservices on 7/6/17.
 */

public class CouponActivity extends AppCompatActivity {

    public static final String COUPONS = "COUPONS";
    TextView lblCouponQuantity;
    private int coupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.mirabilis.schedulesample.R.layout.activity_coupon);
        coupons = getIntent().getIntExtra(COUPONS, 0);

        lblCouponQuantity = (TextView) findViewById(R.id.lblCouponQuantity);
        lblCouponQuantity.setText("Existem " + coupons + " cupons perto de você!");
    }
}
