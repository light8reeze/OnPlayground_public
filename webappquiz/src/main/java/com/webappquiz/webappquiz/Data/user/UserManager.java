package com.webappquiz.webappquiz.data.user;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 유저를 관리하는 클래스
 */
@Component
public class UserManager {
    @Autowired
    private UserInfoRepository userInfoRepository;
    private static UserManager INSTANCE;

    public static UserManager getInstance() {
        return INSTANCE;
    }

    private static final Map<String, UserInfo> userDatabase = new ConcurrentHashMap<>();
    private static Map<Long, UserInstance> userInstanceMap = new HashMap<>();
    private static AtomicLong userIndexCounter = new AtomicLong(1); // 1부터 시작하는 순차적 인덱스 생성기

    public UserManager() {
        INSTANCE = this;
    }
    
    /**
     * 서버 시작 시 모든 사용자 데이터를 DB에서 로드하여 캐시에 저장합니다.
     */
    @PostConstruct
    public void init() {
        List<UserInfo> allUsers = userInfoRepository.findAll();
        userDatabase.clear(); // 기존 캐시 클리어
        
        for (UserInfo user : allUsers) {
            userDatabase.put(user.getUserId(), user);
        }
        
        System.out.println("Loaded " + allUsers.size() + " users into cache.");
    }

    /**
     * 사용자 정보를 등록합니다.
     * 
     * @param userId 사용자 ID
     * @param password 사용자 비밀번호
     * @param userName 사용자 이름
     * @return 등록된 사용자 정보
     */
    public UserInfo registerUserInfo(String userId, String password, String userName) {
        // 이미 존재하는 사용자인지 확인
        if (userInfoRepository.existsByUserId(userId)) {
            throw new RuntimeException("이미 존재하는 사용자 ID입니다.");
        }
        
        UserInfo user = new UserInfo();
        user.setUserId(userId);
        user.setPassword(password);
        user.setUserName(userName);
        
        // MongoDB에 저장
        UserInfo savedUser = userInfoRepository.save(user);
        
        // 로컬 캐시에도 저장 (선택사항)
        userDatabase.put(userId, savedUser);
        
        return savedUser;
    }

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (존재하지 않을 경우 null)
     */
    public UserInfo findUserInfo(String userId) {
        // 로컬 캐시에서 먼저 조회
        UserInfo user = userDatabase.get(userId);
        if (user == null) {
            // 로컬에 없으면 DB에서 조회
            user = userInfoRepository.findByUserId(userId).orElse(null);
            if (user != null) {
                // DB에서 조회된 사용자는 캐시에 저장
                userDatabase.put(userId, user);
            }
        }
        return user;
    }

    public void onUserLogin(UserInfo userInfo, ChannelHandlerContext ctx) {
        // DB에서 사용자 존재 여부 확인
        if (!userInfoRepository.existsByUserId(userInfo.getUserId())) {
            return;
        }

        UserInstance loginUser = new UserInstance();
        long newUserIndex = userIndexCounter.getAndIncrement(); // 순차적으로 userIndex 발급
        loginUser.setUserIndex(newUserIndex);
        loginUser.setUserInfo(userInfo);
        loginUser.setUserContext(ctx);

        userInstanceMap.put(newUserIndex, loginUser);
    }

    public UserInstance getUserInstance(long userIndex){
        return userInstanceMap.get(userIndex);
    }
    
    /**
     * 사용자 ID로 UserInstance를 찾는 메소드
     * 
     * @param userId 찾을 사용자 ID
     * @return 해당 사용자의 UserInstance, 없으면 null
     */
    public UserInstance getUserInstanceByUserId(String userId) {
        UserInfo userInfo = findUserInfo(userId);
        if (userInfo == null) {
            return null;
        }
        
        // 현재 로그인한 사용자 중에서 동일한 UserInfo를 가진 인스턴스를 찾는다
        for (UserInstance instance : userInstanceMap.values()) {
            if (instance.getUserInfo().getUserId().equals(userId)) {
                return instance;
            }
        }
        
        return null;
    }
};