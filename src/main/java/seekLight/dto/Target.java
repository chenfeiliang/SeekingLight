package seekLight.dto;

import lombok.Data;

import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class Target {
    private String id;

    private String title;

    private String type;

    private Question question;

    private String content;

    private String excerpt;

    private String url;
}