import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/seek_light?serverTimezone=Asia/Shanghai", "root", "123456")
                .globalConfig(builder -> builder.outputDir(System.getProperty("user.dir")))
                .packageConfig(builder -> builder.parent("seekLight"))
                .strategyConfig(builder -> {
                    builder.addInclude("plugin_info")
                            .serviceBuilder()
                            .formatServiceFileName("%sDao")
                            .formatServiceImplFileName("%sDaoImpl")
                            ;
                })
                .execute();
    }
}