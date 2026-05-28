package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("PDF")
@Getter
@Setter
@NoArgsConstructor
public class InformationPdf extends Information {

    @Column(name = "informationPdfURL", nullable = false, length = 255)
    private String pdfUrl;

    public InformationPdf(String title,
                          String author,
                          String slug,
                          List<String> tags,
                          Category category,
                          String pdfUrl) {
        super(title, author, slug, tags, category);
        this.pdfUrl = pdfUrl;
        this.setType(InformationType.PDF);
    }
}
