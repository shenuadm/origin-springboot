package com.cosmos.origin.upms.service.impl;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsDepartmentDO;
import com.cosmos.origin.upms.domain.mapper.UpmsDepartmentMapper;
import com.cosmos.origin.upms.service.UpmsDepartmentService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UPMS 部门服务实现
 */
@Service
@RequiredArgsConstructor
public class UpmsDepartmentServiceImpl implements UpmsDepartmentService {

    private final UpmsDepartmentMapper departmentMapper;

    @Override
    public Response<List<UpmsDepartmentDO>> tree() {
        List<UpmsDepartmentDO> all = departmentMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq(UpmsDepartmentDO::getIsDeleted, DeletedEnum.NO.getValue())
                        .orderBy(UpmsDepartmentDO::getSort, true));

        if (CollectionUtils.isEmpty(all)) {
            return Response.success(List.of());
        }

        Map<Long, List<UpmsDepartmentDO>> parentMap = all.stream()
                .collect(Collectors.groupingBy(UpmsDepartmentDO::getParentId));

        List<UpmsDepartmentDO> tree = buildTree(parentMap, 0L);
        return Response.success(tree);
    }

    private List<UpmsDepartmentDO> buildTree(Map<Long, List<UpmsDepartmentDO>> parentMap, Long parentId) {
        List<UpmsDepartmentDO> children = parentMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        for (UpmsDepartmentDO child : children) {
            child.setChildren(buildTree(parentMap, child.getId()));
        }
        return children;
    }

    @Override
    public Response<?> add(UpmsDepartmentDO department) {
        departmentMapper.insert(department);
        return Response.success();
    }

    @Override
    public Response<?> update(UpmsDepartmentDO department) {
        departmentMapper.update(department);
        return Response.success();
    }

    @Override
    public Response<?> delete(Long id) {
        long childCount = departmentMapper.selectCountByQuery(
                QueryWrapper.create()
                        .eq(UpmsDepartmentDO::getParentId, id)
                        .eq(UpmsDepartmentDO::getIsDeleted, DeletedEnum.NO.getValue()));
        if (childCount > 0) {
            return Response.fail("该部门下存在子部门，无法删除");
        }
        departmentMapper.deleteById(id);
        return Response.success();
    }
}
