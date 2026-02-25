import React, { useContext, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import './cardSummary.css';
import ReceiptPopup from '../ReceiptPopup/ReceiptPopup';
import toast from 'react-hot-toast';
import { createOrder, deleteOrder } from '../../service/OrderService';
import { createRazorpayOrder, verifyPayment } from '../../service/PaymentService';
import { AppConstants } from '../../util/constants';


const CardSummary = ({ customerName, mobileNumber, setCustomerName, setMobileNumber }) => {
  const { CardItems, clearCart } = useContext(AppContext);
  const [isProcessing, setIsProcessing] = useState(false);
  const [orderDetails, setOrderDetails] = useState(null);
  const [showPopup, setShowPopup] = useState(false);

  // total amount calculation
  const totalAmount = CardItems.reduce((total, item) => total + item.price * item.quantity, 0);
  const tax = totalAmount * 0.01;
  const grandTotal = totalAmount + tax;

  const clearAll = () => {
    setCustomerName('');
    setMobileNumber('');
    clearCart();
  };

  const placeOrder = async () => {
    setShowPopup(true);
    clearAll();
  };

  const handlePrintReceipt = () => {
    window.print();
  };

  const loadRazorpayScript = () => {
    return new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.head.appendChild(script);
    });
  };

  // delete order on payment failure
  const deleteOrderOnFailure = async (orderId) => {
    try {
      await deleteOrder(orderId);
    } catch (error) {
      console.error('Failed to delete order:', error);
      toast.error('Failed to delete order');
    }
  };

  const completePayment = async (paymentMode) => {
    if (!customerName || !mobileNumber) {
      toast.error('Please enter customer details');
      return;
    }

    if (CardItems.length === 0) {
      toast.error('Cart is empty');
      return;
    }

    const orderdata = {
      customerName,
      phoneNumber: mobileNumber,
      cartItems: CardItems,
      subtotal: totalAmount,
      tax,
      grandTotal,
      paymentMethod: paymentMode.toUpperCase(),
    };

    setIsProcessing(true);
    try {
      const response = await createOrder(orderdata);
      const savedData = response.data;

      if (paymentMode === 'cash') {
        toast.success('Cash received');
        setOrderDetails(savedData);
      } else if (paymentMode === 'upi') {
        const res = await loadRazorpayScript();
        if (!res) {
          toast.error('Unable to load Razorpay');
          await deleteOrderOnFailure(savedData.orderId);
          return;
        }

        // create razorpay order
        const razorpayResponse = await createRazorpayOrder({ amount: grandTotal, currency: 'INR' });

        const options = {
          key: AppConstants.RAZORPAY_KEY_ID,
          amount: razorpayResponse.data.amount,
          currency: razorpayResponse.data.currency,
          order_id: razorpayResponse.data.id,
          name: 'My Retail Store',
          description: 'Test Transaction',
          handler: async function (response) {
            await verifyPaymentHandler(response, savedData);
          },
          prefill: {
            name: customerName,
            contact: mobileNumber,
          },
          theme: {
            color: '#3399cc',
          },
          modal: {
            ondismiss: async function () {
              await deleteOrderOnFailure(savedData.orderId);
              toast.error('Payment cancelled');
            },
          },
        };

        const rzp = new window.Razorpay(options);
        rzp.on('payment.failed', async function (response) {
          await deleteOrderOnFailure(savedData.orderId);
          toast.error('Payment failed');
          console.error(response.error.description); // ✅ fixed log.error
        });
        rzp.open();
      }
    } catch (error) {
      console.error(error);
      toast.error('Payment processing failed');
    } finally {
      setIsProcessing(false);
    }
  };

  const verifyPaymentHandler = async (response, savedOrder) => {
    const paymentdata = {
      razorpay_order_id: response.razorpay_order_id,
      razorpay_payment_id: response.razorpay_payment_id,
      razorpay_signature: response.razorpay_signature,
      orderId: savedOrder.orderId,
    };
    try {
      const paymentResponse = await verifyPayment(paymentdata);
      if (paymentResponse.status === 200) {
        toast.success('Payment successful');
        setOrderDetails({
          ...savedOrder,
          paymentDetails: {
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          },
        });
      } else {
        toast.error('Payment processing failed');
      }
    } catch (error) {
      console.error(error);
      toast.error('Payment failed');
    }
  };

  return (
    <div className="mt-2">
      <div className="cart-summary-details">
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Item: </span>
          <span className="text-light">₹{totalAmount.toFixed(2)}</span>
        </div>
        <div className="d-flex justify-content-between mb-2">
          <span className="text-light">Tax (1%): </span>
          <span className="text-light">₹{tax.toFixed(2)}</span>
        </div>
        <div className="d-flex justify-content-between mb-4">
          <span className="text-light">Total:</span>
          <span className="text-light">₹{grandTotal.toFixed(2)}</span>
        </div>
      </div>

      <div className="d-flex gap-3">
        <button
          className="btn btn-success flex-grow-1"
          onClick={() => completePayment('cash')}
          disabled={isProcessing}
        >
          {isProcessing ? 'Processing...' : 'Cash'}
        </button>
        <button
          className="btn btn-primary flex-grow-1"
          onClick={() => completePayment('upi')}
          disabled={isProcessing}
        >
          {isProcessing ? 'Processing...' : 'UPI'}
        </button>
      </div>

      <div className="d-flex gap-3 mt-3">
        <button
          className="btn btn-warning flex-grow-1"
          onClick={placeOrder}
          disabled={isProcessing}
        >
          {isProcessing ? 'Processing...' : 'Place Order'}
        </button>
      </div>
         {
          showPopup && (
            <ReceiptPopup 
                orderDetails={{
                  ...orderDetails,
                  razorpayOrderId:orderDetails.paymentDetails?.razorpayOrderId,
                  razorpayPaymentId:orderDetails.paymentDetails?.razorpayPaymentId,
                }}
                onClose={() => setShowPopup(false)}
                onPrint={handlePrintReceipt}
            />
          )
         }
    </div>
  );
};

export default CardSummary;
// import { useContext } from 'react';
// import { AppContext } from '../../context/AppContext';
// import './cardSummary.css';
// import ReceiptPopup from '../ReceiptPopup/ReceiptPopup';
// import toast from 'react-hot-toast';
// import { deleteOrder } from '../../service/OrderService';
// import { createRazorpayOrder, verifyPayment } from '../../service/PaymentService';
// import { AppConstants } from '../../util/constants';

// const cardSummary =({customerName, mobileNumber, setCustomerName, setMobileNumber}) =>{

//     const {CardItems,clearCart}=useContext(AppContext);
//     const [isProcessing, setIsProcessing] = useState(false);
//     const [orderDetails, setOrderDetails] = useState(null);
//     const [showPopup, setShowPopup] = useState(false);

//     // total amount calculation
//     const totalAmount=CardItems.reduce((total,item) => total + item.price * item.quantity, 0);
//     const tax= totalAmount * 0.01; 
//     const grandTotal= totalAmount + tax;

//     const clearAll =()=>{
//         setCustomerName('');
//         setMobileNumber('');
//         clearCart();
//     }

//     const placeOrder = async ()=> { 
//         setShowPopup(true);
//         clearAll();
//     }

//     const handlePrintReceipt = () => {
//       window.print();
//     }

//     const loadRazorpayScript = () => {

//             return new Promise((resolve,reject)=>{
//             const script = document.createElement('script');
//             script.src = 'https://checkout.razorpay.com/v1/checkout.js';
//             script.onload = () => resolve(true);
//             script.onerror = () => resolve(false);
//             document.head.appendChild(script);
//         });
//     }
//     // delete order on payment failure
//     const deleteOrderOnFailure = async (orderId) => {
//         try{
//             await deleteOrder(orderId);
//         }catch(error){
//             console.error("Failed to delete order:", error);
//             toast.error("Failed to delete order");
//         }
//     }
    
//     const completePayment = async () => {
//         if(!customerName ||!mobileNumber){
//             toast.error("Please enter customer details");
//             return;
//         }

//         if(CardItems.length===0){
//             toast.error("Cart is empty");
//             return;
//         }

//         const orderdata={
//                 customerName,
//                  phoneNumber: mobileNumber,
//                  CardItems,
//                  subtotal: totalAmount,
//                  tax,
//                  grandTotal,
//                  paymentMethod:paymentMode.toUpperCase()
//         }

//         setIsProcessing(true);
//         try{

//             const response = await createOrder(orderdata);
//             const savedData = response.data;

//             if(response.status === 201 ||paymentMode ==='cash'||response.status ===200){
//                 toast.success("Cash received");
//                 setOrderDetails(response.data);
//             }else if(paymentMode ==='upi' ||response.status ===201 || response.status ===200){
//                 const res = await loadRazorpayScript();
//                 if(!res){
//                     toast.error("Unable to load razorpay");
//                     await deleteOrderOnFailure(savedData.orderId);
//                     return;
//                 }
//                 //create razorpay order
//               const razorpayResponse= await createRazorpayOrder({amount: grandTotal,currency:'INR'});
//             const options ={
//                 key:AppConstants.RAZORPAY_KEY_ID,
//                 amount: razorpayResponse.data.amount,
//                 currency: razorpayResponse.data.currency,
//                 order_id: razorpayResponse.data.id,
//                 name: "My Retail Store",
//                 description: "Test Transaction",
//                 handler: async function (response){
//                     await verifyPaymentHandler(response,savedData);
//                 },
//                 prefill:{
//                     name: customerName,
//                     contact: mobileNumber
//                 },
//                 theme:{
//                     color: "#3399cc"
//                 },
//                 modal:{
//                     ondismiss: async function(){
//                         await deleteOrderOnFailure(savedData.orderId);
//                         toast.error("Payment cancelled");
//                     }
//                 },
                   
//             };
//             const rzp= new window.Razorpay(options);
//             rzp.on('payment.failed', async function (response){
//                 await deleteOrderOnFailure(savedData.orderId);
//                 toast.error("Payment failed");
//                 log.error(response.error.description);
//             });
//             rzp.open();
            
//         }
//     }catch(error){
//         console.error(error);
//         toast.error("Payment processing failed");
//     }finally{
//         setIsProcessing(false);
//     }
     
// }

//     const verifyPaymentHandler = async (response,savedOrder) => {
//         const paymentdata = {
//             razorpay_order_id: response.razorpay_order_id,
//             razorpay_payment_id: response.razorpay_payment_id,
//             razorpay_signature: response.razorpay_signature,
//             orderId: savedOrder.orderId
//         };
//         try{
//             const paymentResponse= await verifyPayment(paymentdata);
//              if(paymentResponse.status ===200){
//                 toast.success("Payment successful");
//                 setOrderDetails({...savedOrder,
//                       paymentDetails:{
//                         razorpayOrderId: response.razorpay_order_id,
//                         razorpayPaymentId: response.razorpay_payment_id,
//                         razorpaySignature: response.razorpay_signature
//                       },
//                 });
//              }else{
//                 toast.error("Payment processing failed");
               
//              }
//         }catch(error){
//             console.error(error);
//             toast.error("Payment failed");
//         }
//     };


//     return(
//         <div className="mt-2">
//             <div className="cart-summary-details">
//                 <div className="d-flex justify-content-between mb-2">
//                 <span className='text-light'>Item: </span>
//                 <span className='text-light'>₹{totalAmount.toFixed(2)}</span>
//                 </div>
//                 <div className="d-flex justify-content-between mb-2">
//                     <span className='text-light'>Tax (1%): </span>
//                     <span className='text-light'>₹{tax.toFixed(2)}</span>   
//                 </div>
//                 <div className="d-flex justify-content-between mb-4">
//                     <span className='text-light'>Total:</span>
//                     <span className='text-light'>₹{grandTotal.toFixed(2)}</span>   
//                 </div>
//             </div>

//             <div className="d-flex gap-3">
//                 <button className="btn btn-success flex-grow-1" onClick={() =>completePayment("cash")} disabled={isProcessing}>
//                     {isProcessing ? 'Processing...' : 'Cash'}
//                 </button>
//                 <button className="btn btn-primary flex-grow-1" onClick={() =>completePayment("upi")} disabled={isProcessing}>
//                     {isProcessing ? 'Processing...' : 'UPI'}
//                 </button>
//             </div>

//             <div className="d-flex gap-3 mt-3">
//                  <button className="btn btn-warning flex-grow-1" onClick={placeOrder} disabled={isProcessing}>
//                     {isProcessing ? 'Processing...' : 'Place Order'}
//                  </button>
//             </div>
         
//         </div>
//     )
// }
// export default cardSummary;