package services.impl;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.IService;
import services.exceptions.ServiceException;

import javax.validation.ConstraintViolationException;

@Transactional
@Data
public abstract class BaseService<T> implements IService<T> {

    @Autowired
    private JpaRepository<T, Long> jpaRepository;

    @Override
    public T save(T t) {
        try {
            return jpaRepository.save(t);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error were detected while saving " + t);
        }
    }

    @Override
    public T get(Long id) {
        try {
            return jpaRepository.findById(id).orElse(null);
        } catch (Exception e) {
            throw new ServiceException("Error were detected while getting " + id);
        }
    }

    @Override
    public T update(T t) {
        try {
            return save(t);
        } catch (Exception e) {
            throw new ServiceException("Error were detected while updating " + t);
        }
    }

    @Override
    public void delete(T t) {
        try {
            jpaRepository.delete(t);
        } catch (Exception e) {
            throw new ServiceException("Error were detected while deleting " + t);
        }
    }
}