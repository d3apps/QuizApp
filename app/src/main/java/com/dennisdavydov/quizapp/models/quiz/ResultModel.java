package com.dennisdavydov.quizapp.models.quiz;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultModel implements Parcelable {

    private String question;
    private String givenAnswer;
    private String correctAnswer;
    private boolean isCorrect;
    private boolean isSkipped;

    public ResultModel(String question, String givenAnswer, String correctAnswer, boolean isCorrect, boolean isSkipped) {
        this.question = question;
        this.givenAnswer = givenAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
        this.isSkipped = isSkipped;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(givenAnswer);
        dest.writeString(correctAnswer);
        dest.writeByte((byte) (isCorrect ? 1 : 0));
        dest.writeByte((byte) (isSkipped ? 1 :0));
    }

    public ResultModel(Parcel in) {
        question = in.readString();
        givenAnswer = in.readString();
        correctAnswer = in.readString();
        isCorrect = in.readByte() != 0;
        isSkipped = in.readByte() != 0;
    }

    public static Creator<ResultModel> getCREATOR (){return CREATOR;}

    //constant field CREATOR done
    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {
        @Override
        public ResultModel createFromParcel(Parcel source) {
            return new ResultModel(source);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };
}
