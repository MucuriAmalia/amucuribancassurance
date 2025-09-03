package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class LocationUtils {

     static final String BASE_DIR = System.getProperty("user.home") + File.separator + ".coresystem";
    static final Integer MAX_FILE_UPLOAD_SIZE_IN_MB = 10;

    private static final Random random = new Random();


    public String saveFile( UploadDocumentForm uploadDocumentForm) throws IOException, BadRequestException {
        final String fileName = uploadDocumentForm.getFile().getOriginalFilename();
        System.out.println("File Name : " + fileName);
        final String uploadDocumentLocation = generateFileParentDirectory(uploadDocumentForm.getEntityType(),
                uploadDocumentForm.getEntityId());
        System.out.println("UploadDocumentLocation : " + uploadDocumentLocation);
        final String fileLocation = uploadDocumentLocation + File.separator + fileName;
        validateFileSizeWithinPermissibleRange(uploadDocumentForm.getFile().getSize(), fileName);
        makeDirectories(uploadDocumentLocation);
        System.out.println("File Location : " + fileLocation);

        writeFileToFileSystem(fileName, uploadDocumentForm.getFile(), fileLocation);
        return fileLocation;
    }

    private String generateFileParentDirectory(final String entityType, final Long entityId) {
        return BASE_DIR + File.separator+ "documents" + File.separator
                + entityType + File.separator + entityId + File.separator + generateRandomString();
    }


    private static String generateRandomString() {
        final String characters = "abcdefghijklmnopqrstuvwxyz123456789";
        final int length = generateRandomNumber();
        final char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return new String(text);
    }

    private static int generateRandomNumber() {
        final Random randomGenerator = new Random();
        return randomGenerator.nextInt(11) + 5;
    }

    private static void validateFileSizeWithinPermissibleRange(final Long fileSize, final String name) throws BadRequestException {
        /**
         * Using Content-Length gives me size of the entire request, which is
         * good enough for now for a fast fail as the length of the rest of the
         * content i.e name and description while compared to the uploaded file
         * size is negligible
         **/
        if (fileSize != null && ((fileSize / (1024 * 1024)) > MAX_FILE_UPLOAD_SIZE_IN_MB)) { throw new BadRequestException("Unable to save the document with name" + name + " since its file Size of " + fileSize
                / (1024 * 1024) + " MB exceeds the max permissable file size  of " + MAX_FILE_UPLOAD_SIZE_IN_MB + " MB"); }
    }

    private void makeDirectories(final String uploadDocumentLocation) {
        if (!new File(uploadDocumentLocation).isDirectory()) {
            new File(uploadDocumentLocation).mkdirs();
        }
    }

    private void writeFileToFileSystem(final String fileName, final MultipartFile uploadedInputStream, final String fileLocation) throws BadRequestException, IOException {
        uploadedInputStream.transferTo(new File(fileLocation));
    }

}
