import { createContext, useEffect, useState } from "react";
import {fetchCategories} from "../service/CategoryService.js";
import { fetchItems } from "../service/ItemService.js";


// Context create
export const AppContext = createContext(null);

// Proper Provider component
export const AppContextProvider = (props) => {
  const [itemsData,setItemsData]=useState([]);
  const [categories, setCategories] = useState([]);
  const [auth,setAuth]=useState({token:null , role:null});
  const [CardItems,setCardItems]=useState([]);

  const addToCart=(item)=>{
   const existingItem = CardItems.find((cartItem) => cartItem.name === item.name);
   if(existingItem){
    setCardItems(CardItems.map(cartItem => 
      cartItem.name === item.name ? {...cartItem, quantity: cartItem.quantity + 1} : cartItem
    ));
   }else{
    setCardItems([...CardItems, {...item, quantity: 1}]);
   }
  };

  const removeFromCart=(itemId)=>{
       setCardItems(CardItems.filter((cartItem) => cartItem.itemId !== itemId));
  }

  const updateQuantity=(itemId, newQuantity)=>{
    setCardItems(CardItems.map(cartItem => 
      cartItem.itemId === itemId ? {...cartItem, quantity: newQuantity} : cartItem
    ));
  }

  useEffect(() => {
    async function loadData() {
      // Check for auth data in localStorage
      if(localStorage.getItem("token") && localStorage.getItem("role")){
        setAuthData(
                  localStorage.getItem("token"),
                  localStorage.getItem("role")
        );
      }
      // Example fetch category, items
      const response = await fetchCategories();
      const itemresponse = await fetchItems();
      setCategories(response.data);
      setItemsData(itemresponse.data);
    }
    loadData();
  }, []);

  const setAuthData =(token,role) =>{
    setAuth({token,role});
  }

    const clearCart = () => {
    setCardItems([]); // ✅ cart clear
  };


// dependency array added

  const contextValue = {
    categories,
    setCategories,
    auth,
    setAuthData,
    itemsData,
    setItemsData,
    addToCart,
    CardItems,
    removeFromCart,
    updateQuantity,
    clearCart,
  };

  return (
    <AppContext.Provider value={contextValue}>
      {props.children}
    </AppContext.Provider>
  );
};