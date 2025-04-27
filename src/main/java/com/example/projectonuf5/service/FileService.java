package com.example.projectonuf5.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    public List<String> getFilesInDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            return List.of(files).stream()
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<String> searchFiles(File directory, String searchText) {
        List<String> results = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        results.add(file.getAbsolutePath());
                    }
                    if (file.isDirectory()) {
                        results.addAll(searchFiles(file, searchText));
                    }
                }
            }
        }
        return results;
    }

    public boolean createDirectory(File directory, String name) {
        return new File(directory, name).mkdir();
    }

    public boolean createFile(File directory, String name) {
        try {
            return new File(directory, name).createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteFileOrDirectory(File file) {
        return file.delete();
    }

    public boolean renameFileOrDirectory(File file, String newName) {
        return file.renameTo(new File(file.getParent(), newName));
    }
} 