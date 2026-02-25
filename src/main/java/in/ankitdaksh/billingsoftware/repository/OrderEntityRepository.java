package in.ankitdaksh.billingsoftware.repository;

import in.ankitdaksh.billingsoftware.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderEntityRepository extends JpaRepository<OrderEntity,Long> {

    Optional<OrderEntity> findByOrderId(String orderId);

    List<OrderEntity> findAllByOrderByCreatedAtDesc();
    @Query("SELECT SUM(o.grandTotal) from OrderEntity o Where DATE(o.createdAt) = :date")
    Double sumSalesByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(o) from OrderEntity o Where DATE(o.createdAt) = :date")
    Long countByOrderDate(@Param("date") LocalDate date);

    @Query("SELECT o FROM OrderEntity o ORDER BY o.createdAt DESC")
    List<OrderEntity> findRecentOrders(Pageable pageable);
}
