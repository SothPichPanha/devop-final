package com.example.demo.service;

import com.example.demo.model.Template;

import java.util.List;

public interface TemplateService {

    List<Template> findAll();

    Template findById(Long id);

    Template findByCode(String code);

    Template create(Template template);

    Template update(Long id, Template template);

    void delete(Long id);

    boolean existsByCode(String code);
}
