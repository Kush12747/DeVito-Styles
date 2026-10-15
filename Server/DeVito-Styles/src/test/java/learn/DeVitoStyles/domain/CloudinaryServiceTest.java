package learn.DeVitoStyles.domain;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    private static final String SECURE_URL = "https://res.cloudinary.com/demo/image/upload/v1/haircut.jpg";

    @Mock private Cloudinary cloudinary;
    @Mock private Uploader uploader;

    @InjectMocks private CloudinaryService cloudinaryService;

    @Test
    void uploadImage_uploadsFileBytesAndReturnsSecureUrl() throws IOException {
        byte[] bytes = {1, 2, 3, 4};
        MultipartFile file = new MockMultipartFile("file", "haircut.jpg", "image/jpeg", bytes);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap()))
                .thenReturn(Map.of("secure_url", SECURE_URL, "url", "http://insecure.example"));

        String url = cloudinaryService.uploadImage(file);

        assertEquals(SECURE_URL, url);
        verify(uploader).upload(
                argThat(arg -> arg instanceof byte[] b && Arrays.equals(b, bytes)),
                argThat(Map::isEmpty));
    }

    @Test
    void uploadImage_whenUploadFails_propagatesIOException() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "haircut.jpg", "image/jpeg", new byte[]{9});
        IOException failure = new IOException("Cloudinary unavailable");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenThrow(failure);

        IOException thrown = assertThrows(IOException.class, () -> cloudinaryService.uploadImage(file));

        assertSame(failure, thrown);
    }

    @Test
    void uploadImage_whenReadingFileFails_propagatesIOExceptionWithoutUploading() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(file.getBytes()).thenThrow(new IOException("disk error"));

        assertThrows(IOException.class, () -> cloudinaryService.uploadImage(file));

        verifyNoInteractions(uploader);
    }
}
