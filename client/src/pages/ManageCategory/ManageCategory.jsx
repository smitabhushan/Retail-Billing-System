
import CategoryForm from '../../components/CategoryForm/CategoryForm';
import CategoryList from '../../components/CategoryList/CategoryList';
import './ManageCategory.css';
const ManageCategory = () => {
  return (
    <div className="category-container text-light">
        
      <div className="left-column">
        {/* यहाँ तुम अपना category form डाल सकते हो  with the help of components */}
        <CategoryForm/>

      </div>

      <div className="right-column">
         <CategoryList/>
      </div>

    </div>
  );
};

export default ManageCategory;