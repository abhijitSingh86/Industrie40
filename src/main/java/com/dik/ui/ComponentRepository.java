package com.dik.ui;

import com.dik.models.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by billa on 2016-12-03.
 */
public interface ComponentRepository extends JpaRepository<Component, Long> {

//    List<Component> getAllComponents();
}
