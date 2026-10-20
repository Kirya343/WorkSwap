package org.workswap.statistic.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.workswap.statistic.datasource.model.SiteView;

public interface SiteViewRepository extends JpaRepository<SiteView, Long>{
    
    SiteView findByCodeName(String codeName);
}
