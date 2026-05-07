package com.cesizen.cesizen_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("PDF")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformationPdf extends Information {

    @Column(name = "pdfURL", length = 500)
    private String pdfUrl;

    @Override
    public InformationType getType() {
        return InformationType.PDF;
    }
}
