package ru.relex.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.relex.entity.AppAudio;

public interface AppAudioDao extends JpaRepository<AppAudio, Long> {
}