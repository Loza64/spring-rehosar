package com.pnc.project.service.impl;

import com.pnc.project.dto.request.registro_hora.Registro_HoraRequest;
import com.pnc.project.dto.response.actividad.ActividadResponse;
import com.pnc.project.dto.response.formulario.FormularioResponse;
import com.pnc.project.dto.response.registro_hora.Registro_HoraResponse;
import com.pnc.project.dto.response.usuario.UsuarioResponse;
import com.pnc.project.entities.Actividad;
import com.pnc.project.entities.Formulario;
import com.pnc.project.entities.Registro_Hora;
import com.pnc.project.entities.Usuario;
import com.pnc.project.repository.Registro_HoraRepository;
import com.pnc.project.service.ActividadService;
import com.pnc.project.service.FormularioService;
import com.pnc.project.service.Registro_HoraService;
import com.pnc.project.service.UsuarioService;
import com.pnc.project.utils.mappers.ActividadMapper;
import com.pnc.project.utils.mappers.FormularioMapper;
import com.pnc.project.utils.mappers.Registro_HoraMapper;
import com.pnc.project.utils.mappers.UsuarioMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class Registro_HoraServiceImpl implements Registro_HoraService {

    private final Registro_HoraRepository registro_HoraRepository;
    private final UsuarioService usuarioService;
    private final ActividadService actividadService;
    private final FormularioService formularioService;

    public Registro_HoraServiceImpl(Registro_HoraRepository registro_HoraRepository,
                                    UsuarioService usuarioService,
                                    ActividadService actividadService,
                                    FormularioService formularioService)
    {
        this.registro_HoraRepository = registro_HoraRepository;
        this.usuarioService = usuarioService;
        this.actividadService = actividadService;
        this.formularioService = formularioService;
    }

    @Override
    public List<Registro_HoraResponse> findAll() {
        return Registro_HoraMapper.toDTOList(registro_HoraRepository.findAll());
    }

    @Override
    public Registro_HoraResponse findById(int id) {
        return Registro_HoraMapper.toDTO(registro_HoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de hora no encontrado")));
    }

    @Override
    public Registro_HoraResponse save(Registro_HoraRequest registroHora) {
        UsuarioResponse usuarioDto = usuarioService.findByCodigo(registroHora.getCodigoUsuario());
        ActividadResponse actividadDto = actividadService.findById(registroHora.getIdActividad());
        FormularioResponse formularioDto = formularioService.findById(registroHora.getIdFormulario());

        Usuario usuario = UsuarioMapper.toEntity(usuarioDto);
        Actividad actividad = ActividadMapper.toEntity(actividadDto);
        Formulario formulario = FormularioMapper.toEntity(formularioDto);

        Registro_Hora entity = Registro_HoraMapper.toEntityCreate(registroHora, usuario, actividad, formulario);

        return Registro_HoraMapper.toDTO(registro_HoraRepository.save(entity));
    }

    @Override
    public Registro_HoraResponse update(Registro_HoraRequest registroHora) {

        Registro_Hora existente = registro_HoraRepository.findById(registroHora.getIdFormulario())
                .orElseThrow(() -> new RuntimeException("Registro de hora no encontrado"));

        UsuarioResponse usuarioDto = usuarioService.findByCodigo(registroHora.getCodigoUsuario());
        ActividadResponse actividadDto = actividadService.findById(registroHora.getIdActividad());
        FormularioResponse formularioDto = formularioService.findById(registroHora.getIdFormulario());

        Usuario usuario = UsuarioMapper.toEntity(usuarioDto);
        Actividad actividad = ActividadMapper.toEntity(actividadDto);
        Formulario formulario = FormularioMapper.toEntity(formularioDto);

        existente.setFechaRegistro(registroHora.getFechaRegistro());
        existente.setHoraInicio(registroHora.getHoraInicio());
        existente.setHoraFin(registroHora.getHoraFin());
        existente.setHorasEfectivas(registroHora.getHorasEfectivas());
        existente.setAula(registroHora.getAula());
        existente.setUsuario(usuario);
        existente.setActividad(actividad);
        existente.setFormulario(formulario);

        return Registro_HoraMapper.toDTO(registro_HoraRepository.save(existente));
    }

    @Override
    public void delete(int id) {
        registro_HoraRepository.deleteById(id);
    }

    @Override
    public List<Registro_HoraResponse> getUsuarioRequests(Usuario usuario) {
        List<Registro_Hora> registros = registro_HoraRepository.findByUsuario(usuario);
        return Registro_HoraMapper.toDTOList(registros);
    }

    @Override
    public List<Registro_HoraResponse> getFormularioRequests(Formulario formulario) {
        List<Registro_Hora> registros = registro_HoraRepository.findByFormulario(formulario);
        return Registro_HoraMapper.toDTOList(registros);
    }

    @Override
    public Registro_HoraResponse calcularHora(LocalDate inicio, LocalDate fin) {
        List<Registro_Hora> registros = registro_HoraRepository.findByFechaRegistroBetween(inicio, fin);

        BigDecimal totalHoras = registros.stream()
                .map(Registro_Hora::getHorasEfectivas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Registro_HoraResponse.builder()
                .horasEfectivas(totalHoras)
                .build();
    }

    @Override
    public List<Registro_HoraResponse> dateList(Usuario usuario, LocalDate inicio, LocalDate fin) {
        List<Registro_Hora> registros = registro_HoraRepository
                .findByUsuarioAndFechaRegistroBetween(usuario, inicio, fin);
        return Registro_HoraMapper.toDTOList(registros);
    }

    @Override
    public List<Registro_HoraResponse> dateListByUsuarioAndRange(
            int idUsuario,
            String fechaInicio,
            String fechaFin) {
        
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;  // yyyy-MM-dd
        LocalDate ini = LocalDate.parse(fechaInicio, fmt);
        LocalDate fin = LocalDate.parse(fechaFin, fmt);

        Usuario usuario = Usuario.builder()
                .idUsuario(idUsuario)
                .build();

        List<Registro_Hora> registros = registro_HoraRepository
                .findByUsuarioAndFechaRegistroBetween(usuario, ini, fin);

        return Registro_HoraMapper.toDTOList(registros);
    }



}
