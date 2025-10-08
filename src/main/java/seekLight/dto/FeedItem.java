package seekLight.dto;


import lombok.Data;

import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class FeedItem {

    private Target target;

    private String type;
}