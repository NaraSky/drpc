package com.rain.rpc.common.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class scanner for scanning all classes under a specified package.
 * Supports scanning class files in the project and class files in jar packages.
 */
public class ClassScanner {
    // File protocol identifier, used to determine if it is a file type URL
    private static final String FILE_PROTOCOL = "file";

    // Jar protocol identifier, used to determine if it is a jar package type URL
    private static final String JAR_PROTOCOL = "jar";

    // Class file suffix, all class files to be scanned must have this suffix
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * Get a list of all class names under the specified package name.
     *
     * @param packageName Package name to scan
     * @return List containing all class names
     * @throws Exception Exceptions that may be thrown during scanning
     */
    public static List<String> getClassNameList(String packageName) throws Exception {
        // Create a class name list to store results
        List<String> classNameList = new ArrayList<String>();
        // Set to recursive scanning
        boolean recursive = true;
        // Convert package name to directory name format
        String packageDirName = packageName.replace('.', '/');
        // Get all resource URLs under the package path
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        // Iterate through all URL resources
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            // Adopt different scanning methods according to different protocol types
            if (FILE_PROTOCOL.equals(protocol)) {
                // If it is a file protocol, scan from the file system
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (JAR_PROTOCOL.equals(protocol)) {
                // If it is a jar protocol, scan from the jar package
                packageName = findAndAddClassesInPackageByJar(packageName, classNameList, recursive, packageDirName, url);
            }
        }
        // Return class name list
        return classNameList;
    }

    /**
     * Scan all class information under the specified package in the current project.
     *
     * @param packageName   Package name to scan
     * @param packagePath   Complete path of the package on disk
     * @param recursive     Whether to call recursively
     * @param classNameList Collection of class names
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<String> classNameList) {
        // Check if the directory exists and is a directory
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // List all files and directories under the directory, filtering out qualified files
        File[] firFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // If it is recursive scanning and is a directory, or is a file ending with .class, then accept
                return (recursive && pathname.isDirectory()) || (pathname.getName().endsWith(CLASS_FILE_SUFFIX));
            }
        });

        // Iterate through all files and directories
        for (File file : firFiles) {
            if (file.isDirectory()) {
                // If it is a directory, recursively scan subdirectories
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classNameList);
            } else {
                // If it is a .class file, extract the class name and add it to the collection
                String className = file.getName().substring(0, file.getName().length() - 6);
                classNameList.add(packageName + "." + className);
            }
        }
    }

    /**
     * Scan all class information under the specified package in the Jar file.
     *
     * @param packageName    Package name to scan
     * @param classNameList  List collection storing completed class names
     * @param recursive      Whether to call recursively
     * @param packageDirName The front part name of the current package name
     * @param url            URL address of the package
     * @return processed package name for the next call
     * @throws IOException if an I/O error occurs
     */
    private static String findAndAddClassesInPackageByJar(String packageName, List<String> classNameList, boolean recursive, String packageDirName, URL url) throws IOException {
        // Get the Jar file object
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        // Get all entries in the Jar file
        Enumeration<JarEntry> entries = jar.entries();
        // Iterate through all entries
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // Process names starting with '/', removing the leading '/'
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            // If the entry name starts with the specified package directory name
            if (name.startsWith(packageDirName)) {
                // Find the position of the last '/'
                int idx = name.lastIndexOf('/');
                // If '/' is found, extract the package name
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                // If '/' is found or recursive processing is needed
                if ((idx != -1) || recursive) {
                    // If it ends with .class and is not a directory, extract the class name and add it to the collection
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()) {
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNameList.add(packageName + '.' + className);
                    }
                }
            }
        }
        // Return the processed package name
        return packageName;
    }
}