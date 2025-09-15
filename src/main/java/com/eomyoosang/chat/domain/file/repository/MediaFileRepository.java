package com.eomyoosang.chat.domain.file.repository;

import com.eomyoosang.chat.domain.file.entity.MediaFile;

import java.util.List;
import java.util.Optional;

public interface MediaFileRepository {
    MediaFile save(MediaFile mediaFile);
    Optional<MediaFile> findById(String id);
    List<MediaFile> findByUploadedBy(String uploadedBy);
    List<MediaFile> findByFileType(String fileType);
    List<MediaFile> findAll();
    void deleteById(String id);
}