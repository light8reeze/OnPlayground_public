package com.webappquiz.webappquiz.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webappquiz.webappquiz.data.game.GameDataRepository;
import com.webappquiz.webappquiz.data.game.GameInfo;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.data.ChannelInstance;
import com.webappquiz.webappquiz.data.ChannelManager;
import com.webappquiz.webappquiz.component.GameInstance;

@Service("GameService")
public class GameService {
    private Map<Integer, GameInstance>         gameInstanceMap = new HashMap<>();
    private final ScheduledExecutorService      gameScheduler = Executors.newScheduledThreadPool(1);

    private static GameService instance;
    public static GameService getInstance() { return instance; }

    @Autowired
    private ApplicationContext applicationContext;

    public GameService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        instance = this;
    }

    public GameInstance getGameInstance(int channelId) {
        return gameInstanceMap.get(channelId);
    }

    public GameInstance createGameInstance(int channelId, GameInfo gameInfo) {
        GameInstance gameInstance = applicationContext.getBean(GameInstance.class);
        gameInstanceMap.put(channelId, gameInstance);

        gameInstance.setNowGameInfo(gameInfo);
        
        ChannelInstance channelInstance = ChannelManager.getInstance().findChannel(channelId);
        gameInstance.setChannelInstance(channelInstance);
        
        return gameInstance;
    }

    public void deleteGameInstance(int channelId) {
        gameInstanceMap.remove(channelId);
    }

    public void startGame(int channelId) {
        if(gameInstanceMap.get(channelId) != null)
            return;

        GameInfo gameInfo = GameDataRepository.getInstance().findGameInfo(0);
        createGameInstance(channelId, gameInfo);

        gameScheduler.schedule(() -> {
            startQuestion(channelId);
        }, 5, TimeUnit.SECONDS);
    }

    //< 새로운 퀴즈 제시
    public void startQuestion(int channelId) {
        GameInstance gameInstance = gameInstanceMap.get(channelId);
        if(null == gameInstance)
            return;

        gameInstance.starQuestion();

        //< 5초 후에 퀴즈 종료시킴
        gameScheduler.schedule(() -> {
            onQuestionTimeout(channelId);
        }, 15, TimeUnit.SECONDS);
    }

    public void onQuestionTimeout(int channelId) {
        GameInstance gameInstance = gameInstanceMap.get(channelId);
        if(null == gameInstance)
            return;

        //< 1초후에 마감
        gameInstance.broadcastQuestionEnd();
        gameScheduler.schedule(() -> {
            endQuestion(channelId);
        }, 1, TimeUnit.SECONDS);
    }

    public void endQuestion(int channelId) {
        GameInstance gameInstance = gameInstanceMap.get(channelId);
        if(null == gameInstance)
            return;

        gameInstance.endQuestion();

        if(gameInstance.isQuestionEnd()){
            gameScheduler.schedule(() -> {
                gameInstance.endGame();
                deleteGameInstance(channelId);
            }, 5, TimeUnit.SECONDS);
        } else {
            gameScheduler.schedule(() -> {
                startQuestion(channelId);
            }, 5, TimeUnit.SECONDS);
        }
    }

    public void onUserSelectAnswer(int channelId, UserInstance user, int answerIndex) {
        GameInstance gameInstance = gameInstanceMap.get(channelId);
        if(null == gameInstance)
            return;

        gameInstance.onUserSelectAnswer(user, answerIndex);
    }
}
