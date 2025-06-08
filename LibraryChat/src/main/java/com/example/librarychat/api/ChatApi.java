package com.example.librarychat.api;
import com.example.librarychat.model.ChatRoomInfo;
import com.example.librarychat.model.Content;
import com.example.librarychat.model.Message;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ChatApi {

        @Headers({
                "Accept: application/json"
        })
        @GET("/messages/getMessages")
        Call<List<Message>> getMessages(
                @Query("chatRoomId") String chatRoomId,
                @Query("lastCreatedAt") String lastCreatedAt,
                @Query("limit") int limit
        );

        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("/messages/send")
        Call<Map<String, Message>> sendMessage(@Body Message message);

        @Headers({
                "Accept: application/json"
        })
        @DELETE("/messages/delete")
        Call<Map<String, String>> deleteMessage(
                @Query("msgId") String msgId,
                @Query("chatroomId") String chatroomId
        );
        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("/messages/update")
        Call<Map<String, Message>> updateMessage(
                @Query("msgId") String msgId,
                @Body Content content
        );

        @Headers({
                "Accept: application/json"
        })
        @POST("/messages/typing/set")
        Call<Void> setTypingStatus(
                @Query("chatRoomId") String chatRoomId,
                @Query("userId") String userId,
                @Query("isTyping") boolean isTyping
        );

        @Headers({
                "Accept: application/json"
        })
        @GET("/messages/typing/get")
        Call<Map<String,Boolean>> getTypingStatus(
                @Query("chatRoomId") String chatRoomId
        );

        @Headers({
                "Accept: application/json"
        })
        @POST("/chatrooms/create")
        Call<Map<String, String>> createChatRoom(@Query("title") String title,@Query("creatorId") String creatorId);

        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        @POST("/chatrooms/addParticipants")
        Call<Map<String, String>> addParticipants(
                @Query("roomId") String roomId,
                @Body List<String> userIds
        );

        @Headers({
                "Accept: application/json"
        })
        @GET("/chatrooms/userRooms")
        Call<List<ChatRoomInfo>> getUserChatRooms(
                @Query("userId") String userId
        );
        @Headers({
                "Accept: application/json"
        })
        @GET("/chatrooms/participants")
        Call<List<String>> getParticipantsInRoom(
                @Query("roomId") String roomId
        );

}
