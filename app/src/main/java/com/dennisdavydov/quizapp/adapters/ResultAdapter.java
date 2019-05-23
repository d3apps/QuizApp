package com.dennisdavydov.quizapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.constants.AppConstants;
import com.dennisdavydov.quizapp.listeners.ListItemClickListener;
import com.dennisdavydov.quizapp.models.quiz.ResultModel;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<ResultModel> mItemList;
    private ListItemClickListener mItemClickListener;

    public ResultAdapter(Context mContext, Activity mActivity, ArrayList<ResultModel> mItemList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mItemList = mItemList;
    }

    public void setmItemClickListener (ListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result,parent,false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ResultModel model = mItemList.get(position);

        viewHolder.tvQuestion.setText(Html.fromHtml(model.getQuestion()));
        viewHolder.tvCorrectAnswer.setText(Html.fromHtml(model.getCorrectAnswer()));

        if (model.isCorrect()) {
            viewHolder.lytAnswerContainer.setVisibility(View.GONE);
        } else {
            viewHolder.tvGivenAnwer.setText(Html.fromHtml(model.getGivenAnswer()));
        }
        int imgPosition;
        if (model.isSkipped()) {
            imgPosition = AppConstants.BUNDLE_KEY_ZERO_INDEX;
        } else if (model.isCorrect()){
            imgPosition = AppConstants.BUNDLE_KEY_FIRST_INDEX;
        } else {
            imgPosition = AppConstants.BUNDLE_KEY_SECOND_INDEX;
        }

        Glide.with(mContext)
                .load(mContext.getResources().getIdentifier(
                        AppConstants.DIRECTORY + imgPosition,
                        null,
                        mContext.getPackageName()))
                .into(viewHolder.imgAnswer);
    }

    @Override
    public int getItemCount() {

        return (null != mItemList ? mItemList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgAnswer;
        private TextView tvQuestion, tvGivenAnwer, tvCorrectAnswer;
        private RelativeLayout lytAnswerContainer;
        private ListItemClickListener itemClickListener;

        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;
            // find views
            imgAnswer = itemView.findViewById(R.id.answer_icon);
            tvQuestion = itemView.findViewById(R.id.question_text);
            tvGivenAnwer = itemView.findViewById(R.id.given_answer_text);
            tvCorrectAnswer = itemView.findViewById(R.id.correct_answer_text);
            lytAnswerContainer = itemView.findViewById(R.id.your_answer_container);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (itemClickListener !=null) {
                itemClickListener.onItemClick(getLayoutPosition(),view);
            }
        }
    }
}
