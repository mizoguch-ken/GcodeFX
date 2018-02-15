/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import ken.mizoguch.console.Console;

/**
 *
 * @author mizoguch-ken
 */
public class JavaLibrary {

    private static class Finder extends SimpleFileVisitor<Path> {

        private final PathMatcher matcher;
        private final List<Path> pathMatches;

        public Finder(String pattern) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            pathMatches = new ArrayList<>();
        }

        public void find(Path file) {
            Path name = file.getFileName();
            if (name != null && matcher.matches(name)) {
                pathMatches.add(file);
            }
        }

        public List<Path> done() {
            return pathMatches;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            find(file);
            return CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            find(dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return CONTINUE;
        }
    }

    private static String className = "";
    private static final List<Path> PATHS = new ArrayList<>();
    private static final List<String> LIBS = new ArrayList<>();

    /**
     *
     * @return
     */
    public static String getClassName() {
        return className;
    }

    /**
     *
     * @param name
     */
    public static void setClassName(String name) {
        className = name;
    }

    /**
     *
     * @param root
     * @param pattern
     */
    public static void findFile(Path root, String pattern) {
        try {
            Finder finder = new Finder(pattern);
            Files.walkFileTree(root, finder);
            finder.done();
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.JAVA_LIBRARY.toString(), ex);
        }
    }

    /**
     *
     * @param path
     * @return
     */
    public static boolean isLibraryPath(Path path) {
        return PATHS.stream().anyMatch((t) -> (t.equals(path)));
    }

    /**
     *
     * @param classLoader
     * @param zipPath
     * @param systemLoad
     * @return
     */
    public static boolean extractResourceZip(ClassLoader classLoader, String zipPath, boolean systemLoad) {
        ZipInputStream zipInputStream;
        ZipEntry zipEntry;
        Path local, file;
        String zipName;

        if (zipPath != null) {
            if (!zipPath.isEmpty()) {
                zipName = removeFileExtension(Paths.get(zipPath).getFileName());
                for (int i = 0; i < 10; i++) {
                    local = Paths.get(System.getProperty("java.io.tmpdir"), getClassName() + "_" + getUserName() + "_" + i).resolve(zipName);
                    // library paths check
                    if (isLibraryPath(local)) {
                        return true;
                    }
                    try {
                        // copy file
                        zipInputStream = new ZipInputStream(classLoader.getResourceAsStream(zipPath));
                        zipEntry = zipInputStream.getNextEntry();
                        while (zipEntry != null) {
                            file = local.resolve(zipEntry.getName());
                            if (!Files.exists(file.getParent())) {
                                Files.createDirectories(file);
                            }
                            Files.copy(zipInputStream, file, StandardCopyOption.REPLACE_EXISTING);
                            zipInputStream.closeEntry();
                            zipEntry = zipInputStream.getNextEntry();
                        }
                        zipInputStream.closeEntry();
                        zipInputStream.close();
                        // add PATHS
                        PATHS.add(local);
                        // set java.library.path
                        return setLibraryPath(local.toString(), systemLoad);
                    } catch (IOException ex) {
                        Console.writeStackTrace(DesignEnums.JAVA_LIBRARY.toString(), ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param classLoader
     * @param resourcePath
     * @param systemLoad
     * @return
     */
    public static boolean extractResourceLibrary(ClassLoader classLoader, String resourcePath, boolean systemLoad) {
        Path resource, local;
        String libraryName;

        if (resourcePath != null) {
            if (!resourcePath.isEmpty()) {
                resource = Paths.get(resourcePath);

                // library name
                libraryName = removeFileExtension(resource.getFileName());
                if (isWindows()) {
                } else if (isLinux()) {
                    libraryName = libraryName.substring(libraryName.indexOf("lib") + 3);
                } else if (isMac()) {
                    libraryName = libraryName.substring(libraryName.indexOf("lib") + 3);
                } else {
                }
                // library load check local
                if (isLibrary(libraryName)) {
                    return false;
                }

                // library load check system
                if (systemLoad && loadLibrary(libraryName, systemLoad)) {
                    return true;
                } else {
                    for (int i = 0; i < 10; i++) {
                        local = Paths.get(System.getProperty("java.io.tmpdir"), getClassName() + "_" + getUserName() + "_" + i);
                        if (addResourceLibraryPath(classLoader, resourcePath, local, libraryName, systemLoad)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param classLoader
     * @param resourcePath
     * @param localPath
     * @param libraryName
     * @param systemLoad
     * @return
     */
    public static boolean addResourceLibraryPath(ClassLoader classLoader, String resourcePath, Path localPath, String libraryName, boolean systemLoad) {
        String prefix, suffix;

        try {
            // mkdir
            if (!Files.exists(localPath)) {
                Files.createDirectories(localPath);
            }
            // set prefix suffix
            if (isWindows()) {
                prefix = "";
                suffix = ".dll";
            } else if (isLinux()) {
                prefix = "lib";
                suffix = ".so";
            } else if (isMac()) {
                prefix = "lib";
                suffix = ".dylib";
            } else {
                prefix = "";
                suffix = "";
            }
            // copy file
            Files.copy(classLoader.getResourceAsStream(resourcePath), localPath.resolve(prefix + libraryName + suffix), StandardCopyOption.REPLACE_EXISTING);
            // library paths check
            if (isLibraryPath(localPath)) {
                return loadLibrary(libraryName, systemLoad);
            }
            // add PATHS
            PATHS.add(localPath);
            // set java.library.path
            return setLibraryPath(libraryName, systemLoad);
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.JAVA_LIBRARY.toString(), ex);
        }
        return false;
    }

    /**
     *
     * @param path
     * @param libraryName
     * @param systemLoad
     * @return
     */
    public static boolean addLibraryPath(Path path, String libraryName, boolean systemLoad) {
        // library paths check
        if (Files.exists(path)) {
            if (Files.isRegularFile(path)) {
                path = path.getParent();
            }
            if (isLibraryPath(path)) {
                return loadLibrary(libraryName, systemLoad);
            }
            // add PATHS
            PATHS.add(path);
            // set java.library.path
            return setLibraryPath(libraryName, systemLoad);
        }
        return false;
    }

    /**
     *
     * @param libraryName
     * @param systemLoad
     * @return
     */
    public static boolean setLibraryPath(String libraryName, boolean systemLoad) {
        if (!PATHS.isEmpty()) {
            StringBuilder pathString = new StringBuilder(System.getProperty("java.library.path"));
            PATHS.stream().forEach((p) -> {
                pathString.append(File.pathSeparator);
                try {
                    pathString.append(p.toRealPath().toString());
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.JAVA_LIBRARY.toString(), ex);
                }
            });

            try {
                System.setProperty("java.library.path", pathString.toString());
                Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);
                return loadLibrary(libraryName, systemLoad);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Console.writeStackTrace(DesignEnums.JAVA_LIBRARY.toString(), ex);
            }
        }
        return false;
    }

    /**
     *
     * @param libraryName
     * @return
     */
    public static boolean isLibrary(String libraryName) {
        return LIBS.stream().anyMatch((lib) -> (lib.equals(libraryName)));
    }

    /**
     *
     * @param libraryName
     * @return
     */
    public static boolean loadLibrary(String libraryName) {
        return loadLibrary(libraryName, true);
    }

    /**
     *
     * @param libraryName
     * @param systemLoad
     * @return
     */
    public static boolean loadLibrary(String libraryName, boolean systemLoad) {
        if (libraryName != null) {
            if (!libraryName.isEmpty()) {
                try {
                    if (isLibrary(libraryName)) {
                        return false;
                    }
                    if (systemLoad) {
                        System.loadLibrary(libraryName);
                    }
                    LIBS.add(libraryName);
                    return true;
                } catch (UnsatisfiedLinkError ex) {
                } catch (SecurityException | NullPointerException ex) {
                    Console.writeStackTrace(DesignEnums.JAVA_LIBRARY.toString(), ex);
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).startsWith("windows");
    }

    /**
     *
     * @return
     */
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).startsWith("linux");
    }

    /**
     *
     * @return
     */
    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).startsWith("mac");
    }

    /**
     *
     * @return
     */
    public static boolean is32Bit() {
        String arch;

        arch = System.getProperty("sun.arch.data.model");
        if (arch != null) {
            arch = arch.trim();
            if (!arch.isEmpty()) {
                return arch.equals("32");
            }
        }

        arch = System.getProperty("os.arch");
        if (arch != null) {
            arch = arch.trim();
            if (!arch.isEmpty()) {
                return arch.endsWith("86");
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static boolean is64Bit() {
        String arch;

        arch = System.getProperty("sun.arch.data.model");
        if (arch != null) {
            arch = arch.trim();
            if (!arch.isEmpty()) {
                return arch.equals("64");
            }
        }

        arch = System.getProperty("os.arch");
        if (arch != null) {
            arch = arch.trim();
            if (!arch.isEmpty()) {
                return arch.endsWith("64");
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     *
     * @param file
     * @return
     */
    public static String removeFileExtension(Path file) {
        return removeFileExtension(file.getFileName().toString());
    }

    /**
     *
     * @param name
     * @return
     */
    public static String removeFileExtension(String name) {
        int lastDotPos = name.lastIndexOf('.');

        if (lastDotPos < 1) {
            return name;
        } else {
            return name.substring(0, lastDotPos);
        }
    }
}
