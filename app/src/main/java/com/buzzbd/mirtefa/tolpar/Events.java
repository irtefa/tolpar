package com.buzzbd.mirtefa.tolpar;

/**
 * Created by mirtefa on 4/11/15.
 */
public class Events {
    public static class ClickedStory {
        public String title = null;
        public String content = null;
        public String imgUri = null;
        public String sourceUrl = null;
        public String objectId = null;

        public ClickedStory(String title, String content, String imgUri, String sourceUrl, String objectId) {
            this.title = title;
            this.content = content;
            this.imgUri = imgUri;
            this.sourceUrl = sourceUrl;
            this.objectId = objectId;
        }

    }
}
