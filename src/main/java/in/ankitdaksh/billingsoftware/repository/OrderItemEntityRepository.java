package in.ankitdaksh.billingsoftware.repository;

import in.ankitdaksh.billingsoftware.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity,Long> {

}
