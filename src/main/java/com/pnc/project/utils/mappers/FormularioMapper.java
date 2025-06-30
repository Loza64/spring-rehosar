package com.pnc.project.utils.mappers;

import com.pnc.project.dto.request.formulario.FormularioRequest;
import com.pnc.project.dto.response.formulario.FormularioResponse;
import com.pnc.project.entities.Formulario;
import com.pnc.project.entities.Usuario;

import java.util.List;

public class FormularioMapper {
    public static Formulario toEntity(FormularioResponse formularioDTO){
        return Formulario.builder()
                .idFormulario(formularioDTO.getIdFormulario())
                .fechaCreacion(formularioDTO.getFechaCreacion())
                .estado(formularioDTO.getEstado())
                .usuario(Usuario.builder().codigoUsuario(formularioDTO.getCodigoUsuario()).build())
                .build();
    }

    public static Formulario toEntityCreate(FormularioRequest formularioDTO, Usuario usuario) {
        return Formulario.builder()
                .fechaCreacion(formularioDTO.getFechaCreacion())
                .estado(formularioDTO.getEstado())
                .usuario(usuario)
                .build();
    }

    public static Formulario toEntityUpdate(FormularioRequest formularioDTO, Usuario usuario) {
        return Formulario.builder()
                .idFormulario(formularioDTO.getIdFormulario())
                .fechaCreacion(formularioDTO.getFechaCreacion())
                .estado(formularioDTO.getEstado())
                .usuario(usuario)
                .build();
    }

    public static FormularioResponse toDTO(Formulario formulario) {
        return FormularioResponse.builder()
                .idFormulario(formulario.getIdFormulario())
                .fechaCreacion(formulario.getFechaCreacion())
                .estado(formulario.getEstado())
                .codigoUsuario(formulario.getUsuario().getCodigoUsuario())
                .build();
    }

    public static List<FormularioResponse> toDTOList(List<Formulario> formularios) {
        return formularios.stream()
                .map(FormularioMapper::toDTO)
                .toList();
    }
}
