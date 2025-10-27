package com.webappquiz.webappquiz.server;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.webappquiz.webappquiz.handler.inbound.AcceptHandler;
import com.webappquiz.webappquiz.handler.outbound.PacketWrappingOutboundHandler;
import com.webappquiz.webappquiz.handler.outbound.StateChangeOutboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import jakarta.annotation.PreDestroy;

@Component
public class NettyServer 
{
    //< EventLoopGroup : 쓰레드 풀 ( Event를 관리하는 쓰레드 묶음 ) , accept 와 rw 따로
    //< NioEventLoopGroup은 내부적으로 감시하여 io 처리
    //< 그리고 소켓 옵션도 상황에 맞춰서 각 그룹에 옵션으로 넣어야 함
    private final EventLoopGroup acceptGroup        = new NioEventLoopGroup(1);
    private final EventLoopGroup rwGroup            = new NioEventLoopGroup(); //< 생성자에 상수 없으면 코어만큼 생성

    //< 소켓 추상화 객체 
    private Channel serverChannel;

    private final int port = 1796; //< 사용하려는 포트

    public void start() throws InterruptedException
    {
        try
        {
            String hostAddress = getLocalHostAddress();
            System.out.println("========================================");
            System.out.println(" Netty WebSocket Server is starting...");
            System.out.println(" Access URL: ws://" + hostAddress + ":" + port);
            System.out.println("========================================");


            ServerBootstrap b = new ServerBootstrap();                  //< 네티 서버 설정 빌더 클레스
            b.group( acceptGroup, rwGroup )                             //< event group을 받는다.
            .channel(NioServerSocketChannel.class)         //< NIO 기반 비동기 채널 사용 설정.
            .childHandler(new ChannelInitializer<SocketChannel>()       //< 클라이언트 accept 되고 파이프라인이 등록되고 설정
            {
                @Override
                protected void initChannel( SocketChannel ch ) //< 연결된 세션의 채널 매개변수
                {
                    ch.pipeline().addLast(
                                    new HttpServerCodec(),      //< 응답을 ByteBuffer 가 아닌 HttpRequest, HttpResponse로 바꿔줌
                                    new HttpObjectAggregator(8192), //< 여러가지 나뉘어진 Http 데이터 컨텐츠들을 붙여서 FullHttpRequest로 합쳐서 줌
                                    new AcceptHandler()   //< Custum Multi Router 핸들러 
                            );

                    ch.pipeline().addFirst(                                
                                    new PacketWrappingOutboundHandler(),        //< PacketWrapper을 생성하고, 최종 전달하는 핸들러
                                    new StateChangeOutboundHandler()            //< 상태 변경을 담당하는 이벤트 핸들러
                            );
                }
                
            })
            .option(ChannelOption.SO_REUSEADDR, true)           //< accept option
            .childOption(ChannelOption.SO_KEEPALIVE, true);     //< rw option

            //< binding 하고 소켓 설정, 저장
            ChannelFuture future = b.bind(port).sync();
            serverChannel = future.channel();

            System.out.println("Netty WebSocket Server started on port "+port);
        }
        catch( Exception e )
        {
            System.out.println("Netty 서버 실행 중 오류 발생: " + e.getMessage());
            stop(); //< 실행중 에러 발생 서버 종료
        }
    }

    @PreDestroy
    public void stop() 
    {
        if (serverChannel != null) serverChannel.close();

        //< 아름다운 종료 ( 점진적 종료 )
        acceptGroup.shutdownGracefully();
        rwGroup.shutdownGracefully();

        System.out.println("Netty Server Shutdown");
    }

    //< 현재 서버의 IP 주소 내놔
    private String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}