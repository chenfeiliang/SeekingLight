package seekLight.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
@Slf4j
public class SeekFileUtils {
    public static void writeLines(File file, List<String> list,boolean append){
        try {
            FileUtils.writeLines(file, list,append);
        }catch (Exception ex){
            log.error("error==>",ex);
        }
    }
}
