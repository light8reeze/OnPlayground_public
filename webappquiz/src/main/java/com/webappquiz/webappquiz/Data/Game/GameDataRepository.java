package com.webappquiz.webappquiz.data.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.webappquiz.webappquiz.protobuf.Protogen.GameModeType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDataRepository {
    // TODO: 추후에 DB로 수정하기, Bean으로 수정하기
    private static GameDataRepository INSTANCE = new GameDataRepository();
    public static GameDataRepository getInstance() {
        return INSTANCE;
    }

    private Map<Long, GameInfo> gameInfoList = new HashMap<>();

    public GameDataRepository(){
     
        // 더미 데이터 생성 및 초기화
        GameInfo gameInfo = new GameInfo(0, GameModeType.QUIZ);
        
        // QuizSetInfo 생성
        QuizSetInfo quizSetInfo = new QuizSetInfo(1);
        
        // 더미 질문 리스트 생성
        Map<Integer, QuizQuestion> questionList = new HashMap<>();
        
        // 첫 번째 질문
        QuizQuestion question1 = new QuizQuestion(1, "대한민국의 수도는 서울이다.", 0);
        
        // 첫 번째 질문의 옵션 추가
        question1.addOption(new QuizQuestionOption(0, "O"));
        question1.addOption(new QuizQuestionOption(1, "X"));
        
        QuizQuestion question2 = new QuizQuestion(2, "1+1 = 2 이다", 0);
        question2.addOption(new QuizQuestionOption(0, "O"));
        question2.addOption(new QuizQuestionOption(1, "X"));

        // 질문 리스트에 추가
        questionList.put(1, question1);
        questionList.put(2, question2);
        
        // QuizSetInfo에 질문 리스트 설정
        quizSetInfo.setQuestionList(new ArrayList<>(questionList.values()));
        
        // GameInfo에 QuizSetInfo 설정
        gameInfo.setQuizSetInfo(quizSetInfo);
        
        // 게임 정보 저장
        addGameInfo(gameInfo);
    }

    public GameInfo findGameInfo(long id){
        return gameInfoList.get(id);
    }

    void addGameInfo(GameInfo gameInfo){
        gameInfoList.put(gameInfo.getGameId(), gameInfo);
    }
}