package seekLight.dto;

import lombok.Data;

import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j
public class Paging {
    private boolean isEnd;

    private String next;
}