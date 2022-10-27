package com.tds.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.gamesave.TapGameSave;
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
import cn.leancloud.LCException;
import cn.leancloud.LCFile;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.livequery.LCLiveQuery;
import cn.leancloud.livequery.LCLiveQuerySubscribeCallback;
import cn.leancloud.types.LCNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022-10-25
 * Describe：数据存储
 */
public class DataSaveFragment extends Fragment implements View.OnClickListener{
    private static DataSaveFragment dataSaveFragment = null;

    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.to_save)
    Button to_save;
    @BindView(R.id.search_data)
    Button search_data;
    @BindView(R.id.save_objectid)
    TextView save_objectid;
    @BindView(R.id.refresh_data)
    Button refresh_data;
    @BindView(R.id.update_data)
    Button update_data;
    @BindView(R.id.delete_data)
    Button delete_data;
    @BindView(R.id.subscriber)
    Button subscriber;




    private String saveObjectId = "";


    private List<TapGameSave> tapSaves = new ArrayList<>();


    public DataSaveFragment() {
    }

    public static final DataSaveFragment getInstance() {
        if (dataSaveFragment == null) {
            dataSaveFragment = new DataSaveFragment();
        }
        return dataSaveFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_data_save_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        to_save.setOnClickListener(this);
        search_data.setOnClickListener(this);
        refresh_data.setOnClickListener(this);
        update_data.setOnClickListener(this);
        delete_data.setOnClickListener(this);
        subscriber.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(DataSaveFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/storage/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.to_save:
                objectSaveToCloud();
                break;
            case R.id.search_data:
                searchData();
                break;
            case R.id.refresh_data:
                refreshData();
                break;
            case R.id.update_data:
                updateData();
                break;
            case R.id.delete_data:
                deleteData();
                break;
            case R.id.subscriber:
                startSubscriber();
                break;



            default:
                break;
        }
    }


    /**
     * LiveQuery 构建订阅
     *
     * */
    private void startSubscriber() {

        LCQuery<LCObject> query = new LCQuery<>("Todo");
        query.whereEqualTo("isComplete", true);

        LCLiveQuery liveQuery = LCLiveQuery.initWithQuery(query);

        liveQuery.subscribeInBackground(new LCLiveQuerySubscribeCallback() {
            @Override
            public void done(LCException e) {
                if (e == null) {
                    // 订阅成功
                    ToastUtil.showCus("订阅成功", ToastUtil.Type.SUCCEED);
                }else {
                    ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);

                }
            }
        });

    }


    /**
     * 删除对象
     *
     * */
    private void deleteData() {

        LCObject todo = LCObject.createWithoutData("Todo", saveObjectId);
        todo.deleteInBackground().subscribe(new Observer<LCNull>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}

            @Override
            public void onNext(LCNull response) {
                // succeed to delete a todo.
                ToastUtil.showCus("删除以上对象成功", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);
            }

            @Override
            public void onComplete() {}
        });

    }


    /**
     * 更新对象
     *
     *
     * */
    private void updateData() {

        if (saveObjectId.isEmpty()){
            ToastUtil.showCus("暂无存储对象数据！", ToastUtil.Type.POINT);
            return;
        }
        LCObject todo = LCObject.createWithoutData("Todo", saveObjectId);
        todo.put("content", "周会改到周三下午三点。");
        todo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject savedTodo) {
                ToastUtil.showCus("更新数据成功！"+todo.toJSONString() , ToastUtil.Type.SUCCEED );

            }
            public void onError(Throwable throwable) {
                ToastUtil.showCus(throwable.getMessage() , ToastUtil.Type.ERROR );
            }
            public void onComplete() {}
        });;


    }


    /**
     * 同步对象
     *
     * 当云端数据发生更改时，你可以调用 fetchInBackground 方法来刷新对象，使之与云端数据同步：
     * */
    private void refreshData() {
        if (saveObjectId.isEmpty()){
            ToastUtil.showCus("暂无存储对象数据！", ToastUtil.Type.POINT);
            return;
        }

        LCObject todo = LCObject.createWithoutData("Todo", saveObjectId);
        todo.fetchInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // todo 已刷新
                ToastUtil.showCus("同步数据成功！"+todo.toJSONString() , ToastUtil.Type.SUCCEED );

            }
            public void onError(Throwable throwable) {
                ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR );

            }
            public void onComplete() {}
        });

    }


    /**
     * 查询对象
     * */
    private void searchData() {
        LCQuery<LCObject> query = new LCQuery<>("Todo");

        if (saveObjectId.isEmpty()){
            ToastUtil.showCus("暂无存储对象数据！", ToastUtil.Type.POINT);
            return;
        }

        query.getInBackground(saveObjectId).subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // todo 就是 objectId 为 582570f38ac247004f39c24b 的 Todo 实例
                String title    = todo.getString("title");
                int priority    = todo.getInt("priority");
                // 获取内置属性
                String objectId = todo.getObjectId();
                Date updatedAt  = todo.getUpdatedAt();
                Date createdAt  = todo.getCreatedAt();
                ToastUtil.showCus("查询成功！"+todo.toJSONString() , ToastUtil.Type.SUCCEED );


            }
            public void onError(Throwable throwable) {
                ToastUtil.showCus(throwable.getMessage() , ToastUtil.Type.ERROR );

            }
            public void onComplete() {}
        });

    }


    /**
     *
     * 存储对象到云端
     * */
    private void objectSaveToCloud() {
        // 构建对象
        LCObject todo = new LCObject("Todo");

        // 为属性赋值
        todo.put("title",   "工程师周会");
        todo.put("content", "周二两点，全体成员");

        // 将对象保存到云端
        todo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                saveObjectId = todo.getObjectId();
                save_objectid.setText(saveObjectId);

                // 成功保存之后，执行其他逻辑
                ToastUtil.showCus("存储成功！可以通过：开发者中心 > 你的游戏 > 游戏服务 > 云服务 > 数据存储 > 结构化数据 > Todo表 查看存储的数据" , ToastUtil.Type.SUCCEED );

            }
            public void onError(Throwable throwable) {
                // 异常处理
                ToastUtil.showCus(throwable.getMessage() , ToastUtil.Type.ERROR );
            }
            public void onComplete() {}
        });


    }


}
