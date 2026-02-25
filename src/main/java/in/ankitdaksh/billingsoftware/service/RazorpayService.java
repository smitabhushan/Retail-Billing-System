package in.ankitdaksh.billingsoftware.service;

import com.razorpay.RazorpayException;
import in.ankitdaksh.billingsoftware.io.RazorpayOrderResponse;

public interface RazorpayService {
    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}
