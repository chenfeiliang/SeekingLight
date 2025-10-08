package seekLight.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2025-10-08
 */
@TableName("plugin_info")
@Data
public class PluginInfo {
    @TableId(type = IdType.NONE	)
    private String pluginId;

    private String input;

    private String output;

    private Date createTime;

    private Date updateTime;
}
