package com.example.projectonuf5.controller;

import com.example.projectonuf5.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public String listFiles(Model model) {
        String currentPath = System.getProperty("user.home");
        List<String> files = fileService.getFilesInDirectory(new File(currentPath));
        model.addAttribute("files", files);
        model.addAttribute("currentPath", currentPath);
        return "files/list";
    }

    @GetMapping("/search")
    public String searchFiles(@RequestParam String query, Model model) {
        String currentPath = System.getProperty("user.home");
        List<String> results = fileService.searchFiles(new File(currentPath), query);
        model.addAttribute("files", results);
        model.addAttribute("currentPath", currentPath);
        model.addAttribute("searchQuery", query);
        return "files/list";
    }

    @PostMapping("/create-directory")
    public String createDirectory(@RequestParam String path, @RequestParam String name, RedirectAttributes redirectAttributes) {
        boolean success = fileService.createDirectory(new File(path), name);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Directory created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not create directory");
        }
        return "redirect:/files";
    }

    @PostMapping("/create-file")
    public String createFile(@RequestParam String path, @RequestParam String name, RedirectAttributes redirectAttributes) {
        boolean success = fileService.createFile(new File(path), name);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "File created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not create file");
        }
        return "redirect:/files";
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam String path, RedirectAttributes redirectAttributes) {
        boolean success = fileService.deleteFileOrDirectory(new File(path));
        if (success) {
            redirectAttributes.addFlashAttribute("message", "File deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not delete file");
        }
        return "redirect:/files";
    }

    @PostMapping("/rename")
    public String renameFile(@RequestParam String path, @RequestParam String newName, RedirectAttributes redirectAttributes) {
        boolean success = fileService.renameFileOrDirectory(new File(path), newName);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "File renamed successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not rename file");
        }
        return "redirect:/files";
    }
} 