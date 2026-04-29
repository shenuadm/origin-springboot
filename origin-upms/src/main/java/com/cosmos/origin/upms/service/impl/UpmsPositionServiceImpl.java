package com.cosmos.origin.upms.service.impl;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsPositionDO;
import com.cosmos.origin.upms.domain.mapper.UpmsPositionMapper;
import com.cosmos.origin.upms.service.UpmsPositionService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * UPMS 岗位服务实现
 */
@Service
@RequiredArgsConstructor
public class UpmsPositionServiceImpl implements UpmsPositionService {

    private final UpmsPositionMapper positionMapper;

    @Override
    public PageResponse<?> page(Long current, Long size) {
        Page<UpmsPositionDO> page = new Page<>(current, size);
        Page<UpmsPositionDO> result = positionMapper.paginate(page,
                QueryWrapper.create()
                        .eq(UpmsPositionDO::getIsDeleted, DeletedEnum.NO.getValue())
                        .orderBy(UpmsPositionDO::getSort, true));
        return PageResponse.success(result, result.getRecords());
    }

    @Override
    public Response<?> add(UpmsPositionDO position) {
        positionMapper.insert(position);
        return Response.success();
    }

    @Override
    public Response<?> update(UpmsPositionDO position) {
        positionMapper.update(position);
        return Response.success();
    }

    @Override
    public Response<?> delete(Long id) {
        positionMapper.deleteById(id);
        return Response.success();
    }
}
