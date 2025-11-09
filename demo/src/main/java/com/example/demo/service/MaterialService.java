package com.example.demo.service;

import com.example.demo.model.Material;
import com.example.demo.model.Semana;
import com.example.demo.model.Professor;
import com.example.demo.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    public List<Material> findBySemana(Semana semana) {
        return materialRepository.findBySemanaOrderByFechaCreacion(semana);
    }

    public List<Material> findBySemanaId(Long semanaId) {
        return materialRepository.findBySemanaIdOrderByFechaCreacion(semanaId);
    }

    public List<Material> findByProfesor(Professor profesor) {
        return materialRepository.findByProfesor(profesor);
    }

    public Material save(Material material) {
        return materialRepository.save(material);
    }

    public Material createMaterial(String nombre, String fileName, String fileType, byte[] fileData, String descripcion, Semana semana, Professor profesor) {
        Material material = new Material();
        material.setNombre(nombre);
        material.setFileName(fileName);
        material.setFileType(fileType);
        material.setFileData(fileData);
        material.setDescripcion(descripcion);
        material.setSemana(semana);
        material.setProfesor(profesor);
        return save(material);
    }

    public Material update(Long id, Material materialDetails) {
        Material material = findById(id).orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));
        material.setNombre(materialDetails.getNombre());
        material.setFileName(materialDetails.getFileName());
        material.setFileType(materialDetails.getFileType());
        material.setFileData(materialDetails.getFileData());
        material.setDescripcion(materialDetails.getDescripcion());
        return save(material);
    }

    public void delete(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new IllegalArgumentException("Material no encontrado");
        }
        materialRepository.deleteById(id);
    }
}