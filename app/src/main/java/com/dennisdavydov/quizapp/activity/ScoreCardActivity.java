package com.dennisdavydov.quizapp.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.adapters.ResultAdapter;
import com.dennisdavydov.quizapp.constants.AppConstants;
import com.dennisdavydov.quizapp.models.quiz.ResultModel;
import com.dennisdavydov.quizapp.utilities.ActivityUtilities;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ScoreCardActivity extends BaseActivity  implements OnChartValueSelectedListener {

    private Activity mActivity;
    private Context mContext;
    private Button mBtnShare, mBtnPlayAgain;
    private TextView mScoreTextView, mWrongAnswerTextView, mSkipTextView, mGreetingTextView;
    private int mScore, mWrongAnswer, mSkip, mQuiestionsCount;
    private String mCategoryId;
    private ArrayList<ResultModel> mResultList;

    private ResultAdapter mAdapter = null;
    private RecyclerView mRecyclerResult;

    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality ();
        initListener();
    }

    private void initVar() {
        mActivity = ScoreCardActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null){
            mScore = intent.getIntExtra(AppConstants.BUNDLE_KEY_SCORE,0);
            mWrongAnswer = intent.getIntExtra(AppConstants.BUNDLE_KEY_WRONG_ANS,0);
            mQuiestionsCount = intent.getIntExtra(AppConstants.QUESTIONS_IN_TEST,0);
            mCategoryId = intent.getStringExtra(AppConstants.BUNDLE_KEY_INDEX);
            mResultList = intent.getParcelableArrayListExtra(AppConstants.BUNDLE_KEY_ITEM);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_score_card);

        mRecyclerResult = findViewById(R.id.rvContentScore);
        mRecyclerResult.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        mBtnShare = findViewById(R.id.btn_share);
        mBtnPlayAgain = findViewById(R.id.btn_play_again);

        mScoreTextView = findViewById(R.id.txt_score);
        mWrongAnswerTextView = findViewById(R.id.txt_wrong);
        mSkipTextView = findViewById(R.id.txt_skip);
        mGreetingTextView = findViewById(R.id.greeting_text);

        initToolbar(true);
        setToolbarTitle(mContext.getString(R.string.results));
        enableUpButton();
    }

    private void initFunctionality() {

        mSkip = mQuiestionsCount - (mScore + mWrongAnswer);

        mSkipTextView.setText(String.valueOf(mScore));
        mWrongAnswerTextView.setText(String.valueOf(mWrongAnswer));
        mSkipTextView.setText(String.valueOf(mSkip));

        float actualScore = (mScore / (mScore + mWrongAnswer + mSkip)) * AppConstants.MULTIPLIER_GRADE;
        switch (Math.round(actualScore)) {
            case 10:
            case 9:
            case 8:
                mGreetingTextView.setText(mContext.getString(R.string.greeting_text3));
                break;
            case 7:
            case 6:
            case 5:
                mGreetingTextView.setText(mContext.getString(R.string.greeting_text2));
                break;
                default:
                    mGreetingTextView.setText(mContext.getString(R.string.greeting_text1));
                    break;
        }
        showPieChart();

        mAdapter = new ResultAdapter(mContext, mActivity, mResultList);
        mRecyclerResult.setAdapter(mAdapter);

        //TODO: ad block
    }

    private void showPieChart() {
        Description description = new Description();
        description.setText(mContext.getString(R.string.results));
        mPieChart = findViewById(R.id.piechart);
        mPieChart.setUsePercentValues(true);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setTransparentCircleRadius(AppConstants.TRANSPARENT_CIRCLE_RADIUS);
        mPieChart.setHoleRadius(AppConstants.TRANSPARENT_CIRCLE_RADIUS);
        mPieChart.setDescription(description);
        mPieChart.animateXY(AppConstants.ANIMATION_VALUE, AppConstants.ANIMATION_VALUE);

        ArrayList<PieEntry> entryValues = new ArrayList<>();
        entryValues.add(new PieEntry(mScore, mContext.getString(R.string.score)));
        entryValues.add(new PieEntry(mWrongAnswer,mContext.getString(R.string.wrong)));
        entryValues.add(new PieEntry(mSkip,mContext.getString(R.string.skipped)));
        PieDataSet dataSet = new PieDataSet(entryValues,AppConstants.EMPTY_STRING);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


//        ArrayList<String> xVals = new ArrayList<>();
//        xVals.add("");
//        xVals.add("");
//        xVals.add("");

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        mPieChart.setData(data);
    }

    private void initListener() {
        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.sharing_text)
                        + AppConstants.PLAY_GOOGLE + mActivity.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"Send:"));
            }
        });
        mBtnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeCommonQuizActivity(mActivity,QuizPromptActivity.class,mCategoryId,true);
            }
        });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
    }
}
