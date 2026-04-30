package com.example.fastmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private EditText etMessage;
    private ImageButton btnSend;

    private String myUid, receiverId, receiverName;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        SharedPreferences prefs = getSharedPreferences("fastmart_prefs", Context.MODE_PRIVATE);
        myUid = prefs.getString("uid", "");

        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");

        if (receiverId == null || receiverId.isEmpty()) {
            Toast.makeText(this, "Invalid chat target", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(receiverName != null ? receiverName : "Chat");

        // Build chatId: smaller uid first (matches Firebase key format)
        String chatId = myUid.compareTo(receiverId) < 0
                ? myUid + "_" + receiverId
                : receiverId + "_" + myUid;

        // ✅ FIX: Firebase structure is chats/{chatId}/messages/{msgId}
        chatRef = FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId).child("messages");

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                if (msg != null) addMessageBubble(msg);
            }

            @Override public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(DataSnapshot snapshot) {}
            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessageBubble(Message msg) {
        boolean isMine = myUid.equals(msg.senderId);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(8, 4, 8, 4);
        row.setLayoutParams(rowParams);
        row.setGravity(isMine ? Gravity.END : Gravity.START);

        CardView card = new CardView(this);
        card.setRadius(24f);
        card.setCardElevation(2f);
        card.setCardBackgroundColor(ContextCompat.getColor(this,
                isMine ? R.color.bubble_mine : R.color.bubble_other));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(isMine ? 80 : 0, 0, isMine ? 0 : 80, 0);
        card.setLayoutParams(cardParams);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(28, 14, 28, 14);

        TextView tvText = new TextView(this);
        // ✅ FIX: Firebase uses "message" field, not "text"
        tvText.setText(msg.message);
        tvText.setTextColor(ContextCompat.getColor(this,
                isMine ? R.color.white : R.color.text_primary));
        tvText.setTextSize(14f);

        TextView tvTime = new TextView(this);
        tvTime.setText(msg.timestamp);
        tvTime.setTextColor(ContextCompat.getColor(this,
                isMine ? R.color.lavender_light : R.color.text_hint));
        tvTime.setTextSize(10f);
        tvTime.setGravity(Gravity.END);

        inner.addView(tvText);
        inner.addView(tvTime);
        card.addView(inner);
        row.addView(card);
        chatContainer.addView(row);

        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String messageId = chatRef.push().getKey();
        // ✅ FIX: Use "message" field name to match Firebase schema
        Message message = new Message(messageId, myUid, receiverId, text, timestamp);
        message.read = false;

        chatRef.child(messageId).setValue(message)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to send: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        etMessage.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}