package seekLight.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2025-10-07
 */
@TableName("raw_flow_info")
@Data
@Slf4j
public class RawFlowInfo  {

    @MppMultiId
    @TableField("busi_sno")
    private String busiSno;

    @MppMultiId
    @TableField("type   ")
    private String type;

    @TableField("input_content")
    private String inputContent;

    @TableField("output_content")
    private String outputContent;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
