package com.dennisdavydov.quizapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.constants.AppConstants;
import com.dennisdavydov.quizapp.data.preference.AppPreference;
import com.dennisdavydov.quizapp.utilities.ActivityUtilities;

public class QuizPromptActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;
    private Button mBtnYes, mBtnNo;
    private TextView firstText, thirdText;
    private String categoryId, score, questionsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initListener();
    }

    private void initVar() {
        mActivity = QuizPromptActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null){
            categoryId = intent.getStringExtra(AppConstants.BUNDLE_KEY_INDEX);
            score = AppPreference.getInstance(mContext).getString(categoryId);
            questionsCount = AppPreference.getInstance(mContext)
                    .getString(categoryId + AppConstants.QUESTIONS_IN_TEST);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_quiz_prompt);

        mBtnYes = findViewById(R.id.btn_yes);
        mBtnNo = findViewById(R.id.btn_no);

        firstText = findViewById(R.id.first_text);
        thirdText = findViewById(R.id.third_text);

        if (score !=null && questionsCount !=null){
            firstText.setText(getString(R.string.quiz_promt_first_text, score, questionsCount));
            thirdText.setText(getString(R.string.try_again));
        }

        initToolbar(true);
        setToolbarTitle(getResources().getString(R.string.qiuz_prompt));
        enableUpButton();
    }

    private void initListener() {
        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance()
                        .invokeCommonQuizActivity(mActivity, QuizActivity.class,categoryId,true);
            }
        });
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity,MainActivity.class,true);
            }
        });
    }
    @Override
    public  boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home){
            ActivityUtilities.getInstance().invokeNewActivity(mActivity,MainActivity.class,true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityUtilities.getInstance().invokeNewActivity(mActivity,MainActivity.class,true);
    }
}
