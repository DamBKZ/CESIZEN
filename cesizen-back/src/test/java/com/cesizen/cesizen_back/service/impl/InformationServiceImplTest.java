package com.cesizen.cesizen_back.service.impl;

import com.cesizen.cesizen_back.entity.*;
import com.cesizen.cesizen_back.repository.InformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InformationServiceImplTest {

    @Mock
    private InformationRepository informationRepository;

    @InjectMocks
    private InformationServiceImpl informationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Information info = new InformationArticle();
        Page<Information> page = new PageImpl<>(List.of(info));

        when(informationRepository.findAll(pageable)).thenReturn(page);

        Page<Information> result = informationService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(informationRepository).findAll(pageable);
    }

    @Test
    void findById_shouldReturnInformation() {
        Information info = new InformationArticle();
        info.setInformationId("abc");

        when(informationRepository.findById("abc")).thenReturn(Optional.of(info));

        Information result = informationService.findById("abc");

        assertEquals("abc", result.getInformationId());
    }

    @Test
    void findById_shouldThrowException() {
        when(informationRepository.findById("404")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> informationService.findById("404"));
    }

    @Test
    void create_shouldSaveInformation() {
        Information info = new InformationArticle();

        when(informationRepository.save(info)).thenReturn(info);

        Information result = informationService.create(info);

        assertEquals(info, result);
        verify(informationRepository).save(info);
    }

    @Test
    void update_shouldUpdateFields() {
        InformationArticle existing = new InformationArticle();
        existing.setInformationId("id1");
        existing.setTitle("Old");
        existing.setContent("Old content");

        InformationArticle updated = new InformationArticle();
        updated.setTitle("New");
        updated.setContent("New content");

        when(informationRepository.findById("id1")).thenReturn(Optional.of(existing));
        when(informationRepository.save(existing)).thenReturn(existing);

        Information result = informationService.update("id1", updated);

        assertEquals("New", result.getTitle());
        assertEquals("New content", ((InformationArticle) result).getContent());
    }

    @Test
    void delete_shouldCallRepository() {
        informationService.delete("id1");
        verify(informationRepository).deleteById("id1");
    }
}
