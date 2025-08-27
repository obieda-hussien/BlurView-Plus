package com.eightbitlab.blurview_sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> messages;
    private Context context;

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.messageText.setText(message.getMessage());
        
        // Style message based on sender
        if (message.isSentByUser()) {
            holder.messageCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            holder.messageText.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.messageCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.messageText.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        MaterialCardView messageCard;

        ChatViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageCard = itemView.findViewById(R.id.messageCard);
        }
    }
}