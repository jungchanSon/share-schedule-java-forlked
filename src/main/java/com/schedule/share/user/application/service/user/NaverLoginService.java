package com.schedule.share.user.application.service.user;

import com.schedule.share.common.exception.Common401Exception;
import com.schedule.share.common.util.JwtUtil;
import com.schedule.share.user.application.port.inbound.LoginServiceUseCase;
import com.schedule.share.user.application.port.outbound.TokenCommandPort;
import com.schedule.share.user.application.port.outbound.SocialLoginPort;
import com.schedule.share.user.application.port.outbound.UserCommandPort;
import com.schedule.share.user.application.port.outbound.UserQueryPort;
import com.schedule.share.user.application.service.user.vo.SocialLoginVO;
import com.schedule.share.user.application.service.user.vo.UserVO;
import com.schedule.share.user.domain.NaverLoginCredential;
import com.schedule.share.user.domain.RefreshToken;
import com.schedule.share.user.domain.User;
import com.schedule.share.user.domain.mapper.SocialLoginMapper;
import com.schedule.share.user.domain.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NaverLoginService implements LoginServiceUseCase<SocialLoginVO.NaverOauthCredential, UserVO.Save> {

    private final SocialLoginPort<NaverLoginCredential> socialLoginPort;
    private final UserQueryPort userQueryPort;
    private final UserCommandPort userCommandPort;
    private final TokenCommandPort tokenCommandPort;

    private final SocialLoginMapper socialLoginMapper;
    private final UserMapper userMapper;

    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public SocialLoginVO.Token sign(SocialLoginVO.NaverOauthCredential naverOauthCredential, UserVO.Save user) {

        NaverLoginCredential naverLoginCredential = socialLoginMapper.naverOauthCredentialVoToDomain(naverOauthCredential);
        User userSave = userMapper.userVoSaveToDomain(user);

        String ci = socialLoginPort.getCi(naverLoginCredential);

        userSave.updateCiMethod(ci, "naver");
        long userId = userCommandPort.create(userSave);

        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        RefreshToken refresh = RefreshToken.builder()
                .userId(userId)
                .ci(ci)
                .refreshToken(refreshToken)
                .build();
        tokenCommandPort.createRefreshToken(refresh);

        return SocialLoginVO.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userSave)
                .build();
    }

    @Override
    @Transactional
    public SocialLoginVO.Token login(SocialLoginVO.NaverOauthCredential naverOauthCredential) {

        NaverLoginCredential naverLoginCredential = socialLoginMapper.naverOauthCredentialVoToDomain(naverOauthCredential);

        String ci = socialLoginPort.getCi(naverLoginCredential);
        User user = userQueryPort.getByCi(ci);

        if (Objects.isNull(user)) {
            throw new Common401Exception();
        } else {
            // 토킅 전달
            String accessToken = jwtUtil.generateAccessToken(user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId());

            tokenCommandPort.updateRefreshToken(user.getId(), refreshToken);

            return SocialLoginVO.Token.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();
        }
    }

}
