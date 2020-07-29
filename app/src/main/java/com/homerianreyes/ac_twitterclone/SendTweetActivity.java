package com.homerianreyes.ac_twitterclone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SendTweetActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTweet;
    private ListView listViewOtherTweets;
    private Button btnViewOtherTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);

        getSupportActionBar().setTitle("Twitter Clone");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtTweet = findViewById(R.id.edtTweet);
        listViewOtherTweets = findViewById(R.id.listViewOtherTweets);

        btnViewOtherTweets = findViewById(R.id.btnViewOtherTweets);
        btnViewOtherTweets.setOnClickListener(this);

    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_tweet_menu, menu);
        //menu.findItem(R.id.sendTweetItem).setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.sendTweetItem) {

            if (!edtTweet.getText().toString().equals("")) {

                ParseObject parseObject = new ParseObject("MyTweet");
                parseObject.put("tweet", edtTweet.getText().toString());
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());

                final ProgressDialog progressDialog = new ProgressDialog(SendTweetActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null){
                            FancyToast.makeText(SendTweetActivity.this, "Tweet Success!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        } else {
                            FancyToast.makeText(SendTweetActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                        progressDialog.dismiss();
                        edtTweet.getText().clear();
                    }
                });
            } else {
                FancyToast.makeText(SendTweetActivity.this, "No tweet", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void rootLayoutTapped(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }//rootLayoutTapped

    @Override
    public void onClick(View view) {

        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        final SimpleAdapter simpleAdapter = new SimpleAdapter(SendTweetActivity.this,
                                                                tweetList,
                                                                android.R.layout.simple_list_item_2,
                                                                new String[] {"tweetUsername", "tweetValue"},
                                                                new int[] {android.R.id.text1, android.R.id.text2});

        final ProgressDialog progressDialog = new ProgressDialog(SendTweetActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        //try block if users has or doesn't have tweets yet
        try {

            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("MyTweet");
            parseQuery.whereContainedIn("username", ParseUser.getCurrentUser().getList("followOf"));
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> followedUsers, ParseException e) {

                    if (followedUsers.size() > 0 && e == null){

                        for (ParseObject followedUser : followedUsers){

                            HashMap<String, String> userTweet = new HashMap<>();
                            userTweet.put("tweetUsername", followedUser.getString("username"));
                            userTweet.put("tweetValue", followedUser.getString("tweet"));
                            tweetList.add(userTweet);

                        }
                        listViewOtherTweets.setAdapter(simpleAdapter);
                        progressDialog.dismiss();
                    }
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}