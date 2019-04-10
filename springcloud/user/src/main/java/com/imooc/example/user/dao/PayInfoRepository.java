package com.imooc.example.user.dao;

import com.imooc.example.user.domain.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayInfoRepository extends JpaRepository<PayInfo, Long> {
    PayInfo findOneByOrderId(Long orderId);
}
