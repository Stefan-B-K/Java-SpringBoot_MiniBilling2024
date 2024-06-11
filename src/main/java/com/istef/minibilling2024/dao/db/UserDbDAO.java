package com.istef.minibilling2024.dao.db;

import com.istef.minibilling2024.dao.UserDAO;
import com.istef.minibilling2024.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDbDAO extends JpaRepository<User, Long>, UserDAO {
}
