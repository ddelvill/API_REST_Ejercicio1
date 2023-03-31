package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entities.Mascota;

public interface MascotaDao extends JpaRepository<Mascota, Long>{


     @Query(value = "select m from Mascota m left join fetch m.cliente")
     public List<Mascota> findAll(Sort sort);
 
    @Query(value = "select m from Mascota m left join fetch m.cliente", countQuery = "select count(m) from Mascota m left join m.cliente")
     public Page<Mascota> findAll(Pageable pageable);
 
     @Query(value = "select m from Mascota m left join fetch m.cliente where m.id = :id")
     public Mascota findById(long id);
 
    
}
