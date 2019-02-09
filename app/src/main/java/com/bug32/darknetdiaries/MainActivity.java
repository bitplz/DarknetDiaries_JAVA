package com.bug32.darknetdiaries;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.RangeValueIterator;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements itemAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    AdView adView;
    private RecyclerView recyclerView;
    private RequestQueue mQueue;
    private ArrayList<Item> itemList = new ArrayList<>() , temp = new ArrayList<>();
    private itemAdapter mitemAdapter;
    private RelativeLayout relativeLayout;
    private String url = "https://feeds.megaphone.fm/darknetdiaries.json", currentVersion;
    private String title = null, desc, imgUrl, audioUrl = null, pubDate, duration;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-6149822515447474~5034601383");
        adView = (AdView) findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);



        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mitemAdapter = new itemAdapter(this, itemList);




        recyclerView.setAdapter(mitemAdapter);
        mQueue = Volley.newRequestQueue(this);

//        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/VT323-Regular.otf");
//
//        SpannableString spannableString = new SpannableString("DARKNET DIARIES");
//        spannableString.setSpan(typeface,0,spannableString.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        getSupportActionBar().setTitle(spannableString);
        getSupportActionBar().setSubtitle("by Jack Rhysider");

        checkInternet();

        if (isNetworkAvailable()) {

            updateChecker();
            parseJson();

        }

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-6149822515447474/5321056743");
        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice("58EDBE2C76A144DDA16B93357D134589").build());

    }


////////////////////////UPDATE CHECKER FUNCTIONS/////////////////////////////

    public void updateChecker(){

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new GetVersionCode().execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName()  + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    Toast.makeText(MainActivity.this, "Update Available!", Toast.LENGTH_LONG).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage("New Version of Darknet Diaries has been released.\nPlease download and install it.");
                    builder.setTitle("INFO");
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

                            }catch (Exception e){
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

                            }

                            updateChecker();
                        }
                    });
                    builder.show();
                }

            }

        }
    }

/////////////////////////////END-UPDATE CHECKER/////////////////////////////




    private void parseJson() {

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject jjj = response.getJSONObject("channel");

                            JSONArray js = jjj.getJSONArray("items");
                            for (int i = 0; i < js.length(); i++) {

                                JSONObject itemObj = js.getJSONObject(i);

                                Iterator<?> keys = itemObj.keys();

                                while (keys.hasNext()) {

                                    String key = (String) keys.next();

                                    if (itemObj.get(key) instanceof JSONObject) {

                                        JSONObject j = itemObj.getJSONObject(key);

                                        title = j.getString("title");
                                        desc = j.getString("summary");
                                        imgUrl = "https://darknetdiaries.com/imgs/darknet-diaries-sq.png";
                                        audioUrl = j.getString("enclosure");
                                        duration = j.getString("duration");
                                        pubDate = j.getString("pubDate");

                                        int d = Integer.parseInt(duration);
                                        d = d / 60;
                                        duration = String.valueOf(d) + " mins";

                                        itemList.add(new Item(title, desc, imgUrl, audioUrl, pubDate, duration));
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Collections.reverse(itemList);
                        temp = itemList;
                        mitemAdapter = new itemAdapter(MainActivity.this, itemList);
                        recyclerView.setAdapter(mitemAdapter);
                        mitemAdapter.setOnClickItemListener(MainActivity.this);
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();

        ArrayList<Item> newList = new ArrayList<>();

        for (Item item : temp){
            String title = item.getmTitle().toLowerCase();
            if (title.contains(newText)){
                newList.add(item);
            }
        }

        mitemAdapter.setFilter(newList);

        itemList = newList;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.about :
                Intent intent = new Intent(MainActivity.this, about.class);
                startActivity(intent);
                break;

            case R.id.share :
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_SUBJECT,"Darknet Diaries");
                intent1.putExtra(Intent.EXTRA_TEXT,"Hey, I found this amazing app called Darknet Diaries." +
                        "Listen to true stories from dark side of internet.\n" +
                        "Download App:\n" +
                        "https://play.google.com/store/apps/details?id=com.bug32.darknetdiaries");
                startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final int position) {

        final  Item onClick = itemList.get(position);

        if (interstitialAd.isLoaded()){
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    Intent intent = new Intent(MainActivity.this, playEpisode.class);
                    intent.putExtra("title", onClick.getmTitle());
                    intent.putExtra("description", onClick.getmDesc());
                    intent.putExtra("audioUrl", onClick.getmAudioUrl());
                    intent.putExtra("imgUrl", onClick.getmImgUrl());
                    intent.putExtra("episode", "" + (position + 1));
                    startActivity(intent);

                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });

        }else {

            Toast.makeText(this, ""+ onClick.getmTitle(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, playEpisode.class);
            intent.putExtra("title", onClick.getmTitle());
            intent.putExtra("description", onClick.getmDesc());
            intent.putExtra("audioUrl", onClick.getmAudioUrl());
            intent.putExtra("imgUrl", onClick.getmImgUrl());
            intent.putExtra("episode", "" + (position + 1));
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want exit ?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        builder.show();
    }

    public void checkInternet() {
        if (!isNetworkAvailable()) {
            recyclerView.setVisibility(View.GONE);
            Snackbar.make(relativeLayout, "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isNetworkAvailable()) {
                                recyclerView.setVisibility(View.VISIBLE);
                                parseJson();
                            } else {
                                checkInternet();
                            }
                        }
                    }).setActionTextColor(Color.parseColor("#009900")).show();
        }
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }



}




