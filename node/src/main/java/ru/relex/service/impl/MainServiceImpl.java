package ru.relex.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.relex.dao.AppUserDao;
import ru.relex.dao.RawDataDao;
import ru.relex.entity.AppUser;
import ru.relex.entity.RawData;
import ru.relex.entity.enums.UserState;
import ru.relex.service.MainService;
import ru.relex.service.ProducerService;

import static ru.relex.entity.enums.UserState.BASIC_STATE;

@Service
public class MainServiceImpl implements MainService {

    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;

    public MainServiceImpl(RawDataDao rawDataDao, ProducerService producerService, AppUserDao appUserDao) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
        this.appUserDao = appUserDao;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var message = update.getMessage();
        var sendMessage = new SendMessage();

        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Hello from NODE!");
        producerService.producerAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(User telegramUser) {
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    // TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }
}
