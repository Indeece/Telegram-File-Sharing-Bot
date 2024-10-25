package ru.relex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.relex.dao.AppAudioDao;
import ru.relex.dao.AppDocumentDao;
import ru.relex.dao.AppPhotoDao;
import ru.relex.entity.AppAudio;
import ru.relex.entity.AppDocument;
import ru.relex.entity.AppPhoto;
import ru.relex.service.FileService;
import ru.relex.utils.CryptoTool;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDao appDocumentDAO;

    private final AppPhotoDao appPhotoDAO;

    private final AppAudioDao appAudioDAO;

    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public AppAudio getAudio(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appAudioDAO.findById(id).orElse(null);
    }
}