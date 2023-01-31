package com.tds.demo.fragment.ranking;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tds.demo.R;
import com.tds.demo.data.User;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.until.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCException;
import cn.leancloud.LCLeaderboard;
import cn.leancloud.LCLeaderboardResult;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.LCRanking;
import cn.leancloud.LCStatistic;
import cn.leancloud.LCStatisticResult;
import cn.leancloud.LCUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022-10-11
 * Describe：排行榜
 */
public class RankingFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "RankingFragment";
    @BindView(R.id.close_button)
    ImageButton imageButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.submit_score)
    Button submit_score;
    @BindView(R.id.search_rank_list)
    Button search_rank_list;
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;
    @BindView(R.id.search_ranking_attr)
    Button search_ranking_attr;




    private static RankingFragment rankingFragment = null;

    public RankingFragment() {

    }

    public static final RankingFragment getInstance() {
        if (rankingFragment == null) {
            rankingFragment = new RankingFragment();
        }
        return rankingFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_ranking_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        submit_score.setOnClickListener(this);
        search_rank_list.setOnClickListener(this);
        search_ranking_attr.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(RankingFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/leaderboard/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.submit_score:
                submitScore();
                break;
            case R.id.search_rank_list:
                searchRankList();
                break;
            case R.id.search_ranking_attr:
                searchRankingAttr();
                break;


            default:
                break;

        }

    }


    /**
     * 查询排行榜属性
     * */
    private void searchRankingAttr() {
        LCLeaderboard.fetchByName("word").subscribe(new Observer<LCLeaderboard>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LCLeaderboard lcLeaderboard) {
               ToastUtil.showCus(lcLeaderboard.getStatisticName()+"榜：更新策略："+lcLeaderboard.getUpdateStrategy()+"  排序："+lcLeaderboard.getOrder() , ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);

            }

            @Override
            public void onComplete() {

            }
        });
    }


    /**
     * 查询排行榜列表
     *
     * */
    private void searchRankList() {
        // 获取排行榜的实例
        LCLeaderboard leaderboard = LCLeaderboard.createWithoutData("word");


        leaderboard.getResults(0, 10, null, null).subscribe(new Observer<LCLeaderboardResult>() {
            @Override
            public void onSubscribe(@NotNull Disposable disposable) {}

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@NotNull LCLeaderboardResult leaderboardResult) {
                List<LCRanking> rankings = leaderboardResult.getResults();
                Log.e(TAG, "onNext:+++++>>>> "+rankings.get(0).getUser().toJSONString() );
//                Log.e(TAG, "onNext:+++++>>>> "+rankings.get(1).getUser().toJSONString() );
//                Log.e(TAG, "onNext:+++++>>>> "+rankings.get(2).getUser().toJSONString() );
//                Log.e(TAG, "onNext:+++++>>>> "+rankings.get(3).getUser().toJSONString() );



                RankingAdapter rankingAdapter = new RankingAdapter();
                rankingAdapter.addData(rankings);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycle_view.setLayoutManager(linearLayoutManager);
                recycle_view.setAdapter(rankingAdapter);
                rankingAdapter.setmOnItemClickListener(new RankingAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(LCRanking lCRanking, int position) {
//                        查询排行榜成员成绩
                        LCUser otherUser = null;
                        try {
                            otherUser = LCUser.createWithoutData(LCUser.class, lCRanking.getUser().getObjectId());
                        } catch (LCException e) {
                            e.printStackTrace();
                        }
                        LCLeaderboard.getUserStatistics(otherUser).subscribe(new Observer<LCStatisticResult>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable disposable) {}

                            @Override
                            public void onNext(@NotNull LCStatisticResult lcStatisticResult) {
                                List<LCStatistic> statistics = lcStatisticResult.getResults();
                                String info = "";
                                for (LCStatistic statistic : statistics) {
                                    info = info +"  "+ statistic.getName()+"榜，得分是："+String.valueOf(statistic.getValue());
                                }
                                ToastUtil.showCus(info, ToastUtil.Type.SUCCEED);
                            }

                            @Override
                            public void onError(@NotNull Throwable throwable) {
                                // handle error
                                ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR);
                            }

                            @Override
                            public void onComplete() {}
                        });
                    }
                });
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                // handle error
            }

            @Override
            public void onComplete() {}
        });

    }

    /**
     * 上传/更新成绩
     * */
    private void submitScore() {

        Map<String, Double> statistic  = new HashMap<>();
        statistic.put("word", 210.00);
        statistic.put("score", 310.00);
        statistic.put("kills", 180.0);
        LCLeaderboard.updateStatistic(LCUser.currentUser(), statistic).subscribe(new Observer<LCStatisticResult>() {
            @Override
            public void onSubscribe(@NotNull Disposable disposable) {}

            @Override
            public void onNext(@NotNull LCStatisticResult jsonObject) {
                // scores saved
                Log.e(TAG, "onNext: "+jsonObject.getResults().get(0).toString());
                ToastUtil.showCus("上传成功：排行榜名："+jsonObject.getResults().get(0).getStatisticName()+ " 排行榜积分："
                        +jsonObject.getResults().get(0).getStatisticValue(), ToastUtil.Type.SUCCEED);

            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR);
            }

            @Override
            public void onComplete() {}
        });
    }





}
