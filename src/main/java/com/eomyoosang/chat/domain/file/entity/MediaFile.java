package com.eomyoosang.chat.domain.file.entity;

import com.eomyoosang.chat.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "media_files")
public class MediaFile extends BaseEntity {

    @Id
    @Column(name = "id", length = 26)
    private String id;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "uploaded_by", nullable = false, length = 26)
    private String uploadedBy;

    protected MediaFile() {
        // JPA를 위한 기본 생성자
    }

    public MediaFile(String id, String originalFilename, String storedFilename,
                    String filePath, String fileType, Long fileSize, String uploadedBy) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedBy = uploadedBy;
    }

    public String getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isImage() {
        return fileType.startsWith("image/");
    }

    public boolean isVideo() {
        return fileType.startsWith("video/");
    }

    public boolean isDocument() {
        return !isImage() && !isVideo();
    }
}