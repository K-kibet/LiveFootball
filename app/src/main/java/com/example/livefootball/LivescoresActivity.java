package com.example.livefootball;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class LivescoresActivity extends AppCompatActivity {
    InterstitialAd mInterstitialAd;
    private AdView adView;
    private FrameLayout adViewContainer;
    private static  final String AD_UNIT_ID = "ca-app-pub-2583214494360788/4395978728";
    WebView webView;
    private String html = "https://www.scorebat.com/embed/livescore/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livescores);

        webView = findViewById(R.id.webView);
        adViewContainer = findViewById(R.id.adViewContainer);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(new Browser());
        webView.loadUrl(html);

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"cca-app-pub-2583214494360788/7163696579", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
        adViewContainer.post(new Runnable() {
            @Override
            public void run() {
                LoadBanner();
            }
        });
    }
    private class Browser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    private void LoadBanner() {
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        adViewContainer.removeAllViews();
        adViewContainer.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(LivescoresActivity.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                    if(webView.canGoBack()) {
                        webView.goBack();
                    } else finish();

                }
            });
        } else {
            if(webView.canGoBack()) {
                webView.goBack();
            } else super.onBackPressed();
        }
    }

}