package com.eightbitlab.blurview_sample;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurTarget;
import eightbitlab.com.blurview.BlurView;


public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageView sendButton;
    private ImageView backButton;
    private ImageView attachmentButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private BlurView topBlurView;
    private BlurView bottomBlurView;
    private BlurTarget chatTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupBlurViews();
        setupRecyclerView();
        setupClickListeners();
        loadSampleMessages();
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.recyclerview1);
        messageEditText = findViewById(R.id.edittext1);
        sendButton = findViewById(R.id.imageview4);
        backButton = findViewById(R.id.imageview1);
        attachmentButton = findViewById(R.id.imageview5);
        topBlurView = findViewById(R.id.topBlurView);
        bottomBlurView = findViewById(R.id.bottomBlurView);
        chatTarget = findViewById(R.id.chatTarget);
    }

    private void setupBlurViews() {
        final float radius = 25f;
        final Drawable windowBackground = getWindow().getDecorView().getBackground();
        
        // Setup top blur view (toolbar)
        topBlurView.setAnimationsEnabled(true)
                .setPerformanceOptimizationEnabled(true)
                .setDynamicColorsEnabled(false)
                .setupWith(chatTarget)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
        
        // Setup bottom blur view (input area)  
        bottomBlurView.setAnimationsEnabled(true)
                .setPerformanceOptimizationEnabled(true)
                .setDynamicColorsEnabled(false)
                .setupWith(chatTarget)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
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
        
        // Add click listener for attachment button with iOS-style popup
        attachmentButton.setOnClickListener(v -> {
            showAttachmentOptionsPopup();
        });
    }

    private void showAttachmentOptionsPopup() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(createAttachmentPopupView(dialog));
        
        // Set up dialog window parameters for iOS-style appearance
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = 100; // Margin from bottom
            window.setAttributes(params);
        }
        
        dialog.show();
    }

    private View createAttachmentPopupView(Dialog dialog) {
        // Create the main blur view container
        BlurView popupBlurView = new BlurView(this);
        popupBlurView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT));
        
        // Setup blur effect for popup
        popupBlurView.setAnimationsEnabled(true)
                .setPerformanceOptimizationEnabled(true)
                .setDynamicColorsEnabled(false)
                .setupWith(chatTarget)
                .setFrameClearDrawable(getWindow().getDecorView().getBackground())
                .setBlurRadius(20f);
        
        // Create content layout
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 24, 32, 32);
        
        // Create option buttons
        String[] options = {"📸 كاميرا", "🖼️ صور", "📁 ملفات", "📍 موقع"};
        String[] descriptions = {"التقط صورة جديدة", "اختر من المعرض", "إرسال ملف", "شارك موقعك"};
        
        for (int i = 0; i < options.length; i++) {
            LinearLayout optionLayout = createOptionItem(options[i], descriptions[i], dialog);
            contentLayout.addView(optionLayout);
            
            // Add separator (except for last item)
            if (i < options.length - 1) {
                View separator = new View(this);
                separator.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
                separator.setBackgroundColor(0x20000000);
                contentLayout.addView(separator);
            }
        }
        
        // Add cancel button
        LinearLayout cancelLayout = createCancelButton(dialog);
        contentLayout.addView(cancelLayout);
        
        popupBlurView.addView(contentLayout);
        return popupBlurView;
    }

    private LinearLayout createOptionItem(String title, String description, Dialog dialog) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 20, 16, 20);
        layout.setBackgroundResource(android.R.drawable.list_selector_background);
        
        layout.setOnClickListener(v -> {
            Toast.makeText(this, "تم اختيار: " + title, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(18);
        titleText.setTextColor(0xFF212020);
        titleText.setGravity(Gravity.CENTER);
        
        TextView descText = new TextView(this);
        descText.setText(description);
        descText.setTextSize(14);
        descText.setTextColor(0xFF666666);
        descText.setGravity(Gravity.CENTER);
        
        layout.addView(titleText);
        layout.addView(descText);
        
        return layout;
    }

    private LinearLayout createCancelButton(Dialog dialog) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 24, 16, 20);
        layout.setBackgroundResource(android.R.drawable.list_selector_background);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 0);
        layout.setLayoutParams(params);
        
        layout.setOnClickListener(v -> dialog.dismiss());
        
        TextView cancelText = new TextView(this);
        cancelText.setText("إلغاء");
        cancelText.setTextSize(18);
        cancelText.setTextColor(0xFFFF3B30); // iOS red color
        cancelText.setGravity(Gravity.CENTER);
        
        layout.addView(cancelText);
        return layout;
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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}