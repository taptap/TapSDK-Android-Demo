package com.tds.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tds.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewFragment extends Fragment {
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.close_button)
    ImageButton imageButton;


    private static WebViewFragment webViewFragment = null;

    public WebViewFragment() {
    }

    public static final WebViewFragment getInstance() {
        if (webViewFragment == null) {
            webViewFragment = new WebViewFragment();
        }
        return webViewFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_webview_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack("webViewFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://developer.taptap.com/docs/sdk/taptap-login/features/");
    }
}
