package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.admin.model.vo.user.*;
import com.cosmos.origin.admin.service.AdminUserService;
import com.cosmos.origin.admin.service.LoginAttemptService;
import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final RedisTemplate<String, Object> redisTemplate;

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
                .orderBy(UserDO::getCreateTime, false)
                .eq(UserDO::getIsDeleted, DeletedEnum.NO.getValue()));

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

    /**
     * 登录与注册
     *
     * @param userRegisterReqVO
     * @return
     */
    @Override
    public Response<String> register(UserRegisterReqVO userRegisterReqVO) {
        String phone = userRegisterReqVO.getPhone();
        Integer type = userRegisterReqVO.getType();

        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);

        Long userId = null;

        // 判断登录类型
        switch (loginTypeEnum) {
            case VERIFICATION_CODE: // 验证码登录
                String verificationCode = userLoginReqVO.getCode();

                // 校验入参验证码是否为空
                if (StringUtils.isBlank(verificationCode)) {
                    return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "验证码不能为空");
                }

                // 构建验证码 Redis Key
                String key = RedisKeyConstants.buildVerificationCodeKey(phone);
                // 查询存储在 Redis 中该用户的登录验证码
                String sentCode = (String) redisTemplate.opsForValue().get(key);

                // 判断用户提交的验证码，与 Redis 中的验证码是否一致
                if (!StringUtils.equals(verificationCode, sentCode)) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }

                // 通过手机号查询记录
                UserDO userDO = userDOMapper.selectByPhone(phone);

                log.info("==> 用户是否注册, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO));

                // 判断是否注册
                if (Objects.isNull(userDO)) {
                    // 若此用户还没有注册，系统自动注册该用户
                    // todo

                } else {
                    // 已注册，则获取其用户 ID
                    userId = userDO.getId();
                }
                break;
            case PASSWORD: // 密码登录
                // todo

                break;
            default:
                break;
        }

        // SaToken 登录用户，并返回 token 令牌
        // todo

        return Response.success("");
    }
}
