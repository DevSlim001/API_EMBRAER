import api from "./../../services/api";

export default async function getManuals(){
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.get("/manual",config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};