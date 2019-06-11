package org.qhn.coexist.shirojwtqhn.service.impl;

import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDO;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnUserDOMapper;
import org.qhn.coexist.shirojwtqhn.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 18:36
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    private QhnUserDOMapper qhnUserDOMapper;

    public UserServiceImpl(
        QhnUserDOMapper qhnUserDOMapper) {
        this.qhnUserDOMapper = qhnUserDOMapper;
    }

    @Override
    public QhnUserDO getUserDetailById(Long id) {
        return qhnUserDOMapper.selectByPrimaryKey(id);
    }
}
