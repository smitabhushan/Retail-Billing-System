package in.ankitdaksh.billingsoftware.service;

import in.ankitdaksh.billingsoftware.io.OrderRequest;
import in.ankitdaksh.billingsoftware.io.OrderResponse;
import in.ankitdaksh.billingsoftware.io.PaymentVerificationRequest;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    void deleteOrder(String orderId);
    List<OrderResponse> getLatestOrders();

    OrderResponse verifyPayment(PaymentVerificationRequest request);

    Double sumSalesByDate(LocalDate date);
    Long countByOrderDate(LocalDate date);
    List<OrderResponse> findRecentOrders();
}
