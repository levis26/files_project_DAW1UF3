package com.example.files_project_DAW1UF3.controller;

import com.example.files_project_DAW1UF3.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/")
    public String home() {
        return "redirect:/files";
    }

    @GetMapping("/files")
    public String listFiles(@RequestParam(required = false) String path, Model model) {
        String currentPath = path != null ? path : System.getProperty("user.home");
        File currentDir = new File(currentPath);
        
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            currentPath = System.getProperty("user.home");
            currentDir = new File(currentPath);
        }

        List<String> files = fileService.getFilesInDirectory(currentDir);
        model.addAttribute("files", files);
        model.addAttribute("currentPath", currentPath);
        model.addAttribute("parentPath", currentDir.getParent());
        return "files/list";
    }

    @GetMapping("/files/search")
    public String searchFiles(@RequestParam(required = false) String query, 
                            @RequestParam(required = false) String path,
                            Model model) {
        String currentPath = path != null ? path : System.getProperty("user.home");
        File currentDir = new File(currentPath);
        
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            currentPath = System.getProperty("user.home");
            currentDir = new File(currentPath);
        }

        List<String> results;
        if (query != null && !query.trim().isEmpty()) {
            results = fileService.searchFiles(currentDir, query.trim());
            model.addAttribute("searchQuery", query);
        } else {
            results = fileService.getFilesInDirectory(currentDir);
        }
        
        model.addAttribute("files", results);
        model.addAttribute("currentPath", currentPath);
        model.addAttribute("parentPath", currentDir.getParent());
        return "files/list";
    }

    @PostMapping("/files/create-directory")
    public String createDirectory(@RequestParam String path, @RequestParam String name, RedirectAttributes redirectAttributes) {
        boolean success = fileService.createDirectory(new File(path), name);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Directory created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not create directory");
        }
        return "redirect:/files?path=" + path;
    }

    @PostMapping("/files/create-file")
    public String createFile(@RequestParam String path, @RequestParam String name, RedirectAttributes redirectAttributes) {
        boolean success = fileService.createFile(new File(path), name);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "File created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not create file");
        }
        return "redirect:/files?path=" + path;
    }

    @PostMapping("/files/delete")
    public String deleteFile(@RequestParam String path, RedirectAttributes redirectAttributes) {
        File file = new File(path);
        String parentPath = file.getParent();
        boolean success = fileService.deleteFileOrDirectory(file);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "File deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not delete file");
        }
        return "redirect:/files?path=" + parentPath;
    }

    @PostMapping("/files/rename")
    public String renameFile(@RequestParam String path, @RequestParam String newName, RedirectAttributes redirectAttributes) {
        File file = new File(path);
        String parentPath = file.getParent();
        boolean success = fileService.renameFileOrDirectory(file, newName);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "File renamed successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not rename file");
        }
        return "redirect:/files?path=" + parentPath;
    }

    @GetMapping("/files/suggestions")
    @ResponseBody
    public List<String> getSuggestions(
        @RequestParam String query,
        @RequestParam String path) {
        
        File currentDir = new File(path != null ? path : System.getProperty("user.home"));
        return fileService.getSuggestions(currentDir, query);
    }
} 