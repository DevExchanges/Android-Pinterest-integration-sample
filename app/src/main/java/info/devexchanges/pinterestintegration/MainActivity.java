package info.devexchanges.pinterestintegration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnPin;
    private TextView userName;
    private ImageView image;
    private TextView totalPins;
    private TextView firstName;
    private TextView lastName;
    private TextView totalFollowers;
    private LinearLayout infoLayout;
    private PDKClient pdkClient;
    private final static String APP_ID = "4819615540182009660";
    private String TAG = MainActivity.class.getSimpleName();

    private void findViews() {
        btnPin = (Button) findViewById(R.id.btn_pin);
        userName = (TextView) findViewById(R.id.user_name);
        image = (ImageView) findViewById(R.id.image);
        firstName = (TextView) findViewById(R.id.first_name);
        lastName = (TextView) findViewById(R.id.last_name);
        totalPins = (TextView)findViewById(R.id.total_pins);
        totalFollowers = (TextView)findViewById(R.id.total_flw);
        infoLayout = (LinearLayout)findViewById(R.id.layout_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        pdkClient = PDKClient.configureInstance(this, APP_ID);
        pdkClient.onConnect(this);

        btnPin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<String> scopes = new ArrayList<>();
                scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
                scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
                scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
                scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
                scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
                scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);

                PDKClient.getInstance().login(MainActivity.this, scopes, new PDKCallback() {

                    @Override
                    public void onSuccess(PDKResponse response) {
                        Log.d(TAG, response.getData().toString());
                        setDataToView(response);
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.e(TAG, "on failed: " + exception.getDetailMessage());
                    }
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(PDKResponse response) {
        infoLayout.setVisibility(View.VISIBLE);
        Log.i(TAG, "set data to view");
        Log.d(TAG, "followers: " + response.getUser().getFollowersCount());

        //get user information from Pinterest SDK response
        PDKUser user = response.getUser();
        userName.setText(user.getUsername());
        firstName.setText(user.getFirstName()); //show user first name
        lastName.setText(user.getLastName());  //show user last name
        Picasso.with(MainActivity.this).load(user.getImageUrl()).into(image); //loading avatar by Picasso

        //total pins
        totalPins.setText("" + response.getPinList().size());
        totalFollowers.setText("" + response.getUser().getFollowersCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pdkClient.onOauthResponse(requestCode, resultCode, data);
    }
}
