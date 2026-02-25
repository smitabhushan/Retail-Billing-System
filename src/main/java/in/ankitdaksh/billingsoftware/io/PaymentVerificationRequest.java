package in.ankitdaksh.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationRequest {
    private String razorPayOrderId;
    private String razorpayPaymentId;
    private  String razorpaySignature;
    private String orderId;
}
