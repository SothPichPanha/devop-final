package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 120)
    private String organizationName;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String layout = "VERTICAL";

    @Column(nullable = false, length = 7)
    @Builder.Default
    private String primaryColor = "#1d4ed8";

    @Column(nullable = false, length = 7)
    @Builder.Default
    private String secondaryColor = "#e0e7ff";

    @Column(nullable = false, length = 7)
    @Builder.Default
    private String textColor = "#111827";

    @Column(length = 255)
    private String tagline;
}
