package ru.relex.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.relex.dao.AppAudioDao;
import ru.relex.dao.AppDocumentDao;
import ru.relex.dao.AppPhotoDao;
import ru.relex.dao.BinaryContentDao;
import ru.relex.entity.AppAudio;
import ru.relex.entity.AppDocument;
import ru.relex.entity.AppPhoto;
import ru.relex.entity.BinaryContent;
import ru.relex.exceptions.UploadFileException;
import ru.relex.service.FileService;
import ru.relex.service.enums.LinkType;
import ru.relex.utils.CryptoTool;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDao appDocumentDAO;

    private final AppPhotoDao appPhotoDAO;

    private final BinaryContentDao binaryContentDAO;

    private final CryptoTool cryptoTool;

    private final AppAudioDao appAudioDao;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        var telegramDoc = telegramMessage.getDocument();
        var fileId = telegramDoc.getFileId();
        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? photoSizeCount - 1 : 0; // Получаем самый большой размер
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex); // Изменено с 0 на photoIndex
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppDoc = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        var filePath = getFilePath(response);
        var fileInByte = downloadFile(filePath);
        var transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        var jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        var fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    @Override
    public AppAudio processAudio(Message telegramMessage) {
        var audio = telegramMessage.getAudio();
        var fileId = audio.getFileId();
        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var transientAppAudio = buildTransientAppAudio(audio, persistentBinaryContent);
            return appAudioDao.save(transientAppAudio);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private AppAudio buildTransientAppAudio(Audio telegramAudio, BinaryContent persistentBinaryContent) {
        return AppAudio.builder()
                .telegramFileId(telegramAudio.getFileId())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramAudio.getMimeType())
                .fileSize(telegramAudio.getFileSize())
                .name(telegramAudio.getFileName())
                .build();
    }
}