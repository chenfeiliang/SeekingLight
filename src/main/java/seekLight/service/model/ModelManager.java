package seekLight.service.model;

public class ModelManager {
    public static BaseModelChatClient getModel(String type){
        if(type.equals("ollama")){
            return new OllamaClient();
        }
        return null;
    }

    public static BaseModelChatClient getModel(String type,String role){
        if(type.equals("ollama")){
            return new OllamaClient(role);
        }else if(type.equals("DeepSeek")){
            return new DeepSeekClient(role);
        }else if(type.equals("doubao")){
            return new DoubaoClient(role);
        }
        return null;
    }

    public static BaseModelChatClient getModel(String type,String role,String model){
        if(type.equals("ollama")){
            return new OllamaClient(role,model);
        }else if(type.equals("DeepSeek")){
            return new DeepSeekClient(role);
        }else if(type.equals("doubao")){
            return new DoubaoClient(role);
        }
        return null;
    }
}
