package com.dennisdavydov.quizapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.adapters.QuizAdapter;
import com.dennisdavydov.quizapp.constants.AppConstants;
import com.dennisdavydov.quizapp.data.preference.AppPreference;
import com.dennisdavydov.quizapp.listeners.ListItemClickListener;
import com.dennisdavydov.quizapp.models.quiz.QuizModel;
import com.dennisdavydov.quizapp.utilities.ActivityUtilities;
import com.dennisdavydov.quizapp.utilities.BeatBox;
import com.dennisdavydov.quizapp.utilities.DialogUtilities;
import com.dennisdavydov.quizapp.utilities.SoundUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends BaseActivity implements DialogUtilities.OnCompleteListener {

    private Activity mActivity;
    private Context mContext;
    
    private ImageButton btnSpeaker;
    private Button btnNext;
    private RecyclerView mRecyclerQuiz;
    private TextView tvQuestionTitle, tvQuestionText;
    private ImageView imgFirstLife, imgSecondLife,imgThirdLife, imgFourthLife, imgFifthLife;
    
    private QuizAdapter mAdapter = null;
    private List<QuizModel> mItemList;
    private ArrayList<String> mOptionList, mBackgroundColorList;
    
    private int mQuiestionPosition = 0,mQuestionCount = 0;
    private int mScore = 0, mWrongAnswer = 0, mSkip = 0;
    private int mLifeCounter = 5;
    
    private boolean mUserHasPressed = false;
    private boolean mIsSkipped = false, mIsCorrect = false;
    
    private String mQuetionText, mGivenAnswerText, mCorrectAnswerText, mCategoryId;
    
    //private BeatBox mBeatBox;
    private List<SoundUtilities> mSounds;
    private boolean isSoundOn;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO: initializeRewardedAds();
        //TODO: loadRewardedVideoAds();
        
        initVar();
        initView();
        loadData();
        initListener();
    }

    private void initVar() {
        mActivity = QuizActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mCategoryId = intent.getStringExtra(AppConstants.BUNDLE_KEY_INDEX);
        }

        mItemList = new ArrayList<>();
        mOptionList = new ArrayList<>();
        mBackgroundColorList = new ArrayList<>();
        // TODO: mResultList = new ArrayList<>();

        //mBeatBox = new BeatBox(mActivity);
        //mSounds = mBeatBox.getSounds();
    }

    private void initView() {
        setContentView(R.layout.activity_quiz);

        imgFirstLife = findViewById(R.id.firstLife);
        imgSecondLife = findViewById(R.id.secondLife);
        imgThirdLife = findViewById(R.id.thirdLife);
        imgFourthLife = findViewById(R.id.fourthLife);
        imgFifthLife = findViewById(R.id.fifthLife);

        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnNext = findViewById(R.id.btnNext);

        tvQuestionText = findViewById(R.id.tvQuestionText);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);

        mRecyclerQuiz = findViewById(R.id.rvQuiz);
        mRecyclerQuiz.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        mAdapter = new QuizAdapter(mContext, mActivity, mOptionList, mBackgroundColorList);
        mRecyclerQuiz.setAdapter(mAdapter);

        initToolbar(true);
        setToolbarTitle(this.getString(R.string.quiz_toolbar_title));
        enableUpButton();
        initLoader();
    }

    private void loadData() {
        showLoader();

        isSoundOn = AppPreference.getInstance(mActivity).getBoolean(AppConstants.KEY_SOUND,true);
        setSpeakerImage();

        loadJson();
    }

    private void setSpeakerImage() {
        if (isSoundOn){
            btnSpeaker.setImageResource(R.drawable.ic_speaker);
        } else {
            btnSpeaker.setImageResource(R.drawable.ic_speaker_not);
        }
    }

    private void initListener() {
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                isSoundOn = !isSoundOn;
                AppPreference.getInstance(mActivity).setBoolean(AppConstants.KEY_SOUND, isSoundOn);
                setSpeakerImage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserHasPressed){
                    FragmentManager manager = getSupportFragmentManager();
                    DialogUtilities dialog = DialogUtilities.newInstance(
                            getResources().getString(R.string.dialog_title_skip),
                            getResources().getString(R.string.dialog_text_skip),
                            getResources().getString(R.string.yes),
                            getResources().getString(R.string.no),
                            AppConstants.BUNDLE_KEY_SKIP_OPTION);
                    dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
                } else {
                    // TODO: updateResultSet ();
                    setNextQuestion ();
                }
            }
        });

        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (!mUserHasPressed) {
                    int clickedAnswerIndex = position;
                    if (mItemList.get(mQuiestionPosition).getCorrectAnswer()!= -1){
                        for (int currentItemIndex = 0; currentItemIndex < mOptionList.size(); currentItemIndex++){
                            if (currentItemIndex == clickedAnswerIndex &&
                                    currentItemIndex == mItemList.get(mQuiestionPosition).getCorrectAnswer()){
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_GREEN);
                                mScore++;
                                mIsCorrect = true;
                                if (isSoundOn){
                                    //mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_ZERO_INDEX));
                                }
                            } else if(currentItemIndex == clickedAnswerIndex &&
                                    !(currentItemIndex == mItemList.get(mQuiestionPosition).getCorrectAnswer())){
                                mBackgroundColorList.set(currentItemIndex,AppConstants.COLOR_RED);
                                mWrongAnswer++;
                                if (isSoundOn){
                                    //mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_SECOND_INDEX));
                                }
                                decreaseLifeAndStatus();
                            } else  if (currentItemIndex == mItemList.get(mQuiestionPosition).getCorrectAnswer()){
                                mBackgroundColorList.set(currentItemIndex,AppConstants.COLOR_GREEN);
                                ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(currentItemIndex);
                            }
                        }
                    } else {
                        mBackgroundColorList.set(clickedAnswerIndex,AppConstants.COLOR_GREEN);
                        mScore++;
                        mIsCorrect = true;
                        //mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_ZERO_INDEX));
                    }
                    mGivenAnswerText = mItemList.get(mQuiestionPosition).getAnswers().get(clickedAnswerIndex);
                    mCorrectAnswerText = mItemList.get(mQuiestionPosition).getAnswers().get(mItemList.get(mQuiestionPosition).getCorrectAnswer());
                    mUserHasPressed = true;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateResultSet() {
    }

    private void decreaseLifeAndStatus() {
        mLifeCounter--;
        setLifeStatus();
    }
    private void increaseLifeAndStatus(){
        if (mLifeCounter<AppConstants.BUNDLE_KEY_MAX_LIFE) {
            mLifeCounter++;
            setLifeStatus();
        }
    }

    private void setLifeStatus() {
        switch (mLifeCounter){
            case 1:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.GONE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 2:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 3:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 4:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.VISIBLE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 5:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.VISIBLE);
                imgFifthLife.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setNextQuestion() {
        if (isSoundOn){
            //mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_FIRST_INDEX));
        }
        mUserHasPressed = false;
        if (mQuiestionPosition < mItemList.size() -1 && mLifeCounter >0) {
            mQuiestionPosition++;
            updateQuestionsAndAnswers ();
        } else if (mQuiestionPosition < mItemList.size() - 1 && mLifeCounter == 0){
            FragmentManager manager = getSupportFragmentManager();
            DialogUtilities dialog = DialogUtilities.newInstance(getResources().getString(R.string.dialog_title_chance),
                    getResources().getString(R.string.dialog_text_chance),
                    getResources().getString(R.string.yes),
                    getResources().getString(R.string.no),
                    AppConstants.BUNDLE_KEY_REWARD_OPTION);
            dialog.show(manager,AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
        }else {
            //TODO: invoke ScoreCardActivity
        }
    }

    private void updateQuestionsAndAnswers() {
        mOptionList.clear();
        mBackgroundColorList.clear();
        ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(AppConstants.BUNDLE_KEY_ZERO_INDEX);

        mOptionList.addAll(mItemList.get(mQuiestionPosition).getAnswers());
        mBackgroundColorList.addAll(mItemList.get(mQuiestionPosition).getBackgroundColors());
        mAdapter.notifyDataSetChanged();

        mQuetionText = mItemList.get(mQuiestionPosition).getQuestion();

        tvQuestionText.setText(Html.fromHtml(mQuetionText));
        tvQuestionTitle.setText(getResources().getString(R.string.quiz_question_title,mQuiestionPosition + 1, mQuestionCount));
    }

    public void quizActivityClosePrompt(){
        FragmentManager manager = getSupportFragmentManager();
        DialogUtilities dialog = DialogUtilities.newInstance(getResources().getString(R.string.exit),
                getResources().getString(R.string.stop_test),
                getResources().getString(R.string.yes),
                getResources().getString(R.string.no),
                AppConstants.BUNDLE_KEY_CLOSE_OPTION);
        dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
    }


    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstants.QUESTION_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        parseJson(sb.toString());
    }

    private void parseJson(String jsonData) {
        try {
            JSONObject jsonObjectMain = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObjectMain.getJSONArray(AppConstants.JSON_KEY_QUESTIONNAIRY);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                String question = jsonObj.getString(AppConstants.JSON_KEY_QUESTION);
                int correctAnswer = Integer.parseInt(jsonObj.getString(AppConstants.JSON_KEY_CORRECT_ANS));
                String categoryId = jsonObj.getString(AppConstants.JSON_KEY_CATEGORY_ID);

                Log.d("TAG", categoryId);

                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstants.JSON_KEY_ANSWERS);
                ArrayList<String> contents = new ArrayList<>();
                ArrayList<String> backgroundColors = new ArrayList<>();
                for (int j = 0; j < jsonArray2.length(); j++){
                    String itemTitle = jsonArray2.get(j).toString();
                    contents.add(itemTitle);
                    backgroundColors.add(AppConstants.COLOR_WHITE);
                }
                if (mCategoryId.equals(categoryId)){
                    mItemList.add(new QuizModel(question, contents, correctAnswer, categoryId, backgroundColors));
                }
            }
            mQuestionCount = mItemList.size();
            Collections.shuffle(mItemList);

            hideLoader();
            updateQuestionsAndAnswers();

        }catch (JSONException e){
            e.printStackTrace();
            showEmptyView();
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            quizActivityClosePrompt();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed (){
        quizActivityClosePrompt();
    }


    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if(isOkPressed){
            if (viewIdText.equals(AppConstants.BUNDLE_KEY_CLOSE_OPTION)) {
                ActivityUtilities.getInstance()
                        .invokeNewActivity(mActivity,MainActivity.class,true);
            } else if (viewIdText.equals(AppConstants.BUNDLE_KEY_SKIP_OPTION)) {
                mSkip++;
                // TODO: mIsSkipped = true;
                mGivenAnswerText = getResources().getString(R.string.skipped_text);
                mCorrectAnswerText = mItemList.get(mQuiestionPosition).getAnswers()
                        .get(mItemList.get(mQuiestionPosition).getCorrectAnswer());
                //TODO: updateResultSet();
                setNextQuestion();
            } else if (viewIdText.equals(AppConstants.BUNDLE_KEY_REWARD_OPTION)) {
                // TODO: mRewardedVideoAd.show();
            }
        } else if (!isOkPressed && viewIdText.equals(AppConstants.BUNDLE_KEY_REWARD_OPTION)) {
            // TODO: invokeScoreCardActivity();
            AppPreference.getInstance(mContext).setQuizResult(mCategoryId, mQuestionCount);
            AppPreference.getInstance(mContext).setQuizQuestionCount(mCategoryId,mQuestionCount);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mBeatBox.release();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
