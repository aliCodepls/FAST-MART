package com.example.fastmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<SellerChatFragment.ChatSummary> chats;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(SellerChatFragment.ChatSummary chat);
    }

    public ChatListAdapter(List<SellerChatFragment.ChatSummary> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerChatFragment.ChatSummary chat = chats.get(position);
        holder.tvName.setText(chat.otherName);
        holder.tvLastMsg.setText(chat.lastMessage);
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMsg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOtherName);
            tvLastMsg = itemView.findViewById(R.id.tvLastMessage);
        }
    }
}
