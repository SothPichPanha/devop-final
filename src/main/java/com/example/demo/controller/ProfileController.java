package com.example.demo.controller;

import com.example.demo.model.Profile;
import com.example.demo.model.ProfileType;
import com.example.demo.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("profiles", profileService.findAll());
        return "profile-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("profile", new Profile());
        model.addAttribute("types", ProfileType.values());
        return "profile-form";
    }

    @PostMapping
    public String create(@ModelAttribute Profile profile,
                         @RequestParam("photo") MultipartFile photo,
                         RedirectAttributes redirectAttributes) throws IOException {
        profileService.create(profile, photo);
        redirectAttributes.addFlashAttribute("message", "Profile created successfully");
        return "redirect:/profiles";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("profile", profileService.findById(id));
        model.addAttribute("types", ProfileType.values());
        return "profile-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Profile profile,
                         @RequestParam("photo") MultipartFile photo,
                         RedirectAttributes redirectAttributes) throws IOException {
        profileService.update(id, profile, photo);
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        return "redirect:/profiles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        profileService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Profile deleted successfully");
        return "redirect:/profiles";
    }

    @GetMapping("/search")
    public String search(@RequestParam("name") String name, Model model) {
        model.addAttribute("profiles", profileService.searchByName(name));
        return "profile-list";
    }

    @GetMapping("/{id}/preview")
    public String preview(@PathVariable Long id, Model model) {
        Profile profile = profileService.findById(id);
        model.addAttribute("profile", profile);
        try {
            String qrData = "http://verify.example.com/card/" + profile.getUuid();
            byte[] qrBytes = profileService.generateQRCode(qrData, 200, 200);
            String qrBase64 = java.util.Base64.getEncoder().encodeToString(qrBytes);
            model.addAttribute("qrCode", "data:image/png;base64," + qrBase64);
        } catch (Exception e) {
            model.addAttribute("qrCode", null);
        }
        return "id-card-preview";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws IOException {
        byte[] pdfBytes = profileService.generateIdCardPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=idcard-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> generateQr(@PathVariable Long id) {
        Profile profile = profileService.findById(id);
        String data = "http://verify.example.com/card/" + profile.getUuid();
        byte[] qrBytes = profileService.generateQRCode(data, 300, 300);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrBytes);
    }

    @GetMapping("/{id}/barcode")
    public ResponseEntity<byte[]> generateBarcode(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "CODE_128") String format) {
        Profile profile = profileService.findById(id);
        String data = profile.getRegistrationNumber();
        byte[] barcodeBytes = profileService.generateBarcode(data, format, 300, 100);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(barcodeBytes);
    }

    @PostMapping("/batch")
    public String batchCreate(@RequestParam("profilesJson") String profilesJson,
                              RedirectAttributes redirectAttributes) {
        throw new UnsupportedOperationException("Batch creation via JSON not yet implemented in UI");
    }

    @GetMapping("/type/{type}")
    public String findByType(@PathVariable ProfileType type, Model model) {
        model.addAttribute("profiles", profileService.findByType(type));
        return "profile-list";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Profile> listApi() {
        return profileService.findAll();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Profile getApi(@PathVariable Long id) {
        return profileService.findById(id);
    }

    @PostMapping("/api")
    @ResponseBody
    public Profile createApi(@RequestBody Profile profile) {
        try {
            return profileService.create(profile, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create profile", e);
        }
    }
}
