package com.webappquiz.webappquiz.data.game;

import com.webappquiz.webappquiz.protobuf.Protogen.GameModeType;

import lombok.Data;

@Data
public class GameInfo {
    private long            gameId;
    private GameModeType    gameModeType;
    private QuizSetInfo     quizSetInfo;

    public GameInfo(long id, GameModeType modeType){
        gameId = id;
        gameModeType = modeType;
    }
}