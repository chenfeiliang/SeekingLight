package seekLight.agent;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMemory implements Memory {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void addMessage(String role, String content) {
        messages.add(role + ": " + content);
    }

    @Override
    public List<String> getRecentMessages(int limit) {
        int start = Math.max(0, messages.size() - limit);
        return messages.subList(start, messages.size());
    }

    @Override
    public void clear() {
        messages.clear();
    }
}