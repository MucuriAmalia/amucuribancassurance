package com.brokersystems.brokerapp.server.utils.sftp;

import com.jcraft.jsch.*;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

@Component
public class SftpUtil {

    private static final Logger logger = LoggerFactory.getLogger(SftpUtil.class);

    @Value("${ec2.host:10.240.235.23}")
    private String SFTP_HOST;

    @Value("${ec2.user:ubuntu}")
    private String SFTP_USER;

    @Value("${ssh.port:22}")
    private String SFTP_PORT;

    @Value("${ec2.remote.path:/home/ubuntu/sap_files}")
    private String REMOTE_DIR;

    @Value("${ssh.key.path:/root/.ssh/id_rsa}")
    private String SSH_KEY_PATH;

    private final JSch jsch = new JSch();
    private Session session;
    private ChannelSftp channel;

    /**
     * Authenticate with EC2 instance using SSH key
     *
     * @throws JSchException If there is problem with credentials or connection
     */
    public void authWithKey() throws JSchException {
        try {
            jsch.addIdentity(SSH_KEY_PATH);
            session = jsch.getSession(SFTP_USER, SFTP_HOST, Integer.parseInt(SFTP_PORT));
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
//            config.put("UserKnownHostsFile", "/dev/null");
            config.put("PreferredAuthentications", "publickey");
            session.setConfig(config);
            logger.info("Connecting to {}@{}:{}", SFTP_USER, SFTP_HOST, SFTP_PORT);
            session.connect();
            logger.info("Established SSH session...");
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            logger.info("Opened SFTP channel...");
        } catch (JSchException e) {
            logger.error("Failed to authenticate with SSH key: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Authenticate with remote using password (kept for backward compatibility)
     *
     * @param password password of remote
     * @throws JSchException If there is problem with credentials or connection
     */
    public void authPassword(String password) throws JSchException {
        session = jsch.getSession(SFTP_USER, SFTP_HOST, Integer.parseInt(SFTP_PORT));
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
    }

    /**
     * Legacy method - now uses SSH key authentication
     */
    public void authKey(String keyPath, String pass) throws JSchException {
        jsch.addIdentity(keyPath, pass);
        session = jsch.getSession(SFTP_USER, SFTP_HOST, Integer.parseInt(SFTP_PORT));
        var config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        logger.info("Connecting........");
        session.connect();
        logger.info("Established session...");
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        logger.info("Opened sftp channel...");
    }

    /**
     * Move a file on the SFTP server from source to destination
     *
     * @param sourcePath Full path of the source file
     * @param destinationPath Full path of the destination file
     * @throws SftpException If there is any problem with moving the file
     * @throws JSchException If there is any problem with the connection
     */
    public void moveFile(String sourcePath, String destinationPath) throws SftpException, JSchException {
        try {
            if (channel == null || !channel.isConnected()) {
                authWithKey();
            }
            // Extract the destination directory from the destination path
            String destinationDir = destinationPath.substring(0, destinationPath.lastIndexOf("/"));
            ensureRemoteDirectoryExists(destinationDir);
            logger.info("Moving file from {} to {}", sourcePath, destinationPath);
            channel.rename(sourcePath, destinationPath);
            logger.info("Successfully moved file to {}", destinationPath);
        } catch (SftpException e) {
            logger.error("Failed to move file from {} to {}: {}", sourcePath, destinationPath, e.getMessage());
            throw e;
        }
    }

    /**
     * Ensure remote directory exists, create if it doesn't
     */
    private void ensureRemoteDirectoryExists(String remoteDir) throws SftpException {
        try {
            channel.cd(remoteDir);
            logger.info("Remote directory exists: {}", remoteDir);
        } catch (SftpException e) {
            try {
                logger.info("Creating remote directory: {}", remoteDir);
                channel.mkdir(remoteDir);
                channel.cd(remoteDir);
                logger.info("Successfully created and changed to directory: {}", remoteDir);
            } catch (SftpException createException) {
                logger.error("Failed to create remote directory {}: {}", remoteDir, createException.getMessage());
                throw createException;
            }
        }
    }

    /**
     * Upload file to EC2 instance
     *
     * @param localFilePath Path to local file
     * @throws SftpException If upload fails
     * @throws JSchException If connection fails
     */
    public void uploadFileToEc2(String localFilePath) throws SftpException, JSchException {
        try {
            if (channel == null || !channel.isConnected()) {
                authWithKey();
            }
            ensureRemoteDirectoryExists(REMOTE_DIR);
            File localFile = new File(localFilePath);
            String fileName = localFile.getName();
            String remotePath = REMOTE_DIR + "/" + fileName;
            logger.info("Uploading {} to {}", localFilePath, remotePath);
            channel.put(localFilePath, remotePath);
            logger.info("File uploaded successfully to EC2: {}", remotePath);
        } catch (SftpException | JSchException e) {
            logger.error("Failed to upload file to EC2: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Test connection to EC2 instance
     */
    public boolean testConnection() {
        try {
            authWithKey();
            return session != null && session.isConnected() && channel != null && channel.isConnected();
        } catch (Exception e) {
            logger.error("Connection test failed: {}", e.getMessage());
            return false;
        } finally {
            close();
        }
    }

    /**
     * List all files including directories
     *
     * @param remoteDir Directory on remote from which files will be listed
     * @throws SftpException If there is any problem with listing files related to permissions etc
     * @throws JSchException If there is any problem with connection
     */
    @SuppressWarnings("unchecked")
    public void listFiles(String remoteDir) throws SftpException, JSchException {
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        logger.info("Listing [{}]...", remoteDir);
        channel.cd(remoteDir);
        Vector<ChannelSftp.LsEntry> files = channel.ls(".");
        for (ChannelSftp.LsEntry file : files) {
            var name = file.getFilename();
            var attrs = file.getAttrs();
            var permissions = attrs.getPermissionsString();
            var size = humanReadableByteCount(attrs.getSize());
            if (attrs.isDir()) {
                size = "PRE";
            }
            logger.info("[{}] {}({})", permissions, name, size);
        }
    }

    public static String humanReadableByteCount(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public String findLatestEodFile() throws JSchException, SftpException {
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.cd(REMOTE_DIR);
        Vector<ChannelSftp.LsEntry> files = channel.ls(".");
        logger.info("FILES LIST: {}", Arrays.toString(files.toArray()));
        String todayDate = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String expectedFileName = "EXT_EOD_BANCA_ACCT_TXNS_" + todayDate;
        String latestFile = null;
        for (ChannelSftp.LsEntry file : files) {
            String fileName = file.getFilename();
            logger.info("FILE NAME: {}", fileName);
            if (fileName.equalsIgnoreCase(expectedFileName)) {
                if (latestFile == null || fileName.compareTo(latestFile) > 0) {
                    latestFile = fileName;
                }
            }
        }
        logger.info("LATEST FILE: {}", latestFile);
        return latestFile;
    }

    /**
     * Download a file from remote
     *
     * @param remotePath full path of remote file
     * @param localPath  full path of where to save file locally
     * @throws SftpException If there is any problem with downloading file related permissions etc
     */
    public String downloadFile(String remotePath, String localPath) throws SftpException {
        logger.info("Downloading [{}] to [{}]...", remotePath, localPath);
        if (channel == null || !session.isConnected()) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.get(remotePath, localPath);
        return localPath;
    }

    public void uploadFile(String localPath) throws SftpException {
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.put(localPath, REMOTE_DIR);
    }

    /**
     * Delete a file on remote
     *
     * @param remoteFile full path of remote file
     * @throws SftpException If there is any problem with deleting file related to permissions etc
     */
    public void delete(String remoteFile) throws SftpException {
        logger.info("Deleting [{}]...", remoteFile);
        if (channel == null) {
            throw new IllegalArgumentException("Connection is not available");
        }
        channel.rm(remoteFile);
    }

    /**
     * Disconnect from remote
     */
    public void close() {
        if (channel != null && channel.isConnected()) {
            channel.exit();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}