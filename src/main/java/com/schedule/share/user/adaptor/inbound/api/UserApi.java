package com.schedule.share.user.adaptor.inbound.api;

import com.schedule.share.common.model.ResponseModel;
import com.schedule.share.common.util.JwtUtil;
import com.schedule.share.user.adaptor.inbound.api.dto.UserRequestDTO;
import com.schedule.share.user.adaptor.inbound.api.dto.UserResponseDTO;
import com.schedule.share.user.adaptor.inbound.api.mapper.UserDTOMapper;
import com.schedule.share.user.application.port.inbound.UserCommand;
import com.schedule.share.user.application.port.inbound.UserQuery;
import com.schedule.share.user.application.service.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "유저", description = "유저 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApi {

    private final UserQuery userQuery;
    private final UserCommand userCommand;
    private final UserDTOMapper userDTOMapper;

    private final JwtUtil jwtUtil;

    // 한명 조회
    @Operation(summary = "유저 단일 조회 API", description = "id로 유저 한명을 조회한다.")
    @GetMapping
    public ResponseModel<UserResponseDTO.Response> get(@RequestHeader("access_token") String accessToken) {
        long userId = jwtUtil.getUserId(accessToken);

        UserVO.User user = userQuery.get(userId);
        UserResponseDTO.Response responseDTO = userDTOMapper.toResponseDTO(user);

        return ResponseModel.of(responseDTO);
    }

    // 모두 조회
    @Operation(summary = "유저 모두 조회 API", description = "모든 유저를 조회한다.")
    @GetMapping("/list")
    public ResponseModel<List<UserResponseDTO.Response>> getList(@RequestHeader("access_token") String accessToken) {
        jwtUtil.checkToken(accessToken);

        List<UserVO.User> list = userQuery.list();
        List<UserResponseDTO.Response> userListResponse = list.stream().map(userDTOMapper::toResponseDTO).toList();

        return ResponseModel.of(userListResponse);
    }

    // 수정
    @Operation(summary = "유저 수정 API", description = "유저 정보를 수정한다.")
    @PutMapping
    public void update(@RequestHeader("access_token") String accessToken, @RequestBody UserRequestDTO.UserUpdate body) {
        long userId = jwtUtil.getUserId(accessToken);

        UserVO.Save vo = userDTOMapper.toVO(body);
        userCommand.update(userId, vo);
    }

    // 탈퇴
    @Operation(summary = "유저 탈퇴 API", description = "유저를 탈퇴시킨다.")
    @DeleteMapping
    public void delete(@RequestHeader("access_token") String accessToken) {
        long userId = jwtUtil.getUserId(accessToken);

        userCommand.delete(userId);
    }

    @Operation(summary = "최근에 본 캘린더 Id 수정", description = "최근에 본 캘린더의 id를 변경한다.")
    @PutMapping("/recent_calendar")
    public void updateRecentCalendarId(@RequestHeader("access_token") String accessToken, @RequestBody UserRequestDTO.RecentCalendar body) {
        long userId = jwtUtil.getUserId(accessToken);

        userCommand.updateCalendarId(userId, body.recentCalendarId());
    }
}
