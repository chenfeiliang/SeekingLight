package seekLight.agent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentTimeTool implements Tool {

    @Override
    public String getName() {
        return "current_time";
    }

    @Override
    public String getDescription() {
        return "Gets the current date and time. No arguments needed.";
    }

    @Override
    public String execute(String args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "当前时间是: " + LocalDateTime.now().format(formatter);
    }
}