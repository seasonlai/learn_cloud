package com.example.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2018/5/21.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
