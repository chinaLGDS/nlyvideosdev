package com.nly.service;


import com.nly.pojo.Bgm;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nly
 *
 */
public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    public List<Bgm> queryBgmList();

    /**
     * 根据id查询bgm信息
     * @param bgmId
     * @return
     */
    public Bgm queryBgmById(String bgmId);

}
