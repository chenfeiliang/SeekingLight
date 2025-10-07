package seekLight.dto;

import lombok.Data;

@Data
public class Target {
    private String id;

    private String title;

    private String type;

    private Question question;

    private String content;

    private String excerpt;

    private String url;
}