package com.cosmos.origin.upms.service.impl;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.*;
import com.cosmos.origin.upms.domain.mapper.*;
import com.cosmos.origin.upms.service.UpmsUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UPMS 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UpmsUserServiceImpl implements UpmsUserService {

    private final UpmsUserMapper userMapper;
    private final UpmsRoleMapper roleMapper;
    private final UpmsUserRoleRelMapper userRoleRelMapper;
    private final UpmsUserDepartmentRelMapper userDepartmentRelMapper;
    private final UpmsUserPositionRelMapper userPositionRelMapper;

    @Override
    public Response<?> findByUsername(String username) {
        UpmsUserDO user = userMapper.findByUsername(username);
        if (user == null) {
            return Response.fail("用户不存在");
        }

        List<String> roleKeys = findRoleKeysByUserId(user.getId());
        List<Long> deptIds = findDepartmentIdsByUserId(user.getId());
        List<Long> posIds = findPositionIdsByUserId(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("status", user.getStatus());
        result.put("roles", roleKeys);
        result.put("departments", deptIds);
        result.put("positions", posIds);

        return Response.success(result);
    }

    @Override
    public PageResponse<?> page(Long current, Long size, String nickname) {
        Page<UpmsUserDO> page = new Page<>(current, size);
        QueryWrapper qw = QueryWrapper.create()
                .eq(UpmsUserDO::getIsDeleted, DeletedEnum.NO.getValue())
                .orderBy(UpmsUserDO::getCreateTime, false);
        if (Objects.nonNull(nickname) && !nickname.isBlank()) {
            qw.like(UpmsUserDO::getNickname, nickname);
        }
        Page<UpmsUserDO> result = userMapper.paginate(page, qw);
        return PageResponse.success(result, result.getRecords());
    }

    @Override
    public List<String> findRoleKeysByUserId(Long userId) {
        List<UpmsRoleDO> roles = roleMapper.selectListByQuery(
                QueryWrapper.create()
                        .select(UpmsRoleDO::getRoleKey)
                        .from(UpmsRoleDO.class)
                        .innerJoin(UpmsUserRoleRelDO.class).on(UpmsRoleDO::getId, UpmsUserRoleRelDO::getRoleId)
                        .where(UpmsUserRoleRelDO::getUserId).eq(userId)
                        .eq(UpmsRoleDO::getIsDeleted, DeletedEnum.NO.getValue()));
        return roles.stream().map(UpmsRoleDO::getRoleKey).collect(Collectors.toList());
    }

    @Override
    public List<Long> findDepartmentIdsByUserId(Long userId) {
        List<UpmsUserDepartmentRelDO> list = userDepartmentRelMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq(UpmsUserDepartmentRelDO::getUserId, userId)
                        .eq(UpmsUserDepartmentRelDO::getIsDeleted, DeletedEnum.NO.getValue()));
        return list.stream().map(UpmsUserDepartmentRelDO::getDepartmentId).collect(Collectors.toList());
    }

    @Override
    public List<Long> findPositionIdsByUserId(Long userId) {
        List<UpmsUserPositionRelDO> list = userPositionRelMapper.selectListByQuery(
                QueryWrapper.create()
                        .eq(UpmsUserPositionRelDO::getUserId, userId)
                        .eq(UpmsUserPositionRelDO::getIsDeleted, DeletedEnum.NO.getValue()));
        return list.stream().map(UpmsUserPositionRelDO::getPositionId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> assignRoles(Long userId, List<Long> roleIds) {
        userRoleRelMapper.deleteByQuery(
                QueryWrapper.create().eq(UpmsUserRoleRelDO::getUserId, userId));
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                userRoleRelMapper.insert(UpmsUserRoleRelDO.builder()
                        .userId(userId)
                        .roleId(roleId)
                        .build());
            }
        }
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> assignDepartments(Long userId, List<Long> departmentIds, Long primaryDepartmentId) {
        userDepartmentRelMapper.deleteByQuery(
                QueryWrapper.create().eq(UpmsUserDepartmentRelDO::getUserId, userId));
        if (!CollectionUtils.isEmpty(departmentIds)) {
            for (Long deptId : departmentIds) {
                userDepartmentRelMapper.insert(UpmsUserDepartmentRelDO.builder()
                        .userId(userId)
                        .departmentId(deptId)
                        .isPrimary(deptId.equals(primaryDepartmentId) ? (byte) 1 : (byte) 0)
                        .build());
            }
        }
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> assignPositions(Long userId, List<Long> positionIds) {
        userPositionRelMapper.deleteByQuery(
                QueryWrapper.create().eq(UpmsUserPositionRelDO::getUserId, userId));
        if (!CollectionUtils.isEmpty(positionIds)) {
            for (Long posId : positionIds) {
                userPositionRelMapper.insert(UpmsUserPositionRelDO.builder()
                        .userId(userId)
                        .positionId(posId)
                        .build());
            }
        }
        return Response.success();
    }
}
