package com.tds.demo.fragment.achievement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapsdk.antiaddictionui.AntiAddictionUIKit;
import com.tapsdk.antiaddictionui.widget.AntiToastManager;
import com.tapsdk.bootstrap.account.TDSUser;
import com.tds.achievement.AchievementCallback;
import com.tds.achievement.AchievementException;
import com.tds.achievement.GetAchievementListCallBack;
import com.tds.achievement.TapAchievement;
import com.tds.achievement.TapAchievementBean;
import com.tds.demo.R;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.until.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022/10/21
 * Describe：成就系统
 */
public class AchievementFragment  extends Fragment implements View.OnClickListener {

    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.show_achievement_page)
    Button show_achievement_page;
    @BindView(R.id.my_archieve)
    Button my_archieve;
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;


    private static AchievementFragment achievementFragment = null;

    public AchievementFragment() {

    }

    public static final AchievementFragment getInstance() {
        if (achievementFragment == null) {
            achievementFragment = new AchievementFragment();
        }
        return achievementFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.achievement_fragment, container, false);
        ButterKnife.bind(this, view);
       /**
        * 初始化数据
        *
        * 由于成就系统会在本地记录用户的成就数据，所以请在用户登录后初始化数据。如果用户切换账号时，务必重新调用该接口，不然数据可能会存在账号存储混乱的问题。
        * 这个步骤是异步操作，需要确认收到成功回调时才能进行更多操作。
        * */
        if(TDSUser.getCurrentUser() == null){
            ToastUtil.showCus("请先完成登录操作！", ToastUtil.Type.WARNING);
            return null;
        }
        TapAchievement.initData();


        // 注册监听回调
        TapAchievement.registerCallback(new AchievementCallback() {
            @Override
            public void onAchievementSDKInitSuccess() {
                // 数据加载成功
                ToastUtil.showCus("数据加载成功", ToastUtil.Type.SUCCEED);
                /**
                 * 获取全部成就数据
                 *
                 * */
                searchArchieList();
            }
            @Override
            public void onAchievementSDKInitFail(AchievementException exception) {
                // 数据加载失败，请重试
                Log.e("TAG", "onAchievementSDKInitFail: "+ exception.getMessage() );
                ToastUtil.showCus("数据加载失败，请重试", ToastUtil.Type.ERROR);
            }
            @Override
            public void onAchievementStatusUpdate(TapAchievementBean item, AchievementException exception) {
                if (exception != null) {
                    // 成就更新失败
                    ToastUtil.showCus("成就更新失败", ToastUtil.Type.ERROR);
                    return;
                }
                if (item != null) {
                    // item 更新成功
                    ToastUtil.showCus("item 更新成功", ToastUtil.Type.SUCCEED);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        show_achievement_page.setOnClickListener(this);
        my_archieve.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(AchievementFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                entryDocument();
                break;
            case R.id.show_achievement_page:
                toshowAchievementPage();
                break;
            case R.id.my_archieve:
                searchMyArchieve();
                break;
            default:
                break;


        }
    }
    /**
     * 查询当前用户的成就
     *
     * */
    private void searchMyArchieve() {
// 本地数据
        List<TapAchievementBean> userList = TapAchievement.getLocalUserAchievementList();
        String archieveName= "";
        for (int i= 0; i< userList.size(); i++) {
            Log.e("TAG", "全部成就数据: "+ userList.get(i).getAchievementId()+"  "+userList.get(i).getTitle()+"  "+userList.get(i).isFullAchievement()+" "+ userList.get(i).isFullReached()+" "+ userList.get(i).getReachedStep() );
            archieveName =archieveName+userList.get(i).getTitle()+"、";
        }
        if(archieveName.isEmpty()){
            ToastUtil.showCus("暂未获取任何成就", ToastUtil.Type.SUCCEED);
        }else{
            ToastUtil.showCus("已经获取的成就有："+archieveName, ToastUtil.Type.SUCCEED);
        }

// 服务端数据
        TapAchievement.fetchUserAchievementList(new GetAchievementListCallBack() {
            @Override
            public void onGetAchievementList(List<TapAchievementBean> achievementList, AchievementException exception) {
                if (exception != null) {
                    switch (exception.errorCode) {
                        case AchievementException.SDK_NOT_INIT:
                            // SDK 还未初始化数据
                            break;
                        default:
                            // 数据获取失败
                    }
                } else {
                    // 成功获取数据

                }
            }
        });


    }


    /**
     * 直接达成某个成就
     * */
    private void finishAchieve(String displayId) {
        TapAchievement.reach(displayId);

    }


    /**
     * 增加分步成就达成的步数
     * growSteps 中传递当前增量达成的步数（例如：多走了 5 步，则传递 5 即可
     * makeSteps 中传递当前成就已达成的步数，(例如：当前已经走了 100 步，则传递 100)
     * */
    private void addStep(String displayId) {
        // displayID 是在开发者中心中添加成就时自行设定的 成就ID
        TapAchievement.growSteps(displayId, 1);
//        TapAchievement.makeSteps("win_battle_10", 10);

    }


    /**
     * 打开成就展示页
     *
     * */
    private void toshowAchievementPage() {
        TapAchievement.showAchievementPage();

    }

    /**
     * 获取全部成就数据
     *
     * */
    private void searchArchieList() {


        // 本地数据
        List<TapAchievementBean> allList = TapAchievement.getLocalAllAchievementList();
        for (int i = 0; i < allList.size(); i++) {
            Log.e("TAG", "============ "+ allList.get(i).getDisplayId()+"  "+ allList.get(i).isFullAchievement()+"  " +allList.get(i).getReachedStep()+"  "+allList.get(i).isFullReached()+
                    "  "+allList.get(i).getStep());
        }
        ArchievementAdapter archievementAdapter = new ArchievementAdapter();
        archievementAdapter.addData(allList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(archievementAdapter);
        archievementAdapter.setmOnItemClickListener(new ArchievementAdapter.OnItemClickListener() {
            @Override
            public void onClick(TapAchievementBean tapAchievementBean, int position, int hadle_type) {
                if(hadle_type == 1){
                    Log.e("TAG", "onClick:getDisplayId :"+ tapAchievementBean.getDisplayId());
                    addStep(tapAchievementBean.getDisplayId());
                    ToastUtil.showCus("增加一步成功！", ToastUtil.Type.SUCCEED);
                }else{
                    finishAchieve(tapAchievementBean.getDisplayId());
                    ToastUtil.showCus("直接完成该成就！", ToastUtil.Type.SUCCEED);

                }

            }


        });


        // 服务端数据
        TapAchievement.fetchAllAchievementList(new GetAchievementListCallBack() {
            @Override
            public void onGetAchievementList(List<TapAchievementBean> achievementList, AchievementException exception) {
                if (exception != null) {
                    Log.e("TAG", "onGetAchievementList: "+ exception.errorCode );
                    switch (exception.errorCode) {
                        case AchievementException.SDK_NOT_INIT:
                            // SDK 还未初始化数据
                            ToastUtil.showCus("SDK 还未初始化数据", ToastUtil.Type.WARNING);
                            break;
                        default:
                            break;
                    }
                } else {
                    // 成功获取数据
                    for (int i = 0; i < achievementList.size(); i++) {
                        Log.e("TAG", "+++++++++++  "+ achievementList.get(i).getDisplayId()+"  "+ achievementList.get(i).isFullAchievement()+"  " +achievementList.get(i).getReachedStep()+"  "+achievementList.get(i).isFullReached()+
                                "  "+achievementList.get(i).getStep());
                    }
                }
            }
        });

    }

    private void entryDocument() {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/achievement/features/"), null)
                    .addToBackStack("webViewFragment")
                    .commit();
        }
}
