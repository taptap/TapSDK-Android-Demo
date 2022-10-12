package com.tds.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.moment.TapMoment;
import com.tds.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022-10-11
 * Describe：内嵌动态
 */
public class InLineDynamicFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.close_button)
    ImageButton imageButton;
    @BindView(R.id.show_dynamic_page)
    Button show_dynamic_page;


    private static InLineDynamicFragment inLineDynamicFragment = null;

    public InLineDynamicFragment() {

    }

    public static final InLineDynamicFragment getInstance() {
        if (inLineDynamicFragment == null) {
            inLineDynamicFragment = new InLineDynamicFragment();
        }
        return inLineDynamicFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_dynamic_fragment, container, false);
        ButterKnife.bind(this, view);


        TapMoment.setCallback(new TapMoment.TapMomentCallback() {
            @Override
            public void onCallback(int code, String msg) {
                Log.e("TAG", "onCallback: "+code+msg );
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        show_dynamic_page.setOnClickListener(this);
        imageButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(InLineDynamicFragment.getInstance()).commit();
                break;
            case R.id.show_dynamic_page:
                TapMoment.open(TapMoment.ORIENTATION_PORTRAIT);
                break;
            default:
                break;

        }

    }
}
