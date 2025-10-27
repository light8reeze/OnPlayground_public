package com.webappquiz.webappquiz.data.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    
    // 기본 쿼리 메서드
    Optional<UserInfo> findByUserId(String userId);
    Optional<UserInfo> findByObjectId(String id);
    boolean existsByUserId(String userId);
    
    // 로그인용 메서드
    Optional<UserInfo> findByUserIdAndPassword(String userId, String password);
    
    // 유저명 기반 검색 (부분 일치)
    List<UserInfo> findByUserNameContaining(String userName);
    
    // 커스텀 쿼리 예제
    @Query("{'userId': ?0}")
    UserInfo findUserWithUserId(String userId);
    
    // 여러 조건으로 사용자 검색 (OR 조건)
    @Query("{'$or': [{'userId': ?0}, {'userName': ?1}]}")
    List<UserInfo> findByUserIdOrUserName(String userId, String userName);
}