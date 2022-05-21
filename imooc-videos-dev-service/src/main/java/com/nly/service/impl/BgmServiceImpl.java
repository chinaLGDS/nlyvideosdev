package com.nly.service.impl;



import com.nly.mapper.BgmMapper;
import com.nly.pojo.Bgm;
import com.nly.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nly
 * @since 2021-05-15
 */
@Service
public class BgmServiceImpl  implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;


    /**
     * 查询背景音乐列表
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm queryBgmById(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}
