package com.locol.locol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.locol.locol.R;
import com.locol.locol.helpers.Preferences;
import com.locol.locol.pojo.Account;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends ActionBarActivity {
    private String TAG = "LoginActivity";
    public static final String PREF_FILE_NAME = "user_info";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_AVATAR = "avatar";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO should move `rsvp_event`, `rsvp_event` and `user_events` permission to other activity (when user actually need them)
        List<String> permissions = Arrays.asList("public_profile", "email", "user_friends", "user_events");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Toast.makeText(LoginActivity.this, "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_SHORT).show();
                } else if (user.isNew()) {
                    Toast.makeText(LoginActivity.this, "User signed up and logged in through Facebook!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "User logged in through Facebook!", Toast.LENGTH_SHORT).show();
                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                Account.setUserName(object.getString(KEY_USER_NAME));
                                Account.setUserFBId(object.getString(KEY_USER_ID));
                                Account.setUserEmail(object.getString(KEY_USER_EMAIL));
                                Account.setUserAvatarUrl("https://graph.facebook.com/" + Account.getUserFBId() + "/picture?type=normal");

                                Preferences.saveToPreferences(LoginActivity.this, PREF_FILE_NAME, KEY_USER_ID, Account.getUserFBId());
                                Preferences.saveToPreferences(LoginActivity.this, PREF_FILE_NAME, KEY_USER_EMAIL, Account.getUserEmail());
                                Preferences.saveToPreferences(LoginActivity.this, PREF_FILE_NAME, KEY_USER_NAME, Account.getUserName());
                                Preferences.saveToPreferences(LoginActivity.this, PREF_FILE_NAME, KEY_USER_AVATAR, Account.getUserAvatarUrl());

                                final ParseUser user = ParseUser.getCurrentUser();
                                user.put("avatar_url", Account.getUserAvatarUrl());
                                user.put("fbId", Account.getUserFBId());
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        GraphRequestBatch batch = new GraphRequestBatch(
                                                GraphRequest.newMyFriendsRequest(
                                                        AccessToken.getCurrentAccessToken(),
                                                        new GraphRequest.GraphJSONArrayCallback() {
                                                            @Override
                                                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                                                // Application code for users friends
                                                                final List<String> friendsList = new ArrayList<String>();
                                                                for (int i = 0; i < jsonArray.length(); ++i) {
                                                                    try {
                                                                        friendsList.add(jsonArray.getJSONObject(i).getString("id"));
                                                                    } catch (JSONException e1) {
                                                                        e1.printStackTrace();
                                                                    }
                                                                }

                                                                // TODO REMOVE this on production
                                                                friendsList.add(Account.getUserFBId());

                                                                ParsePush.subscribeInBackground("fb" + Account.getUserFBId());
                                                                ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
                                                                currentInstall.put("user", ParseUser.getCurrentUser());
                                                                currentInstall.saveInBackground();

                                                                user.put("friendIds", friendsList);
                                                                user.saveInBackground(new SaveCallback() {
                                                                      @Override
                                                                      public void done
                                                                              (ParseException e) {
                                                                          // Construct a ParseUser query that will find friends whose
                                                                          // facebook IDs are contained in the current user's friend list.
                                                                          ParseQuery friendQuery = ParseUser.getQuery();
                                                                          friendQuery.whereContainedIn("fbId", friendsList);
                                                                          friendQuery.findInBackground(new FindCallback<ParseUser>() {
                                                                              public void done(List<ParseUser> objects, ParseException e) {
                                                                                  if (e == null) {
                                                                                      for (ParseUser object : objects) {
//                                                                                    ParseUser object = objects.get(0);
                                                                                          HashMap<String, Object> params = new HashMap<>();
                                                                                          params.put("recipientId", "fb" + object.get("fbId"));
                                                                                          params.put("message", Account.getUserName() + " has using Locol!");
                                                                                          ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
                                                                                              @Override
                                                                                              public void done(String s, ParseException e) {
                                                                                                  // Push sent successfully
//                                                                                                ParsePush.subscribeInBackground("fb" + Account.getUserFBId(), new SaveCallback() {
//                                                                                                    @Override
//                                                                                                    public void done(ParseException e) {
//                                                                                                        if (e == null) {
//                                                                                                            startActivity(new Intent(LoginActivity.this, ChooseCategoryActivity.class));
//                                                                                                            finish();
//                                                                                                        } else {
//                                                                                                            e.printStackTrace();
//                                                                                                        }
//                                                                                                    }
//                                                                                                });
                                                                                                  startActivity(new Intent(LoginActivity.this, CategoryViewActivity.class));

                                                                                              }
                                                                                          });
//                                                                                        ParsePush push = new ParsePush();
//                                                                                        push.setChannel("fb" + object.get("fbId"));
//                                                                                        push.setMessage(Account.getUserName() + " has using Locol!");
//                                                                                        push.sendInBackground();
                                                                                      }
//                                                                                    startActivity(new Intent(LoginActivity.this, ChooseCategoryActivity.class));
//                                                                                    finish();

                                                                                  } else {
                                                                                      e.printStackTrace();
                                                                                  }
                                                                              }
                                                                          });
                                                                      }
                                                                  }

                                                                );

                                                            }
                                                        })

                                        );
                                        batch.addCallback(new GraphRequestBatch.Callback() {
                                            @Override
                                            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                                                // Application code for when the batch finishes
                                            }
                                        });
                                        batch.executeAsync();

                                        Bundle parameters = new Bundle();
                                        parameters.putString("fields", "id,name,link,picture");

                                    }
                                });

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
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
