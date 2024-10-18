//package ru.relex.service;
//
//import org.springframework.core.io.FileSystemResource;
//import ru.relex.entity.AppDocument;
//import ru.relex.entity.AppPhoto;
//import ru.relex.entity.BinaryContent;
//
//public interface FileService {
//
//    AppDocument getDocument(String id);
//
//    AppPhoto getPhoto(String id);
//
//}
package ru.relex.service;

import ru.relex.entity.AppDocument;
import ru.relex.entity.AppPhoto;
import ru.relex.entity.AppAudio;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);

    AppAudio getAudio(String id);

}
