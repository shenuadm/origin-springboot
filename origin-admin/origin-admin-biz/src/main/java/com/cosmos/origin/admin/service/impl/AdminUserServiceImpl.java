package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.admin.event.UserOperationEvent;
import com.cosmos.origin.admin.model.vo.user.*;
import com.cosmos.origin.admin.service.AdminUserService;
import com.cosmos.origin.admin.service.LoginAttemptService;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.event.publisher.EventPublisher;
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
    private final LoginAttemptService loginAttemptService;
    private final EventPublisher eventPublisher;

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
        UserDO userDO = UserDO.builder()
                .username(addUserReqVO.getUsername())
                .password(passwordEncoder.encode(addUserReqVO.getPassword()))
                .nickname(addUserReqVO.getNickname())
                .avatar(addUserReqVO.getAvatar())
                .phone(addUserReqVO.getPhone())
                .email(addUserReqVO.getEmail())
                .build();
        int insert = userMapper.insert(userDO);

        // 发布用户创建事件（演示事件发布）
        if (insert == 1) {
            UserOperationEvent.UserOperationData data = new UserOperationEvent.UserOperationData(
                    userDO.getId(), // 用户ID，实际应该从数据库获取新增后的ID
                    addUserReqVO.getUsername(),
                    "CREATE",
                    "管理员创建新用户: " + addUserReqVO.getUsername()
            );
            eventPublisher.publishEvent(new UserOperationEvent(this, data));
        }

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
        // 先查询用户信息
        UserDO user = userMapper.selectOneById(deleteUserReqVO.getId());

        int delete = userMapper.deleteById(deleteUserReqVO.getId());

        // 发布用户删除事件（演示事件发布）
        if (delete == 1 && user != null) {
            UserOperationEvent.UserOperationData data = new UserOperationEvent.UserOperationData(
                    user.getId(),
                    user.getUsername(),
                    "DELETE",
                    "管理员删除用户: " + user.getUsername()
            );
            eventPublisher.publishEvent(new UserOperationEvent(this, data));
        }

        return delete == 1 ? Response.success() : Response.fail();
    }

    /**
     * 手动解锁用户账号（管理员使用）
     *
     * @param unlockUserReqVO 解锁用户请求参数
     * @return {@link Response }<{@link ? }> 解锁结果
     */
    @Override
    public Response<?> unlockUser(UnlockUserReqVO unlockUserReqVO) {
        String username = unlockUserReqVO.getUsername();

        // 验证用户是否存在
        UserDO user = userMapper.findByUsername(username);
        if (user == null) {
            return Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
        }

        // 调用 LoginAttemptService 解锁账号
        loginAttemptService.unlock(username);

        return Response.success("用户 [" + username + "] 账号已成功解锁");
    }
}
