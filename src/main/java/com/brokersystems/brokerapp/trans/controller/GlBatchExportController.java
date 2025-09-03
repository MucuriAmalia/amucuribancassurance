package com.brokersystems.brokerapp.trans.controller;

import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.server.utils.sftp.SftpUtil;
import com.brokersystems.brokerapp.trans.service.GlBatchExportService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("protected/api/gl-batch-export")
public class GlBatchExportController {

    private final GlBatchExportService glBatchExportService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private SftpUtil sftpUtil;

    @Autowired
    public GlBatchExportController(GlBatchExportService glBatchExportService) {
        this.glBatchExportService = glBatchExportService;
    }

    @RequestMapping(value = "sap", method = RequestMethod.GET)
    public String sapPage(Model model, HttpServletRequest request) {
        auditTrailLogger.log("Accessed SAP Page", request, "Sap Page");
        return "sap";
    }

    @RequestMapping(value = "/export-all", method = {RequestMethod.GET})
    @ResponseBody
    public String exportAllBatchesToExcel() {
        String localFolderPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "GLBatchExports";
        StringBuilder result = new StringBuilder();

        try {
            // Export to local path and transfer to EC2
            glBatchExportService.exportAllBatchesToExcel(localFolderPath);
            result.append("✅ Export and transfer completed successfully!\n");
            result.append("📁 Local export path: ").append(localFolderPath).append("\n");
            result.append("🚀 File transferred to EC2 instance: /home/ubuntu/sap_files\n");
            result.append("⏰ Export completed at: ").append(new java.util.Date());

            return result.toString();
        } catch (Exception e) {
            result.append("❌ Export failed: ").append(e.getMessage()).append("\n");
            result.append("💡 Check logs for detailed error information");
            return result.toString();
        }
    }

    // New download-zip endpoint
    @RequestMapping(value = "/download-zip", method = {RequestMethod.GET})
    public void downloadAllBatchesAsZip(HttpServletRequest request, HttpServletResponse response) {
        String localFolderPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "GLBatchExports";

        try {
            // Create ZIP file
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String zipFileName = "GL_Batch_Export_" + timestamp + ".zip";
            String zipFilePath = localFolderPath + File.separator + zipFileName;

            createZipFile(localFolderPath, zipFilePath);

            // Log the download
            auditTrailLogger.log("Downloaded GL Batch Export ZIP", request, "File Download");

            // Set response headers for file download
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

            // Stream the file to response
            File zipFile = new File(zipFilePath);
            response.setContentLength((int) zipFile.length());

            try (FileInputStream fileInputStream = new FileInputStream(zipFile);
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            // Clean up the temporary ZIP file
            zipFile.delete();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Error creating zip file: " + e.getMessage());
            } catch (IOException ioException) {
                // Log the error
                System.err.println("Error writing error response: " + ioException.getMessage());
            }
        }
    }

    // Helper method to create ZIP file
    private void createZipFile(String sourceFolderPath, String zipFilePath) throws IOException {
        File sourceFolder = new File(sourceFolderPath);

        // Find the most recent Excel file
        File[] excelFiles = sourceFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));
        File mostRecentExcel = null;
        if (excelFiles != null && excelFiles.length > 0) {
            for (File file : excelFiles) {
                if (mostRecentExcel == null || file.lastModified() > mostRecentExcel.lastModified()) {
                    mostRecentExcel = file;
                }
            }
        }

        // Find the most recent text file
        File[] textFiles = sourceFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        File mostRecentText = null;
        if (textFiles != null && textFiles.length > 0) {
            for (File file : textFiles) {
                if (mostRecentText == null || file.lastModified() > mostRecentText.lastModified()) {
                    mostRecentText = file;
                }
            }
        }

        // Check if we have at least one file to zip
        if (mostRecentExcel == null && mostRecentText == null) {
            throw new IOException("No Excel or text files found to zip");
        }

        // Create ZIP with only the most recent files
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            if (mostRecentExcel != null) {
                addFileToZip(mostRecentExcel, zos);
            }

            if (mostRecentText != null) {
                addFileToZip(mostRecentText, zos);
            }
        }
    }

    // Helper method to add file to ZIP
    private void addFileToZip(File file, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
        }

        zos.closeEntry();
    }

    @RequestMapping(value = "/test-sftp", method = {RequestMethod.GET})
    @ResponseBody
    public String testSftpConnection() {
        StringBuilder result = new StringBuilder();

        try {
            result.append("🔍 Testing SFTP connection to EC2 instance...\n");

            boolean connectionSuccess = sftpUtil.testConnection();

            if (connectionSuccess) {
                result.append("✅ SFTP connection successful!\n");
                result.append("🔑 SSH key authentication working\n");
                result.append("📡 Can connect to EC2 instance\n");
                result.append("📂 Remote directory accessible\n");
            } else {
                result.append("❌ SFTP connection failed\n");
                result.append("🔧 Please check:\n");
                result.append("  - EC2 instance is running\n");
                result.append("  - SSH key is properly configured\n");
                result.append("  - Network connectivity from container to EC2\n");
                result.append("  - EC2 security groups allow SSH (port 22)\n");
            }

            return result.toString();

        } catch (Exception e) {
            result.append("❌ Error testing SFTP connection: ").append(e.getMessage()).append("\n");
            result.append("📋 Error details: ").append(e.getClass().getSimpleName()).append("\n");
            return result.toString();
        }
    }

    @RequestMapping(value = "/test-file-upload", method = {RequestMethod.GET})
    @ResponseBody
    public String testFileUpload() {
        StringBuilder result = new StringBuilder();

        try {
            result.append("🧪 Creating test file for upload...\n");

            // Create a test file
            String testFolderPath = System.getProperty("user.home") + File.separator + "temp";
            File testFolder = new File(testFolderPath);
            if (!testFolder.exists()) {
                testFolder.mkdirs();
            }

            String testFilePath = testFolderPath + File.separator + "test_upload_" + System.currentTimeMillis() + ".txt";
            File testFile = new File(testFilePath);

            try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
                writer.write("Test file for SFTP upload\n");
                writer.write("Created at: " + new java.util.Date() + "\n");
                writer.write("From container to EC2 instance\n");
            }

            result.append("📄 Test file created: ").append(testFilePath).append("\n");

            // Test upload
            result.append("🚀 Attempting SFTP upload...\n");
            sftpUtil.authWithKey();
            sftpUtil.uploadFileToEc2(testFilePath);

            result.append("✅ Test file uploaded successfully!\n");
            result.append("📂 File location on EC2: /home/ubuntu/sap_files/").append(testFile.getName()).append("\n");

            // Clean up test file
            if (testFile.delete()) {
                result.append("🧹 Local test file cleaned up\n");
            }

            return result.toString();

        } catch (JSchException e) {
            result.append("❌ SFTP authentication failed: ").append(e.getMessage()).append("\n");
            result.append("🔑 Check SSH key configuration\n");
            return result.toString();
        } catch (SftpException e) {
            result.append("❌ SFTP upload failed: ").append(e.getMessage()).append("\n");
            result.append("📂 Check remote directory permissions\n");
            return result.toString();
        } catch (Exception e) {
            result.append("❌ Test failed: ").append(e.getMessage()).append("\n");
            return result.toString();
        } finally {
            sftpUtil.close();
        }
    }

    @RequestMapping(value = "/list-remote-files", method = {RequestMethod.GET})
    @ResponseBody
    public String listRemoteFiles() {
        StringBuilder result = new StringBuilder();

        try {
            result.append("📂 Listing files in remote directory /home/ubuntu/sap_files...\n\n");

            sftpUtil.authWithKey();

            // Capture system output to string
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(baos));

            sftpUtil.listFiles("/home/ubuntu/sap_files");

            // Restore original output
            System.setOut(originalOut);
            String fileList = baos.toString();

            if (fileList.trim().isEmpty()) {
                result.append("📭 No files found in remote directory\n");
            } else {
                result.append("📋 Remote files:\n");
                result.append(fileList);
            }

            return result.toString();

        } catch (Exception e) {
            result.append("❌ Failed to list remote files: ").append(e.getMessage()).append("\n");
            return result.toString();
        } finally {
            sftpUtil.close();
        }
    }
}


