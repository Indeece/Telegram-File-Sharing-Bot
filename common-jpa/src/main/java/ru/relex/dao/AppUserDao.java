package ru.relex.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relex.entity.AppUser;

public interface AppUserDao extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
