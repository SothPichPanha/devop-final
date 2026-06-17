package com.example.demo.service;

import com.example.demo.model.Profile;
import com.example.demo.model.ProfileType;
import com.example.demo.repository.ProfileRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    @Value("${app.photo.dir:uploads/photos}")
    private String photoDir;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    @Override
    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));
    }

    @Override
    public Profile findByUuid(String uuid) {
        return profileRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with uuid: " + uuid));
    }

    @Override
    public Profile create(Profile profile, MultipartFile photo) throws IOException {
        profile.setUuid(UUID.randomUUID().toString());
        profile.setRegistrationNumber(generateRegistrationNumber(profile.getType()));
        if (photo != null && !photo.isEmpty()) {
            savePhoto(profile, photo);
        }
        return profileRepository.save(profile);
    }

    @Override
    public Profile update(Long id, Profile profile, MultipartFile photo) throws IOException {
        Profile existing = findById(id);
        existing.setFullName(profile.getFullName());
        existing.setType(profile.getType());
        existing.setDepartment(profile.getDepartment());
        existing.setTitle(profile.getTitle());
        existing.setEmail(profile.getEmail());
        existing.setPhone(profile.getPhone());
        existing.setBloodGroup(profile.getBloodGroup());
        existing.setDateOfBirth(profile.getDateOfBirth());
        existing.setExpiryDate(profile.getExpiryDate());
        existing.setTemplate(profile.getTemplate());
        existing.setBarcodeType(profile.getBarcodeType());
        if (photo != null && !photo.isEmpty()) {
            savePhoto(existing, photo);
        }
        return profileRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Profile profile = findById(id);
        if (profile.hasPhoto()) {
            deletePhotoFile(profile);
        }
        profileRepository.delete(profile);
    }

    @Override
    public List<Profile> searchByName(String name) {
        return profileRepository.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    public List<Profile> findByType(ProfileType type) {
        return profileRepository.findByType(type);
    }

    @Override
    public byte[] generateIdCardPdf(Long id) throws IOException {
        Profile profile = findById(id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);
        PdfFont boldFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        document.add(new Paragraph("ID CARD")
                .setFont(boldFont).setFontSize(20));
        document.add(new Paragraph("Registration: " + profile.getRegistrationNumber()).setFont(normalFont));
        document.add(new Paragraph("Name: " + profile.getFullName()).setFont(normalFont));
        document.add(new Paragraph("Type: " + profile.getType()).setFont(normalFont));
        if (profile.getDepartment() != null)
            document.add(new Paragraph("Department: " + profile.getDepartment()).setFont(normalFont));
        if (profile.getTitle() != null)
            document.add(new Paragraph("Title: " + profile.getTitle()).setFont(normalFont));
        if (profile.getEmail() != null)
            document.add(new Paragraph("Email: " + profile.getEmail()).setFont(normalFont));
        if (profile.getPhone() != null)
            document.add(new Paragraph("Phone: " + profile.getPhone()).setFont(normalFont));
        if (profile.getBloodGroup() != null)
            document.add(new Paragraph("Blood Group: " + profile.getBloodGroup()).setFont(normalFont));
        if (profile.getIssueDate() != null)
            document.add(new Paragraph("Issue Date: " + profile.getIssueDate()).setFont(normalFont));
        if (profile.getExpiryDate() != null)
            document.add(new Paragraph("Expiry Date: " + profile.getExpiryDate()).setFont(normalFont));

        try {
            byte[] qrBytes = generateQRCode(
                    "http://verify.example.com/card/" + profile.getUuid(), 150, 150);
            Image qrImage = new Image(ImageDataFactory.create(qrBytes));
            document.add(qrImage);
        } catch (Exception e) {
            document.add(new Paragraph("QR: unavailable").setFont(normalFont));
        }

        document.close();
        return baos.toByteArray();
    }

    @Override
    public byte[] generateQRCode(String data, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data,
                    com.google.zxing.BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    @Override
    public byte[] generateBarcode(String data, String format, int width, int height) {
        try {
            BarcodeFormat barcodeFormat = "EAN_13".equalsIgnoreCase(format)
                    ? BarcodeFormat.EAN_13
                    : BarcodeFormat.CODE_128;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, barcodeFormat, width, height, hints);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }

    @Override
    public List<Profile> batchCreate(List<Profile> profiles) {
        long base = profileRepository.count();
        int i = 0;
        for (Profile profile : profiles) {
            if (profile.getUuid() == null) {
                profile.setUuid(UUID.randomUUID().toString());
            }
            if (profile.getRegistrationNumber() == null) {
                String prefix;
                if (profile.getType() == ProfileType.STUDENT) prefix = "STU";
                else if (profile.getType() == ProfileType.EMPLOYEE) prefix = "EMP";
                else prefix = "USR";
                String year = String.valueOf(Year.now().getValue());
                profile.setRegistrationNumber(year + "-" + prefix + "-" + String.format("%03d", base + ++i));
            }
        }
        return profileRepository.saveAll(profiles);
    }

    @Override
    public String generateRegistrationNumber(ProfileType type) {
        String prefix;
        if (type == ProfileType.STUDENT) prefix = "STU";
        else if (type == ProfileType.EMPLOYEE) prefix = "EMP";
        else prefix = "USR";
        String year = String.valueOf(Year.now().getValue());
        long count = profileRepository.count() + 1;
        return year + "-" + prefix + "-" + String.format("%03d", count);
    }

    private void savePhoto(Profile profile, MultipartFile photo) throws IOException {
        String originalFilename = StringUtils.cleanPath(photo.getOriginalFilename());
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = profile.getUuid() + ext;
        Path uploadPath = Paths.get(photoDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        Path targetPath = uploadPath.resolve(filename);
        Files.copy(photo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        profile.setPhotoFileName(filename);
        profile.setPhotoContentType(photo.getContentType());
    }

    private void deletePhotoFile(Profile profile) {
        try {
            Path filePath = Paths.get(photoDir).resolve(profile.getPhotoFileName());
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }
}
