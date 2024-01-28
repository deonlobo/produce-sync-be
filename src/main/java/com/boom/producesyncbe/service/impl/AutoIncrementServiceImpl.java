package com.boom.producesyncbe.service.impl;

import com.boom.producesyncbe.Data.IdCount;
import com.boom.producesyncbe.repository.IdCountRepository;
import com.boom.producesyncbe.service.AutoIncrementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutoIncrementServiceImpl implements AutoIncrementService {
    @Autowired
    private IdCountRepository repository;

    @Override
    public String getOrUpdateIdCount(String stringId) {
        Optional<IdCount> existingDocumentOptional = repository.findById(stringId);

        if (existingDocumentOptional.isPresent()) {
            IdCount exists = existingDocumentOptional.get();
            exists.setCount(exists.getCount()+1);
            return (stringId+"::"+repository.save(exists).getCount());
        } else{
            IdCount countDoc = new IdCount();
            countDoc.setId(stringId);
            countDoc.setCount(1001);
            return (stringId+"::"+repository.insert(countDoc).getCount());
        }
    }
}
