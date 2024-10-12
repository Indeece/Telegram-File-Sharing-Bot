package ru.relex.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.relex.controller.UpdateController;
import ru.relex.service.AnswerConsumer;

import static ru.relex.model.RabbitQueue.ANSWER_MESSAGE;

@Log4j
@Service
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        if (sendMessage == null || sendMessage.getChatId() == null || sendMessage.getText() == null) {
            log.debug("Invalid SendMessage object received: " + sendMessage);
            return;
        }
        updateController.setView(sendMessage);
    }


}
