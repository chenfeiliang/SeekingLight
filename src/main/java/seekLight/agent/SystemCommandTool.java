package seekLight.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemCommandTool implements Tool {

    @Override
    public String getName() {
        return "system_command";
    }

    @Override
    public String getDescription() {
        return "在本地系统执行Shell命令并返回输出结果，可用于操作系统交互（如查看文件、进程），参数为需要执行的完整命令字符串";
    }

    @Override
    public String execute(String args) {
        try {
            Process process = new ProcessBuilder(args.split(" ")).start();

            // 读取标准输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 读取错误输出
            StringBuilder error = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "命令执行成功. Output:\n" + output.toString();
            } else {
                return "命令执行失败， exit code " + exitCode + ". Error:\n" + error.toString();
            }
        } catch (Exception e) {
            return "无法执行命令: " + e.getMessage();
        }
    }
}