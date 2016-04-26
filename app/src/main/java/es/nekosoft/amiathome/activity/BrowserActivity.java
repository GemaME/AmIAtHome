package es.nekosoft.amiathome.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import es.nekosoft.amiathome.R;
import es.nekosoft.amiathome.utils.Constants;


public class BrowserActivity extends AppCompatActivity {

    private ProgressBar pbBrowser;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        webView = (WebView) findViewById(R.id.web);
        pbBrowser = (ProgressBar) findViewById(R.id.pb_browser);

        setupToolbar();
        showWeb();
    }

    private void showWeb(){

        //Get URL
        String url = getIntent().getStringExtra(Constants.BROW_URL);

        //Config browser
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                pbBrowser.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        //URL here we go!!!
        webView.loadUrl(url);
    }

    private void setupToolbar(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
