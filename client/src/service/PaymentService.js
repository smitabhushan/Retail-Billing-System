import axios from "axios";  
 export const createRazorpayOrder = async (data) => {
    return await axios.post("http://localhost:8080/payments/create-order", data, {headers: {'Authorization':`Bearer ${localStorage.getItem('token')}`}});
 }

 export const verifyPayment = async (paymentdata) => {
    return await axios.post("http://localhost:8080/payments/verify", paymentdata, {headers: {'Authorization':`Bearer ${localStorage.getItem('token')}`}});
 }

 