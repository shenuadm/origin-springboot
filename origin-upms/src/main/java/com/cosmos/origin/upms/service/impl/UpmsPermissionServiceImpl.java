package com.cosmos.origin.upms.service.impl;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsPermissionDO;
import com.cosmos.origin.upms.domain.mapper.UpmsPermissionMapper;
import com.cosmos.origin.upms.service.UpmsPermissionService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UPMS 权限/菜单服务实现
 */
@Service
@RequiredArgsConstructor
public class UpmsPermissionServiceImpl implements UpmsPermissionService {

    private final UpmsPermissionMapper permissionMapper;

    @Override
    public Response<List<UpmsPermissionDO>> tree() {
        List<UpmsPermissionDO> all = permissionMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq(UpmsPermissionDO::getIsDeleted, DeletedEnum.NO.getValue())
                        .orderBy(UpmsPermissionDO::getSort, true));

        if (CollectionUtils.isEmpty(all)) {
            return Response.success(List.of());
        }

        Map<Long, List<UpmsPermissionDO>> parentMap = all.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));

        List<UpmsPermissionDO> tree = buildTree(parentMap, 0L);
        return Response.success(tree);
    }

    private List<UpmsPermissionDO> buildTree(Map<Long, List<UpmsPermissionDO>> parentMap, Long parentId) {
        List<UpmsPermissionDO> children = parentMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        for (UpmsPermissionDO child : children) {
            child.setChildren(buildTree(parentMap, child.getId()));
        }
        return children;
    }

    @Override
    public Response<?> add(UpmsPermissionDO permission) {
        permissionMapper.insert(permission);
        return Response.success();
    }

    @Override
    public Response<?> update(UpmsPermissionDO permission) {
        permissionMapper.update(permission);
        return Response.success();
    }

    @Override
    public Response<?> delete(Long id) {
        long childCount = permissionMapper.selectCountByQuery(
                QueryWrapper.create()
                        .eq(UpmsPermissionDO::getParentId, id)
                        .eq(UpmsPermissionDO::getIsDeleted, DeletedEnum.NO.getValue()));
        if (childCount > 0) {
            return Response.fail("该权限下存在子节点，无法删除");
        }
        permissionMapper.deleteById(id);
        return Response.success();
    }
}
