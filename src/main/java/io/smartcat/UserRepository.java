package io.smartcat;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import io.smartcat.model.User;

/**
 * SpringData Repo.
 */
public interface UserRepository extends CrudRepository<User, Serializable> {

}
