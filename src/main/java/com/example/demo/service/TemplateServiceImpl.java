package com.example.demo.service;

import com.example.demo.model.Template;
import com.example.demo.repository.TemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public List<Template> findAll() {
        return templateRepository.findAll();
    }

    @Override
    public Template findById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id: " + id));
    }

    @Override
    public Template findByCode(String code) {
        return templateRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with code: " + code));
    }

    @Override
    public Template create(Template template) {
        return templateRepository.save(template);
    }

    @Override
    public Template update(Long id, Template template) {
        Template existing = findById(id);
        existing.setName(template.getName());
        existing.setCode(template.getCode());
        existing.setOrganizationName(template.getOrganizationName());
        existing.setLayout(template.getLayout());
        existing.setPrimaryColor(template.getPrimaryColor());
        existing.setSecondaryColor(template.getSecondaryColor());
        existing.setTextColor(template.getTextColor());
        existing.setTagline(template.getTagline());
        return templateRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Template template = findById(id);
        templateRepository.delete(template);
    }

    @Override
    public boolean existsByCode(String code) {
        return templateRepository.existsByCode(code);
    }
}
