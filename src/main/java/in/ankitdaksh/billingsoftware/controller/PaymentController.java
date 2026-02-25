package in.ankitdaksh.billingsoftware.controller;

import com.razorpay.RazorpayException;
import in.ankitdaksh.billingsoftware.io.OrderResponse;
import in.ankitdaksh.billingsoftware.io.PaymentRequest;
import in.ankitdaksh.billingsoftware.io.PaymentVerificationRequest;
import in.ankitdaksh.billingsoftware.io.RazorpayOrderResponse;
import in.ankitdaksh.billingsoftware.service.OrderService;
import in.ankitdaksh.billingsoftware.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final RazorpayService razorpayService;
    private final OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest request) throws RazorpayException{
        return razorpayService.createOrder(request.getAmount(),request.getCurrency());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request){
       return orderService.verifyPayment(request);
    }
}

