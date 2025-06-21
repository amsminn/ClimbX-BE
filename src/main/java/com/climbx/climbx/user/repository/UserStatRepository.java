package com.climbx.climbx.user.repository;

import com.climbx.climbx.user.entity.UserAccount;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatRepository extends JpaRepository<UserAccount, BigInteger> {

}
