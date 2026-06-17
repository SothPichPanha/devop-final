package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.TemplateRepository;
import com.example.demo.service.ProfileService;
import com.example.demo.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FinalApplicationTests {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private TemplateService templateService;

	@Test
	void contextLoads() {
	}

	@Test
	void testProfileRepositoryExists() {
		assertThat(profileRepository).isNotNull();
	}

	@Test
	void testTemplateRepositoryExists() {
		assertThat(templateRepository).isNotNull();
	}

	@Test
	void testProfileServiceExists() {
		assertThat(profileService).isNotNull();
	}

	@Test
	void testTemplateServiceExists() {
		assertThat(templateService).isNotNull();
	}

	@Test
	void testCreateAndFindProfile() {
		Profile profile = Profile.builder()
				.fullName("Test User")
				.type(ProfileType.STUDENT)
				.uuid("test-uuid-1")
				.registrationNumber("2026-TEST-001")
				.email("test@example.com")
				.build();
		Profile saved = profileRepository.save(profile);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getFullName()).isEqualTo("Test User");

		Profile found = profileRepository.findByUuid("test-uuid-1").orElse(null);
		assertThat(found).isNotNull();
		assertThat(found.getRegistrationNumber()).isEqualTo("2026-TEST-001");
	}

	@Test
	void testCreateAndFindTemplate() {
		Template template = Template.builder()
				.code("TEST-TPL")
				.name("Test Template")
				.organizationName("Test Org")
				.build();
		Template saved = templateRepository.save(template);
		assertThat(saved.getId()).isNotNull();

		Template found = templateRepository.findByCode("TEST-TPL").orElse(null);
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Test Template");
	}

	@Test
	void testProfileBuilder() {
		Profile profile = new ProfileBuilder()
				.firstName("John")
				.lastName("Doe")
				.email("john@example.com")
				.phone("1234567890")
				.profileType(ProfileType.EMPLOYEE)
				.build();
		assertThat(profile.getFullName()).isEqualTo("John Doe");
		assertThat(profile.getEmail()).isEqualTo("john@example.com");
		assertThat(profile.getType()).isEqualTo(ProfileType.EMPLOYEE);
	}

	@Test
	void testRegistrationNumberGeneration() {
		String regNumber = profileService.generateRegistrationNumber(ProfileType.STUDENT);
		assertThat(regNumber).startsWith("2026-STU-");
	}

	@Test
	void testQRCodeGeneration() {
		byte[] qrBytes = profileService.generateQRCode("test-data", 100, 100);
		assertThat(qrBytes).isNotNull();
		assertThat(qrBytes.length).isGreaterThan(0);
	}

	@Test
	void testBarcodeGeneration() {
		byte[] barcodeBytes = profileService.generateBarcode("TEST123", "CODE_128", 200, 80);
		assertThat(barcodeBytes).isNotNull();
		assertThat(barcodeBytes.length).isGreaterThan(0);
	}

	@Test
	void testProfileTypeEnum() {
		assertThat(ProfileType.values()).hasSize(3);
		assertThat(ProfileType.valueOf("STUDENT")).isEqualTo(ProfileType.STUDENT);
		assertThat(ProfileType.valueOf("EMPLOYEE")).isEqualTo(ProfileType.EMPLOYEE);
		assertThat(ProfileType.valueOf("USER")).isEqualTo(ProfileType.USER);
	}

	@Test
	void testBarcodeTypeEnum() {
		assertThat(BarcodeType.values()).hasSize(2);
		assertThat(BarcodeType.valueOf("CODE_128")).isEqualTo(BarcodeType.CODE_128);
		assertThat(BarcodeType.valueOf("EAN_13")).isEqualTo(BarcodeType.EAN_13);
	}

	@Test
	void testSearchProfiles() {
		List<Profile> results = profileService.searchByName("Test");
		assertThat(results).isNotNull();
	}

	@Test
	void testFindByType() {
		List<Profile> students = profileService.findByType(ProfileType.STUDENT);
		assertThat(students).isNotNull();
	}

	@Test
	void testBatchCreate() {
		Profile p1 = Profile.builder().fullName("Batch1").type(ProfileType.USER).build();
		Profile p2 = Profile.builder().fullName("Batch2").type(ProfileType.USER).build();
		List<Profile> saved = profileService.batchCreate(List.of(p1, p2));
		assertThat(saved).hasSize(2);
		for (Profile p : saved) {
			assertThat(p.getUuid()).isNotNull();
			assertThat(p.getRegistrationNumber()).isNotNull();
		}
	}
}
