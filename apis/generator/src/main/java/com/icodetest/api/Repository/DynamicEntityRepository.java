package com.icodetest.api.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.icodetest.api.model.DynamicEntity;

public interface DynamicEntityRepository extends JpaRepository<DynamicEntity, Long> {
    @Procedure(value = "create_pivoted_table")
    public void createPivotedTable(@Param("table_name") String table_name);

}
