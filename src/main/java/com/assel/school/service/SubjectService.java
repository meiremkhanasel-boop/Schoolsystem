package com.assel.school.service;

import com.assel.school.model.Subject;
import com.assel.school.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> findAll() {
        return subjectRepository.findAllByOrderByNameAsc();
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public Subject save(Subject subject) {
        if (subjectRepository.existsByName(subject.getName())) {
            throw new IllegalArgumentException("Предмет с таким названием уже существует");
        }
        return subjectRepository.save(subject);
    }

    public void update(Long id, String name, String description) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));

        if (!subject.getName().equals(name) && subjectRepository.existsByName(name)) {
            throw new IllegalArgumentException("Предмет с таким названием уже существует");
        }
        subject.setName(name);
        subject.setDescription(description);
        subjectRepository.save(subject);
    }

    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }

    public long count() {
        return subjectRepository.count();
    }
}

