package com.webappquiz.webappquiz.component;

import java.security.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.webappquiz.webappquiz.data.ChannelInstance;
import com.webappquiz.webappquiz.data.game.GameInfo;
import com.webappquiz.webappquiz.data.game.GameScoreBoard;
import com.webappquiz.webappquiz.data.game.QuizQuestion;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.GameEndResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.GameQuizOptionSelectResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.GameQuizQuestionEndResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.GameQuizResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.GameRankInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.QuizOptionMessage;
import com.webappquiz.webappquiz.protobuf.Protogen.QuizQuestionMessage;

import lombok.Data;

@Data
@Component
@Scope("prototype")
public class GameInstance {
    private GameInfo        nowGameInfo;
    private ChannelInstance channelInstance;
    private QuizQuestion    nowQuizQuestion;

    private Map<Long, GameScoreBoard>   gameScoreBoardMap = new HashMap<>();
    private Set<Integer>                beforeQuizIndexes = new HashSet<>();

    public GameInstance() {
        this.nowGameInfo = null;
    }

    public void setNowGameInfo(GameInfo gameInfo) {
        this.nowGameInfo = gameInfo;
    }

    public void addGameScoreBoard(long userIndex, String userName) {
        if(gameScoreBoardMap.containsKey(userIndex)){
            gameScoreBoardMap.get(userIndex).setScore(0);
            return;
        }

        gameScoreBoardMap.put(userIndex, new GameScoreBoard(userIndex, userName, 0));
    }

    public GameScoreBoard getGameScoreBoard(long userIndex) {
        return gameScoreBoardMap.get(userIndex);
    }

    public void starQuestion() {
        if(beforeQuizIndexes.size() == nowGameInfo.getQuizSetInfo().getQuizCount()){
            broadcastQuestionEnd();
            endGame();
            return;
        }

        int quizCount = nowGameInfo.getQuizSetInfo().getQuizCount();
        int randomIndex = (int)(Math.random() * quizCount);
        while(beforeQuizIndexes.contains(randomIndex)){
            randomIndex = (int)(Math.random() * quizCount);
        }

        beforeQuizIndexes.add(randomIndex);
        nowQuizQuestion = nowGameInfo.getQuizSetInfo().getQuizQuestion(randomIndex);

        QuizQuestionMessage quizQuestionMessage = createQuizQuestionMessage(nowQuizQuestion);
        
        //< 퀴즈 문제 전송
        GameQuizResponse gameQuizResponse = GameQuizResponse.newBuilder()
                .setQuestion(quizQuestionMessage)
                .build();

        channelInstance.broadcastMessage(CallType.GAME, PacketType.GAME_QUIZ_QUESTION_RESPONSE, gameQuizResponse);
    }

    public boolean isQuestionEnd() {
        return beforeQuizIndexes.size() == nowGameInfo.getQuizSetInfo().getQuizCount();
    }

    public void broadcastQuestionEnd() {
        if(null == nowQuizQuestion)
            return;

        GameQuizQuestionEndResponse response = createGameQuizQuestionEndResponse(nowQuizQuestion.getAnswerOptionNo());
        channelInstance.broadcastMessage(CallType.GAME, PacketType.GAME_QUIZ_QUESTION_END_RESPONSE, response);
    }

    public void endQuestion() {
        nowQuizQuestion = null;
    }

    public void endGame() {
        // 결과 순위를 집계하여 channelInstance에 전달
        ArrayList<GameScoreBoard> scoreList = new ArrayList<>(gameScoreBoardMap.values());
        ArrayList<GameScoreBoard> topRankList = new ArrayList<>(3);
        scoreList.sort((a, b) -> Integer.compare(b.getScore(), a.getScore())); // 점수 내림차순 정렬

        // 순위 설정
        for (int i = 0; i < scoreList.size(); i++) {
            scoreList.get(i).setRank(i + 1);

            if(i < 3)
                topRankList.add(scoreList.get(i));
        }

        ArrayList<GameRankInfo> rankerInfos = createGameRankInfoList(topRankList);
        for(GameScoreBoard scoreBoard : scoreList){
            GameRankInfo userRankInfo = createGameRankInfo(scoreBoard);
            GameEndResponse response = createGameEndResponse(rankerInfos, userRankInfo);
            channelInstance.sendToUser(scoreBoard.getUserIndex(), CallType.GAME, PacketType.GAME_END_RESPONSE, response);
        }
    }

    public void onUserSelectAnswer(UserInstance user, int answerIndex) {
        if(null == nowQuizQuestion)
            return;

        GameQuizOptionSelectResponse response;
        if(answerIndex == nowQuizQuestion.getAnswerOptionNo()) {
            //< 정답
            // 정답일 경우 10점 추가∏
            GameScoreBoard scoreBoard = gameScoreBoardMap.get(user.getUserIndex());
            if(null == scoreBoard){
                addGameScoreBoard(user.getUserIndex(), user.getUserInfo().getUserName());
                scoreBoard = gameScoreBoardMap.get(user.getUserIndex());
            }
            
            scoreBoard.addScore(10);
            response = createGameQuizOptionSelectResponse(true, 0);
        } else {
            //< 오답
            response = createGameQuizOptionSelectResponse(false, 1);
        }

        PacketTransferData packetData = new PacketTransferData(CallType.GAME, PacketType.GAME_QUIZ_OPTION_SELECT_RESPONSE, response);
        user.getUserContext().write(packetData);
    }

    public QuizQuestionMessage createQuizQuestionMessage(QuizQuestion question) {
        long endTime = Instant.now().getEpochSecond() + 15;

        ArrayList<QuizOptionMessage> quizOptionMessages = new ArrayList<>();
        for(int i = 0; i < question.getQuizOptionList().size(); i++){
            quizOptionMessages.add(QuizOptionMessage.newBuilder()
                    .setOptionNo(question.getQuizOptionList().get(i).getOptionNo())
                    .setOptionText(question.getQuizOptionList().get(i).getOptionText())
                    .build());
        }

        QuizQuestionMessage quizQuestionMessage = QuizQuestionMessage.newBuilder()
                                            .setQuizText(question.getQuestionText())
                                            .setQuestionTimestamp(endTime)
                                            .addAllOptionList(quizOptionMessages)
                                            .build();        

        return quizQuestionMessage;
    }
    
    public GameQuizOptionSelectResponse createGameQuizOptionSelectResponse(boolean success, int errorCode) {
        GameQuizOptionSelectResponse response = GameQuizOptionSelectResponse.newBuilder()
                .setSuccess(success)
                .setErrorCode(errorCode)
                .build();

        return response;
    }

    public GameQuizQuestionEndResponse createGameQuizQuestionEndResponse(int answerOptionNo) {
        GameQuizQuestionEndResponse response = GameQuizQuestionEndResponse.newBuilder()
                .setAnswerNo(answerOptionNo)
                .build();

        return response;
    }

    public GameRankInfo createGameRankInfo(GameScoreBoard gameScoreBoard) {
        GameRankInfo response = GameRankInfo.newBuilder()
                .setUserId(gameScoreBoard.getUserIndex())
                .setUserName(gameScoreBoard.getUserName())
                .setScore(gameScoreBoard.getScore())
                .setRank(gameScoreBoard.getRank())
                .build();

        return response;
    }

    public ArrayList<GameRankInfo> createGameRankInfoList(ArrayList<GameScoreBoard> gameScoreBoards) {
        ArrayList<GameRankInfo> rankInfos = new ArrayList<>();
        for(GameScoreBoard gameScoreBoard : gameScoreBoards){
            rankInfos.add(createGameRankInfo(gameScoreBoard));
        }

        return rankInfos;
    }

    public GameEndResponse createGameEndResponse(ArrayList<GameRankInfo> topRankInfos, GameRankInfo myRankInfo) {
        GameEndResponse response = GameEndResponse.newBuilder()
                .addAllTopRankInfo(topRankInfos)
                .setMyRank(myRankInfo)
                .build();

        return response;
    }
}