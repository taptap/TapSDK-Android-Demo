package com.tds.demo.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.gamesave.TapGameSave;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
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
import cn.leancloud.callback.ProgressCallback;
import cn.leancloud.livequery.LCLiveQuery;
import cn.leancloud.livequery.LCLiveQueryConnectionHandler;
import cn.leancloud.livequery.LCLiveQueryEventHandler;
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
    private static final String TAG = "DataSaveFragment";
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
    @BindView(R.id.listener_network)
    Button listener_network;
    @BindView(R.id.cancel_listener)
    Button cancel_listener;
    @BindView(R.id.save_file)
    Button save_file;
    @BindView(R.id.show_progress)
    Button show_progress;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.download_file)
    Button download_file;
    @BindView(R.id.delete_file)
    Button delete_file;


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
        // 网络状态响应
        listenerNetwork();

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
        listener_network.setOnClickListener(this);
        cancel_listener.setOnClickListener(this);
        save_file.setOnClickListener(this);
        show_progress.setOnClickListener(this);
        download_file.setOnClickListener(this);
        delete_file.setOnClickListener(this);
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
            case R.id.listener_network:
                listenerNetwork();
                break;

            case R.id.cancel_listener:
                cancelListener();
                break;
            case R.id.save_file:
                saveFile();
                break;
            case R.id.show_progress:
                showProgress();
                break;

            case R.id.download_file:
                downloadFile();
                break;
            case R.id.delete_file:
                deleteFile();
                break;



            default:
                break;
        }
    }


    /**
     * 删除文件
     * */
    private void deleteFile() {

        LCFile file = null;
        try {
            file = LCFile.withAbsoluteLocalPath("logo.png", copyAssetGetFilePath("logo.png"));
            file.saveInBackground().subscribe(new Observer<LCFile>() {
                public void onSubscribe(Disposable disposable) {}
                public void onNext(LCFile file) {
                    Log.e(TAG, "onNext: "+file.toString() );

                    // 删除文件
                    LCObject del_file = LCObject.createWithoutData("_File", file.getObjectId());
                    del_file.deleteInBackground().subscribe(new Observer<LCNull>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {}

                        @Override
                        public void onNext(LCNull response) {
                            ToastUtil.showCus("删除文件成功", ToastUtil.Type.SUCCEED);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);
                        }

                        @Override
                        public void onComplete() {}
                    });



                }
                public void onError(Throwable throwable) {
                    ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR);
                }
                public void onComplete() {}
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }


    /**
     * 下载文件
     *
     * */
    private void downloadFile() {
        final String[] fileUrl = {""};

        LCFile file = null;
        try {
            file = LCFile.withAbsoluteLocalPath("logo.png", copyAssetGetFilePath("logo.png"));
            file.getDataInBackground().subscribe(new Observer<byte[]>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(byte[] bytes) {
                            ToastUtil.showCus("下载的文件资源大小："+ bytes.length, ToastUtil.Type.SUCCEED);
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/logo.png";
                            //创建文件
                            File file = new File(path);
                            try {
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                                //通过字节的方式写入数据
                                fileOutputStream.write(bytes);
                                //写入完毕记得关闭输出流
                                fileOutputStream.close();
                                ToastUtil.showCus("下载完成，存储路径是："+path, ToastUtil.Type.SUCCEED );

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);

                        }

                        @Override
                        public void onComplete() {}
                    });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }


    /**
     * 显示上传文件的进度
     * */
    private void showProgress() {
        LCFile file = null;
        try {
            file = LCFile.withAbsoluteLocalPath("logo.png",  copyAssetGetFilePath("存档元文件.csv"));
            file.saveInBackground(new ProgressCallback() {
                @Override
                public void done(Integer percent) {
                    progressbar.setProgress(percent);

                    if (percent == 100){
                        ToastUtil.showCus("文件保存完成", ToastUtil.Type.SUCCEED);
                    }
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


    /**
     * 保存文件
     * */
    private void saveFile() {


        LCFile file = null;
        try {
            file = LCFile.withAbsoluteLocalPath("logo.png", copyAssetGetFilePath("logo.png"));

            file.saveInBackground().subscribe(new Observer<LCFile>() {
                public void onSubscribe(Disposable disposable) {}
                public void onNext(LCFile file) {
                    ToastUtil.showCus("文件保存完成,可以在 开发者中心 > 你的游戏 > 游戏服务 > 云服务 > 数据存储 > 文件 中找到；返回的Url: "+ file.getUrl(), ToastUtil.Type.SUCCEED);
                }
                public void onError(Throwable throwable) {
                    ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR);
                }
                public void onComplete() {}
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 取消订阅
     *
     * */
    private void cancelListener() {
        LCQuery<LCObject> query = new LCQuery<>("Todo");
        query.whereEqualTo("time", "2022-10-29");
        LCLiveQuery liveQuery = LCLiveQuery.initWithQuery(query);

        liveQuery.unsubscribeInBackground(new LCLiveQuerySubscribeCallback() {
            @Override
            public void done(LCException e) {
                if (e == null) {
                    // 订阅成功
                    ToastUtil.showCus("订阅取消成功", ToastUtil.Type.SUCCEED);
                }else {
                    Log.e(TAG, "done: "+ e.toString());
                    ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);

                }
            }
        });

    }


    /**
     *
     * 网络状态响应
     * */
    private void listenerNetwork() {

        LCLiveQuery.setConnectionHandler(new LCLiveQueryConnectionHandler() {
            @Override
            public void onConnectionOpen() {
                Log.e(TAG, "LiveQuery 网络链接开启" );
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showCus("LiveQuery 网络链接开启", ToastUtil.Type.SUCCEED);
                    }
                });
            }

            @Override
            public void onConnectionClose() {
                Log.e(TAG, "LiveQuery 网络链接关闭" );
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showCus("LiveQuery 网络链接关闭", ToastUtil.Type.WARNING);
                    }
                });

            }

            @Override
            public void onConnectionError(int code, String reason) {
                Log.e(TAG, "LiveQuery 网络链接异常："+ reason);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showCus("LiveQuery 网络链接异常："+ reason, ToastUtil.Type.ERROR);
                    }
                });

            }
        });

    }


    /**
     * LiveQuery 构建订阅
     *
     * */
    private void startSubscriber() {

        LCQuery<LCObject> query = new LCQuery<>("Todo");
        query.whereEqualTo("time", "2022-10-29");
        LCLiveQuery liveQuery = LCLiveQuery.initWithQuery(query);

        // 当另一个设备中新建 Todo 一个对象，那么下面的代码可以获取到这个新的
        liveQuery.setEventHandler(new LCLiveQueryEventHandler() {
            @Override
            public void onObjectCreated(LCObject newTodo) {
                Log.e(TAG, "创建了新的对象："+ newTodo.toJSONString());
            }
        });
        // 此时如果有人把 Todo 的 content 改为 进行修改，那么下面的代码可以获取到本次更新
        liveQuery.setEventHandler(new LCLiveQueryEventHandler() {
            @Override
            public void onObjectUpdated(LCObject updatedTodo, List<String> updatedKeys) {
                Log.e(TAG, "对象被更新成如下："+ updatedTodo.toJSONString() +"更新的Key是："+updatedKeys.toString());

            }
        });

        // 当一个已存在的、原本符合 LCQuery 查询条件的 LCObject 被删除，delete 事件会被触发。下面的 object 就是被删除的 LCObject 的 objectId：
        liveQuery.setEventHandler(new LCLiveQueryEventHandler() {
            @Override
            public void onObjectDeleted(String object) {
                Log.e(TAG, "被删除的ObjectId是："+ object);

            }
        });

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
                Log.e(TAG, "数据存储onNext: "+ todo.toJSONString() );
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
        todo.put("title",   "工程师周会纠结纠结、");
        todo.put("content", "周二两点，uuuuu全体成员");
        todo.put("time", "2023-10-29");

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
