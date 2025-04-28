package com.example.files_project_DAW1UF3.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

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
        if (searchText == null || searchText.trim().isEmpty()) {
            return getFilesInDirectory(directory);
        }
    
        String searchTextLower = searchText.toLowerCase();
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        List<Future<?>> futures = new ArrayList<>();
    
        searchFilesRecursive(directory, searchTextLower, results, futures);
    
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                // Manejar error de ejecución
            }
        }
    
        return new ArrayList<>(results);
    }

    private void searchFilesRecursive(File directory, String searchText,
            ConcurrentLinkedQueue<String> results,
            List<Future<?>> futures) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().toLowerCase().contains(searchText)) {
                results.add(file.getAbsolutePath());
            }

            if (file.isDirectory()) {
                futures.add(executorService.submit(() -> 
                    searchFilesRecursive(file, searchText, results, futures)
                ));
            }
        }
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

    public List<String> getSuggestions(File directory, String query) {
        String queryLower = query.toLowerCase();
        return Arrays.stream(directory.listFiles())
            .filter(file -> file.getName().toLowerCase().contains(queryLower))
            .map(File::getName)
            .limit(10) // Limitar sugerencias
            .collect(Collectors.toList());
    }
} 