package com.github.p4535992.gatebasic.object;

import java.nio.charset.StandardCharsets;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
public class MapContent {

    private String content;

    public MapContent(){}

    public MapContent(String content){
        this.content = cleanText(encodingToUTF8(content));
    }

    //GETTER AND SETTER

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = cleanText(encodingToUTF8(content));
    }

    // OTHER

    private String cleanText(String text){
        return text.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", " ").trim();
    }

    private String encodingToUTF8(String text){
        return new String(text.getBytes(), StandardCharsets.UTF_8);
    }

    public Boolean isEmpty(){
        return (content == null) || content.equals("") || content.isEmpty() || content.trim().isEmpty() ;
    }

    @Override
    public String toString() {
        return content;

    }
}
