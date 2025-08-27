package com.eightbitlab.blurview_sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurTarget;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.BlurViewFacade;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageView sendButton;
    private ImageView backButton;
    private BlurView chatBlurView;
    private BlurTarget chatTarget;
    private BlurViewFacade blurViewFacade;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupBlurView();
        setupRecyclerView();
        setupClickListeners();
        loadSampleMessages();
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.recyclerview1);
        messageEditText = findViewById(R.id.edittext1);
        sendButton = findViewById(R.id.imageview4);
        backButton = findViewById(R.id.imageview1);
        chatBlurView = findViewById(R.id.chatBlurView);
        chatTarget = findViewById(R.id.chatTarget);
    }

    private void setupBlurView() {
        View decorView = getWindow().getDecorView();
        Drawable windowBackground = decorView.getBackground();
        
        blurViewFacade = chatBlurView.setupWith(chatTarget, new RenderScriptBlur(this), 4f, false)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(15f)
                .setBlurAutoUpdate(true);
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messages);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
        
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        
        // Add click listener for attachment button
        ImageView attachmentButton = findViewById(R.id.imageview5);
        attachmentButton.setOnClickListener(v -> {
            Toast.makeText(this, "مميزات BlurView: تأثيرات ضبابية متقدمة!", Toast.LENGTH_SHORT).show();
            
            // Demonstrate BlurView features
            animateBlurRadius();
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            ChatMessage userMessage = new ChatMessage(messageText, true);
            chatAdapter.addMessage(userMessage);
            messageEditText.setText("");
            
            // Scroll to bottom
            chatRecyclerView.scrollToPosition(messages.size() - 1);
            
            // Simulate response with BlurView features demo
            simulateResponse();
        }
    }

    private void simulateResponse() {
        // Simulate a delay for response
        chatRecyclerView.postDelayed(() -> {
            String[] responses = {
                "مرحباً! هذا تطبيق BlurView-Plus الرائع 🎉",
                "تستطيع رؤية التأثيرات الضبابية المذهلة في الخلفية ✨",
                "المكتبة تدعم الرسوم المتحركة الناعمة والألوان الديناميكية 🌈",
                "جرب الضغط على زر الإرفاق لرؤية المزيد من المميزات!"
            };
            
            String response = responses[(int) (Math.random() * responses.length)];
            ChatMessage botMessage = new ChatMessage(response, false);
            chatAdapter.addMessage(botMessage);
            chatRecyclerView.scrollToPosition(messages.size() - 1);
        }, 1000);
    }

    private void loadSampleMessages() {
        messages.add(new ChatMessage("مرحباً! أهلاً بك في تطبيق BlurView-Plus", false));
        messages.add(new ChatMessage("هذا النشاط يعرض مميزات المكتبة في بيئة شات عصرية", false));
        messages.add(new ChatMessage("يمكنك رؤية التأثيرات الضبابية الجميلة في الخلفية", false));
        chatAdapter.notifyDataSetChanged();
    }

    private void animateBlurRadius() {
        // Demonstrate dynamic blur radius animation using the facade
        float currentRadius = blurViewFacade.getBlurRadius();
        float targetRadius = currentRadius == 15f ? 25f : 15f;
        
        // Simple blur animation
        blurViewFacade.setBlurRadius(targetRadius);
        
        Toast.makeText(this, "تم تغيير شدة الضبابية إلى " + targetRadius, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}