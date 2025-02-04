package com.schedule.share.user.application.service.user;

import com.schedule.share.common.exception.AbstractException;
import com.schedule.share.common.exception.Common403Exception;
import com.schedule.share.user.application.port.inbound.FavoriteCommand;
import com.schedule.share.user.application.port.outbound.FavoriteCommandPort;
import com.schedule.share.user.application.port.outbound.FavoriteQueryPort;
import com.schedule.share.user.application.service.user.vo.FavoriteVO;
import com.schedule.share.user.domain.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteWriter implements FavoriteCommand {

    private final FavoriteQueryPort favoriteQueryPort;
    private final FavoriteCommandPort favoriteCommandPort;
    private final FavoriteMapper favoriteMapper;

    @Override
    public long create(long userId, FavoriteVO.save param) {
        FavoriteVO.save favoriteWithUserId = param.of(userId);

        return favoriteCommandPort.create(favoriteMapper.favoriteVoSaveToDomain(favoriteWithUserId));
    }

    @Override
    public void delete(long userId, long id) throws AbstractException {
        if(!validateFavoriteOwn(userId, id)) throw new Common403Exception();
        favoriteCommandPort.delete(id);
    }

    @Override
    public void delete(long userId, List<Long> ids) throws AbstractException {
        if(!validateFavoriteOwn(userId, ids)) throw new Common403Exception();
        favoriteCommandPort.delete(ids);
    }

    private boolean validateFavoriteOwn(long userId, long id) {
        return userId == favoriteQueryPort.get(id).getUserId();
    }

    private boolean validateFavoriteOwn(long userId, List<Long> ids) {
        for (long itemId : ids) {
            if (userId != favoriteQueryPort.get(itemId).getUserId()) {
                return false;
            }
        }
        return true;
    }
}
