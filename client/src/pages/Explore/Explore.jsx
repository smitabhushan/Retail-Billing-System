import { useContext, useState } from 'react';
import './Explore.css';
import { AppContext } from '../../context/AppContext';
import DisplayCategory from '../../components/DisplayCategory/DisplayCategory';
import DisplayItems from '../../components/DisplayItems/DisplayItems';
import CustomerForm from '../../components/CustomerForm/CustomerForm';
import CardItems from '../../components/CardItems/CardItems';
import CardSummary from '../../components/CardSummary/CardSummary'; 

const Explore = () => {
    const { categories } = useContext(AppContext);
    const [selectedCategory, setSelectedCategory] = useState("");
    const [customerName, setCustomerName] = useState("");
    const [mobileNumber, setMobileNumber] = useState("");
          
    console.log(categories);
  return (
    <div className="explore-container text-light">
      <div className="left-column">
        <div className="first-row" style={{ overflowY: "auto" }}>
          {/* content for first row */}
           <DisplayCategory 
                 setSelectedCategory={setSelectedCategory}
                 selectedCategory={selectedCategory} 
                 categories={categories} />
        </div>

        <hr className="horizontal-line" />

        <div className="second-row" style={{ overflowY: "auto" }}>
          {/* content for second row */}
           <DisplayItems selectedCategory={selectedCategory}/>
        </div>
      </div>

      <div className="right-column d-flex flex-column">
        <div className="customer-form-container" style={{ height: "15%" }}>
          {/* customer form content */}
           <CustomerForm
               customerName={customerName}
               mobileNumber={mobileNumber}
               setCustomerName={setCustomerName}
               setMobileNumber={setMobileNumber}
           />
        </div>

        <hr className="my-3 text-light" />

        <div
          className="cart-items-container"
          style={{ height: "50%", overflowY: "auto" }}>
            {/* cart items content */}
            <CardItems/>
        </div>

        <div className="cart-summary-container" style={{height:'35%'}}>
            {/* cart summary */}
            <CardSummary
               customerName={customerName}
               mobileNumber={mobileNumber}
               setCustomerName={setCustomerName}
               setMobileNumber={setMobileNumber}
            />

        </div>
      </div>
    </div>
  );
};

export default Explore;