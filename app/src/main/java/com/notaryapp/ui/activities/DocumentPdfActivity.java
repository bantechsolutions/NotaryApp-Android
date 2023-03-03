package com.notaryapp.ui.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.R;
import com.notaryapp.utils.AppUrl;

public class DocumentPdfActivity extends AppCompatActivity {

    private int i = 0;
    private Button btnClose;
    private WebView pdfView;
    private ProgressBar progress;
    private String removePdfTopIcon = "javascript:(function() {" + "document.querySelector('[role=\"toolbar\"]').remove();})()";

    private String docName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_document_pdf);
        btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        docName = getIntent().getStringExtra("stampName");
        if(docName == null){
            docName = "";
        }

        String strArray = AppUrl.VERIFY_AUTHENTICATE_URL + docName + ".pdf" ;

        pdfView = findViewById(R.id.pdfView);
        progress = findViewById(R.id.progress);


        showPdfFile(strArray);

    }

    private void showPdfFile(final String imageString) {
        showProgress();
        pdfView.invalidate();
        pdfView.getSettings().setJavaScriptEnabled(true);
        pdfView.getSettings().setSupportZoom(true);
       // pdfView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + imageString);

        pdfView.loadUrl(AppUrl.VIEW_PDF + Uri.encode(imageString));//URLEncoder.encode(imageString));

        pdfView.setWebViewClient(new WebViewClient() {
            boolean checkOnPageStartedCalled = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkOnPageStartedCalled = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkOnPageStartedCalled) {
                    pdfView.loadUrl(removePdfTopIcon);
                    hideProgress();
                } else {
                    showPdfFile(imageString);
                }
            }
        });
    }

    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

}
