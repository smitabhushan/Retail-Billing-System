import { useEffect, useState } from 'react';
import './OrderHistory.css';
import { latestOrder, summarizeOrders } from '../../service/OrderService';

const OrderHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState("");
  const [summaryLoading, setSummaryLoading] = useState(false);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await latestOrder();
        setOrders(response.data);
      } catch (e) {
        console.log(e);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  const handleSummarize = async () => {
    try {
      setSummaryLoading(true);
      const response = await summarizeOrders();
      setSummary(response.data);
    } catch (e) {
      console.log(e);
      setSummary("Failed to generate summary.");
    } finally {
      setSummaryLoading(false);
    }
  };

  const formatItems = (items = []) => {
    return items.map((item) => `${item.name} x ${item.quantity}`).join(', ');
  };

  const formatDate = (dateString) => {
    const options = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return new Date(dateString).toLocaleDateString('en-US', options);
  };

  if (loading) {
    return <div className="text-center py-4">Loading orders...</div>;
  }

  return (
    <div className="orders-history-container">
      <h2 className="mb-3 text-light">All Orders</h2>

      {/* Summarize Button */}
      <div className="d-flex justify-content-end mb-3">
        <button
          className="btn btn-primary"
          onClick={handleSummarize}
          disabled={summaryLoading || orders.length === 0}
        >
          {summaryLoading ? "Generating..." : "Summarize Orders"}
        </button>
      </div>

      {orders.length === 0 && (
        <div className="text-center py-4">No order found</div>
      )}

      {orders.length > 0 && (
        <div className="table-responsive">
          <table className="table table-striped table-hover">
            <thead className="table-dark">
              <tr>
                <th>Order Id</th>
                <th>Customer</th>
                <th>Items</th>
                <th>Total</th>
                <th>Payment</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.orderId}>
                  <td>{order.orderId}</td>
                  <td>
                    {order.customerName} <br />
                    <small className="text-muted">{order.phoneNumber}</small>
                  </td>
                  <td>{formatItems(order.items)}</td>
                  <td>₹{order.grandTotal}</td>
                  <td>{order.paymentMethod}</td>
                  <td>
                    <span
                      className={`badge ${
                        order.paymentDetails?.status === 'COMPLETED'
                          ? 'bg-success'
                          : 'bg-warning text-dark'
                      }`}
                    >
                      {order.paymentDetails?.status || 'PENDING'}
                    </span>
                  </td>
                  <td>{formatDate(order.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* AI Summary */}
      {summary && (
  <div className="summary-wrapper mt-4">
    <div className="summary-card shadow">
      <h5 className="mb-3">📊 AI Order Summary</h5>
      <div className="summary-content">
        {summary}
      </div>
    </div>
  </div>
)}
    </div>
  );
};

export default OrderHistory;