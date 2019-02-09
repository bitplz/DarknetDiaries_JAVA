package com.bug32.darknetdiaries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class about extends AppCompatActivity {

    private TextView aboutText, developerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setSubtitle("About");
        aboutText = (TextView) findViewById(R.id.about);
        developerText = (TextView) findViewById(R.id.developerContact);

        aboutText.setText(Html.fromHtml("<p>Darknet Diaries is a podcast covering true stories from the dark side of the Internet. Stories about hackers, defenders, threats, malware, botnets, breaches, and privacy.</p>\n" +
                "\n" +
                "<center>\n" +
                "</center>\n" +
                "\n" +
                "<p>The show is produced by Jack Rhysider, a veteran to the security world. He gained his professional knowledge of security by working in a Security Operations Center, a place to where threats are detected and stopped.</p>\n" +
                "\n" +
                "<p>Jack runs a blog which can be found at <a href=\"https://tunnelsup.com\">TunnelsUp.com</a>. He has appeared on numerous other podcasts.</p>\n" + "<p>Contact: <br />\n" +
                " <i> twitter : </i>darknetdiaries</p>\n" + "<i>email :</i> jack@darknetdiaries.com" +
                "\n"));

        developerText.setText(Html.fromHtml("<p> App Developer Contact:</p>\n"+"<i>email : </i> rchauhan.439@gmail.com<br>"
        + "<i>instagram : </i> @goodguyrahul"));
    }
}
