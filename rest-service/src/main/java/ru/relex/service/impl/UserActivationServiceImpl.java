package ru.relex.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.relex.dao.AppUserDao;
import ru.relex.service.UserActivationService;
import ru.relex.utils.CryptoTool;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUserDao.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }
}
