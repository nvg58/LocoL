package com.locol.locol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.locol.locol.pojo.Account;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class WalkthroughActivity extends ActionBarActivity {

    public static final String PREF_FILE_NAME = "user_info";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_AVATAR = "avatar";

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_walkthrough);

        callbackManager = CallbackManager.Factory.create();

        if (AccessToken.getCurrentAccessToken() != null) {
            Account.setUserFBId(Preferences.readFromPreferences(WalkthroughActivity.this, PREF_FILE_NAME, KEY_USER_ID, null));
            Account.setUserEmail(Preferences.readFromPreferences(WalkthroughActivity.this, PREF_FILE_NAME, KEY_USER_EMAIL, null));
            Account.setUserName(Preferences.readFromPreferences(WalkthroughActivity.this, PREF_FILE_NAME, KEY_USER_NAME, null));
            Account.updateUserDataToParse();

            startActivity(new Intent(WalkthroughActivity.this, MyNavigationDrawer.class));
            finish();
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email", "user_friends", "user_events");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Account.setUserName(object.getString(KEY_USER_NAME));
                                    Account.setUserFBId(object.getString(KEY_USER_ID));
                                    Account.setUserEmail(object.getString(KEY_USER_EMAIL));
                                    Account.updateUserDataToParse();

                                    Preferences.saveToPreferences(WalkthroughActivity.this, PREF_FILE_NAME, KEY_USER_ID, Account.getUserFBId());
                                    Preferences.saveToPreferences(WalkthroughActivity.this, PREF_FILE_NAME, KEY_USER_EMAIL, Account.getUserEmail());
                                    Preferences.saveToPreferences(WalkthroughActivity.this, PREF_FILE_NAME, KEY_USER_NAME, Account.getUserName());

                                    startActivity(new Intent(WalkthroughActivity.this, MyNavigationDrawer.class));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, link, email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(WalkthroughActivity.this, "You have cancelled Facebook Login!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(WalkthroughActivity.this, "Error during Login by Facebook!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}