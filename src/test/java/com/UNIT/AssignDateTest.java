package com.UNIT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import application.controller.MyController;
import application.entities.Tarea;
import application.repositories.TareaRepository;
// CM7
class AssignDateUnitTest {

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private MyController controller;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignDate_success() {
        Tarea tarea = new Tarea();

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        LocalDate futureDate = LocalDate.now().plusDays(2);

        String result = controller.assignDate(futureDate, 1L, 10L, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/vivienda/10/listTareas");
        assertThat(tarea.getFechaRealizacion()).isEqualTo(futureDate);

        verify(tareaRepository).save(tarea);
        verify(redirectAttributes).addFlashAttribute(eq("successMsg"), anyString());
    }

    @Test
    void assignDate_taskNotFound() {
        when(tareaRepository.findById(1L)).thenReturn(Optional.empty());

        String result = controller.assignDate(
                LocalDate.now().plusDays(1), 1L, 10L, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/vivienda/10/listTareas");

        verify(redirectAttributes).addFlashAttribute(eq("errorMsg"), anyString());
        verify(tareaRepository, never()).save(any());
    }

    @Test
    void assignDate_invalidDate() {
        Tarea tarea = new Tarea();

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        String result = controller.assignDate(
                LocalDate.now(), 1L, 10L, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/vivienda/10/listTareas");

        verify(redirectAttributes).addFlashAttribute(eq("errorMsg"), anyString());
        verify(tareaRepository, never()).save(any());
    }

    @Test
    void assignDate_taskAlreadyCompleted() {
        Tarea tarea = new Tarea();
        tarea.setCompletada(true);

        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        String result = controller.assignDate(
                LocalDate.now().plusDays(1), 1L, 10L, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/vivienda/10/listTareas");

        verify(redirectAttributes).addFlashAttribute(eq("errorMsg"), anyString());
        verify(tareaRepository, never()).save(any());
    }
}