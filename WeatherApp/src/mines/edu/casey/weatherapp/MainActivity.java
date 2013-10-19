/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mines.edu.casey.weatherapp;

import edu.mines.rbower.internetaccessdemo.DownloadDocumentTask;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class MainActivity extends FragmentActivity 
        implements ZipCode_Fragment.OnHeadlineSelectedListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            ZipCode_Fragment firstFragment = new ZipCode_Fragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }
    public void getXML( View view )
    {
      Log.d( "INTERNET ACCESS DEMO", "getXML()..." );

      // Make sure there is an available network connection before starting the AsynchTask.
      ConnectivityManager connMgr = (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      if( networkInfo != null && networkInfo.isConnected() )
      {
        new DownloadDocumentTask( this ).execute( "http://www.ecb.int/stats/eurofxref/eurofxref-daily.xml" );
      }
      else
      {
        this.textView.setText( "No network connection available." );
      }
    }
    public void onArticleSelected(int position) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
        Zip_Weather_Fragment articleFrag = (Zip_Weather_Fragment)
                getSupportFragmentManager().findFragmentById(R.id.article);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.updateArticleView(position);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            Zip_Weather_Fragment newFragment = new Zip_Weather_Fragment();
            Bundle args = new Bundle();
            args.putInt(Zip_Weather_Fragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
}