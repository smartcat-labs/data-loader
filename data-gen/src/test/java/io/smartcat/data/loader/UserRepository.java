package io.smartcat.data.loader;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import io.smartcat.data.loader.model.User;

/**
 * SpringData Repo.
 */
public interface UserRepository extends CrudRepository<User, Serializable> {

}