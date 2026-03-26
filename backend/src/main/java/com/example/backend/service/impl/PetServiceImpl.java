package com.example.backend.service.impl;

import com.example.backend.entity.Pet;
import com.example.backend.repository.PetRepository;
import com.example.backend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetRepository petRepository;

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public List<Pet> getAvailablePets() {
        return petRepository.findByStatus(Pet.PetStatus.AVAILABLE);
    }

    @Override
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    @Override
    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    @Override
    public List<Pet> searchPets(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAvailablePets();
        }
        return petRepository.findByNameContainingIgnoreCaseAndStatus(
                keyword, Pet.PetStatus.AVAILABLE);
    }

    @Override
    public List<Pet> getPetsByType(String type) {
        try {
            Pet.PetType petType = Pet.PetType.valueOf(type);
            return petRepository.findByTypeAndStatus(petType, Pet.PetStatus.AVAILABLE);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public long countByStatus(Pet.PetStatus status) {
        return petRepository.countByStatus(status);
    }
}