package com.ok.utils;

import java.io.*;
import java.nio.file.*;

/**
 * 文件工具类
 * 提供文件读写、路径管理等通用功能
 */
public class FileUtil {

    // ==================== 私有辅助方法 ====================

    /**
     * 确保文件的父目录存在
     * @param filePath 文件路径
     */
    private static void ensureParentDirectory(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                System.err.println("创建目录失败: " + parentDir.getAbsolutePath());
            }
        }
    }

    // ==================== 文件读写基础方法 ====================

    /**
     * 读取文本文件内容
     * @param filePath 文件路径
     * @return 文件内容字符串，失败返回null
     */
    public static String readTextFile(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            System.err.println("读取文件失败: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 写入文本文件
     * @param filePath 文件路径
     * @param content 文件内容
     * @return 是否写入成功
     */
    public static boolean writeTextFile(String filePath, String content) {
        try {
            ensureParentDirectory(filePath);
            Files.write(Paths.get(filePath), content.getBytes("UTF-8"));
            return true;
        } catch (IOException e) {
            System.err.println("写入文件失败: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 追加内容到文本文件
     * @param filePath 文件路径
     * @param content 追加的内容
     * @return 是否追加成功
     */
    public static boolean appendTextFile(String filePath, String content) {
        try {
            ensureParentDirectory(filePath);
            Files.write(Paths.get(filePath), content.getBytes("UTF-8"),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("追加文件失败: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 对象序列化 ====================

    /**
     * 将对象序列化保存到文件
     * @param obj 要保存的对象
     * @param filePath 文件路径
     * @return 是否保存成功
     */
    public static boolean saveObject(Object obj, String filePath) {
        try {
            ensureParentDirectory(filePath);
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath))) {
                oos.writeObject(obj);
                return true;
            }
        } catch (IOException e) {
            System.err.println("保存对象失败: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从文件加载序列化对象
     * @param filePath 文件路径
     * @return 加载的对象，失败返回null
     */
    public static Object loadObject(String filePath) {
        if (!fileExists(filePath)) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载对象失败: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    // ==================== JSON文件操作 ====================

    /**
     * 从JSON文件读取内容（纯文本，需要配合JSON解析库使用）
     * @param filePath 文件路径
     * @return JSON字符串，失败返回null
     */
    public static String readJsonFile(String filePath) {
        return readTextFile(filePath);
    }

    /**
     * 将对象写入JSON文件（需要配合JSON解析库，这里只写文本）
     * @param filePath 文件路径
     * @param jsonContent JSON字符串
     * @return 是否写入成功
     */
    public static boolean writeJsonFile(String filePath, String jsonContent) {
        return writeTextFile(filePath, jsonContent);
    }

    // ==================== 文件/目录操作 ====================

    /**
     * 检查文件是否存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 检查目录是否存在
     * @param dirPath 目录路径
     * @return 是否存在
     */
    public static boolean directoryExists(String dirPath) {
        Path path = Paths.get(dirPath);
        return Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * 创建目录（如果不存在）
     * @param dirPath 目录路径
     * @return 是否创建成功（已存在也返回true）
     */
    public static boolean createDirectory(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return true;
        } catch (IOException e) {
            System.err.println("创建目录失败: " + dirPath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建文件的父目录
     * @param filePath 文件路径
     * @return 是否创建成功
     */
    public static boolean createParentDirectories(String filePath) {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                System.err.println("创建父目录失败: " + parent);
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("删除文件失败: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除目录及其所有内容
     * @param dirPath 目录路径
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(String dirPath) {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            return true;
        }
        try {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            System.err.println("删除失败: " + p);
                        }
                    });
            return true;
        } catch (IOException e) {
            System.err.println("删除目录失败: " + dirPath);
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 路径操作 ====================

    /**
     * 获取文件名（不含路径）
     * @param filePath 完整路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }

    /**
     * 获取文件扩展名
     * @param filePath 文件路径
     * @return 扩展名（不含点），如 "json"，如果没有扩展名返回空字符串
     */
    public static String getFileExtension(String filePath) {
        String fileName = getFileName(filePath);
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * 获取文件名（不含扩展名）
     * @param filePath 文件路径
     * @return 文件名（不含扩展名）
     */
    public static String getFileNameWithoutExtension(String filePath) {
        String fileName = getFileName(filePath);
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(0, lastDot);
        }
        return fileName;
    }

    /**
     * 组合路径
     * @param parts 路径各部分
     * @return 组合后的路径
     */
    public static String joinPath(String... parts) {
        return Paths.get(parts[0], java.util.Arrays.copyOfRange(parts, 1, parts.length))
                .toString();
    }

    // ==================== 资源文件读取（从jar内读取） ====================

    /**
     * 从classpath读取资源文件
     * @param resourcePath 资源路径（如 "/images/plant.png"）
     * @return 文件内容字符串，失败返回null
     */
    public static String readResource(String resourcePath) {
        try (InputStream is = FileUtil.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("资源不存在: " + resourcePath);
                return null;
            }
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            System.err.println("读取资源失败: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从classpath读取资源文件为字节数组
     * @param resourcePath 资源路径
     * @return 字节数组，失败返回null
     */
    public static byte[] readResourceBytes(String resourcePath) {
        try (InputStream is = FileUtil.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("资源不存在: " + resourcePath);
                return null;
            }
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toByteArray();
        } catch (IOException e) {
            System.err.println("读取资源失败: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    // ==================== 备份和恢复 ====================

    /**
     * 备份文件
     * @param filePath 原文件路径
     * @param backupPath 备份文件路径
     * @return 是否备份成功
     */
    public static boolean backupFile(String filePath, String backupPath) {
        if (!fileExists(filePath)) {
            return false;
        }
        try {
            ensureParentDirectory(backupPath);
            Files.copy(Paths.get(filePath), Paths.get(backupPath),
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("备份文件失败: " + filePath + " -> " + backupPath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 恢复备份
     * @param backupPath 备份文件路径
     * @param filePath 目标文件路径
     * @return 是否恢复成功
     */
    public static boolean restoreBackup(String backupPath, String filePath) {
        return backupFile(backupPath, filePath);
    }

    // ==================== 获取应用目录 ====================

    /**
     * 获取应用程序数据目录（用于保存存档）
     * @param appName 应用名称
     * @return 数据目录路径
     */
    public static String getAppDataDirectory(String appName) {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        String baseDir;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            baseDir = appData != null ? appData : userHome;
        } else if (os.contains("mac")) {
            baseDir = userHome + "/Library/Application Support";
        } else {
            baseDir = userHome;
            appName = "." + appName;
        }

        String dataDir = joinPath(baseDir, appName);
        createDirectory(dataDir);
        return dataDir;
    }

    /**
     * 获取当前工作目录
     * @return 工作目录路径
     */
    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    // ==================== 文件信息 ====================

    /**
     * 获取文件大小（字节）
     * @param filePath 文件路径
     * @return 文件大小，文件不存在返回-1
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 获取文件最后修改时间
     * @param filePath 文件路径
     * @return 最后修改时间（毫秒），失败返回-1
     */
    public static long getLastModified(String filePath) {
        try {
            return Files.getLastModifiedTime(Paths.get(filePath)).toMillis();
        } catch (IOException e) {
            return -1;
        }
    }

    // ==================== 私有构造函数 ====================

    private FileUtil() {
        // 工具类不应被实例化
    }
}