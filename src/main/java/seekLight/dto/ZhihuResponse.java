package seekLight.dto;

import lombok.Data;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class ZhihuResponse {
    private List<FeedItem> data;

    private Paging paging;
}