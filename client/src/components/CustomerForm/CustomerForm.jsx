import './CustomerForm.css';

const CustomerForm = ({customerName, mobileNumber, setCustomerName, setMobileNumber}) => {
    return(
        <div className="p-3">
            <div className="mb-3">
                <div className="d-flex align-items-center gap-2">
                    <label htmlFor="customerName" className='col-4'>Customer Name</label>
                    <input type="text" 
                    id="customerName" 
                    className="form-control form-control-sm" 
                    onChange={(e)=> setCustomerName(e.target.value)}
                    value={customerName}
                    required
                    />
                </div>
                
            </div>
            <div className="mb-3">
                <div className="d-flex align-items-center gap-2">
                    <label htmlFor="mobileNumber" className='col-4'>Mobile number</label>
                    <input type="text" 
                    id="mobileNumber" 
                    className="form-control form-control-sm" 
                    onChange={(e)=> setMobileNumber(e.target.value)}
                    value={mobileNumber}
                    required
                    />
                </div>
                
            </div>
        </div>
    )
}       
export default CustomerForm;