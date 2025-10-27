package com.webappquiz.webappquiz.data.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPosition {
    private int xPos = 0;
    private int yPos = 0;

    public void move(int xPos, int yPos) {
        this.xPos += xPos;
        this.yPos += yPos;
    }

    public void moveTo(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
