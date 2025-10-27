package com.webappquiz.webappquiz.data.game;

import lombok.Data;

@Data
public class GameScoreBoard {
    private long userIndex;
    private String userName;
    private int score;
    private int rank;

    public GameScoreBoard(long userIndex, String userName, int score) {
        this.userIndex = userIndex;
        this.userName = userName;
        this.score = score;
        this.rank = 0;
    }

    public void addScore(int score) {
        this.score += score;
    }
}
