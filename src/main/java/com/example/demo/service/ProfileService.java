package com.example.demo.service;

import com.example.demo.model.Profile;
import com.example.demo.model.ProfileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProfileService {

    List<Profile> findAll();

    Profile findById(Long id);

    Profile findByUuid(String uuid);

    Profile create(Profile profile, MultipartFile photo) throws IOException;

    Profile update(Long id, Profile profile, MultipartFile photo) throws IOException;

    void delete(Long id);

    List<Profile> searchByName(String name);

    List<Profile> findByType(ProfileType type);

    byte[] generateIdCardPdf(Long id) throws IOException;

    byte[] generateQRCode(String data, int width, int height);

    byte[] generateBarcode(String data, String format, int width, int height);

    List<Profile> batchCreate(List<Profile> profiles);

    String generateRegistrationNumber(ProfileType type);
}
