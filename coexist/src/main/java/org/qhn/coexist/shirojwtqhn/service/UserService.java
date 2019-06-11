package org.qhn.coexist.shirojwtqhn.service;

import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDO;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 18:36
 * @Description:
 */
public interface UserService {

    QhnUserDO getUserDetailById(Long id);

}
