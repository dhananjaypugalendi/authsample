package dhananjay.authsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    private LoginButton facebookLoginButton;
    private TextView loginStatus;

    String loginStatusString="";

    CallbackManager facebookCallbackManager;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson=new Gson();

        facebookLoginButton = (LoginButton)findViewById(R.id.facebook_login_button);
        loginStatus= (TextView) findViewById(R.id.login_status);

        setupFacebookLogin();

    }

    private void setupFacebookLogin(){
        facebookCallbackManager=CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: "+gson.toJson(loginResult) );
                loginStatusString+=gson.toJson(loginResult);
                loginStatus.setText(loginStatusString);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.d(TAG, "onCompleted: "+response.toString());
                                loginStatusString+="\n\n"+response.toString();
                                loginStatus.setText(loginStatusString);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender,hometown,location,relationship_status");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "onError: "+exception);
                loginStatus.setText(exception.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
