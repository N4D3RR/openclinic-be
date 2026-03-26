package naderdeghaili.capstoneproject.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;


    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) {

        try {
            String contentType = file.getContentType();
            String resourceType = (contentType != null && contentType.equals("application/pdf"))
                    ? "raw"
                    : "image";

            Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType, "use_filename", true, "unique_filename", true));
            log.info("Cloudinary upload result: {}", result);
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file", e);
        }
    }
}
