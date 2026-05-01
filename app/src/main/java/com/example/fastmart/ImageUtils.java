package com.example.fastmart;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {

    private static final String TAG = "ImageUtils";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface ValidationCallback {
        void onResult(boolean isValid);
    }

    /**
     * Extracts File ID from various Google Drive URL formats.
     */
    public static String extractFileId(String url) {
        if (url == null || url.isEmpty()) return "";

        // Pattern 1: /d/[FILE_ID]/
        Pattern pattern1 = Pattern.compile("/d/([a-zA-Z0-9_-]{25,})");
        Matcher matcher1 = pattern1.matcher(url);
        if (matcher1.find()) return matcher1.group(1);

        // Pattern 2: id=[FILE_ID]
        Pattern pattern2 = Pattern.compile("id=([a-zA-Z0-9_-]{25,})");
        Matcher matcher2 = pattern2.matcher(url);
        if (matcher2.find()) return matcher2.group(1);

        // Pattern 3: Fallback for raw 33-char IDs
        Pattern pattern3 = Pattern.compile("([a-zA-Z0-9_-]{33})");
        Matcher matcher3 = pattern3.matcher(url);
        if (matcher3.find()) return matcher3.group(1);

        return "";
    }

    /**
     * Returns a working Drive URL based on attempt index.
     * 0: lh3 (Best for Glide)
     * 1: uc?export=view (Classic)
     * 2: thumbnail (Reliable fallback)
     */
    public static String getDriveFormat(String fileId, int attempt) {
        if (fileId == null || fileId.isEmpty()) return "";
        
        switch (attempt) {
            case 0: return "https://lh3.googleusercontent.com/d/" + fileId + "=s0";
            case 1: return "https://drive.google.com/uc?export=view&id=" + fileId;
            case 2: return "https://drive.google.com/thumbnail?id=" + fileId + "&sz=w1000";
            default: return "";
        }
    }

    /**
     * Validates if a URL is accessible (returns 200 OK) without downloading full content.
     */
    public static void validateUrl(String urlString, ValidationCallback callback) {
        executor.execute(() -> {
            boolean isValid = false;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                int responseCode = connection.getResponseCode();
                isValid = (responseCode == HttpURLConnection.HTTP_OK);
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Validation failed for: " + urlString, e);
            }

            final boolean result = isValid;
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(result));
        });
    }
}
