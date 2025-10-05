package seekLight.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin(origins = "*")
@Controller
public class MainController {
    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }
}
