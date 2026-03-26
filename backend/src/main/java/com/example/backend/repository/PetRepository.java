package com.example.backend.repository;

import com.example.backend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByStatus(Pet.PetStatus status);

    List<Pet> findByTypeAndStatus(Pet.PetType type, Pet.PetStatus status);

    List<Pet> findByNameContainingIgnoreCaseAndStatus(String name, Pet.PetStatus status);

    long countByStatus(Pet.PetStatus status);
}