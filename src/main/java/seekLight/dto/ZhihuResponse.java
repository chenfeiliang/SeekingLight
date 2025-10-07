package seekLight.dto;

import lombok.Data;

import java.util.List;
@Data
public class ZhihuResponse {
    private List<FeedItem> data;

    private Paging paging;
}