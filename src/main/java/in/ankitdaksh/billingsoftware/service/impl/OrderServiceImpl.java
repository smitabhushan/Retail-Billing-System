package in.ankitdaksh.billingsoftware.service.impl;

import in.ankitdaksh.billingsoftware.entity.OrderEntity;
import in.ankitdaksh.billingsoftware.entity.OrderItemEntity;
import in.ankitdaksh.billingsoftware.io.*;
import in.ankitdaksh.billingsoftware.repository.OrderEntityRepository;
import in.ankitdaksh.billingsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderEntityRepository orderEntityRepository;

    // new order banata h, request ko entity me convert karke DB me save karta hai aur response return karta hai
    @Override
    public OrderResponse createOrder(OrderRequest request) {
       OrderEntity newOrder= convertToOrderEntity(request);

        PaymentDetails paymentDetails=new PaymentDetails();
        paymentDetails.setStatus(newOrder.getPaymentMethod() == PaymentMethod.CASH
                ? PaymentDetails.PaymentStatus.COMPLETED : PaymentDetails.PaymentStatus.PENDING);
        newOrder.setPaymentDetails(paymentDetails);

        // Request ke cartItems ko OrderItemEntity list me convert karke newOrder me set kar raha hai
        List<OrderItemEntity> orderItems=request.getCartItems().stream()
                .map(this::convertToOrderItemEntity)
                .collect(Collectors.toList());
        newOrder.setItems(orderItems);

        newOrder=orderEntityRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    // OrderRequest ko OrderEntity me convert karta hai (DB save ke liye)
    private OrderEntity convertToOrderEntity(OrderRequest request) {
       return OrderEntity.builder()
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())
                .subtotal(request.getSubtotal())
                .tax(request.getTax())
                .grandTotal(request.getGrandTotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();
    }
    // OrderEntity ko OrderResponse me convert karta hai (client ko bhejne ke liye)
    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .customerName(newOrder.getCustomerName())
                .phoneNumber(newOrder.getPhoneNumber())
                .subtotal(newOrder.getSubtotal())
                .tax(newOrder.getTax())
                .grandTotal(newOrder.getGrandTotal())
                .paymentMethod(newOrder.getPaymentMethod())
                // OrderEntity ke items ko OrderResponse ke items me convert karke list banata hai aur response object me set karta hai.
                .items(newOrder.getItems().stream()
                        .map(this::convertToItemResponse)
                        .collect(Collectors.toList()))
                .paymentDetails(newOrder.getPaymentDetails())
                .createdAt(newOrder.getCreatedAt())
                .build();
    }
    // OrderItemEntity ko OrderItemResponse me convert karta hai
    private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemEntity orderItemEntity) {
        return OrderResponse.OrderItemResponse.builder()
                .itemId(orderItemEntity.getItemId())
                .name(orderItemEntity.getName())
                .price(orderItemEntity.getPrice())
                .quantity(orderItemEntity.getQuantity())
                .build();

    }
    // OrderItemRequest ko OrderItemEntity me convert karta hai
    private OrderItemEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
      return OrderItemEntity.builder()
               .itemId(orderItemRequest.getItemId())
               .name(orderItemRequest.getName())
               .price(orderItemRequest.getPrice())
               .quantity(orderItemRequest.getQuantity())
               .build();
    }

    @Override
    public void deleteOrder(String orderId) {
       OrderEntity existingOrder= orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
       orderEntityRepository.delete(existingOrder);

    }

    @Override
    public List<OrderResponse> getLatestOrders() {
       return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse verifyPayment(PaymentVerificationRequest request) {
        // Find existing order
        OrderEntity existingOrder= orderEntityRepository.findByOrderId(request.getOrderId())
                .orElseThrow(()-> new RuntimeException("Order not found"));

        if(!verifyRazorpaySignature(request.getRazorPayOrderId(),request.getRazorpayPaymentId(),request.getRazorpaySignature())){
            throw new RuntimeException("Payment verification failed");
        }

        // Update payment details
        PaymentDetails paymentDetails=existingOrder.getPaymentDetails();
        paymentDetails.setRazorpayOrderId(request.getRazorPayOrderId());
        paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
        paymentDetails.setRazorpaySignature(request.getRazorpaySignature());
        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);
        // Save updated order
        existingOrder=orderEntityRepository.save(existingOrder);
        // Convert to response
        return convertToResponse(existingOrder);
    }

    @Override
    public Double sumSalesByDate(LocalDate date) {
        return orderEntityRepository.sumSalesByDate(date);
    }

    @Override
    public Long countByOrderDate(LocalDate date) {
        return orderEntityRepository.countByOrderDate(date);
    }

    @Override
    public List<OrderResponse> findRecentOrders() {
       return orderEntityRepository.findRecentOrders(PageRequest.of(0,5))
                .stream()
                .map(orderEntity -> convertToResponse(orderEntity))
                .collect(Collectors.toList());
    }


    private boolean verifyRazorpaySignature(String razorPayOrderId, String razorPayOrderId1, String razorpaySignature) {
        return true;
    }
}
