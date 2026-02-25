import axios from "axios";

export const latestOrder = async () => {
    return await axios.get("http://localhost:8080/orders/latest",{headers:{'Authorization':`Bearer ${localStorage.getItem("token")}`}});
}

export const createOrder = async (order) => {
    return await axios.post("http://localhost:8080/orders", order,{headers:{'Authorization':`Bearer ${localStorage.getItem("token")}`}});
}

export const deleteOrder = async (orderId) => {
    return await axios.delete(`http://localhost:8080/orders/${orderId}`,{headers:{'Authorization':`Bearer ${localStorage.getItem("token")}`}});
}