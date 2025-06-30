package com.pnc.project.controllers;

import com.pnc.project.dto.request.registro_hora.Registro_HoraRequest;
import com.pnc.project.dto.response.registro_hora.Registro_HoraResponse;
import com.pnc.project.service.Registro_HoraService;
import com.pnc.project.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/registros")
public class Registro_HoraController {

    private final Registro_HoraService registroHoraService;
    private final UsuarioService usuarioService;

    public Registro_HoraController(Registro_HoraService registroHoraService, UsuarioService usuarioService) {
        this.registroHoraService = registroHoraService;
        this.usuarioService = usuarioService;
    }

    // Obtener todos los registros de horas
    @GetMapping("/horas")
    public ResponseEntity<List<Registro_HoraResponse>> findAll() {
        List<Registro_HoraResponse> registros = registroHoraService.findAll();
        return ResponseEntity.ok(registros);
    }

    // Obtener un registro de hora por su ID
    @GetMapping("/horas/{id}")
    public ResponseEntity<Registro_HoraResponse> findById(@PathVariable("id") int id) {
        Registro_HoraResponse registro = registroHoraService.findById(id);
        return ResponseEntity.ok(registro);
    }

    // Crear un nuevo registro de hora
    @PostMapping("/horas")
    public ResponseEntity<Registro_HoraResponse> save(@Valid @RequestBody Registro_HoraRequest registroHoraRequest) {
        Registro_HoraResponse registroGuardado = registroHoraService.save(registroHoraRequest);
        return ResponseEntity.ok(registroGuardado);
    }

    // Actualizar un registro de hora existente
    @PutMapping("/horas/{id}")
    public ResponseEntity<Registro_HoraResponse> update(
            @PathVariable("id") int id,
            @Valid @RequestBody Registro_HoraRequest registroHoraRequest
    ) {
        registroHoraRequest.setIdRegistro(id); // Establece el ID del registro al DTO.
        Registro_HoraResponse registroActualizado = registroHoraService.update(registroHoraRequest);
        return ResponseEntity.ok(registroActualizado);
    }

    // Eliminar un registro de hora por su ID
    @DeleteMapping("/horas/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        registroHoraService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Listar registros por rango de fechas y usuario
    @GetMapping("/manage/horas/usuario/fecha")
    public ResponseEntity<List<Registro_HoraResponse>> findByDateRangeAndUsuario(
            @RequestParam("idUsuario") int idUsuario,
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin
    ) {
        List<Registro_HoraResponse> registros = registroHoraService.dateListByUsuarioAndRange(
                idUsuario, fechaInicio, fechaFin
        );
        return ResponseEntity.ok(registros);
    }

    // Listar registros por rango de fechas y código de usuario
    @GetMapping("/manage/horas/usuario/codigo/fecha")
    public ResponseEntity<List<Registro_HoraResponse>> findByDateRangeAndUsuarioCodigo(
            @RequestParam("codigoUsuario") String codigoUsuario,
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin
    ) {
        try {
            // Buscar el usuario por código
            var usuario = usuarioService.findByCodigo(codigoUsuario);
            List<Registro_HoraResponse> registros = registroHoraService.dateListByUsuarioAndRange(
                    usuario.getIdUsuario(), fechaInicio, fechaFin
            );
            return ResponseEntity.ok(registros);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}