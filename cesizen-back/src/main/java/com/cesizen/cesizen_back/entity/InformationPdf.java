package com.cesizen.cesizen_back.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("PDF")
@Getter
@Setter
@NoArgsConstructor
public class InformationPdf extends Information {

    @Column(name = "informationPdfUrl", length = 500)
    private String pdfUrl;

    @Override
    public InformationType getType() {
        return InformationType.PDF;
    }

    public InformationPdf(String title, String author, String slug, List<String> tags,
                          Category category, String pdfUrl) {
        super(null, title, category, null, author, tags, slug);
        this.pdfUrl = pdfUrl;
    }
}

