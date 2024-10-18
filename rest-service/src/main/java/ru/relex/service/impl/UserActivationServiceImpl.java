package ru.relex.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.relex.dao.AppUserDao;
import ru.relex.service.UserActivationService;
import ru.relex.utils.CryptoTool;
import ru.relex.service.ProducerService;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;
    private final ProducerService producerService;

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUserDao.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDao.save(user);

            // Send confirmation message to user
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getTelegramUserId());
            sendMessage.setText("Регистрация завершена! Теперь Вы можете отправлять файлы и фотографии.");
            producerService.producerAnswer(sendMessage);

            return true;
        }
        return false;
    }
}