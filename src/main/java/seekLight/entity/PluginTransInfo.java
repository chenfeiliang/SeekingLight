package seekLight.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2025-10-07
 */
@TableName("plugin_trans_info")
@Slf4j
@Data
public class PluginTransInfo {

    @TableId(type = IdType.NONE	)
    private String busiSno;

    private String content;

    private Date createTime;

    private Date updateTime;

}
