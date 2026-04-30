package com.example.fastmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellerChatFragment extends Fragment {

    private RecyclerView rvChats;
    private ChatListAdapter adapter;
    private List<ChatSummary> chatList = new ArrayList<>();
    private String myUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myUid = FirebaseAuth.getInstance().getUid();
        if (myUid == null) return;

        rvChats = view.findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(chatList, chatSummary -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("receiverId", chatSummary.otherUid);
            intent.putExtra("receiverName", chatSummary.otherName);
            startActivity(intent);
        });
        rvChats.setAdapter(adapter);

        loadChats();
    }

    private void loadChats() {
        FirebaseDatabase.getInstance().getReference("chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        chatList.clear();
                        for (DataSnapshot chatSnap : snapshot.getChildren()) {
                            String chatId = chatSnap.getKey();
                            if (chatId != null && chatId.contains(myUid)) {
                                String otherUid = chatId.replace(myUid, "").replace("_", "");
                                fetchOtherUserDetails(otherUid, chatSnap.child("messages"));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void fetchOtherUserDetails(String otherUid, DataSnapshot messagesSnap) {
        FirebaseDatabase.getInstance().getReference("users").child(otherUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        String name = (user != null) ? user.name : "Customer";
                        
                        // Get last message
                        String lastMsg = "No messages";
                        for (DataSnapshot m : messagesSnap.getChildren()) {
                            Message msg = m.getValue(Message.class);
                            if (msg != null) lastMsg = msg.message;
                        }

                        ChatSummary summary = new ChatSummary(otherUid, name, lastMsg);
                        
                        // Avoid duplicates
                        boolean exists = false;
                        for (int i = 0; i < chatList.size(); i++) {
                            if (chatList.get(i).otherUid.equals(otherUid)) {
                                chatList.set(i, summary);
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) chatList.add(summary);
                        
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void updateEmptyState() {
        View view = getView();
        if (view != null) {
            View emptyState = view.findViewById(R.id.llEmptyState);
            if (emptyState != null) {
                emptyState.setVisibility(chatList.isEmpty() ? View.VISIBLE : View.GONE);
                rvChats.setVisibility(chatList.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
    }

    public static class ChatSummary {
        public String otherUid;
        public String otherName;
        public String lastMessage;

        public ChatSummary(String otherUid, String otherName, String lastMessage) {
            this.otherUid = otherUid;
            this.otherName = otherName;
            this.lastMessage = lastMessage;
        }
    }
}
