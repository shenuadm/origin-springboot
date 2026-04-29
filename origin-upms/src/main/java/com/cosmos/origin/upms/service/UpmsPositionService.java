package com.cosmos.origin.upms.service;

import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsPositionDO;

/**
 * UPMS 岗位服务接口
 */
public interface UpmsPositionService {

    PageResponse<?> page(Long current, Long size);

    Response<?> add(UpmsPositionDO position);

    Response<?> update(UpmsPositionDO position);

    Response<?> delete(Long id);
}
