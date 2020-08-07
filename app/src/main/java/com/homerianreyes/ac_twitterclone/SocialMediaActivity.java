package com.homerianreyes.ac_twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SocialMediaActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        setTitle("Twitter Clone");
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);


        FancyToast.makeText(this, "Welcome " + ParseUser.getCurrentUser().getUsername(), FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();

        swipeRefreshLayoutContainer = findViewById(R.id.swipeRefreshLayoutContainer);
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(SocialMediaActivity.this, android.R.layout.simple_list_item_checked, arrayList);
        //check and unchecked feature in listView
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);

        final ProgressDialog progressDialog = new ProgressDialog(SocialMediaActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        //try catch cuz there might be no users and the app will crush
        try {
            //get users in parse server
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            //condition: get all username except current user
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            //find data
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {

                    if (e == null){

                        if (users.size() > 0) {

                            for(ParseUser user : users){
                                arrayList.add(user.getUsername());
                            }
                            listView.setAdapter(arrayAdapter);

                            //checking users who followed the current user
                            for (String twitterUsers : arrayList) {
                                //if current user following other users
                                if(ParseUser.getCurrentUser().getList("followOf") != null){
                                    
                                    if (ParseUser.getCurrentUser().getList("followOf").contains(twitterUsers)) {
                                        listView.setItemChecked(arrayList.indexOf(twitterUsers), true);
                                    }
                                }
                            }//for loop
                        }
                    }
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayoutContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {

                    ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereNotContainedIn("username", arrayList);

                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> users, ParseException e) {

                            if (users.size() > 0) {

                                if (e == null) {

                                    for (ParseUser user : users){
                                        arrayList.add(user.getUsername());
                                    }
                                    arrayAdapter.notifyDataSetChanged();

                                    //checking users who followed the current user
                                    for (String twitterUsers: arrayList) {
                                        //if current user following other users
                                        if (ParseUser.getCurrentUser().getList("followOf") != null) {

                                            if (ParseUser.getCurrentUser().getList("followOf").contains(twitterUsers)){
                                                listView.setItemChecked(arrayList.indexOf(twitterUsers), true);
                                            }
                                        }
                                    }//for loop
                                    if (swipeRefreshLayoutContainer.isRefreshing()) {
                                        swipeRefreshLayoutContainer.setRefreshing(false);
                                        }
                                }
                            } else {
                                if (swipeRefreshLayoutContainer.isRefreshing()){
                                    swipeRefreshLayoutContainer.setRefreshing(false);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.goToTweetItem){
            transitionToSendTweetActivity();

        } else if (item.getItemId() == R.id.logoutUserItem) {

            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    //logging out
                    if (e == null){
                        transitionToLoginActivity();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void transitionToSendTweetActivity() {
        Intent intent = new Intent(SocialMediaActivity.this, SendTweetActivity.class);
        startActivity(intent);
    }

    private void transitionToLoginActivity(){
        Intent intent = new Intent(SocialMediaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CheckedTextView checkedTextView = (CheckedTextView) view;

        if (checkedTextView.isChecked()){
            FancyToast.makeText(SocialMediaActivity.this, "Your now following " + arrayList.get(position), FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();

            //adding the followed Users
            ParseUser.getCurrentUser().add("followOf",arrayList.get(position));

        } else {
            FancyToast.makeText(SocialMediaActivity.this, "Unfollowed " + arrayList.get(position), FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();

            //unfollow users
            ParseUser.getCurrentUser().getList("followOf").remove(arrayList.get(position));
            //get current followed users
            List currentUserFollowOfList = ParseUser.getCurrentUser().getList("followOf");
            //remove the old list of followed users
            ParseUser.getCurrentUser().remove("followOf");
            //add the updated list of followed users
            ParseUser.getCurrentUser().put("followOf", currentUserFollowOfList);
        }
        //save the followed users in server
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null){
                    FancyToast.makeText(SocialMediaActivity.this, "Saved!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                }
            }
        });
    }

}