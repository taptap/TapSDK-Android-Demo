package com.tds.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.gamesave.TapGameSave;
import com.tapsdk.lc.LCFile;
import com.tapsdk.lc.types.LCNull;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022-10-25
 * Describe：云存档
 */
public class CloudSaveFragment extends Fragment implements View.OnClickListener{
    private static CloudSaveFragment cloudSaveFragment = null;

    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.save_name)
    EditText save_name;
    @BindView(R.id.save_description)
    EditText save_description;
    @BindView(R.id.create_save)
    Button create_save;
    @BindView(R.id.search_save)
    Button search_save;
    @BindView(R.id.delete_save)
    Button delete_save;
    private List<TapGameSave> tapSaves = new ArrayList<>();


    public CloudSaveFragment() {
    }

    public static final CloudSaveFragment getInstance() {
        if (cloudSaveFragment == null) {
            cloudSaveFragment = new CloudSaveFragment();
        }
        return cloudSaveFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_cloud_save_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        create_save.setOnClickListener(this);
        search_save.setOnClickListener(this);
        delete_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(CloudSaveFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/gamesaves/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.create_save:
                createSave();
                break;

            case R.id.search_save:
                searchSave();
                break;
            case R.id.delete_save:
                deleteSave();
                break;
            default:
                break;
        }
    }


    /**
     * 删除存档
     *
     * */
    private void deleteSave() {

            searchSave();
            if (tapSaves.isEmpty()){
                ToastUtil.showCus("当前用户暂无存档！", ToastUtil.Type.SUCCEED);
                return;
            }

            tapSaves.get(0).deleteInBackground().subscribe(new Observer<LCNull>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {}

                @Override
                public void onNext(LCNull response) {
                    ToastUtil.showCus("删除成功！", ToastUtil.Type.SUCCEED);

                }

                @Override
                public void onError(@NonNull Throwable e) {
                    System.out.println("Failed to delete:" + e.getMessage());
                }

                @Override
                public void onComplete() {}
            });

    }


    /**
     * 查询用户存档
     * */
    private void searchSave() {
        TapGameSave.getCurrentUserGameSaves()
                .subscribe(new Observer<List<TapGameSave>>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {}

                    @Override
                    public void onNext(@NotNull List<TapGameSave> tapGameSaves) {
                        tapSaves.clear();

                        tapSaves.addAll(tapGameSaves);

                        for (TapGameSave gameSave : tapGameSaves) {
                            String summary = gameSave.getSummary();
                            Date modifiedAt = gameSave.getModifiedAt();
                            double playedTime = gameSave.getPlayedTime();
                            int progressValue = gameSave.getProgressValue();
                            LCFile cover = gameSave.getCover();
                            LCFile gameFile = gameSave.getGameFile();
                            Log.e("TAG", "=====  "+gameSave.toString() );
                            ToastUtil.showCus("存档名："+gameSave.getName()+" 描述："+summary+" 游戏时间："+playedTime, ToastUtil.Type.SUCCEED);
                        }
                    }
                    @Override
                    public void onError(@NotNull Throwable e) {
                        e.printStackTrace();
                        Log.e("TAG", "onError: "+e.getLocalizedMessage() );
                    }

                    @Override
                    public void onComplete() {}
                });
    }


    /**
     * 创建存档
     * */
    private void createSave() {
        if(save_name.getText().toString().trim().isEmpty()){
            ToastUtil.showCus("请输入存档名称！", ToastUtil.Type.POINT);
            return;
        }
        if(save_description.getText().toString().trim().isEmpty()){
            ToastUtil.showCus("请输入存档描述！", ToastUtil.Type.POINT);
            return;
        }
        Log.e("TAG", "createSave: "+ copyAssetGetFilePath("存档元文件.csv"));

        TapGameSave snapshot = new TapGameSave();
        snapshot.setName(save_name.getText().toString());
        snapshot.setSummary(save_description.getText().toString());
        snapshot.setPlayedTime(70000); // ms
        snapshot.setProgressValue(102);
        snapshot.setCover(copyAssetGetFilePath("logo.png")); // jpg/png
        snapshot.setGameFile(copyAssetGetFilePath("存档元文件.csv"));
        snapshot.setModifiedAt(new Date());
        snapshot.saveInBackground().subscribe(new Observer<TapGameSave>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {}

            @Override
            public void onNext(@NotNull TapGameSave gameSave) {
                ToastUtil.showCus("存档保存成功", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.e("TAG", "onError: "+e.getLocalizedMessage()  );
            }

            @Override
            public void onComplete() {}
        });


    }




    private String copyAssetGetFilePath(String fileName) {
        try {
            File cacheDir = getContext().getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return null;
                }
            } else {
                if (outFile.length() > 10) {//表示已经写入一次
                    return outFile.getPath();
                }
            }
            InputStream is = getContext().getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return outFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
