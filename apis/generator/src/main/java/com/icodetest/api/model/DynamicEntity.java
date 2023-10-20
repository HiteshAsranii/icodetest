package com.icodetest.api.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.Map;

@Entity
public class DynamicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "dynamic_entity_fields", joinColumns = @JoinColumn(name = "entity_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    private Map<String, String> fields;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    // Getter and setter for id (if needed)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
