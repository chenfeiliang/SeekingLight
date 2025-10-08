package seekLight.dao.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import seekLight.dao.RawFlowInfoDao;
import seekLight.entity.RawFlowInfo;
import seekLight.mapper.RawFlowInfoMapper;
@Service
@Slf4j

public class RawFlowInfoDaoImpl extends MppServiceImpl<RawFlowInfoMapper, RawFlowInfo> implements RawFlowInfoDao {

}
