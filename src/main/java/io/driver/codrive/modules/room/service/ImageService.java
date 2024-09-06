package io.driver.codrive.modules.room.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import io.driver.codrive.global.exception.IllegalArgumentApplicationException;
import io.driver.codrive.global.exception.InternalServerErrorApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
	private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
	private static final String DIR_PATH = "images/";
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String uploadImage(MultipartFile requestMultipartFile) throws IOException {
		validateMultipartFile(requestMultipartFile);
		File file = convertToFile(requestMultipartFile).orElseThrow(() ->
			new InternalServerErrorApplicationException("파일 변환 중 오류가 발생했습니다."));
		String imageUrl = uploadToS3(file);
		removeLocalFile(file);
		return imageUrl;
	}

	public String modifyImage(String imageUrl, MultipartFile requestMultipartFile) throws IOException {
		deleteImage(getFileNameByUrl(imageUrl));
		return uploadImage(requestMultipartFile);
	}

	public void deleteImage(String fileName) {
		try {
			amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, DIR_PATH + fileName));
			log.info("S3 이미지 삭제를 성공했습니다. : {}", fileName);
		} catch (SdkClientException e) {
			log.error("S3 이미지 삭제를 실패했습니다. : {}", e.getMessage());
		}
	}

	private void validateMultipartFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentApplicationException("이미지 파일이 존재하지 않습니다.");
		}

		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentApplicationException("이미지 파일 크기는 5MB 이하로 제한됩니다.");
		}

		String fileName = file.getOriginalFilename();
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentApplicationException("이미지 파일 이름이 존재하지 않습니다.");
		}

		String fileExtension = getFileExtension(fileName);
		if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			throw new IllegalArgumentApplicationException("지원하지 않는 이미지 확장자입니다.");
		}
	}

	private Optional<File> convertToFile(MultipartFile file) throws IOException {
		File convertFile = new File(file.getOriginalFilename());
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}

	private String uploadToS3(File file) {
		String fileName = DIR_PATH + UUID.randomUUID() + "." + getFileExtension(file.getName());
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, file)
				.withCannedAcl(CannedAccessControlList.PublicRead)
		);
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	private void removeLocalFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("로컬에 저장된 이미지가 삭제되었습니다.");
		} else {
			log.info("로컬에 저장된 이미지가 삭제되지 않았습니다.");
		}
	}

	public String getFileNameByUrl(String imageUrl) {
		return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
	}

	public String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}
