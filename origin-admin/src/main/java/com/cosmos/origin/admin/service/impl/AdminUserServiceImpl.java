package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.common.domain.dos.UserDO;
import com.cosmos.origin.common.domain.mapper.UserMapper;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.admin.model.vo.user.*;
import com.cosmos.origin.admin.service.AdminUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 修改密码
     *
     * @param updateAdminUserPasswordReqVO 修改密码请求参数
     * @return 修改密码结果
     */
    @Override
    public Response<?> updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        // 拿到用户名、密码
        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();

        // 加密密码
        String encodePassword = passwordEncoder.encode(password);

        // 更新到数据库
        int count = userMapper.updatePasswordByUsername(username, encodePassword);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public Response<?> findUserInfo() {
        // 获取存储在 ThreadLocal 中的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 拿到用户名
        String username = authentication.getName();
        // 通过用户名查询用户信息
        UserDO userDO = userMapper.findByUsername(username);

        return Response.success(FindUserInfoRspVO.builder()
                .username(username)
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .phone(userDO.getPhone())
                .email(userDO.getEmail())
                .build());
    }

    /**
     * 用户分页数据获取
     *
     * @return {@link Response }<{@link ? }> 用户分页数据
     */
    @Override
    public PageResponse<?> findUserPageList(FindUserPageListReqVO findUserPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findUserPageListReqVO.getCurrent();
        Long size = findUserPageListReqVO.getSize();

        // 分页对象(查询第几页、每页多少数据)
        Page<UserDO> page = new Page<>(current, size);

        // 执行分页查询
        Page<UserDO> userDOPage = userMapper.paginate(page, QueryWrapper.create()
                .like(UserDO::getNickname, findUserPageListReqVO.getNickname())
                .orderBy(UserDO::getCreateTime, false));

        List<UserDO> userDOS = userDOPage.getRecords();

        // DO 转 VO
        List<FindUserPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(userDOS)) {
            vos = userDOS.stream()
                    .map(userDO -> FindUserPageListRspVO.builder()
                            .id(userDO.getId())
                            .username(userDO.getUsername())
                            .nickname(userDO.getNickname())
                            .avatar(userDO.getAvatar())
                            .phone(userDO.getPhone())
                            .email(userDO.getEmail())
                            .createTime(userDO.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(userDOPage, vos);
    }

    /**
     * 添加用户
     *
     * @param addUserReqVO 添加用户请求参数
     * @return {@link Response }<{@link ? }> 添加用户结果
     */
    @Override
    public Response<?> addUser(AddUserReqVO addUserReqVO) {
        int insert = userMapper.insert(UserDO.builder()
                .username(addUserReqVO.getUsername())
                .password(passwordEncoder.encode(addUserReqVO.getPassword()))
                .nickname(addUserReqVO.getNickname())
                .avatar(addUserReqVO.getAvatar())
                .phone(addUserReqVO.getPhone())
                .email(addUserReqVO.getEmail())
                .build());
        return insert == 1 ? Response.success() : Response.fail();
    }

    /**
     * 更新用户
     *
     * @param updateUserReqVO 更新用户请求参数
     * @return {@link Response }<{@link ? }> 更新用户结果
     */
    @Override
    public Response<?> updateUser(UpdateUserReqVO updateUserReqVO) {
        int update = userMapper.update(UserDO.builder()
                .id(updateUserReqVO.getId())
                .username(updateUserReqVO.getUsername())
                .password(passwordEncoder.encode(updateUserReqVO.getPassword()))
                .nickname(updateUserReqVO.getNickname())
                .avatar(updateUserReqVO.getAvatar())
                .build());
        return update == 1 ? Response.success() : Response.fail();
    }

    /**
     * 删除用户
     *
     * @param deleteUserReqVO 删除用户请求参数
     * @return {@link Response }<{@link ? }> 删除用户结果
     */
    @Override
    public Response<?> deleteUser(DeleteUserReqVO deleteUserReqVO) {
        int delete = userMapper.deleteById(deleteUserReqVO.getId());
        return delete == 1 ? Response.success() : Response.fail();
    }
}
