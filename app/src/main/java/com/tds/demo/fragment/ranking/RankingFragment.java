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

import com.tapsdk.bootstrap.account.TDSUser;
import com.tapsdk.lc.LCException;
import com.tapsdk.lc.LCLeaderboard;
import com.tapsdk.lc.LCLeaderboardResult;
import com.tapsdk.lc.LCObject;
import com.tapsdk.lc.LCQuery;
import com.tapsdk.lc.LCRanking;
import com.tapsdk.lc.LCStatistic;
import com.tapsdk.lc.LCStatisticResult;
import com.tapsdk.lc.LCUser;
import com.tapsdk.lc.json.JSON;
import com.tds.demo.R;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.until.ToastUtil;

import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.search_rank_patly)
    Button search_rank_patly;
    @BindView(R.id.search_user_score)
    Button search_user_score;






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
        search_rank_patly.setOnClickListener(this);
        search_user_score.setOnClickListener(this);

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
            case R.id.search_rank_patly:
                searchRankPatly();
                break;
            case R.id.search_user_score:
                searchUserScore();
                break;

            default:
                break;

        }

    }


    /**
     * 查询用户的成绩
     *
     */
    private void searchUserScore() {

        // 查询排行榜成员成绩，那 onjectID 就传对应用户的 objectid
        LCUser otherUser = null;
        try {
            otherUser = LCUser.createWithoutData(LCUser.class, "5c76107144d90400536fc88b");
        } catch (LCException e) {
            e.printStackTrace();
        }

        // 这里演示的是获取自己的成绩
        LCLeaderboard.getUserStatistics(TDSUser.currentUser()).subscribe(new Observer<LCStatisticResult>() {
            @Override
            public void onSubscribe(@NotNull Disposable disposable) {}

            @Override
            public void onNext(@NotNull LCStatisticResult lcStatisticResult) {
                List<LCStatistic> statistics = lcStatisticResult.getResults();
                for (LCStatistic statistic : statistics) {

                    Log.e(TAG, "用户排行榜分数："+String.valueOf(statistic.getValue()) + " 上传的排行榜名称："+statistic.getName());

                }
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                Log.e(TAG, "onError: "+ throwable.getLocalizedMessage() );
            }

            @Override
            public void onComplete() {}
        });

    }

    //    查询指定区间的排行榜
    private void searchRankPatly() {
        LCLeaderboard leaderboard = LCLeaderboard.createWithoutData("word");

        leaderboard.getResults(0, 10, null, null).subscribe(new Observer<LCLeaderboardResult>() {
            @Override
            public void onSubscribe(@NotNull Disposable disposable) {}

            @Override
            public void onNext(@NotNull LCLeaderboardResult leaderboardResult) {
                List<LCRanking> rankings = leaderboardResult.getResults();
                for (LCRanking ranking : rankings) {
                    Log.e(TAG, "onNext==: "+ JSON.toJSONString(ranking));
                }
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                // handle error
                Log.e(TAG, "onError: "+throwable.getLocalizedMessage()  );
            }

            @Override
            public void onComplete() {}
        });



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
     * */
    private void searchRankList() {


        // 获取排行榜的实例
        LCLeaderboard leaderboard = LCLeaderboard.createWithoutData("word");

        List<String> selectKeys = new ArrayList<>();
        selectKeys.add("nickname");
        selectKeys.add("avatar");

        leaderboard.getResults(0, 100, selectKeys, null).subscribe(new Observer<LCLeaderboardResult>() {
            @Override
            public void onSubscribe(@NotNull Disposable disposable) {}

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@NotNull LCLeaderboardResult leaderboardResult) {
                List<LCRanking> rankings = leaderboardResult.getResults();


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
        statistic.put("word", 400.00);
        statistic.put("score", 60.00);
        statistic.put("kills", 100.0);
        LCLeaderboard.updateStatistic(TDSUser.currentUser(), statistic).subscribe(new Observer<LCStatisticResult>() {
            @Override
            public void onSubscribe(@NotNull Disposable disposable) {}

            @Override
            public void onNext(@NotNull LCStatisticResult jsonObject) {
                Log.d("上传成绩 onNext", String.valueOf(jsonObject.getResults().size()));

                for (int i = 0; i < jsonObject.getResults().size(); i++) {
                    Log.e(TAG, "上传的排行榜分数："+jsonObject.getResults().get(i).getStatisticValue() + " 上传的排行榜名称："+jsonObject.getResults().get(i).getStatisticName() );
                }
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

    /**
     * score 实际的分数
     * ts 时间戳
     * 将实际分数与时间戳组合生成一个新的数据上传到服务器
     */
    public static double encryptScoreAndTs(double score, long ts) {
        int int_score = (int) score;   // 实际分数的整数部分
        float float_score = (float) (score - int_score); // 实际分数的小数部分
        long encryptedScoreTs = ((long) int_score << 32) | (ts & 0xFFFFFFFFL); // 将整数部分分数与时间戳进行加密处理合并成一个新的score
        double newScore =  (double)encryptedScoreTs + float_score;  // 然后将加密生成的数据拼接上小数部分的数据，上传到服务器
        return newScore;
    }


    /**
     * 将加密的数据进行解析出实际分数和当时提交成绩时的时间戳
     *
     */
    public static int[] decryptNewScore(long encryptedNewScore) {
        // 从newScore中分离出原来的score和ts
        int score = (int) (encryptedNewScore >> 32);
        long ts = encryptedNewScore & 0xFFFFFFFFL;
        return new int[]{score, (int) ts};
    }







}
