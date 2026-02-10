package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.admin.domain.dos.RoleDO;
import com.cosmos.origin.admin.domain.mapper.RoleMapper;
import com.cosmos.origin.admin.model.vo.role.AddRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.DeleteRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.FindRolePageListReqVO;
import com.cosmos.origin.admin.model.vo.role.UpdateRoleReqVO;
import com.cosmos.origin.admin.service.AdminRoleService;
import com.cosmos.origin.common.model.vo.SelectRspVO;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {

    private final RoleMapper roleMapper;

    /**
     * 角色分页数据获取
     *
     * @return {@link PageResponse }<{@link ? }> 角色分页数据
     */
    @Override
    public PageResponse<?> findRolePageList(FindRolePageListReqVO findRolePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findRolePageListReqVO.getCurrent();
        Long size = findRolePageListReqVO.getSize();

        // 执行分页查询
        Page<RoleDO> page = new Page<>(current, size);
        Page<RoleDO> roleDOPage = roleMapper.paginate(page, QueryWrapper.create()
                .like(RoleDO::getRoleName, findRolePageListReqVO.getRoleName()));
        return PageResponse.success(roleDOPage, roleDOPage.getRecords());
    }

    /**
     * 添加角色
     *
     * @param addRoleReqVO 添加角色请求参数
     * @return {@link Response }<{@link ? }> 添加结果
     */
    @Override
    public Response<?> add(AddRoleReqVO addRoleReqVO) {
        int insert = roleMapper.insert(RoleDO.builder()
                .roleName(addRoleReqVO.getRoleName())
                .roleKey(addRoleReqVO.getRoleKey())
                .build());
        return insert == 1 ? Response.success() : Response.fail();
    }

    /**
     * 更新角色
     *
     * @param updateRoleReqVO 更新角色请求参数
     * @return {@link Response }<{@link ? }> 更新结果
     */
    @Override
    public Response<?> update(UpdateRoleReqVO updateRoleReqVO) {
        int update = roleMapper.update(RoleDO.builder()
                .id(updateRoleReqVO.getId())
                .roleName(updateRoleReqVO.getRoleName())
                .roleKey(updateRoleReqVO.getRoleKey())
                .build());
        return update == 1 ? Response.success() : Response.fail();
    }

    /**
     * 删除角色
     *
     * @param deleteRoleReqVO 删除角色请求参数
     * @return {@link Response }<{@link ? }> 删除结果
     */
    @Override
    public Response<?> delete(DeleteRoleReqVO deleteRoleReqVO) {
        int delete = roleMapper.deleteById(deleteRoleReqVO.getId());
        return delete == 1 ? Response.success() : Response.fail();
    }

    @Override
    public Response<?> findRoleSelectList() {
        // 查询所有角色
        List<RoleDO> roleDOS = roleMapper.selectListByQuery(QueryWrapper.create());

        // DO 转 VO
        List<SelectRspVO> selectRspVOS = null;
        // 如果角色数据不为空
        if (!CollectionUtils.isEmpty(roleDOS)) {
            // 将角色 ID 作为 Value 值，将角色名称作为 label 展示
            selectRspVOS = roleDOS.stream()
                    .map(roleDO -> SelectRspVO.builder()
                            .label(roleDO.getRoleName())
                            .value(roleDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(selectRspVOS);
    }
}
