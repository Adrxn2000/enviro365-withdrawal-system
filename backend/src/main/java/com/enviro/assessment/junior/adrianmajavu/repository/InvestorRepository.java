package com.enviro.assessment.junior.adrianmajavu.repository;

import com.enviro.assessment.junior.adrianmajavu.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Extending JpaRepository<Investor, Long> gives us save(), findById(),
 * findAll(), delete() etc. for free - Spring generates the implementation
 * at startup by proxying this interface. No SQL written here.
 */
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
