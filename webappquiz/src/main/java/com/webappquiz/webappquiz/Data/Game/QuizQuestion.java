package com.webappquiz.webappquiz.data.game;

import java.util.ArrayList;

import lombok.Data;

@Data
public class QuizQuestion {
    private int     questionId;
    private String  questionText;
    private int     answerOptionNo;
    
    private ArrayList<QuizQuestionOption> quizOptionList;

    public QuizQuestion(int questionId, String questionText, int answerOptionNo){
        this.questionId     = questionId;
        this.questionText   = questionText;
        this.answerOptionNo = answerOptionNo;

        this.quizOptionList = new ArrayList<>();
    }

    public void addOption(QuizQuestionOption option){
        quizOptionList.add(option);
    }
}
