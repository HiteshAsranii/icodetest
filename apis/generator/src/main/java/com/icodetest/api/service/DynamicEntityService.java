package com.icodetest.api.service;

import org.springframework.stereotype.Service;

import com.icodetest.api.model.DynamicEntity;

import java.util.Map;

@Service
public class DynamicEntityService {

    public DynamicEntity createDynamicEntity(Map<String, String> fieldValues) {
        DynamicEntity dynamicEntity = new DynamicEntity();
        dynamicEntity.setFields(fieldValues);
        return dynamicEntity;
    }
}
