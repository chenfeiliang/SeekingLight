package seekLight.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import seekLight.dao.PluginTransInfoDao;
import seekLight.entity.PluginTransInfo;
import seekLight.mapper.PluginTransInfoMapper;

import java.sql.Wrapper;
import java.util.List;

@Service
@Slf4j
public class PluginTransInfoDaoImpl extends ServiceImpl<PluginTransInfoMapper, PluginTransInfo> implements PluginTransInfoDao {
    public List<PluginTransInfo> listOrderTime() {
        QueryWrapper<PluginTransInfo> wapper = new QueryWrapper<>();
        wapper.orderByDesc("update_time");
        wapper.last("LIMIT 100");
        return list(wapper);
    }
}
