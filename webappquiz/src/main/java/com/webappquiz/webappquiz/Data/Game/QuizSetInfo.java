package com.webappquiz.webappquiz.data.game;

import java.util.ArrayList;

import lombok.Data;

@Data
public class QuizSetInfo {
    private int                     quizSetId;
    private ArrayList<QuizQuestion> questionList = new ArrayList<>();

    public QuizSetInfo(int id){
        quizSetId = id;
    }

    public int getQuizCount() {
        return questionList.size();
    }

    public QuizQuestion getQuizQuestion(int index) {
        return questionList.get(index);
    }
}
