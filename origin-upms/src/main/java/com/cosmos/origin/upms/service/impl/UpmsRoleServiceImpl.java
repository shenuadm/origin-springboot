package com.cosmos.origin.upms.service.impl;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsRoleDO;
import com.cosmos.origin.upms.domain.dos.UpmsRolePermissionRelDO;
import com.cosmos.origin.upms.domain.mapper.UpmsRoleMapper;
import com.cosmos.origin.upms.domain.mapper.UpmsRolePermissionRelMapper;
import com.cosmos.origin.upms.service.UpmsRoleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * UPMS 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class UpmsRoleServiceImpl implements UpmsRoleService {

    private final UpmsRoleMapper roleMapper;
    private final UpmsRolePermissionRelMapper rolePermissionRelMapper;

    @Override
    public PageResponse<?> page(Long current, Long size) {
        Page<UpmsRoleDO> page = new Page<>(current, size);
        Page<UpmsRoleDO> result = roleMapper.paginate(page,
                QueryWrapper.create()
                        .eq(UpmsRoleDO::getIsDeleted, DeletedEnum.NO.getValue())
                        .orderBy(UpmsRoleDO::getSort, true));
        return PageResponse.success(result, result.getRecords());
    }

    @Override
    public Response<?> findSelectList() {
        List<UpmsRoleDO> list = roleMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq(UpmsRoleDO::getIsDeleted, DeletedEnum.NO.getValue())
                        .eq(UpmsRoleDO::getStatus, (byte) 0)
                        .orderBy(UpmsRoleDO::getSort, true));
        return Response.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionRelMapper.deleteByQuery(
                QueryWrapper.create().eq(UpmsRolePermissionRelDO::getRoleId, roleId));

        if (!CollectionUtils.isEmpty(permissionIds)) {
            for (Long permissionId : permissionIds) {
                rolePermissionRelMapper.insert(UpmsRolePermissionRelDO.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build());
            }
        }
        return Response.success();
    }
}
