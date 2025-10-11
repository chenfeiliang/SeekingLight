package seekLight.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import seekLight.workflow.flow.Flow;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2025-10-07
 */
@TableName("work_flow")
@Data
@Slf4j
public class WorkFlow implements Flow {
    @TableId(type = IdType.NONE	)
    private String busiSno;

    private String relyBusiSno;

    private String step;

    private String transStatus = "S";

    private String transType = "flow";

    private String route;

    private Date transTime;

    private Date createTime;

    private Date updateTime;
}

