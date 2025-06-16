package com.avitaliskhakov.librarychat.api;

import android.util.Log;

import com.avitaliskhakov.librarychat.model.ChatRoomInfo;
import com.avitaliskhakov.librarychat.model.Content;
import com.avitaliskhakov.librarychat.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatController {

    private static final String BASE_URL = "https://straightforward-freddy-avital-bcd688c4.koyeb.app";
    private final CallBack_Chat callBackChat;
    private final ChatApi chatApi;

    public ChatController(CallBack_Chat callBackChat) {
        this.callBackChat = callBackChat;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Increased from 10
                .readTimeout(30, TimeUnit.SECONDS)     // Increased from 15
                .writeTimeout(30, TimeUnit.SECONDS)    // Increased from 15
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        chatApi = retrofit.create(ChatApi.class);
    }

    public void fetchMessages(String chatRoomId, String lastCreatedAt, int limit) {
        chatApi.getMessages(chatRoomId, lastCreatedAt, limit).enqueue(createListCallback(response -> {
            // Handle empty response gracefully
            if (response == null) {
                callBackChat.success(new ArrayList<>());
            } else {
                callBackChat.success(response);
            }
        }));
    }

    public void sendMessage(Message message) {
        chatApi.sendMessage(message).enqueue(createMapCallback(callBackChat::messageSent));
    }

    public void deleteMessage(String msgId, String chatRoomId) {
        chatApi.deleteMessage(msgId, chatRoomId).enqueue(createMapStringCallback(callBackChat::messageDeleted));
    }

    public void updateMessage(String msgId, Content content) {
        chatApi.updateMessage(msgId, content).enqueue(createMapCallback(callBackChat::messageUpdated));
    }

    public void setTypingStatus(String chatRoomId, String userId, boolean isTyping) {
        chatApi.setTypingStatus(chatRoomId, userId, isTyping).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callBackChat.error("Failed to set typing status: " + t.getMessage());
            }
        });
    }

    public void fetchTypingStatus(String chatRoomId) {
        chatApi.getTypingStatus(chatRoomId).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callBackChat.typingStatusUpdated(response.body());
                } else {
                    callBackChat.error("Typing status error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                // Don't report typing status failures as errors
                Log.d("ChatController", "Typing status check failed: " + t.getMessage());
            }
        });
    }

    public void createChatRoom(String title, String creatorId) {
        chatApi.createChatRoom(title, creatorId).enqueue(createMapStringCallback(callBackChat::chatRoomCreated));
    }

    public void addParticipants(String roomId, List<String> userIds) {
        chatApi.addParticipants(roomId, userIds).enqueue(createMapStringCallback(response -> {}));
    }

    public void getUserChatRooms(String userId) {
        chatApi.getUserChatRooms(userId).enqueue(new Callback<List<ChatRoomInfo>>() {
            @Override
            public void onResponse(Call<List<ChatRoomInfo>> call, Response<List<ChatRoomInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callBackChat.userChatRoomsFetched(response.body());
                } else {
                    callBackChat.error("Failed to fetch chat rooms: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoomInfo>> call, Throwable t) {
                callBackChat.error("Error fetching user chat rooms: " + t.getMessage());
            }
        });
    }

    public void getParticipantsInRoom(String roomId) {
        chatApi.getParticipantsInRoom(roomId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callBackChat.participantsFetched(response.body());
                } else {
                    callBackChat.error("Failed to fetch participants: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                callBackChat.error("Error fetching participants: " + t.getMessage());
            }
        });
    }

    private <T> Callback<List<T>> createListCallback(OnSuccess<List<T>> onSuccess) {
        return new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatController", "onResponse success");
                    List<T> body = response.body();
                    onSuccess.run(body != null ? body : new ArrayList<>());
                } else {
                    if (response.code() == 404 || response.code() == 204) {
                        onSuccess.run(new ArrayList<>());
                    } else {
                        callBackChat.error("List callback error: " + getError(response));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                // More detailed error message
                String errorMsg = "Network error: ";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Request timeout - ";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "No internet connection - ";
                } else if (t instanceof IOException) {
                    errorMsg = "Connection error - ";
                }
                callBackChat.error(errorMsg + t.getMessage());
            }
        };
    }

    private <T> Callback<Map<String, T>> createMapCallback(OnSuccess<Map<String, T>> onSuccess) {
        return new Callback<Map<String, T>>() {
            @Override
            public void onResponse(Call<Map<String, T>> call, Response<Map<String, T>> response) {
                if (response.isSuccessful()) {
                    onSuccess.run(response.body());
                } else {
                    callBackChat.error("Map callback error: " + getError(response));
                }
            }

            @Override
            public void onFailure(Call<Map<String, T>> call, Throwable t) {
                callBackChat.error("Map callback failure: " + t.getMessage());
            }
        };
    }

    private Callback<Map<String, String>> createMapStringCallback(OnSuccess<Map<String, String>> onSuccess) {
        return new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    onSuccess.run(response.body());
                } else {
                    callBackChat.error("Map<String, String> error: " + getError(response));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callBackChat.error("Map<String, String> failure: " + t.getMessage());
            }
        };
    }

    private String getError(Response<?> response) {
        try {
            return response.errorBody() != null ? response.errorBody().string() : "Unknown error";
        } catch (IOException e) {
            return "Error reading errorBody: " + e.getMessage();
        }
    }

    public interface CallBack_Chat {
        void success(List<Message> messages);
        void messageSent(Map<String, Message> response);
        void messageDeleted(Map<String, String> response);
        void messageUpdated(Map<String, Message> response);
        void error(String error);
        void typingStatusUpdated(Map<String, Boolean> typingUsers);
        void chatRoomCreated(Map<String, String> response);
        void userChatRoomsFetched(List<ChatRoomInfo> chatRooms);
        void participantsFetched(List<String> participants);
    }

    private interface OnSuccess<T> {
        void run(T result);
    }
}
//package com.avitaliskhakov.librarychat.api;
//
//import android.util.Log;
//
//import com.avitaliskhakov.librarychat.model.ChatRoomInfo;
//import com.avitaliskhakov.librarychat.model.Content;
//import com.avitaliskhakov.librarychat.model.Message;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class ChatController {
//
////    private static final String BASE_URL = "http://10.0.2.2:8088/";
//
//    private static final String BASE_URL = "https://straightforward-freddy-avital-bcd688c4.koyeb.app";
//    private final CallBack_Chat callBackChat;
//    private final ChatApi chatApi;
//
//    public ChatController(CallBack_Chat callBackChat) {
//        this.callBackChat = callBackChat;
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(15, TimeUnit.SECONDS)
//                .build();
//
//        Gson gson = new GsonBuilder().setLenient().create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build();
//
//        chatApi = retrofit.create(ChatApi.class);
//    }
//
//    public void fetchMessages(String chatRoomId, String lastCreatedAt, int limit) {
//        chatApi.getMessages(chatRoomId, lastCreatedAt, limit).enqueue(createListCallback(callBackChat::success));
//    }
//
//    public void sendMessage(Message message) {
//        chatApi.sendMessage(message).enqueue(createMapCallback(callBackChat::messageSent));
//    }
//
//    public void deleteMessage(String msgId, String chatRoomId) {
//        chatApi.deleteMessage(msgId, chatRoomId).enqueue(createMapStringCallback(callBackChat::messageDeleted));
//    }
//
//    public void updateMessage(String msgId, Content content) {
//        chatApi.updateMessage(msgId, content).enqueue(createMapCallback(callBackChat::messageUpdated));
//    }
//
//    public void setTypingStatus(String chatRoomId, String userId, boolean isTyping) {
//        chatApi.setTypingStatus(chatRoomId, userId, isTyping).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {}
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                callBackChat.error("Failed to set typing status: " + t.getMessage());
//            }
//        });
//    }
//
//    public void fetchTypingStatus(String chatRoomId) {
//        chatApi.getTypingStatus(chatRoomId).enqueue(new Callback<Map<String, Boolean>>() {
//            @Override
//            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    callBackChat.typingStatusUpdated(response.body());
//                } else {
//                    callBackChat.error("Typing status error: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
//                callBackChat.error("Typing status failed: " + t.getMessage());
//            }
//        });
//    }
//
//    public void createChatRoom(String title, String creatorId) {
//        chatApi.createChatRoom(title, creatorId).enqueue(createMapStringCallback(callBackChat::chatRoomCreated));
//    }
//
//    public void addParticipants(String roomId, List<String> userIds) {
//        chatApi.addParticipants(roomId, userIds).enqueue(createMapStringCallback(response -> {}));
//    }
//
//    public void getUserChatRooms(String userId) {
//        chatApi.getUserChatRooms(userId).enqueue(new Callback<List<ChatRoomInfo>>() {
//            @Override
//            public void onResponse(Call<List<ChatRoomInfo>> call, Response<List<ChatRoomInfo>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    callBackChat.userChatRoomsFetched(response.body());
//                } else {
//                    callBackChat.error("Failed to fetch chat rooms: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ChatRoomInfo>> call, Throwable t) {
//                callBackChat.error("Error fetching user chat rooms: " + t.getMessage());
//            }
//        });
//    }
//
//    public void getParticipantsInRoom(String roomId) {
//        chatApi.getParticipantsInRoom(roomId).enqueue(new Callback<List<String>>() {
//            @Override
//            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    callBackChat.participantsFetched(response.body());
//                } else {
//                    callBackChat.error("Failed to fetch participants: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<String>> call, Throwable t) {
//                callBackChat.error("Error fetching participants: " + t.getMessage());
//            }
//        });
//    }
//
//    private <T> Callback<List<T>> createListCallback(OnSuccess<List<T>> onSuccess) {
//        return new Callback<List<T>>() {
//            @Override
//            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
//                if (response.isSuccessful()) {
//                    Log.d("ChatController", "onResponse 123 ");
//                    onSuccess.run(response.body());
//                } else {
//                    callBackChat.error("List callback error: " + getError(response));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<T>> call, Throwable t) {
//                callBackChat.error("List callback failure: 123" + t.getMessage());
//            }
//        };
//    }
//
//    private <T> Callback<Map<String, T>> createMapCallback(OnSuccess<Map<String, T>> onSuccess) {
//        return new Callback<Map<String, T>>() {
//            @Override
//            public void onResponse(Call<Map<String, T>> call, Response<Map<String, T>> response) {
//                if (response.isSuccessful()) {
//                    onSuccess.run(response.body());
//                } else {
//                    callBackChat.error("Map callback error: " + getError(response));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Map<String, T>> call, Throwable t) {
//                callBackChat.error("Map callback failure: " + t.getMessage());
//            }
//        };
//    }
//
//    private Callback<Map<String, String>> createMapStringCallback(OnSuccess<Map<String, String>> onSuccess) {
//        return new Callback<Map<String, String>>() {
//            @Override
//            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
//                if (response.isSuccessful()) {
//                    onSuccess.run(response.body());
//                } else {
//                    callBackChat.error("Map<String, String> error: " + getError(response));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Map<String, String>> call, Throwable t) {
//                callBackChat.error("Map<String, String> failure: " + t.getMessage());
//            }
//        };
//    }
//
//    private String getError(Response<?> response) {
//        try {
//            return response.errorBody() != null ? response.errorBody().string() : "Unknown error";
//        } catch (IOException e) {
//            return "Error reading errorBody: " + e.getMessage();
//        }
//    }
//
//    public interface CallBack_Chat {
//        void success(List<Message> messages);
//        void messageSent(Map<String, Message> response);
//        void messageDeleted(Map<String, String> response);
//        void messageUpdated(Map<String, Message> response);
//        void error(String error);
//        void typingStatusUpdated(Map<String, Boolean> typingUsers);
//        void chatRoomCreated(Map<String, String> response);
//        void userChatRoomsFetched(List<ChatRoomInfo> chatRooms);
//        void participantsFetched(List<String> participants);
//    }
//
//    private interface OnSuccess<T> {
//        void run(T result);
//    }
//}
